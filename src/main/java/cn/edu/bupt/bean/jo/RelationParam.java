package cn.edu.bupt.bean.jo;

import lombok.Data;

@Data
public class RelationParam {

    private long id;

    private String content;

    private boolean passed;

    private long relationId;

    private long statId;

}
