package com.cyzest.cyrello.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private static ThreadLocal<Long> increment = new ThreadLocal<>();

    @BeforeEach
    public void setup() {
        entityManager.persistAndFlush(new User("id", "cyzest@nate.com", "password", LocalDateTime.now()));
    }

    @Test
    public void saveTest() {

        Task task1 = createBasedTask();
        taskRepository.saveAndFlush(task1);
        Assertions.assertEquals(getIncrement(), task1.getId().longValue());

        Task task2 = createBasedTask();
        taskRepository.saveAndFlush(task2);
        Assertions.assertEquals(getIncrement(), task2.getId().longValue());

        Task task3 = createBasedTask();
        taskRepository.saveAndFlush(task3);
        Assertions.assertEquals(getIncrement(), task3.getId().longValue());

        Task task4 = createBasedTask();
        task4.setRelationTasks(Arrays.asList(task1, task2));
        taskRepository.saveAndFlush(task4);
        Assertions.assertEquals(getIncrement(), task4.getId().longValue());

        Task task5 = createBasedTask();
        task5.setRelationTasks(Arrays.asList(task2, task3));
        taskRepository.saveAndFlush(task5);
        Assertions.assertEquals(getIncrement(), task5.getId().longValue());
    }

    @Test
    public void findByIdTest() {

        List<Long> idList = persistTestTasks();

        long id;

        id = idList.get(0);
        Optional<Task> optionalTask1 = taskRepository.findById(id);
        Assertions.assertTrue(optionalTask1.isPresent());
        Assertions.assertEquals(id, optionalTask1.get().getId().longValue());

        id = idList.get(1);
        Optional<Task> optionalTask2 = taskRepository.findById(id);
        Assertions.assertTrue(optionalTask2.isPresent());
        Assertions.assertEquals(id, optionalTask2.get().getId().longValue());

        id = idList.get(2);
        Optional<Task> optionalTask3 = taskRepository.findById(id);
        Assertions.assertTrue(optionalTask3.isPresent());
        Assertions.assertEquals(id, optionalTask3.get().getId().longValue());

        id = idList.get(3);
        Optional<Task> optionalTask4 = taskRepository.findById(id);
        Assertions.assertTrue(optionalTask4.isPresent());
        Assertions.assertEquals(id, optionalTask4.get().getId().longValue());
        List<Task> relationTask4 = optionalTask4.get().getRelationTasks();
        Assertions.assertNotNull(relationTask4);
        Assertions.assertEquals(2, relationTask4.size());
        Assertions.assertEquals(idList.get(0).longValue(), relationTask4.get(0).getId().longValue());
        Assertions.assertEquals(idList.get(1).longValue(), relationTask4.get(1).getId().longValue());

        id = idList.get(4);
        Optional<Task> optionalTask5 = taskRepository.findById(id);
        Assertions.assertTrue(optionalTask5.isPresent());
        Assertions.assertEquals(id, optionalTask5.get().getId().longValue());
        List<Task> relationTask5 = optionalTask5.get().getRelationTasks();
        Assertions.assertNotNull(relationTask5);
        Assertions.assertEquals(2, relationTask5.size());
        Assertions.assertEquals(idList.get(1).longValue(), relationTask5.get(0).getId().longValue());
        Assertions.assertEquals(idList.get(2).longValue(), relationTask5.get(1).getId().longValue());
    }

    @Test
    public void findByUserTest() {

        persistTestTasks();

        User user = entityManager.find(User.class, "id");

        Page<Task> tasksPage = taskRepository.findByUser(user, null);
        Assertions.assertNotNull(tasksPage);
        Assertions.assertEquals(5, tasksPage.getTotalElements());

        PageRequest pageRequest;

        pageRequest = PageRequest.of(0, 2, new Sort(Sort.Direction.DESC, "id"));
        tasksPage = taskRepository.findByUser(user, pageRequest);
        Assertions.assertNotNull(tasksPage);
        Assertions.assertNotNull(tasksPage.getContent());
        Assertions.assertEquals(5, tasksPage.getTotalElements());
        Assertions.assertEquals(2, tasksPage.getNumberOfElements());

        pageRequest = PageRequest.of(2, 2, new Sort(Sort.Direction.DESC, "id"));
        tasksPage = taskRepository.findByUser(user, pageRequest);
        Assertions.assertNotNull(tasksPage);
        Assertions.assertNotNull(tasksPage.getContent());
        Assertions.assertEquals(5, tasksPage.getTotalElements());
        Assertions.assertEquals(1, tasksPage.getNumberOfElements());

        pageRequest = PageRequest.of(3, 2, new Sort(Sort.Direction.DESC, "id"));
        tasksPage = taskRepository.findByUser(user, pageRequest);
        Assertions.assertNotNull(tasksPage);
        Assertions.assertNotNull(tasksPage.getContent());
        Assertions.assertEquals(5, tasksPage.getTotalElements());
        Assertions.assertEquals(0, tasksPage.getNumberOfElements());
    }

    @Test
    public void findByUserAndIdsTest() {

        List<Long> idList = persistTestTasks();

        User user = entityManager.find(User.class, "id");

        List<Task> tasks = taskRepository.findByUserAndIdIn(user, idList);

        Assertions.assertNotNull(tasks);
        Assertions.assertEquals(5, tasks.size());
    }

    private List<Long> persistTestTasks() {

        List<Long> idList = new ArrayList<>();

        Task task1 = createBasedTask();
        entityManager.persistAndFlush(task1);
        idList.add(getIncrement());

        Task task2 = createBasedTask();
        entityManager.persistAndFlush(task2);
        idList.add(getIncrement());

        Task task3 = createBasedTask();
        entityManager.persistAndFlush(task3);
        idList.add(getIncrement());

        Task task4 = createBasedTask();
        task4.setRelationTasks(Arrays.asList(task1, task2));
        entityManager.persistAndFlush(task4);
        idList.add(getIncrement());

        Task task5 = createBasedTask();
        task5.setRelationTasks(Arrays.asList(task2, task3));
        entityManager.persistAndFlush(task5);
        idList.add(getIncrement());

        return idList;
    }

    private Task createBasedTask() {
        LocalDateTime currentDate = LocalDateTime.now();
        Task task = new Task();
        task.setRegisterDate(currentDate);
        task.setUpdateDate(currentDate);
        task.setContent("test");
        task.setUser(entityManager.find(User.class, "id"));
        return task;
    }

    private long getIncrement() {
        Long value = increment.get();
        if (value == null) {
            value = 1L;
        }
        increment.set(value + 1L);
        return value;
    }

}
