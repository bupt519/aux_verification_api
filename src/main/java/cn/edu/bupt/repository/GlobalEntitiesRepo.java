package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.GlobalEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GlobalEntitiesRepo extends JpaRepository<GlobalEntities, Long> {

    GlobalEntities findById(long id);

    GlobalEntities findByEntityName(String entity_name);

    @Override
    void deleteById(Long aLong);

    @Transactional
    void deleteAllByCountEquals(int value);
}
