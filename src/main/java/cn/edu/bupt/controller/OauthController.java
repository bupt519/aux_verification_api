package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.constant.EnvConsts;
import cn.edu.bupt.service.UserService;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.ResultTypeEnum;
import cn.edu.bupt.util.token.Identity;
import cn.edu.bupt.util.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/oauth")
@Slf4j
public class OauthController {

    private final UserService userService;

    private final EnvConsts envConsts;

    public OauthController(UserService userService, EnvConsts envConsts) {
        this.userService = userService;
        this.envConsts = envConsts;
    }

    @PostMapping("register")
    public ResponseResult<String> submitResister(@RequestBody User user){
        // 检查邮箱或密码是否为空
        if(StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword())){
            return ResponseResult.error("注册信息不完整");
        }
        User resUser = userService.addUser(user);
        if(resUser==null){
            return ResponseResult.error("邮箱已被注册");
        }else{
            return ResponseResult.success("注册成功");
        }
    }

    @PostMapping("login")
    public ResponseResult<Identity> submitLogin(@RequestBody User user){
        if(StringUtils.isEmpty(user.getEmail())||StringUtils.isEmpty(user.getPassword())){
            return ResponseResult.error(ResultTypeEnum.PARAM_ERROR, "登录参数不完整");
        }
        boolean result = userService.verifyUser(user);
        if(!result){
            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "用户名或密码错误");
        }
        User targetUser = userService.getUser(user.getEmail());
        Identity identity = new Identity();
        if(targetUser != null){
            identity.setId(user.getId());
            identity.setIssuer(envConsts.TOKEN_ISSUER);
            identity.setClientId(user.getEmail());
            identity.setDuration(envConsts.TOKEN_DURATION);
            String token = TokenUtil.createToken(identity, envConsts.TOKEN_API_KEY_SECRET);
            identity.setToken(token);
        }
        return ResponseResult.success("登录成功", identity);
    }

    @RequestMapping("error/{code}")
    public ResponseEntity<String> oauthError(@PathVariable("code") Integer code) {
        log.debug("进入oauth错误返回控制器");
        if (code == HttpStatus.NON_AUTHORITATIVE_INFORMATION.value()) {
            // 如果错误码是认证信息不存在
            return new ResponseEntity<>(HttpStatus.NON_AUTHORITATIVE_INFORMATION);
        }
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

}
