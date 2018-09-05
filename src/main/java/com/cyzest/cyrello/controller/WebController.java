package com.cyzest.cyrello.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class WebController {

    @GetMapping("/")
    public String index(HttpSession session) {

        Object securityContextObject =
                session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContextObject != null) {
            return "tasks";
        } else {
            return "index";
        }
    }

}
