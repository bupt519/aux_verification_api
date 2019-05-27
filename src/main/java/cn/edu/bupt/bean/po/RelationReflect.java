package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "rela_name_no")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@Data
public class RelationReflect {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "rela_name")
    private String rName;

    @OneToMany(mappedBy = "reflect", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("reflect")
    private List<RelationMark> marks;

}
