package cn.edu.bupt.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ver_statement")
@Data
public class VerifyStatement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "pdf_path", columnDefinition = "text")
    private String pdfUrl;

    @Column(name = "pdf_no")
    private int pdfNo;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User verUser;

    @Column(name = "state", columnDefinition = "int default 0")
    private int state;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EntityMark> entityMarks;

    @OneToMany(mappedBy = "statement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RelationMark> relationMarks;
}
