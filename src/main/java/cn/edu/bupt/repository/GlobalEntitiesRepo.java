package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.GlobalEntities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalEntitiesRepo extends JpaRepository<GlobalEntities, Long> {

    GlobalEntities findById(long id);

    @Override
    void deleteById(Long aLong);
}
