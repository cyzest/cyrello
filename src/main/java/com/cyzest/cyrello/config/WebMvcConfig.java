package com.cyzest.cyrello.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.WebContentInterceptor;

@Configuration
@EnableSpringHttpSession
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        WebContentInterceptor webContentInterceptor = new WebContentInterceptor();
        webContentInterceptor.setCacheSeconds(0);
        registry.addInterceptor(webContentInterceptor);
    }

    @Bean
    public SessionRepository<ExpiringSession> sessionRepository() {
        MapSessionRepository mapSessionRepository = new MapSessionRepository();
        mapSessionRepository.setDefaultMaxInactiveInterval(60 * 60);
        return mapSessionRepository;
    }

    @Bean
    public HttpSessionStrategy httpSessionStrategy() {
        HeaderHttpSessionStrategy headerHttpSessionStrategy = new HeaderHttpSessionStrategy();
        headerHttpSessionStrategy.setHeaderName(HttpHeaders.AUTHORIZATION);
        return headerHttpSessionStrategy;
    }

}
