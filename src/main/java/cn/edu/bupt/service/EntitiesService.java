package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.*;
import cn.edu.bupt.bean.vo.EntityReflectVo;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.repository.*;
import cn.edu.bupt.util.ResponseResult;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EntitiesService {

    private final EntityMarkRepo entityMarkRepo;

    private final RelationMarkRepo relationMarkRepo;


    private final GlobalEntitiesRepo globalEntitiesRepo;

    private final StmtEntitiesRepo stmtEntitiesRepo;

    @Autowired
    public EntitiesService(EntityMarkRepo entityMarkRepo, RelationMarkRepo relationMarkRepo,
                           StmtEntitiesRepo stmtEntitiesRepo,GlobalEntitiesRepo globalEntitiesRepo) {
        this.entityMarkRepo = entityMarkRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.stmtEntitiesRepo = stmtEntitiesRepo;
        this.globalEntitiesRepo = globalEntitiesRepo;
    }

    @Transactional
    public ResponseResult<String> dealWithEntity(long userId, long entityId, long statId, String content, int passed,
                                                 String description) {
        EntityMark record = getEntity(entityId);
        if (record == null)  // 审批文本不存在
            return ResponseResult.of("审批失败", "审批文本不存在");
        // 提交的内容还没有分配审批人或系统记录的审批人id和用户id不相等
        if (record.getStatement() == null || record.getStatement().getVerUser() == null
                || record.getStatement().getVerUser().getId() != userId) {
            return ResponseResult.of("审批失败", "提交的内容还没有分配审批人或系统记录的审批人和用户不一致");
        }

        content = EntityMark.recoverTagContent(content);
        record.setPassed(passed);
        record.setVerDate(new Date());
        System.out.println("before change :"+ record.getContent());
        record.setDescription(description);
        boolean hasChange = record.updateVerifyResult(content);
        record.setContent(content);
        if(hasChange){
            /*  通过且发生了修改，需要获取并处理所有的修改*/
            this.dealWithEntitiesModify(record);
        }
        entityMarkRepo.save(record);
        return ResponseResult.of("审批成功", null);
    }

    public List<StmtEntities> dealWithEntitiesModify(EntityMark record){
        List<Pair<Integer, Pair<Integer, String>>> curEntitiesLoc = record.getEntitiesLoc(record.getContent());
        System.out.println("now :"+ record.getContent());
        VerifyStatement statement = record.getStatement();
        // 取出句子原来的所有entities
        List<StmtEntities> originalEntities = this.stmtEntitiesRepo.findAllByStatementOrderByHead(statement);
        List<StmtEntities> curEntities = StmtEntities.list2EntitiesWithTag(curEntitiesLoc, statement);
        List<StmtEntities> resEntities = new ArrayList<>();  // 最终的所有实体（先放修改前后都没改变的实体）
        List<StmtEntities> updateEntities = new ArrayList<>(); // 实际发生了更新的实体
        for(StmtEntities originalEntity: originalEntities){
            for(StmtEntities curEntity: curEntities){
                if(originalEntity.isIntersect(curEntity)){ // 原实体与某个现实体有交集，视作这个原实体的变化
                    if(!originalEntity.isEqual(curEntity))
                        originalEntity.updateSE(curEntity);
                    updateEntities.add(originalEntity);
                    curEntities.remove(curEntity);
                    break;
                }
            }
        }

        updateEntities.addAll(curEntities);
        String nonTagContent = record.getNonTagContent();
        for(StmtEntities updateEntity: updateEntities){ // 处理发生了修改的实体及新实体
            this.updateGlobalEntity(updateEntity, nonTagContent);
            resEntities.add(updateEntity); // 更新后的实体合并到最终的实体结果里
        }

        originalEntities.removeAll(resEntities); // 没有再被覆盖到的将被删除
        for(StmtEntities removeEntity: originalEntities){
            //需要更新stmtEntities附着的关系记录（删除时会使他变Null）
            this.deleteOriginEntities(removeEntity);
        }

        this.deleteGlobalEntitiesByCountEqualsZERO();
        return resEntities;
    }

    @Transactional
    public void updateGlobalEntity(StmtEntities modified_record, String nonTagContent){
        //修改StmtEntities原本对应的实体的计数值（若存在） 并对修改后的实体加上计数值
        GlobalEntities origiGlobalEntity = modified_record.getGlobalEntity();
        if(origiGlobalEntity != null){
            origiGlobalEntity = this.globalEntitiesRepo.findById(origiGlobalEntity.getId()); // 因为同一句里可能有重复的实体，重新取出它的当前值
            boolean gEntityHasOther = origiGlobalEntity.updateCount(-1);
            this.globalEntitiesRepo.save(origiGlobalEntity);
        }

        // 新实体
        String entityName = nonTagContent.substring(modified_record.getHead(), modified_record.getTail());
        GlobalEntities newGlobalEntity = this.globalEntitiesRepo.findByEntityName(entityName);
        if(newGlobalEntity != null){
            newGlobalEntity.updateCount(1);
            this.globalEntitiesRepo.save(newGlobalEntity);
        }else{
            newGlobalEntity = new GlobalEntities(entityName);
            this.globalEntitiesRepo.save(newGlobalEntity);
        }
        modified_record.setGlobalEntity(newGlobalEntity);
        this.stmtEntitiesRepo.save(modified_record);
    }

    @Transactional
    void deleteOriginEntities(StmtEntities toRemoveRecord){ // 删除没有适配对象的旧实体，并清除指向它们的关系数据中的相应字段
        List<RelationMark> markWithE1 = toRemoveRecord.getMarks_e1();
        for(RelationMark relation: markWithE1){
            relation.setStmtEntity1(null);
            this.relationMarkRepo.save(relation);
        }

        List<RelationMark> markWithE2 = toRemoveRecord.getMarks_e2();
        for(RelationMark relation: markWithE2){
            relation.setStmtEntity2(null);
            this.relationMarkRepo.save(relation);
        }

        //清除这个局部实体对象,修改全局计数
        GlobalEntities origiGlobalEntity = toRemoveRecord.getGlobalEntity();
        if(origiGlobalEntity != null){
            origiGlobalEntity = this.globalEntitiesRepo.findById(origiGlobalEntity.getId()); // 因为同一句里可能有重复的实体，重新取出它的当前值
            origiGlobalEntity.updateCount(-1);
            this.globalEntitiesRepo.save(origiGlobalEntity);
        }
        this.stmtEntitiesRepo.deleteById(toRemoveRecord.getId());
    }

    void deleteGlobalEntitiesByCountEqualsZERO(){
        // 删除所有计数值为0的全局实体
        this.globalEntitiesRepo.deleteAllByCountEquals(0);
    }

    @Transactional
    public EntityMark getEntity(long entityMarkId) {
        Optional<EntityMark> recordOptional = entityMarkRepo.findById(entityMarkId);
        return recordOptional.orElse(null);
    }

    @Transactional
    public RelationMark getRelationMark(long relationMarkId) {
        Optional<RelationMark> recordOptional = relationMarkRepo.findById(relationMarkId);
        return recordOptional.orElse(null);
    }
}
