package com.cyzest.cyrello.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUser(User user, Pageable pageable);

    List<Task> findByUserAndIdIn(User user, Iterable<Long> ids);

    @Query(value =
            "SELECT " +
            "COUNT(TASK_ID) > 0 " +
            "FROM " +
            "REL_TASK " +
            "WHERE " +
            "TASK_ID IN ?2 AND REL_TASK_ID = ?1", nativeQuery = true)
    boolean existsInverseRelationTask(Long id, Iterable<Long> relationTaskIds);

    @Query(value =
            "SELECT " +
            "COUNT(REL.TASK_ID) > 0 " +
            "FROM " +
            "REL_TASK AS REL INNER JOIN TASK AS MST ON REL.TASK_ID = MST.ID " +
            "WHERE " +
            "REL.REL_TASK_ID = ?1 AND MST.COMPLETE_DATE IS NULL", nativeQuery = true)
    boolean existsNonCompleteInverseRelationTask(Long id);

}
