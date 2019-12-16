package cn.edu.bupt.service;

import cn.edu.bupt.bean.po.*;
import cn.edu.bupt.bean.vo.RelationReflectVo;
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
    public boolean resetPreviousViewedStatement(long userId) {
        Optional<VerifyStatement> statementOptional = Optional.empty();
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) { // 如果用户在上次审批时，没有将该段落的所有文本全部审核完毕
            statementOptional = verStateRepo.findFirstByVerUserAndStateOrderByIdDesc(user.get(), VerifyStatement.State.END.ordinal());
        }

        if (statementOptional.isPresent()) {  // 找到了这样的一条已有文本，将其复位
            VerifyStatement statement = statementOptional.get();
            statement.setState(VerifyStatement.State.STARTED.ordinal());
            verStateRepo.save(statement);
            return true;
        }
        return false;
    }

    @Transactional
    public VerMarksVo curUnViewedStatement(long userId, int pageNo, int pageSize) {
        Optional<VerifyStatement> statementOptional = Optional.empty();
        Optional<User> user = userRepo.findById(userId);
        if (user.isPresent()) { // 如果用户在上次审批时，没有将该段落的所有文本全部审核完毕
            statementOptional = verStateRepo.findFirstByVerUserAndState(user.get(), VerifyStatement.State.STARTED.ordinal());
        }
//        if (!statementOptional.isPresent()) { // 查找第一条待审核的段落
//            statementOptional = verStateRepo.findFirstByState(0);
//        }
        if (statementOptional.isPresent()) {
            VerifyStatement statement = statementOptional.get();
            VerMarksVo result = new VerMarksVo();
            result.setId(statement.getId());
            result.setPdfUrl(statement.getPdfUrl());
            result.setPdfNo(statement.getPdfNo());
            List<VerMarksVo.Reflect> reflects = new ArrayList<>();
            List<RelationReflect> originReflects = relaReflectRepo.findAll();
            for (RelationReflect originReflect : originReflects) {
                reflects.add(new VerMarksVo.Reflect(originReflect.getId(), originReflect.getRName()));
            }
            EntityMark entityMark = statement.getEntityMark();
            if (entityMark != null) {
                result.setRawContent(entityMark.getNonTagContent());
                result.addEntities(entityMark);
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
            statementOptional = verStateRepo.findFirstByVerUserAndState(userOptional.get(), VerifyStatement.State.STARTED.ordinal());
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

        // 如果有下一条数据，则不分配，直接返回
        statementOptional = verStateRepo.findFirstByVerUserAndState(userOptional.get(), VerifyStatement.State.STARTED.ordinal());
        if (statementOptional.isPresent()) { // 存在这个已经分配过的数据
            return true;
        }

        // 没有未审核的历史数据，分配一条新的数据
        statementOptional = verStateRepo.findFirstByState(VerifyStatement.State.UNSTARTED.ordinal());
        if (statementOptional.isPresent()) {
            VerifyStatement statement = statementOptional.get();
            // 更新审批文本状态
            if (statement.getEntityMark() != null) {
                EntityMark mark = statement.getEntityMark();
                // 文本更新为已审核状态
                mark.setReviewed(1);
                entityMarkRepo.save(mark);
            }

            for (int i = 0, size = statement.getRelationMarks().size(); i < size; ++i) {
                RelationMark mark = statement.getRelationMarks().get(i);
                // 文本更新为已审核状态
                mark.setReviewed(1);
                relationMarkRepo.save(mark);
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

    @Transactional
    public RelationReflectVo getRelationReflect(){
        List<RelationReflect> relationReflectList = this.relaReflectRepo.findAll();
        RelationReflectVo result = new RelationReflectVo();
        result.addRelations(relationReflectList);
        return result;
    }

    public List<VerifyStatement> getStatements(long beginId){
        return this.verStateRepo.findAllByIdGreaterThanEqual(beginId);
    }

    public List<VerifyStatement> getStatementsBetween(long beginId, long endId){
        return this.verStateRepo.findAllByIdBetween(beginId, endId);
    }

    public List<RelationMark> getRelationMarksByStatement(VerifyStatement statement){
        return this.relationMarkRepo.findAllByStatement(statement);
    }
}
