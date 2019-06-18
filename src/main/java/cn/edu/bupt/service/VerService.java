package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.*;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.bean.vo.EntityReflectVo;
import cn.edu.bupt.repository.*;
import cn.edu.bupt.util.ResponseResult;
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
public class VerService {

    private final EntityMarkRepo entityMarkRepo;

    private final RelationMarkRepo relationMarkRepo;

    private final VerStateRepo verStateRepo;

    private final RelaReflectRepo relaReflectRepo;

    private final UserRepo userRepo;

    private final MarkOpinionRepo markOpinionRepo;

    private final EntiReflectRepo entiReflectRepo;

    @Autowired
    public VerService(EntityMarkRepo entityMarkRepo, RelationMarkRepo relationMarkRepo, VerStateRepo verStateRepo,
                      RelaReflectRepo relaReflectRepo, UserRepo userRepo, MarkOpinionRepo markOpinionRepo, EntiReflectRepo entiReflectRepo) {
        this.entityMarkRepo = entityMarkRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.verStateRepo = verStateRepo;
        this.relaReflectRepo = relaReflectRepo;
        this.userRepo = userRepo;
        this.markOpinionRepo = markOpinionRepo;
        this.entiReflectRepo = entiReflectRepo;
    }

    @Transactional
    public ResponseResult<String> dealWithEntity(long userId, long entityId, long statId, String content, int passed,
                                                 String description) {
        EntityMark record = getEntity(entityId);
        if (record == null)  // 审批文本不存在
            return ResponseResult.of("审批失败", null);
        // 提交的内容还没有分配审批人或系统记录的审批人id和用户id不相等
        if (record.getStatement() == null || record.getStatement().getVerUser() == null
                || record.getStatement().getVerUser().getId() != userId) {
            return ResponseResult.of("审批失败", null);
        }
        // 审批文本所属段落的id和用户提交的段落id不相等
        if (record.getStatement().getId() != statId)
            return ResponseResult.of("审批失败", null);
        record.setPassed(passed);
        record.setVerDate(new Date());
        record.setContent(content);
        record.setDescription(description);
        record.updateVerifyResult();
        entityMarkRepo.save(record);
        // 如果该段落所有文本全部审核完毕，更新段落表的状态
//        if (relationMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0 &&
//                entityMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0) {
//            VerifyStatement statement = record.getStatement();
//            statement.setState(2);
//            verStateRepo.save(statement);
//        }
        return ResponseResult.of("审批成功", null);
    }

    @Transactional
    public ResponseResult<String> dealWithRelation(long userId, long relationMarkId, long statId, String content,
                                                   int passed, long relationId, String description) {
        RelationMark record = getRelationMark(relationMarkId);
        if (record == null) { // 审批文本不存在
            return ResponseResult.of("审批失败", "审批文本不存在");
        }
        // 所属段落没有分配审批人或分配的审批人和用户id不相等
        if (record.getStatement() == null || record.getStatement().getVerUser() == null ||
                userId != record.getStatement().getVerUser().getId()) {
            return ResponseResult.of("审批失败", null);
        }
        // 提交的段落id和文本所属段落的id不相等
        if (record.getStatement().getId() != statId) {
            return ResponseResult.of("审批失败", null);
        }
        Optional<RelationReflect> refOptional = relaReflectRepo.findById(relationId);
        // 提交的关系id不存在
        if (!refOptional.isPresent()) {
            return ResponseResult.of("审批失败", null);
        }
        record.setContent(content);
        record.setPassed(passed);
        record.setVerDate(new Date());
        record.setDescription(description);
        record.setReflect(refOptional.get());
        record.updateVerifyResult();
        relationMarkRepo.save(record);
        // 如果该段落所有文本全部审核完毕，更新段落表的状态
//        if (relationMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0 &&
//                entityMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0) {
//            VerifyStatement statement = record.getStatement();
//            statement.setState(2);
//            verStateRepo.save(statement);
//        }
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
        // 提交的关系id不存在
        if (!refOptional.isPresent()) {
            return ResponseResult.of("审批失败", "提交的关系id不存在");
        }

        RelationMark record = new RelationMark(content, passed, description, refOptional.get(), recordStmt);

        relationMarkRepo.save(record);
        return ResponseResult.of("关系添加成功", null);
    }

