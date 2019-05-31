package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerStateRepo extends JpaRepository<VerifyStatement, Long> {
    List<VerifyStatement> findByVerUser(User user);

    Optional<VerifyStatement> findFirstByState(int state);

    Optional<VerifyStatement> findByVerUserAndState(User user, int state);
}
