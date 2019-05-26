package cn.edu.bupt.config;

import cn.edu.bupt.interceptor.AccessTokenCheckInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    AccessTokenCheckInterceptor accessTokenCheckInterceptor(){
        return new AccessTokenCheckInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessTokenCheckInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/api/oauth/**");
    }
}
