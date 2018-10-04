package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.config.CookieAuthentications;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.session.ExpiringSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@AllArgsConstructor
public class WebController {

    private SessionRepository<ExpiringSession> sessionRepository;

    @GetMapping("/")
    public String index(HttpServletRequest request) {

        Cookie cookie = CookieAuthentications.getCookie(request);

        if (cookie != null) {
            Session session = sessionRepository.getSession(cookie.getValue());
            if (session != null) {
                return "tasks";
            }
        }

        return "index";
    }

}
