package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.StmtEntities;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StmtEntitiesRepo extends JpaRepository<StmtEntities, Long> {

    List<StmtEntities> findAllByStatementOrderByHead(VerifyStatement statement);

    List<StmtEntities> findAllByStatementAndHeadLessThanAndTailGreaterThanOrderByHeadAsc(VerifyStatement statement,int anotherTail,int anotherHead);

    @Override
    void deleteById(Long aLong);
}
