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
public class RelationService {

    private final EntityMarkRepo entityMarkRepo;

    private final RelationMarkRepo relationMarkRepo;

    private final VerStateRepo verStateRepo;

    private final RelaReflectRepo relaReflectRepo;

    private final GlobalEntitiesRepo globalEntitiesRepo;

    private final StmtEntitiesRepo stmtEntitiesRepo;

    @Autowired
    public RelationService(EntityMarkRepo entityMarkRepo, RelationMarkRepo relationMarkRepo, VerStateRepo verStateRepo,
                           RelaReflectRepo relaReflectRepo, StmtEntitiesRepo stmtEntitiesRepo, GlobalEntitiesRepo globalEntitiesRepo) {
        this.entityMarkRepo = entityMarkRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.verStateRepo = verStateRepo;
        this.relaReflectRepo = relaReflectRepo;
        this.stmtEntitiesRepo = stmtEntitiesRepo;
        this.globalEntitiesRepo = globalEntitiesRepo;
    }

    @Transactional
    public ResponseResult<String> dealWithRelation(long userId, long relationMarkId, long statId, String content,
                                                   int passed, long relationId, String description) {
        RelationMark record = getRelationMark(relationMarkId);
        if (record == null) { // 审批文本不存在
            return ResponseResult.of("审批失败", "审批文本不存在");
        }

        if (record.getStatement() == null || record.getStatement().getVerUser() == null ||
                userId != record.getStatement().getVerUser().getId()) {
            return ResponseResult.of("审批失败", "所属段落没有分配审批人或分配的审批人和用户id不相等");
        }
        Optional<RelationReflect> refOptional = relaReflectRepo.findById(relationId);
        // 提交的关系id不存在
        if (!refOptional.isPresent()) {
            return ResponseResult.of("审批失败", null);
        }

        record.setContent(content);
        record.setDescription(description);
        record.setReflect(refOptional.get());
        //  检查待更新的content， 其中的文本是否合法
        Pair<Boolean, String> checkEntity = this.checkEntityExistence(record);
        if (!checkEntity.getKey()){ // 实体不合法
            this.relationMarkRepo.save(record);
            return ResponseResult.of("审批失败", checkEntity.getValue());
        }
        record.setPassed(passed);
        record.setVerDate(new Date());
        record.updateVerifyResult();
        relationMarkRepo.save(record);

        return ResponseResult.of("审批成功", null);
    }

    @Transactional
    public ResponseResult<String> addNewRelation(long userId, long relationMarkId, long statId, String content,
                                                   int passed, long relationId, String description) {
        VerifyStatement recordStmt = getStatement(statId);
        // 所属段落没有分配审批人或分配的审批人和用户id不相等
        if (recordStmt == null || recordStmt.getVerUser() == null ||
                userId != recordStmt.getVerUser().getId()) {
            return ResponseResult.of("审批失败", "所属段落没有分配审批人或分配的审批人和用户id不相等");
        }
        Optional<RelationReflect> refOptional = relaReflectRepo.findById(relationId);


        RelationMark record = new RelationMark(content, passed, description, refOptional.get(), recordStmt);
//  检查待更新的content， 其中的文本是否合法
        Pair<Boolean, String> checkEntity = this.checkEntityExistence(record);
        if(!checkEntity.getKey()) // 实体不合法
            return ResponseResult.of("关系添加失败", checkEntity.getValue());

        relationMarkRepo.save(record);
        return ResponseResult.of("关系添加成功", null);
    }


    public Pair<Boolean, String> checkEntityExistence(RelationMark record){
        Pair<Boolean, String> checkRes = new Pair<>(true, "关系数据审核成功");
        List<Pair<Integer, Integer>> entitiesLoc = RelationMark.getEntitiesLoc(record.getContent());
        VerifyStatement statement = record.getStatement();
        EntityMark entityMark = statement.getEntityMark();
        String nonTagContent = entityMark.getNonTagContent();
        List<StmtEntities> curEntities = StmtEntities.list2Entities(entitiesLoc, statement);
        StmtEntities entity1 = curEntities.get(0);
        StmtEntities entity2 = curEntities.get(1);

        List<StmtEntities> coverEntities = this.stmtEntitiesRepo.findAllByStatementAndHeadLessThanAndTailGreaterThanOrderByHeadAsc(
                statement,entity1.getTail(),entity1.getHead());
        if(coverEntities.size() == 0){
            record.setStmtEntity1(null);
            return new Pair<>(false,"选中的实体e1：‘" + nonTagContent.substring(entity1.getHead(),entity1.getTail())
                    + "’ 不存在于实体标注中");
        }
        log.info("---------------找到的实体1名为：" + coverEntities.get(0).getGlobalEntity().getEntityName());
        record.setStmtEntity1(coverEntities.get(0)); // 第一个覆盖了的实体

        coverEntities = this.stmtEntitiesRepo.findAllByStatementAndHeadLessThanAndTailGreaterThanOrderByHeadAsc(
                statement,entity2.getTail(),entity2.getHead());
        if(coverEntities.size() == 0){
            record.setStmtEntity2(null);
            return new Pair<>(false,"选中的实体e2：‘" + nonTagContent.substring(entity2.getHead(),entity2.getTail())
                    + "’ 不存在于实体标注中");
        }
        log.info("---------------找到的实体名2为：" + coverEntities.get(0).getGlobalEntity().getEntityName());
        record.setStmtEntity2(coverEntities.get(0)); // 第一个覆盖了的实体
        record.setContentToFront();
        return checkRes;
    }

    @Transactional
    public Pair<Boolean, String> checkEntityExistAndSav(RelationMark record){
        Pair<Boolean, String> checkEntity = this.checkEntityExistence(record);
        this.relationMarkRepo.save(record);
        return checkEntity;
    }

    @Transactional
    public RelationMark getRelationMark(long relationMarkId) {
        Optional<RelationMark> recordOptional = relationMarkRepo.findById(relationMarkId);
        return recordOptional.orElse(null);
    }

    @Transactional
    public VerifyStatement getStatement(long statementId) {
        Optional<VerifyStatement> recordOptional = verStateRepo.findById(statementId);
        return recordOptional.orElse(null);
    }

}
