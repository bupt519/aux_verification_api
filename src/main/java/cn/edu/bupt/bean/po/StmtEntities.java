package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "statement_entities")   //  单个句子的实体词典，包括实体编号（全局），实体编号（单句）和实体在这句话的开头位置和结尾位置
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@Data
public class StmtEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    @JsonIgnoreProperties("StmtEntities")
    private GlobalEntities globalEntity;  // 对应的全局实体号

    @Column(name="entity_head", nullable = false)
    private int head;  //实体的头位置

    @Column(name="entity_tail", nullable = false)
    private int tail;  //实体的尾位置

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id")
    @JsonIgnoreProperties("entities")
    private VerifyStatement statement;
}
