package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.RelationReflect;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class RelationListVo {

    private long pageNo;
    private long totalCount;
    private List<RelationHistory> data;

    @Data
    public static class RelationHistory{
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
        private String description;
        private List<Reflect> reflects;

        public RelationHistory(RelationMark mark, List<Reflect> reflects) {
            BeanUtils.copyProperties(mark, this);
            this.relationId = mark.getReflect().getId();
            this.relationName = mark.getReflect().getRName();
            this.statId = mark.getStatement().getId();
            this.pdfUrl = mark.getStatement().getPdfUrl();
            this.pdfNo = mark.getStatement().getPdfNo();
            this.reflects = reflects;
        }
    }

    @Data
    @AllArgsConstructor
    public static class Reflect{
        private long relationId;
        private String relationName;
    }
}
