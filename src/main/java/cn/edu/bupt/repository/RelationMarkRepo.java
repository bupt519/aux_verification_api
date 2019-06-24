package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelationMarkRepo extends JpaRepository<RelationMark, Long> {

    Page<RelationMark> findByPassedAndStatementIn(int passed, List<VerifyStatement> stats, Pageable pageable);

    Page<RelationMark> findByStatementIn(List<VerifyStatement> stats, Pageable pageable);

   List<RelationMark> findAllByStatement(VerifyStatement stats);

    int countByPassedAndStatement(int passed, VerifyStatement statement);
}
