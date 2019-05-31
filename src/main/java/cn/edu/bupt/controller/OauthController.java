package cn.edu.bupt.controller;

import cn.edu.bupt.bean.po.User;
import cn.edu.bupt.bean.vo.Identity;
import cn.edu.bupt.constant.EnvConsts;
import cn.edu.bupt.service.UserService;
import cn.edu.bupt.util.ResponseResult;
import cn.edu.bupt.util.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@Slf4j
public class OauthController {

    private final UserService userService;

    private final EnvConsts envConsts;

    public OauthController(UserService userService, EnvConsts envConsts) {
        this.userService = userService;
        this.envConsts = envConsts;
    }

    @PostMapping("register")
    public ResponseEntity<ResponseResult<String>> submitResister(@RequestBody User user) {
        // 检查邮箱或密码是否为空
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseResult.of("注册信息不完整", "注册失败"));
//            return ResponseResult.error(ResultTypeEnum.PARAM_ERROR, "注册信息不完整", null);
        }
        User resUser = userService.addUser(user);
        if (resUser == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ResponseResult.of("邮箱已被注册", "注册失败"));
//            return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR, "邮箱已被注册", null);
        } else {
            return ResponseEntity.ok(ResponseResult.of("注册成功", "注册成功"));
//            return ResponseResult.success("注册成功", null);
        }
    }

    @PostMapping("login")
    public ResponseEntity<ResponseResult<Identity>> submitLogin(@RequestBody User user) {
        Identity identity = new Identity();
        identity.setLogin(true);
        // 检查登录参数是否为空
        if (StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResult.of("登录信息不完整", identity));
        }
        // 检查用户是否存在，密码是否正确
        boolean result = userService.verifyUser(user);
        if (!result) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseResult.of("用户名或密码错误", identity));
        }
        User targetUser = userService.getUser(user.getEmail());
//        Identity identity = new Identity();
        if (targetUser != null) {
            identity.setId(targetUser.getId());
            identity.setIssuer(envConsts.TOKEN_ISSUER);
            identity.setClientId(targetUser.getEmail());
            identity.setDuration(envConsts.TOKEN_DURATION);
            log.error("{}", identity);
            String token = TokenUtil.createToken(identity, envConsts.TOKEN_API_KEY_SECRET);
            identity.setToken(token);
        }
        return ResponseEntity.ok(ResponseResult.of("登录成功", identity));
    }

    @PostMapping("logout")
    public ResponseEntity<ResponseResult<String>> submitLogout() {
        return ResponseEntity.ok(ResponseResult.of("登出成功", "登出成功"));
//        return ResponseResult.success("登出成功", null);
    }

    @RequestMapping("error/{code}")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<String> oauthError(@PathVariable("code") Integer code) {
        log.debug("进入oauth错误返回控制器");
        if (code == HttpStatus.NON_AUTHORITATIVE_INFORMATION.value()) {
            // 如果错误码是认证信息不存在
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("认证失败");
//            return ResponseResult.error(ResultTypeEnum.NON_AUTHORITATIVE_INFORMATION_ERROR);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("服务器内部错误");
//        return ResponseResult.error(ResultTypeEnum.SERVICE_ERROR);
    }

}
