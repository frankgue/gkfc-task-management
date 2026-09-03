package com.gkfcsolution.taskmanager.domain.repository;

import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/**
 * Created on 2026 at 09:49
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:49
 */
public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findByAssigneeId(UUID assigneeId);

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectAndStatus(@Param("projectId") UUID projectId, @Param("status") TaskStatus status);
}
