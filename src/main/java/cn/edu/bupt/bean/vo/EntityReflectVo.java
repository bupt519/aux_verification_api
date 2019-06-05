package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.EntityReflect;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class EntityReflectVo {

    private List<EntityVo> entityList = new ArrayList<>();

    @Data
    public static class EntityVo{
        private int id;
        private String name;
        private String value;
        private String color;
    }

    public void addEntities(List<EntityReflect> originEntities){
        for (EntityReflect originEntitie: originEntities) {
            EntityVo vo = new EntityVo();
            vo.setId(originEntitie.getId());
            vo.setName(originEntitie.getEntityName());
            vo.setValue(originEntitie.getEntityTag());
            vo.setColor(originEntitie.getColor());
            entityList.add(vo);
        }
    }

}
