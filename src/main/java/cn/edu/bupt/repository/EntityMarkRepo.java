package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface EntityMarkRepo extends JpaRepository<EntityMark, Long> {

    Page<EntityMark> findByPassedAndStatementIn(int passed, List<VerifyStatement> statements, Pageable pageable);

    Page<EntityMark> findByStatementIn(List<VerifyStatement> statements, Pageable pageable);

    Page<EntityMark> findByStatementIdIn(List<Long> statementsId, Pageable pageable);

    int countByPassedAndStatement(int passed, VerifyStatement statement);

    @Query("select count(id) from EntityMark e where e.passed=?1")
    int countIdByPassed(int passed);
}
