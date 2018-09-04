package com.cyzest.cyrello.dto;

import com.cyzest.cyrello.dao.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelationTaskInfo {

    private Long id;
    private String content;
    private LocalDateTime registerDate;
    private LocalDateTime updateDate;
    private LocalDateTime completeDate;

    public RelationTaskInfo(Task task) {
        Assert.notNull(task, "task must not be null");
        this.id = task.getId();
        this.content = task.getContent();
        this.registerDate = task.getRegisterDate();
        this.updateDate = task.getUpdateDate();
        this.completeDate = task.getCompleteDate();
    }

}
