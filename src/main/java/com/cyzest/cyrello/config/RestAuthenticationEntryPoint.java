package com.cyzest.cyrello.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cyzest.commons.spring.web.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        Optional.ofNullable(request.getSession(false)).ifPresent(HttpSession::invalidate);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

        response.addCookie(CookieAuthentications.createInvalidCookie());

        PrintWriter writer = response.getWriter();

        mapper.writeValue(writer, new ApiResponse(HttpStatus.UNAUTHORIZED));

        writer.flush();
    }

}
