package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.RelationReflect;
import cn.edu.bupt.bean.po.VerifyStatement;
import cn.edu.bupt.bean.vo.VerMarksVo;
import cn.edu.bupt.repository.*;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.ResultTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class VerService {

    private final EntityMarkRepo entityMarkRepo;

    private final RelationMarkRepo relationMarkRepo;

    private final VerStateRepo verStateRepo;

    private final RelaReflectRepo relaReflectRepo;

    private final UserRepo userRepo;

    @Autowired
    public VerService(EntityMarkRepo entityMarkRepo, RelationMarkRepo relationMarkRepo, VerStateRepo verStateRepo,
                      RelaReflectRepo relaReflectRepo, UserRepo userRepo) {
        this.entityMarkRepo = entityMarkRepo;
        this.relationMarkRepo = relationMarkRepo;
        this.verStateRepo = verStateRepo;
        this.relaReflectRepo = relaReflectRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ResponseResult<String> dealWithEntity(long userId, long entityId, long statId, String content, int passed) {
        EntityMark record = getEntity(entityId);
        if (record == null)  // 需要审批的文本不存在
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        // 提交的内容还没有分配审批人或系统记录的审批人id和用户id不相等
        if (record.getStatement() == null || record.getStatement().getVerUser() == null
                || record.getStatement().getVerUser().getId() != userId) {
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        }
        // 审批文本所属段落的id和用户提交的段落id不相等
        if (record.getStatement().getId() != statId)
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        record.setPassed(passed);
        record.setVerDate(new Date());
        record.setContent(content);
        entityMarkRepo.saveAndFlush(record);
        // 如果该段落所有文本全部审核完毕，更新段落表的状态
        if (relationMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0 &&
                entityMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0) {
            VerifyStatement statement = record.getStatement();
            statement.setState(2);
            verStateRepo.save(statement);
        }
        return ResponseResult.success("审批成功", null);
    }

    @Transactional
    public ResponseResult<String> dealWithRelation(long userId, long relationMarkId, long statId, String content,
                                                   int passed, long relationId) {
        RelationMark record = getRelationMark(relationMarkId);
        if (record == null) { // 审批文本不存在
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        }
        if (record.getStatement() == null || record.getStatement().getVerUser() == null ||
                userId != record.getStatement().getVerUser().getId()) {
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        }
        if (record.getStatement().getId() != statId) {
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        }
        Optional<RelationReflect> refOptional = relaReflectRepo.findById(relationId);
        if (!refOptional.isPresent()) {
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "审批失败", null);
        }
        record.setContent(content);
        record.setPassed(passed);
        record.setVerDate(new Date());
        record.setReflect(refOptional.get());
        relationMarkRepo.save(record);
        if (relationMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0 &&
                entityMarkRepo.countByPassedAndStatement(-1, record.getStatement()) == 0) {
            VerifyStatement statement = record.getStatement();
            statement.setState(2);
            verStateRepo.save(statement);
        }
        return ResponseResult.success("审批成功", null);
    }

    @Transactional
    public VerMarksVo nextUnViewedStatement(long userId) {
        Optional<VerifyStatement> statementOptional = verStateRepo.findFirstByState(0);
        if (statementOptional.isPresent()) {
            VerifyStatement statement = statementOptional.get();
            statement.setVerUser(userRepo.getOne(userId));
            statement.setState(1);
            verStateRepo.save(statement);

            // 更新审批文本状态
            for (int i = 0, size = statement.getEntityMarks().size(); i < size; ++i) {
                EntityMark mark = statement.getEntityMarks().get(i);
                mark.setReviewed(1);
                entityMarkRepo.save(mark);
            }
            for (int i = 0, size = statement.getRelationMarks().size(); i < size; ++i) {
                RelationMark mark = statement.getRelationMarks().get(i);
                mark.setReviewed(1);
                relationMarkRepo.save(mark);
            }
            VerMarksVo result = new VerMarksVo();
            result.setId(statement.getId());
            result.setPdfUrl(statement.getPdfUrl());
            result.setPdfNo(statement.getPdfNo());
            result.setEntityMarks(statement.getEntityMarks());
            result.setRelationMarks(statement.getRelationMarks());
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

}
