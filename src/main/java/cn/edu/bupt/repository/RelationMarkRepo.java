package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationMarkRepo extends JpaRepository<RelationMark, Long> {

    List<RelationMark> findByPassedAndStatementIn(boolean passed, List<VerifyStatement> stats);
}
