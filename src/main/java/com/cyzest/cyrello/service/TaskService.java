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

            /*
                참조 태스크를 추가하여 등록할 경우
                유효하지 않은 태스크 및 완료처리 된 태스크가 없는지 확인한다.
             */

            List<Task> validRelationTasks = taskRepository.findByUserAndIdIn(user, relationTaskIds);

            if (CollectionUtils.isEmpty(validRelationTasks) || relationTaskIds.size() > validRelationTasks.size()) {
                throw new BasedException(TaskExceptionType.CONTAINS_INVALID_REL_TASK);
            }

            long completedRelationTaskCount = validRelationTasks.stream()
                    .filter(relationTask -> relationTask.getCompleteDate() != null).count();

            if (completedRelationTaskCount > 0) {
                throw new BasedException(TaskExceptionType.CONTAINS_COMPLETED_REL_TASK);
            }

            task.setRelationTasks(validRelationTasks);
        }

        return new TaskInfo(taskRepository.saveAndFlush(task));
    }

}
