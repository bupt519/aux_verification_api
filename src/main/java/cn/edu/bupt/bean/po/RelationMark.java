package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.util.Pair;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String description; // 评价

    @Column(name = "verify_result", columnDefinition = "int default -1")
//    @Enumerated(EnumType.ORDINAL)
    private int verifyResult;

    @Column(nullable = true)
    private String relaRank;//  模型提供的关系排名，可能为Null

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id")
    @JsonIgnoreProperties("relationMarks")
    private VerifyStatement statement;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "stmt_e1")
    @JsonIgnoreProperties("marks_e1")
    private StmtEntities stmtEntity1;   // 存放标注实体在statement实体表里的id

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
    @JoinColumn(name = "stmt_e2")
    @JsonIgnoreProperties("marks_e2")
    private StmtEntities stmtEntity2;

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

    private static String tagPatternStr = "<e[12]>[^<]+</e[12]>";
    private static Pattern tagPattern = Pattern.compile(tagPatternStr);
    public static List<Pair<Integer, Integer>> getEntitiesLoc(String content){
        List<Pair<Integer, Integer>> entities = new ArrayList<>();
        //  类似getFullContent, 找</tag> 的位置，并构造出原始字符串
        Matcher matcher = tagPattern.matcher(content);

        int offset = 0; // 头标签是4个offset， 尾标签5个，则一个实体9个
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            System.out.println(content.substring(start, end));
            entities.add(new Pair<>(start - offset, end - offset - 9));
            offset += 9;
        }
        return entities;
    }
}
