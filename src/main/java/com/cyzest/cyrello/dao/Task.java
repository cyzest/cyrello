package com.cyzest.cyrello.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime registerDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    @Column
    private LocalDateTime completeDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToMany
    @JoinTable(
            name="REL_TASKS",
            joinColumns = @JoinColumn(name="TASK_ID"),
            inverseJoinColumns = @JoinColumn(name="REL_TASK_ID"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"TASK_ID", "REL_TASK_ID"})
    )
    private List<Task> relationTasks;

}
