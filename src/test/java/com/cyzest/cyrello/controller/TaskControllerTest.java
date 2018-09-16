package com.cyzest.cyrello.controller;

import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void updateTaskTest() throws Exception {

        doNothing().when(taskService).updateTask(anyString(), anyLong(), any(TaskRegParam.class));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");

        mvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(taskRegParam)))
                .andExpect(status().isOk());

        verify(taskService, times(1))
                .updateTask(anyString(), anyLong(), any(TaskRegParam.class));

        clearInvocations(taskService);

        updateTaskExceptionTest(TaskExceptionType.COMPLETED_TASK);

        updateTaskExceptionTest(TaskExceptionType.CONTAINS_COMPLETED_REL_TASK);

        updateTaskExceptionTest(TaskExceptionType.CONTAINS_INVALID_REL_TASK);

        updateTaskExceptionTest(TaskExceptionType.EXIST_INVERSE_REL_TASK);

        updateTaskExceptionTest(TaskExceptionType.CONTAINS_SELF_REL_TASK);
    }

    @Test
    public void completeTaskTest() throws Exception {

        doNothing().when(taskService).completeTask(anyString(), anyLong());

        mvc.perform(post("/api/tasks/1/complete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isOk());

        verify(taskService, times(1)).completeTask(anyString(), anyLong());

        clearInvocations(taskService);

        completeTaskExceptionTest(TaskExceptionType.EXIST_NON_COMPLETE_INVERSE_REL_TASK);
    }

    @Test
    public void getTasksTest() throws Exception {

        when(taskService.getTasks(eq("id"), eq(new PagingParam(1, 10)))).thenAnswer(mock -> {
            TaskInfoResult taskInfoResult = new TaskInfoResult();
            taskInfoResult.setTotalCount(20);
            taskInfoResult.setTaskInfos(
                    LongStream.range(1, 11)
                            .mapToObj(this::createDefaultTaskInfo)
                            .collect(Collectors.toList()));
            return taskInfoResult;
        });

        mvc.perform(get("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extra.totalCount", is(20)))
                .andExpect(jsonPath("$.extra.taskInfos[0].id", is(1)));

        verify(taskService, times(1))
                .getTasks(eq("id"), eq(new PagingParam(1, 10)));

        clearInvocations(taskService);

        when(taskService.getTasks(eq("id"), eq(new PagingParam(2, 10)))).thenAnswer(mock -> {
            TaskInfoResult taskInfoResult = new TaskInfoResult();
            taskInfoResult.setTotalCount(20);
            taskInfoResult.setTaskInfos(
                    LongStream.range(11, 21)
                            .mapToObj(this::createDefaultTaskInfo)
                            .collect(Collectors.toList()));
            return taskInfoResult;
        });

        mvc.perform(get("/api/tasks?page=2")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extra.totalCount", is(20)))
                .andExpect(jsonPath("$.extra.taskInfos[0].id", is(11)));

        verify(taskService, times(1))
                .getTasks(eq("id"), eq(new PagingParam(2, 10)));

        clearInvocations(taskService);

        mvc.perform(get("/api/tasks?page=0")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));

        mvc.perform(get("/api/tasks?size=-1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())));
    }

    @Test
    public void getTaskTest() throws Exception {

        when(taskService.getTask(eq("id"), eq(1L))).thenReturn(createDefaultTaskInfo(1L));

        mvc.perform(get("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extra.taskInfo.id", is(1)));

        verify(taskService, times(1)).getTask(eq("id"), eq(1L));

        clearInvocations(taskService);
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
                .andExpect(jsonPath("$.code", is(exceptionType.getResultCode())));

        verify(taskService, times(1)).registerTask(eq("id"), any(TaskRegParam.class));

        clearInvocations(taskService);
    }

    private void updateTaskExceptionTest(ExceptionType exceptionType) throws Exception {

        doThrow(new BasedException(exceptionType))
                .when(taskService).updateTask(anyString(), anyLong(), any(TaskRegParam.class));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");

        mvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(objectMapper.writeValueAsString(taskRegParam)))
                .andExpect(status().is(exceptionType.getStatusCode().value()))
                .andExpect(jsonPath("$.code", is(exceptionType.getResultCode())));

        verify(taskService, times(1))
                .updateTask(anyString(), anyLong(), any(TaskRegParam.class));

        clearInvocations(taskService);
    }

    private void completeTaskExceptionTest(ExceptionType exceptionType) throws Exception {

        doThrow(new BasedException(exceptionType))
                .when(taskService).completeTask(anyString(), anyLong());

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");

        mvc.perform(post("/api/tasks/1/complete")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .principal(authentication)
                .content(""))
                .andExpect(status().is(exceptionType.getStatusCode().value()))
                .andExpect(jsonPath("$.code", is(exceptionType.getResultCode())));

        verify(taskService, times(1)).completeTask(anyString(), anyLong());

        clearInvocations(taskService);
    }

    private TaskInfo createDefaultTaskInfo(long id) {
        TaskInfo taskInfo = new TaskInfo();
        LocalDateTime currentDate = LocalDateTime.now();
        taskInfo.setId(id);
        taskInfo.setContent("test");
        taskInfo.setRegisterDate(currentDate);
        taskInfo.setUpdateDate(currentDate);
        taskInfo.setUser(new UserInfo());
        return taskInfo;
    }

}
