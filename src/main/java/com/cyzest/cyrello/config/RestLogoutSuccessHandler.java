package com.cyzest.cyrello.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response, Authentication authentication) throws IOException {

        response.setStatus(HttpStatus.OK.value());

        response.addCookie(CookieAuthentications.createInvalidCookie());

        response.getWriter().flush();
    }
}
