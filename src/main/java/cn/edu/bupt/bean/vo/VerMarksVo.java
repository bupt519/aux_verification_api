package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import lombok.Data;

import java.util.List;

@Data
public class VerMarksVo {

    private long id;

    private String pdfUrl;

    private int pdfNo;

    private List<EntityMark> entityMarks;

    private List<RelationMark> relationMarks;

}
