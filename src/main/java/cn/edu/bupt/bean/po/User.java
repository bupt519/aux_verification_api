package cn.edu.bupt.bean.po;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String password;

    @OneToMany(mappedBy = "verUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VerifyStatement> statements;
}
