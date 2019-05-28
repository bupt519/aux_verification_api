package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class VerMarksVo {

    private long id;

    private String pdfUrl;

    private int pdfNo;

    private List<EntityMarkVo> entityMarks;

    private List<RelationMarkVo> relationMarks;

    @Data
    public static class EntityMarkVo {
        private long id;
        private String content;
        private int passed;
        private int reviewed;
        private Date verDate;

        public EntityMarkVo(EntityMark mark) {
            BeanUtils.copyProperties(mark, this);
        }
    }

    @Data
    public static class RelationMarkVo {
        private long id;
        private String content;
        private int passed;
        private int reviewed;
        private Date verData;
        private long relationId;
        private String relationName;

        public RelationMarkVo(RelationMark mark) {
            BeanUtils.copyProperties(mark, this);
        }
    }

    public void setEntityMarks(List<EntityMark> marks) {
        entityMarks = new ArrayList<>();
        for (EntityMark mark : marks) {
            entityMarks.add(new EntityMarkVo(mark));
        }
    }

    public void setRelationMarks(List<RelationMark> marks) {
        relationMarks = new ArrayList<>();
        for (RelationMark mark : marks) {
            RelationMarkVo vo = new RelationMarkVo(mark);
            vo.setRelationId(mark.getReflect().getId());
            vo.setRelationName(mark.getReflect().getRName());
            relationMarks.add(vo);
        }
        relationMarks.sort((m1, m2) -> (int) (m1.getRelationId() - m2.getRelationId()));
    }
}
