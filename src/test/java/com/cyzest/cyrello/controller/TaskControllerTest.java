package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dto.DefaultAuthUser;
import com.cyzest.cyrello.dto.TaskInfo;
import com.cyzest.cyrello.dto.TaskRegParam;
import com.cyzest.cyrello.exception.BasedException;
import com.cyzest.cyrello.exception.CommonExceptionType;
import com.cyzest.cyrello.exception.ExceptionType;
import com.cyzest.cyrello.exception.TaskExceptionType;
import com.cyzest.cyrello.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mvc;

    private ObjectMapper objectMapper;

    private Authentication authentication;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    public void setup() {

        User user = new User("id", "cyzest@nate.com", "password", LocalDateTime.now());
        authentication = new TestingAuthenticationToken(new DefaultAuthUser(user), null);

        MappingJackson2HttpMessageConverter httpMessageConverter = new MappingJackson2HttpMessageConverter();
        objectMapper = httpMessageConverter.getObjectMapper();

        mvc = MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(httpMessageConverter)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    public void registerTaskTest() throws Exception {

        when(taskService.registerTask(eq("id"), any(TaskRegParam.class))).thenAnswer(mock -> {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setId(1L);
            return taskInfo;
        });

        TaskRegParam taskRegParam1 = new TaskRegParam();
        taskRegParam1.setContent("test");

        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(taskRegParam1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.extra.id", is(1)));

        verify(taskService, times(1)).registerTask(eq("id"), any(TaskRegParam.class));

        clearInvocations(taskService);

        registerTaskExceptionTest(CommonExceptionType.UNAUTHORIZED);

        registerTaskExceptionTest(TaskExceptionType.CONTAINS_COMPLETED_REL_TASK);

        registerTaskExceptionTest(TaskExceptionType.CONTAINS_INVALID_REL_TASK);
    }

    private void registerTaskExceptionTest(ExceptionType exceptionType) throws Exception {

        when(taskService.registerTask(eq("id"), any(TaskRegParam.class)))
                .thenThrow(new BasedException(exceptionType));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");

        mvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(taskRegParam)))
                .andExpect(status().is(exceptionType.getStatusCode().value()))
                .andExpect(jsonPath("$.code",is(exceptionType.getResultCode())));

        verify(taskService, times(1)).registerTask(eq("id"), any(TaskRegParam.class));

        clearInvocations(taskService);
    }

}
