package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "entity_dict")   //  全局的实体词典，包括实体内容，实体编号（全局）和实体计数（同一个句子里标注多次则记多次)
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@Data
public class GlobalEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 127)
    private String entity_name;  // 实体名字，非空

    @Column(columnDefinition = "int default 1")
    private String count;

    @OneToMany(mappedBy = "globalEntity", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("globalEntity")
    private List<StmtEntities> stmtEntities;
}
