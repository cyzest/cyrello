package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dto.UserInfo;
import com.cyzest.cyrello.dto.UserRegParam;
import com.cyzest.cyrello.exception.BasedException;
import com.cyzest.cyrello.exception.UserExceptionType;
import com.cyzest.cyrello.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(userController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void registerUserTest() throws Exception {

        when(userService.registerUser(any(UserRegParam.class)))
                .thenReturn(new UserInfo("id", "cyzest@nate.com", LocalDateTime.now()));

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=cyzest@nate.com&password=password"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.extra.id", is("id")));

        verify(userService, times(1)).registerUser(any(UserRegParam.class));

        clearInvocations(userService);

        when(userService.registerUser(any(UserRegParam.class)))
                .thenThrow(new BasedException(UserExceptionType.EXIST_USER));

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=cyzest@nate.com&password=password"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(UserExceptionType.EXIST_USER.getResultCode())));

        verify(userService, times(1)).registerUser(any(UserRegParam.class));

        clearInvocations(userService);

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=cyzest@nate.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content("email=cyzest&password=password"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

}
