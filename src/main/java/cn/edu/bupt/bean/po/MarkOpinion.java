package cn.edu.bupt.bean.po;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "mark_option")
@Data
public class MarkOpinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String opinion;
}
