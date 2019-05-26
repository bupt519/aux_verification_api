package cn.edu.bupt.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rela_name_no")
@Data
public class RelationReflect {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "rela_name")
    private String rName;

    @OneToMany(mappedBy = "reflect", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RelationMark> marks;

}
