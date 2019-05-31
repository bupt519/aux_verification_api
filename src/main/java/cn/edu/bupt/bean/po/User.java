package cn.edu.bupt.bean.po;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    @Column(columnDefinition = "varchar(255) default '天野远子'")
    private String name;

    @Column(columnDefinition = "varchar(255) default '/avatar.2.jpg'")
    private String avatar;

    private String password;

    @Column(columnDefinition = "varchar(255) default 'admin'")
    private String role;

    @OneToMany(mappedBy = "verUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("verUser")
    private List<VerifyStatement> statements;
}
