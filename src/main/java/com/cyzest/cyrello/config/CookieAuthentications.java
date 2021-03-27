package com.cyzest.cyrello.config;

import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieAuthentications {

    private static final String AUTHENTICATION_TOKEN_COOKIE = "at";

    private CookieAuthentications() {}

    public static Cookie createCookie(String token) {
        Cookie cookie = new Cookie(AUTHENTICATION_TOKEN_COOKIE, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        return cookie;
    }

    public static Cookie createInvalidCookie() {
        Cookie cookie = new Cookie(AUTHENTICATION_TOKEN_COOKIE, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    public static Cookie getCookie(HttpServletRequest request) {
        return WebUtils.getCookie(request, AUTHENTICATION_TOKEN_COOKIE);
    }

}
