package cn.edu.bupt.bean.vo;

import cn.edu.bupt.bean.po.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class UserInfoVo {

    private long id;
    private String username;
    private String password;
    private String name;
    private String avatar;
    private String role;

    public UserInfoVo(User user){
        BeanUtils.copyProperties(user, this);
    }

}
