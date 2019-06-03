package cn.edu.bupt.repository;

import cn.edu.bupt.bean.po.MarkOpinion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarkOpinionRepo extends JpaRepository<MarkOpinion, Long> {

    List<MarkOpinion> findByOpinionStartingWith(String prefix);

}
