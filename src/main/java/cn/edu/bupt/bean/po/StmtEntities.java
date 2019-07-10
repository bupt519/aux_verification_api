package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javafx.util.Pair;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "statement_entities")   //  单个句子的实体词典，包括实体编号（全局），实体编号（单句）和实体在这句话的开头位置和结尾位置
@Data
public class StmtEntities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id")
    @JsonIgnore
    private GlobalEntities globalEntity;  // 对应的全局实体号

    @Column(name="entity_head", nullable = false)
    private int head;  //实体的头位置

    @Column(name="entity_tail", nullable = false)
    private int tail;  //实体的尾位置

    @Column(name = "entity_tag")
    private String entityTag; //实体的类型

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stat_id")
    @JsonIgnore
    private VerifyStatement statement;

    @OneToMany(mappedBy = "stmtEntity1",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RelationMark> marks_e1;

    @OneToMany(mappedBy = "stmtEntity2",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<RelationMark> marks_e2;

    public List<RelationMark> getMarks_e1() {
        return marks_e1;
    }

    public StmtEntities(){}

    private StmtEntities(VerifyStatement statement, int head, int tail){
        this.statement = statement;
        this.head = head;
        this.tail = tail;
    }

    private StmtEntities(VerifyStatement statement, int head, int tail, String entityTag){
        this.statement = statement;
        this.head = head;
        this.tail = tail;
        this.entityTag = entityTag;
    }

    public void updateSE(StmtEntities curValue){
        this.head = curValue.getHead();
        this.tail = curValue.getTail();
        this.entityTag = curValue.getEntityTag();
    }

    public void updateGlobalEntity(String nonTagContent){
        if(this.globalEntity == null){
            String entityName = nonTagContent.substring(this.head, this.tail);
            this.globalEntity = new GlobalEntities(entityName);
        }
    }

    public boolean isIntersect(StmtEntities another){
        if(another.tail <= this.head || another.head >= this.tail) //  b区间的上限< a区间的下限 或 b区间的下限>a的上限 则不想交
            return false;
        return true;
    }

    public boolean isEqual(StmtEntities another){
        if(this.entityTag==null)
            return false;
        return another.head==this.head && another.tail==this.tail&&this.entityTag.equals(another.entityTag);
    }

    public static List<StmtEntities> list2Entities(List<Pair<Integer, Integer>> entitiesLoc, VerifyStatement statement){
        // 把修改后的实体坐标转换成实体类，方便比较更新/插入实体表
        List<StmtEntities> res_list = new ArrayList<>();
        for(Pair<Integer, Integer> entity: entitiesLoc){
            int start = entity.getKey();
            int end = entity.getValue();
            res_list.add(new StmtEntities(statement, start, end));
        }
        return  res_list;
    }

    public static List<StmtEntities> list2EntitiesWithTag(List<Pair<Integer, Pair<Integer, String>>> entitiesLoc, VerifyStatement statement){
        // 把修改后的实体坐标转换成实体类，方便比较更新/插入实体表
        List<StmtEntities> res_list = new ArrayList<>();
        for(Pair<Integer, Pair<Integer, String>> entity: entitiesLoc){
            int start = entity.getKey();
            Pair<Integer, String> value = entity.getValue();
            int end = value.getKey();
            String tag = value.getValue();
            res_list.add(new StmtEntities(statement, start, end, tag));
        }
        return  res_list;
    }
}
