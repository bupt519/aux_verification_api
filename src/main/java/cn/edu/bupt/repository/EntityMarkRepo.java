package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Statement;
import java.util.List;

public interface EntityMarkRepo extends JpaRepository<EntityMark, Long> {

    List<EntityMark> findByPassedAndStatementIn(boolean passed, List<VerifyStatement> stats);
}
