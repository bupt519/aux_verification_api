package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.EntityMark;
import cn.edu.bupt.bean.po.RelationMark;
import cn.edu.bupt.bean.vo.EntityListVo;
import cn.edu.bupt.bean.vo.RelationListVo;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.service.UserService;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.token.Identity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("entities")
    public ResponseResult<List<EntityListVo>> reviewedEntities(@RequestBody Map<String, Object> params,
                                                               HttpSession httpSession) {
        Identity identity = (Identity) httpSession.getAttribute(OauthConsts.KEY_IDENTITY);
        boolean passed = (boolean) params.get("passed");
        int interPassed = passed ? 1 : 0;
        List<EntityListVo> marks = userService.listEntities(identity.getId(), interPassed);
        return ResponseResult.success(marks);
    }

    @GetMapping("relations")
    public ResponseResult<List<RelationListVo>> reviewedRelations(@RequestBody Map<String, Object> params,
                                                                  HttpSession session) {
        Identity identity = (Identity) session.getAttribute(OauthConsts.KEY_IDENTITY);
        boolean passed = (boolean) params.get("passed");
        int interPassed = passed ? 1 : 0;
        List<RelationListVo> marks = userService.listRelations(identity.getId(), interPassed);
        return ResponseResult.success(marks);
    }

}
