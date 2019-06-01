package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.po.RelationReflect;
import lombok.AllArgsConstructor;
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

    private int pageNo;

    private int totalCount;

//    private List<EntityMarkVo> entityMarks;
//
//    private List<RelationMarkVo> relationMarks;

    private List<MarkVo> data = new ArrayList<>();

    @Data
    public static class MarkVo{
        private long id;
        private String content;
        private int passed;
        private int reviewed;
        private Date verDate;
        private long relationId;
        private String relationName;
        private List<Reflect> reflects;
        private long stateId;
        private int type;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static  class Reflect{
        private long relationId;
        private String relationName;
    }

    public void addEntities(List<EntityMark> originMarks){
        for (EntityMark originMark : originMarks) {
            MarkVo vo = new MarkVo();
            vo.setId(originMark.getId());
            vo.setContent(originMark.getContent());
            vo.setPassed(originMark.getPassed());
            vo.setReviewed(originMark.getReviewed());
            vo.setVerDate(originMark.getVerDate());
            vo.setType(0);
            vo.setStateId(originMark.getStatement().getId());
            vo.setDescription(originMark.getDescription());
            data.add(vo);
        }
    }

    public void addRelations(List<RelationMark> originMarks, List<Reflect> reflects){
        for (RelationMark originMark : originMarks) {
            MarkVo vo = new MarkVo();
            vo.setId(originMark.getId());
            vo.setContent(originMark.getContent());
            vo.setPassed(originMark.getPassed());
            vo.setReviewed(originMark.getReviewed());
            vo.setVerDate(originMark.getVerDate());
            vo.setRelationId(originMark.getReflect().getId());
            vo.setRelationName(originMark.getReflect().getRName());
            vo.setReflects(reflects);
            vo.setType(1);
            vo.setStateId(originMark.getStatement().getId());
            vo.setDescription(originMark.getDescription());
            data.add(vo);
        }
    }

//    @Data
//    public static class EntityMarkVo {
//        private long id;
//        private String content;
//        private int passed;
//        private int reviewed;
//        private Date verDate;
//
//        public EntityMarkVo(EntityMark mark) {
//            BeanUtils.copyProperties(mark, this);
//        }
//    }
//
//    @Data
//    public static class RelationMarkVo {
//        private long id;
//        private String content;
//        private int passed;
//        private int reviewed;
//        private Date verData;
//        private long relationId;
//        private String relationName;
//
//        public RelationMarkVo(RelationMark mark) {
//            BeanUtils.copyProperties(mark, this);
//        }
//    }
//
//    public void setEntityMarks(List<EntityMark> marks) {
//        entityMarks = new ArrayList<>();
//        for (EntityMark mark : marks) {
//            entityMarks.add(new EntityMarkVo(mark));
//        }
//    }
//
//    public void setRelationMarks(List<RelationMark> marks) {
//        relationMarks = new ArrayList<>();
//        for (RelationMark mark : marks) {
//            RelationMarkVo vo = new RelationMarkVo(mark);
//            vo.setRelationId(mark.getReflect().getId());
//            vo.setRelationName(mark.getReflect().getRName());
//            relationMarks.add(vo);
//        }
//        relationMarks.sort((m1, m2) -> (int) (m1.getRelationId() - m2.getRelationId()));
//    }
}
