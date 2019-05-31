package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntityMarkRepo extends JpaRepository<EntityMark, Long> {

    Page<EntityMark> findByPassedAndStatementIn(int passed, List<VerifyStatement> statements, Pageable pageable);

    Page<EntityMark> findByStatementIn(List<VerifyStatement> statements, Pageable pageable);

    int countByPassedAndStatement(int passed, VerifyStatement statement);
}
