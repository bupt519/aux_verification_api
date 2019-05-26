package cn.edu.bupt.interceptor;

import cn.edu.bupt.constant.EnvConsts;
import cn.edu.bupt.constant.OauthConsts;
import cn.edu.bupt.util.token.Identity;
import cn.edu.bupt.util.token.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AccessTokenCheckInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private EnvConsts envConsts;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
            Exception {

        log.debug("进入AccessTokenCheckInterceptor");

        // 验证token的有效性
        Long appId;
        String accessToken;
        try {

            accessToken = request.getHeader(OauthConsts.KEY_ACCESS_TOKEN);
            Identity identity = TokenUtil.parseToken(accessToken, envConsts.TOKEN_API_KEY_SECRET);
            appId = identity.getId();

            //把identity存入session中(其中包含用户名、角色、过期时间戳等)
            request.getSession().setAttribute(OauthConsts.KEY_IDENTITY, identity);

            log.debug("app_id={}, client_id={}, access_token通过认证", appId, identity.getClientId());
            return true;

        } catch (Exception e) {
            log.debug("access_token无效, 原因为: {}", e.getMessage());
            log.debug("正转向认证失败控制器");
            response.sendRedirect("/api/oauth/error/" + HttpStatus.NON_AUTHORITATIVE_INFORMATION);

            return false;
        }
    }

}
