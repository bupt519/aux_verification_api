package cn.edu.bupt.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvConsts {

//    @Value("${accesstoken.apiKeySecret}")
//    public String ACCESS_TOKEN_API_KEY_SECRET;
//
//    @Value("${refreshtoken.apiKeySecret}")
//    public String REFRESH_TOKEN_API_KEY_SECRET;

    @Value("${token.issuer}")
    public String TOKEN_ISSUER;

    @Value("${token.duration}")
    public Long TOKEN_DURATION;

    @Value("${token.apiKeySecret}")
    public String TOKEN_API_KEY_SECRET;

}
