package com.cyzest.cyrello.dto;

import com.cyzest.cyrello.dao.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskInfo {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String content;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;
    private LocalDateTime completeDate;
    private UserInfo user;
    private List<RelationTaskInfo> relationTasks;

    public TaskInfo(Task task) {
        Assert.notNull(task, "task must not be null");
        this.id = task.getId();
        this.content = task.getContent();
        this.registerDate = task.getRegisterDate();
        this.updateDate = task.getUpdateDate();
        this.completeDate = task.getCompleteDate();
        this.user = Optional.ofNullable(task.getUser()).map(UserInfo::new).orElse(null);
        this.relationTasks = Optional.ofNullable(task.getRelationTasks())
                .orElse(Collections.emptyList())
                .stream().map(RelationTaskInfo::new)
                .collect(Collectors.toList());
    }

    public String getRegisterDateToFormatString() {
        return Optional.ofNullable(registerDate)
                .map(registerDate -> registerDate.format(dateFormatter)).orElse("");
    }

    public String getUpdateDateToFormatString() {
        return Optional.ofNullable(updateDate)
                .map(registerDate -> registerDate.format(dateFormatter)).orElse("");
    }

}
