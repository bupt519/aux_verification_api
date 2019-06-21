package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.util.Pair;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table(name = "entity_mark")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@Data
public class EntityMark {

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
    @JsonIgnoreProperties("entityMarks")
    private VerifyStatement statement;

    public boolean updateVerifyResult(){
        if (this.content.equals(this.getOriginContent())) { //没有发生修改
            if (this.passed == 0) this.setVerifyResult(VerifyResult.DENIED.ordinal()); //没有通过- 拒绝
            else this.setVerifyResult(VerifyResult.ACCEPT.ordinal()); // 通过 - 直接通过
            return false;
        } else {  //发生了修改
            if (this.passed == 0) this.setVerifyResult(VerifyResult.MODIFY_DENIED.ordinal());
            else this.setVerifyResult(VerifyResult.MODIFY_ACCEPT.ordinal());
            return true;
        }
    }

    private static String tagPatternTailStr = "</[on][a-z]?>";
    private static Pattern tagPatternTail = Pattern.compile(tagPatternTailStr);
    private static String otherTagHead = "<o>";
    private static String otherTagTail = "</o>";
    private static String tagPatternHeadStr = "<[on][a-z]?>";
    private static Pattern tagPatternHead = Pattern.compile(tagPatternHeadStr);
    public String getNonTagContent(){
        // 将所有的标签匹配去除掉
        Matcher matcher = tagPatternTail.matcher(this.content);
        return matcher.replaceAll("");
    }

    public String getFullTagContent(){
        // 对只有结束标签的字符串做补全
        Matcher matcher = tagPatternTail.matcher(this.content);
        Stack<Pair<Integer, String>> tailStack = new Stack<>();
        int end = 0; //最初的end是句子的首部
        while(matcher.find()){
            int start = matcher.start();
            String tag = this.content.substring(start, matcher.end());
            tailStack.push(new Pair<>(end, tag.replace("/","")));
            end = matcher.end();
        }

        String fullTagContent = new String("");
        end = this.content.length(); // 实际上若数据正确的话，上一个循环结束后end就应该在句子末尾
        while(!tailStack.empty()){
            Pair<Integer, String> tagUnit = tailStack.pop();
            int loc = tagUnit.getKey();
            String tag = tagUnit.getValue();
            if(tag.equals(otherTagHead))
                tag = "";
            String previous = this.content.substring(loc, end);
            fullTagContent = tag.concat(previous).concat(fullTagContent);
            end = loc;
        }
        return fullTagContent.replace(otherTagTail, "");
    }

    public String recoverTagContent(String content){
        // 将前端返回的字符串还原回实体标注格式
        //把每个<n*> 改成</o> , 若结尾不是</n*>则加上</o>
        Matcher matcher = tagPatternHead.matcher(content);
        String tagContent = matcher.replaceAll(otherTagTail);
        String tagPattern = "</n[a-z]>$";
        boolean hasTail = tagPattern.matches(tagContent);
        Pattern pattern = Pattern.compile(tagPattern);
        matcher = pattern.matcher(tagContent);
        if(!matcher.find())
            tagContent = tagContent.concat(otherTagTail); //补上结尾的</o>

        tagPattern = "^" + otherTagTail;
        boolean hasHead = tagPattern.matches(tagContent);
        if(hasHead)
            tagContent = tagContent.substring(3); // 去除首部的<o>

        return tagContent.replace("></o>",">"); // 最后消除紧跟在别的实体后面的的</o>
    }
}