    @Transactional
    public VerMarksVo curUnViewedStatement(long userId, int pageNo, int pageSize) {
        Optional<VerifyStatement> statementOptional = Optional.empty();
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) { // 如果用户在上次审批时，没有将该段落的所有文本全部审核完毕
            statementOptional = verStateRepo.findByVerUserAndState(user.get(), VerifyStatement.State.STARTED.ordinal());
        }
//        if (!statementOptional.isPresent()) { // 查找第一条待审核的段落
//            statementOptional = verStateRepo.findFirstByState(0);
//        }
        if (statementOptional.isPresent()) {
            VerifyStatement statement = statementOptional.get();
//            // 分配审核人
//            statement.setVerUser(userRepo.getOne(userId));
//            // 设置段落审核状态(开始审核但未全部审核完毕)
//            statement.setState(1);
//            verStateRepo.save(statement);
//
//            // 更新审批文本状态
//            for (int i = 0, size = statement.getEntityMarks().size(); i < size; ++i) {
//                EntityMark mark = statement.getEntityMarks().get(i);
//                // 文本更新为已审核状态
//                mark.setReviewed(1);
//                entityMarkRepo.save(mark);
//            }
//            for (int i = 0, size = statement.getRelationMarks().size(); i < size; ++i) {
//                RelationMark mark = statement.getRelationMarks().get(i);
//                // 文本更新为已审核状态
//                mark.setReviewed(1);
//                relationMarkRepo.save(mark);
//            }
            VerMarksVo result = new VerMarksVo();
            result.setId(statement.getId());
            result.setPdfUrl(statement.getPdfUrl());
            result.setPdfNo(statement.getPdfNo());
            List<VerMarksVo.Reflect> reflects = new ArrayList<>();
            List<RelationReflect> originReflects = relaReflectRepo.findAll();
            for (RelationReflect originReflect : originReflects) {
                reflects.add(new VerMarksVo.Reflect(originReflect.getId(), originReflect.getRName()));
            }
            if (statement.getEntityMarks() != null && statement.getEntityMarks().size() > 0) {
                result.addEntities(statement.getEntityMarks());
            }
            if (statement.getRelationMarks() != null && statement.getRelationMarks().size() > 0) {
                result.addRelations(statement.getRelationMarks(), reflects);
            }
            result.setPageNo(1);
            result.setTotalCount(result.getData().size());
            return result;
        }
        return null;
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

    @Transactional
    public VerifyStatement getStatement(long statementId) {
        Optional<VerifyStatement> recordOptional = verStateRepo.findById(statementId);
        return recordOptional.orElse(null);
    }

    @Transactional
    public boolean beginNext(Long id, boolean completeLast) {
        Optional<VerifyStatement> statementOptional = Optional.empty();
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {  // 试图找到已经分配给这个用户的数据
            statementOptional = verStateRepo.findByVerUserAndState(userOptional.get(), VerifyStatement.State.STARTED.ordinal());
        }
        if (statementOptional.isPresent()) { // 存在这个已经分配过的数据
            if (completeLast) { // 标志已经完成这一条
                VerifyStatement statement = statementOptional.get();
                statement.setState(VerifyStatement.State.END.ordinal());
                verStateRepo.save(statement);
            } else {
                return true;
            }
        }

        //分配一条新的数据
        statementOptional = verStateRepo.findFirstByState(VerifyStatement.State.UNSTARTED.ordinal());
        if (statementOptional.isPresent()) {
            VerifyStatement statement = statementOptional.get();
            // 更新审批文本状态
//            if (statement.getEntityMarks() != null && statement.getEntityMarks().size() > 0) {
            for (int i = 0, size = statement.getEntityMarks().size(); i < size; ++i) {
                EntityMark mark = statement.getEntityMarks().get(i);
                // 文本更新为已审核状态
                mark.setReviewed(1);
                entityMarkRepo.save(mark);
            }
//            }
//            if (statement.getRelationMarks() != null && statement.getRelationMarks().size() > 0) {
            for (int i = 0, size = statement.getRelationMarks().size(); i < size; ++i) {
                RelationMark mark = statement.getRelationMarks().get(i);
                // 文本更新为已审核状态
                mark.setReviewed(1);
                relationMarkRepo.save(mark);
//                }
            }
            statement.setVerUser(userRepo.getOne(id));
            statement.setState(VerifyStatement.State.STARTED.ordinal());
            verStateRepo.save(statement);
            return true;
        }
        return false;
    }

    @Transactional
    public List<String> beginWithPrefixOpinion(String prefix){
        List<MarkOpinion> markOpinions = markOpinionRepo.findByOpinionStartingWith(prefix);
        List<String> opinions = null;
        if(markOpinions!=null&&markOpinions.size()>0){
            opinions = markOpinions.stream().map(MarkOpinion::getOpinion).collect(Collectors.toList());
        }
        return opinions;
    }

    @Transactional
    public EntityReflectVo getEntityReflect(){
        List<EntityReflect> entityReflectList = this.entiReflectRepo.findAll();
        EntityReflectVo result = new EntityReflectVo();
        result.addEntities(entityReflectList);
        return result;
    }
}
