package com.cyzest.cyrello.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class CookieAuthenticationFilter extends GenericFilterBean {

    private SessionRepository sessionRepository;

    private RequestMatcher requestMatcher;

    public CookieAuthenticationFilter(String pattern, SessionRepository sessionRepository) {
        this.requestMatcher = new AntPathRequestMatcher(pattern);
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void doFilter(
            ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (requestMatcher.matches(httpServletRequest)) {

            Cookie cookie = CookieAuthentications.getCookie(httpServletRequest);

            if (cookie != null) {

                Session session = sessionRepository.getSession(cookie.getValue());

                if (session != null) {

                    Object securityContextObject =
                            session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

                    if (securityContextObject != null) {
                        SecurityContextHolder.getContext()
                                .setAuthentication(((SecurityContext) securityContextObject).getAuthentication());
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

}
