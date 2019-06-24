package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ver_statement")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@Data
public class VerifyStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pdf_path", columnDefinition = "varchar(255)")
    private String pdfUrl;

    @Column(name = "pdf_no")
    private int pdfNo;

    @Column(name = "mark_id")
    private int mark_id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User verUser;

    @Column(name = "mark_user", columnDefinition = "varchar(10)")
    private String markUser;

    @Column(name = "state", columnDefinition = "int default 0")
//    @Enumerated(EnumType.ORDINAL)
    private int state;

    @OneToOne(mappedBy = "statement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("statement")
    private EntityMark entityMark;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("statement")
    private List<RelationMark> relationMarks;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("statement")
    private List<StmtEntities> entities;

    public enum State{
        UNSTARTED,
        STARTED,
        END
    }
}
