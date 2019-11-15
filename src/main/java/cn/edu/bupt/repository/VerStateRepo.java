package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VerStateRepo extends JpaRepository<VerifyStatement, Long> {
    List<VerifyStatement> findByVerUser(User user);

    List<VerifyStatement> findAllByVerUserNotNull();

    @Query(value="select v.id from VerifyStatement v where v.verUser.id is not NULL ")
    List<Long> findIdByVerUserNotNull();

    @Query(value="select v.id from VerifyStatement v where v.verUser.id=?1")
    List<Long> findIdByVerUser(Long user_id);

    @Query(value="select v.id from VerifyStatement v where v.verUser.id=?1 and v.id=?2")
    List<Long> findIdByVerUserAndId(Long user_id, Long stmt_id);

    Optional<VerifyStatement> findFirstByState(int state);

    Optional<VerifyStatement> findFirstByVerUserAndState(User user, int state);

    List<VerifyStatement> findAllByIdGreaterThanEqual(long statementId);

    List<VerifyStatement> findAllByIdBetween(long statementIdLwb, long statementIdUpb);
}
