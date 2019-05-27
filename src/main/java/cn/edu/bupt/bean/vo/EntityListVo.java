package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityMark;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
public class EntityListVo {

    private long id;
    private String content;
    private int passed;
    private int reviewed;
    private Date verDate;
    private long statId;
    private String pdfUrl;
    private int pdfNo;

    public EntityListVo(EntityMark mark) {
        BeanUtils.copyProperties(mark, this);
        this.statId = mark.getStatement().getId();
        this.pdfUrl = mark.getStatement().getPdfUrl();
        this.pdfNo = mark.getStatement().getPdfNo();
    }

}
