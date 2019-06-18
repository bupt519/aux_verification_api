package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "relation_mark")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@Data
public class RelationMark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "text")
    private String originContent;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, columnDefinition = "int default -1")
    private int passed;

    @Column(nullable = false, columnDefinition = "int default 0")
    private int reviewed;

    @Column(name = "ver_date", columnDefinition = "date")
    private Date verDate;

    private String description;

    @Column(name = "verify_result", columnDefinition = "int default -1")
//    @Enumerated(EnumType.ORDINAL)
    private int verifyResult;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id")
    @JsonIgnoreProperties("relationMarks")
    private VerifyStatement statement;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "relation_id")
    @JsonIgnoreProperties("marks")
    private RelationReflect reflect;

    public RelationMark(){ }

    public RelationMark(String content, int passed, String description,RelationReflect refOptional, VerifyStatement recordStmt){
        this.setOriginContent(content);
        this.setContent(content);
        this.setPassed(passed);
        this.setVerDate(new Date());
        this.setDescription(description);
        this.setReflect(refOptional);
        this.updateVerifyResult();
        this.setStatement(recordStmt);
        this.setReviewed(1);
    }

    public void updateVerifyResult(){
        if (this.content.equals(this.getOriginContent())) { //没有发生修改
            if (this.passed == 0) this.setVerifyResult(VerifyResult.DENIED.ordinal()); //没有通过- 拒绝
            else this.setVerifyResult(VerifyResult.ACCEPT.ordinal()); // 通过 - 直接通过
        } else {  //发生了修改
            if (this.passed == 0) this.setVerifyResult(VerifyResult.MODIFY_DENIED.ordinal());
            else this.setVerifyResult(VerifyResult.MODIFY_ACCEPT.ordinal());
        }
    }
}
