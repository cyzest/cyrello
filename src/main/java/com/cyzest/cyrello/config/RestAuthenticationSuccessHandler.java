package com.cyzest.cyrello.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cyzest.commons.spring.web.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@AllArgsConstructor
public class RestAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String sessionId = request.getSession().getId();

        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

        if ("true".equals(request.getParameter("isWeb"))) {
            response.addCookie(CookieAuthentications.createCookie(sessionId));
        }

        PrintWriter writer = response.getWriter();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.putExtra("token", sessionId);

        mapper.writeValue(writer, apiResponse);

        writer.flush();
    }

}
