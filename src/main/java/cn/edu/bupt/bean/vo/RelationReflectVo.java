package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.RelationReflect;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RelationReflectVo {

    private List<RelationVo> relationList = new ArrayList<>();

    @Data
    public static class RelationVo{
        private long relationId;
        private String relationName;
    }

    public void addRelations(List<RelationReflect> originRelations){
        for (RelationReflect originRelation: originRelations) {
            RelationVo vo = new RelationVo();
            vo.setRelationId(originRelation.getId());
            vo.setRelationName(originRelation.getRName());
            this.relationList.add(vo);
        }
    }

}
