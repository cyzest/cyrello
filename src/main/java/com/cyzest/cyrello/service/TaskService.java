package com.cyzest.cyrello.service;

import com.cyzest.cyrello.dao.Task;
import com.cyzest.cyrello.dao.TaskRepository;
import com.cyzest.cyrello.dao.User;
import com.cyzest.cyrello.dao.UserRepository;
import com.cyzest.cyrello.dto.TaskInfo;
import com.cyzest.cyrello.dto.TaskRegParam;
import com.cyzest.cyrello.exception.BasedException;
import com.cyzest.cyrello.exception.CommonExceptionType;
import com.cyzest.cyrello.exception.TaskExceptionType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    public TaskInfo registerTask(String userId, TaskRegParam taskRegParam) throws Exception {

        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(taskRegParam, "taskRegParam must not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BasedException(CommonExceptionType.UNAUTHORIZED));

        Task task = new Task();

        LocalDateTime currentDate = LocalDateTime.now();

        task.setContent(taskRegParam.getContent());
        task.setRegisterDate(currentDate);
        task.setUpdateDate(currentDate);
        task.setUser(user);

        List<Long> relationTaskIds = taskRegParam.getRelationTaskIds();

        if (!CollectionUtils.isEmpty(relationTaskIds)) {
            // 참조 태스크를 추가하여 등록 할 경우 유효하지 않은 태스크 및 완료처리 된 태스크가 없는지 확인한다.
            task.setRelationTasks(getValidRelationTasks(user, relationTaskIds));
        }

        return new TaskInfo(taskRepository.saveAndFlush(task));
    }

    public void updateTask(String userId, long taskId, TaskRegParam taskRegParam) throws Exception {

        Assert.notNull(userId, "userId must not be null");
        Assert.notNull(taskRegParam, "taskRegParam must not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BasedException(CommonExceptionType.UNAUTHORIZED));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BasedException(TaskExceptionType.NOT_EXIST_TASK));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new BasedException(CommonExceptionType.FORBIDDEN);
        }

        if (task.getCompleteDate() != null) {
            // 완료처리 된 태스크는 수정 할 수 없다.
            throw new BasedException(TaskExceptionType.COMPLETED_TASK);
        }

        task.setContent(taskRegParam.getContent());

        List<Long> relationTaskIds = taskRegParam.getRelationTaskIds();

        if (!CollectionUtils.isEmpty(relationTaskIds)) {

            if (relationTaskIds.contains(taskId)) {
                // 참조 태스크에 수정 할 태스크가 존재하면 예외 처리
                throw new BasedException(TaskExceptionType.CONTAINS_INVALID_REL_TASK);
            }

            // 참조 태스크가 유효하지 않은 태스크 및 완료처리 된 태스크가 없는지 확인한다.
            List<Task> validRelationTasks = getValidRelationTasks(user, relationTaskIds);

            List<Long> validRelationTaskIds = validRelationTasks.stream().map(Task::getId).collect(Collectors.toList());

            if (taskRepository.existsInverseRelationTask(taskId, validRelationTaskIds)) {
                // 참조 태스크가 수정 할 태스크를 참조하고 있으면 예외 처리
                throw new BasedException(TaskExceptionType.EXIST_INVERSE_REL_TASK);
            }

            task.setRelationTasks(validRelationTasks);

        } else {
            task.setRelationTasks(null);
        }

        task.setUpdateDate(LocalDateTime.now());

        taskRepository.saveAndFlush(task);
    }

    public void completeTask(String userId, long taskId) throws Exception {

        Assert.notNull(userId, "userId must not be null");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BasedException(CommonExceptionType.UNAUTHORIZED));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BasedException(TaskExceptionType.NOT_EXIST_TASK));

        if (!user.getId().equals(task.getUser().getId())) {
            throw new BasedException(CommonExceptionType.FORBIDDEN);
        }

        if (task.getCompleteDate() == null) {

            if (taskRepository.existsNonCompleteInverseRelationTask(taskId)) {
                // 완료처리 되지 않은 역 참조 태스크가 존재하면 예외처리
                throw new BasedException(TaskExceptionType.EXIST_NON_COMPLETE_INVERSE_REL_TASK);
            }

            LocalDateTime localDateTime = LocalDateTime.now();

            task.setUpdateDate(localDateTime);
            task.setCompleteDate(localDateTime);

            taskRepository.saveAndFlush(task);
        }
    }

    private List<Task> getValidRelationTasks(User user, List<Long> relationTaskIds) throws BasedException {

        List<Task> validRelationTasks = taskRepository.findByUserAndIdIn(user, relationTaskIds);

        if (CollectionUtils.isEmpty(validRelationTasks) || relationTaskIds.size() > validRelationTasks.size()) {
            throw new BasedException(TaskExceptionType.CONTAINS_INVALID_REL_TASK);
        }

        long completedRelationTaskCount = validRelationTasks.stream()
                .filter(relationTask -> relationTask.getCompleteDate() != null).count();

        if (completedRelationTaskCount > 0) {
            throw new BasedException(TaskExceptionType.CONTAINS_COMPLETED_REL_TASK);
        }

        return validRelationTasks;
    }

}
