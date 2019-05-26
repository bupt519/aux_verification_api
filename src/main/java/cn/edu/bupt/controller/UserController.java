package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.service.UserService;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.token.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserService userService;

    public ResponseResult<List<EntityMark>> reviewedEntities(@RequestBody Map<String, Object> params,
                                                             HttpSession httpSession){
        Identity identity = (Identity) httpSession.getAttribute(OauthConsts.KEY_IDENTITY);
        boolean passed = (boolean) params.get("passed");
        List<EntityMark> marks = userService.listEntities(identity.getId(), passed);
        return ResponseResult.success(marks);
    }

    public ResponseResult<List<RelationMark>> reviewedRelations(@RequestBody Map<String, Object> params,
                                                                HttpSession session){
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        boolean passed = (boolean) params.get("passed");
        return ResponseResult.success(userService.listRelations(identity.getId(), passed));
    }

}
