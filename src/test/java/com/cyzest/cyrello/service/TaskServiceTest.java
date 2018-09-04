package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.Task;
import com.cyzest.cyrello.dao.TaskRepository;
import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.TaskInfo;
import com.cyzest.cyrello.dto.TaskRegParam;
import com.cyzest.cyrello.exception.BasedException;
import com.cyzest.cyrello.exception.TaskExceptionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private User defaultUser;

    @BeforeEach
    public void setup() {
        defaultUser = new User("id", "cyzest@nate.com", "password", LocalDateTime.now());
    }

    @Test
    @DisplayName("registerTaskTest() - 기본")
    public void registerTaskTest1() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.saveAndFlush(any(Task.class))).thenReturn(createDefaultTask(1, false));

        TaskRegParam taskRegParam1 = new TaskRegParam();
        taskRegParam1.setContent("test");

        TaskInfo taskInfo1 = taskService.registerTask(anyString(), taskRegParam1);

        Assertions.assertNotNull(taskInfo1);
        Assertions.assertEquals(1, taskInfo1.getId().longValue());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).saveAndFlush(any(Task.class));
    }

    @Test
    @DisplayName("registerTaskTest() - 참조 태스크 포함")
    public void registerTaskTest2() throws Exception {

        int relationTaskCount = 3;

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findByUserAndIdIn(any(User.class), anyIterable()))
                .thenReturn(LongStream.range(1, relationTaskCount + 1)
                        .mapToObj(idx -> createDefaultTask(idx, false))
                        .collect(Collectors.toList()));
        when(taskRepository.saveAndFlush(any(Task.class))).thenAnswer(mock -> {
            Task task = createDefaultTask(4, false);
            task.setRelationTasks(LongStream.range(1, relationTaskCount + 1)
                    .mapToObj(idx -> createDefaultTask(idx, false))
                    .collect(Collectors.toList()));
            return task;
        });

        TaskRegParam taskRegParam2 = new TaskRegParam();
        taskRegParam2.setContent("test");
        taskRegParam2.setRelationTaskIds(Arrays.asList(1L, 2L, 3L));

        TaskInfo taskInfo2 = taskService.registerTask(anyString(), taskRegParam2);

        Assertions.assertNotNull(taskInfo2);
        Assertions.assertEquals(4, taskInfo2.getId().longValue());
        Assertions.assertNotNull(taskInfo2.getRelationTasks());
        Assertions.assertEquals(relationTaskCount, taskInfo2.getRelationTasks().size());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findByUserAndIdIn(any(User.class), anyIterable());
        verify(taskRepository, times(1)).saveAndFlush(any(Task.class));
    }

    @Test
    @DisplayName("registerTaskTest() - 완료처리 된 참조 태스크 포함")
    public void registerTaskTest3() {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findByUserAndIdIn(any(User.class), anyIterable()))
                .thenReturn(LongStream.range(1, 4)
                        .mapToObj(idx -> createDefaultTask(idx, true))
                        .collect(Collectors.toList()));

        TaskRegParam taskRegParam3 = new TaskRegParam();
        taskRegParam3.setContent("test");
        taskRegParam3.setRelationTaskIds(Arrays.asList(1L, 2L, 3L));

        Assertions.assertEquals(
                TaskExceptionType.CONTAINS_COMPLETED_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.registerTask(anyString(), taskRegParam3)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findByUserAndIdIn(any(User.class), anyIterable());
    }

    @Test
    @DisplayName("registerTaskTest() - 유효하지 않은 참조 태스크 포함")
    public void registerTaskTest4() {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findByUserAndIdIn(any(User.class), anyIterable()))
                .thenReturn(LongStream.range(1, 3)
                        .mapToObj(idx -> createDefaultTask(idx, false))
                        .collect(Collectors.toList()));

        TaskRegParam taskRegParam4 = new TaskRegParam();
        taskRegParam4.setContent("test");
        taskRegParam4.setRelationTaskIds(Arrays.asList(1L, 2L, 3L));

        Assertions.assertEquals(
                TaskExceptionType.CONTAINS_INVALID_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.registerTask(anyString(), taskRegParam4)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findByUserAndIdIn(any(User.class), anyIterable());
    }

    @Test
    @DisplayName("updateTaskTest() - 기본")
    public void updateTaskTest1() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");

        taskService.updateTask("id", 1, taskRegParam);

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).saveAndFlush(any());
    }

    @Test
    @DisplayName("updateTaskTest() - 참조 태스크")
    public void updateTaskTest2() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.findByUserAndIdIn(eq(defaultUser), anyIterable()))
                .thenReturn(LongStream.range(2, 4)
                        .mapToObj(idx -> createDefaultTask(idx, false))
                        .collect(Collectors.toList()));
        when(taskRepository.existsInverseRelationTask(eq(1L), anyIterable())).thenReturn(false);

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");
        taskRegParam.setRelationTaskIds(Arrays.asList(2L, 3L));

        taskService.updateTask("id", 1, taskRegParam);

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).findByUserAndIdIn(eq(defaultUser), anyIterable());
        verify(taskRepository, times(1)).existsInverseRelationTask(eq(1L), anyIterable());
        verify(taskRepository, times(1)).saveAndFlush(any());
    }

    @Test
    @DisplayName("updateTaskTest() - 자신을 참조")
    public void updateTaskTest3() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");
        taskRegParam.setRelationTaskIds(Arrays.asList(1L, 2L));

        Assertions.assertEquals(
                TaskExceptionType.CONTAINS_INVALID_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.updateTask("id", 1, taskRegParam)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("updateTaskTest() - 유효하지 않은 참조 태스크 포함")
    public void updateTaskTest4() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.findByUserAndIdIn(eq(defaultUser), anyIterable()))
                .thenReturn(LongStream.range(2, 3)
                        .mapToObj(idx -> createDefaultTask(idx, false))
                        .collect(Collectors.toList()));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");
        taskRegParam.setRelationTaskIds(Arrays.asList(2L, 3L));

        Assertions.assertEquals(
                TaskExceptionType.CONTAINS_INVALID_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.updateTask("id", 1, taskRegParam)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).findByUserAndIdIn(eq(defaultUser), anyIterable());
    }

    @Test
    @DisplayName("updateTaskTest() - 완료 처리된 참조 태스크 포함")
    public void updateTaskTest5() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.findByUserAndIdIn(eq(defaultUser), anyIterable()))
                .thenReturn(LongStream.range(2, 4)
                        .mapToObj(idx -> createDefaultTask(idx, true))
                        .collect(Collectors.toList()));

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");
        taskRegParam.setRelationTaskIds(Arrays.asList(2L, 3L));

        Assertions.assertEquals(
                TaskExceptionType.CONTAINS_COMPLETED_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.updateTask("id", 1, taskRegParam)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).findByUserAndIdIn(eq(defaultUser), anyIterable());
    }

    @Test
    @DisplayName("updateTaskTest() - 참조 태스크가 수정 할 태스크를 참조")
    public void updateTaskTest6() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.findByUserAndIdIn(eq(defaultUser), anyIterable()))
                .thenReturn(LongStream.range(2, 4)
                        .mapToObj(idx -> createDefaultTask(idx, false))
                        .collect(Collectors.toList()));
        when(taskRepository.existsInverseRelationTask(eq(1L), anyIterable())).thenReturn(true);

        TaskRegParam taskRegParam = new TaskRegParam();
        taskRegParam.setContent("test");
        taskRegParam.setRelationTaskIds(Arrays.asList(2L, 3L));

        Assertions.assertEquals(
                TaskExceptionType.EXIST_INVERSE_REL_TASK,
                Assertions.assertThrows(
                        BasedException.class,
                        () -> taskService.updateTask("id", 1, taskRegParam)).getExceptionType());

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).findByUserAndIdIn(eq(defaultUser), anyIterable());
        verify(taskRepository, times(1)).existsInverseRelationTask(eq(1L), anyIterable());
    }

    @Test
    @DisplayName("completeTaskTest() - 기본")
    public void completeTaskTest1() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.existsNonCompleteInverseRelationTask(anyLong())).thenReturn(false);
        when(taskRepository.saveAndFlush(any(Task.class))).thenReturn(any());

        taskService.completeTask("id", 1L);

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).existsNonCompleteInverseRelationTask(anyLong());
        verify(taskRepository, times(1)).saveAndFlush(any());
    }

    @Test
    @DisplayName("completeTaskTest() - 역 참조 태스크 존재")
    public void completeTaskTest2() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, false)));
        when(taskRepository.existsNonCompleteInverseRelationTask(anyLong())).thenReturn(true);

        Assertions.assertThrows(
                BasedException.class,
                () -> taskService.completeTask("id", 1L));

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, times(1)).existsNonCompleteInverseRelationTask(anyLong());
    }

    @Test
    @DisplayName("completeTaskTest() - 이미 완료 처리")
    public void completeTaskTest3() throws Exception {

        when(userRepository.findById(anyString())).thenReturn(Optional.of(defaultUser));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createDefaultTask(1, true)));

        taskService.completeTask("id", 1L);

        verify(userRepository, times(1)).findById(anyString());
        verify(taskRepository, times(1)).findById(anyLong());
    }

    private Task createDefaultTask(long id, boolean isCompleted) {
        Task task = new Task();
        LocalDateTime currentDate = LocalDateTime.now();
        task.setId(id);
        task.setContent("test");
        task.setRegisterDate(currentDate);
        task.setUpdateDate(currentDate);
        if (isCompleted) {
            task.setCompleteDate(currentDate);
        }
        task.setUser(defaultUser);
        return task;
    }

}
