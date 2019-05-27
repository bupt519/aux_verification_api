package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.RelationMark;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class RelationListVo {

    private long id;
    private String content;
    private int passed;
    private int reviewed;
    private Date verDate;
    private long relationId;
    private String relationName;
    private long statId;
    private String pdfUrl;
    private int pdfNo;

    public RelationListVo(RelationMark mark) {
        BeanUtils.copyProperties(mark, this);
        this.relationId = mark.getReflect().getId();
        this.relationName = mark.getReflect().getRName();
        this.statId = mark.getStatement().getId();
        this.pdfUrl = mark.getStatement().getPdfUrl();
        this.pdfNo = mark.getStatement().getPdfNo();
    }
}
