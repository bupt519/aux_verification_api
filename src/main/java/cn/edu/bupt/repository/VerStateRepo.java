package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.po.VerifyStatement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerStateRepo extends JpaRepository<VerifyStatement, Long> {
    List<VerifyStatement> findByVerUser(User user);
}
