// task-domain/src/main/java/com/gkfcsolution/taskmanager/domain/repository/TaskRepository.java
package com.gkfcsolution.taskmanager.domain.repository;

import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findByAssigneeId(UUID assigneeId);

    List<Task> findByProjectId(UUID projectId);

    List<Task> findByStatus(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :now AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectAndStatus(@Param("projectId") UUID projectId, @Param("status") TaskStatus status);

    //  Récupère toutes les tâches où l'utilisateur est :
    // - le créateur (creator)
    // - l'assigné (assignee)
    // - ou membre du projet (project.members)
    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN t.project p " +
            "LEFT JOIN p.members m " +
            "WHERE t.creator.id = :userId " +
            "OR t.assignee.id = :userId " +
            "OR m.id = :userId")
    List<Task> findTasksByUserAsCreatorAssigneeOrMember(@Param("userId") UUID userId);

    //  Version avec pagination
    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN t.project p " +
            "LEFT JOIN p.members m " +
            "WHERE t.creator.id = :userId " +
            "OR t.assignee.id = :userId " +
            "OR m.id = :userId")
    Page<Task> findTasksByUserAsCreatorAssigneeOrMember(@Param("userId") UUID userId, Pageable pageable);

    //  Récupère les tâches d'un projet où l'utilisateur est membre
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND " +
            "(t.creator.id = :userId OR t.assignee.id = :userId OR :userId IN (SELECT m.id FROM t.project.members m))")
    List<Task> findTasksByProjectAndUser(@Param("projectId") UUID projectId, @Param("userId") UUID userId);

    // Compte les tâches par statut pour un utilisateur
    @Query("SELECT COUNT(t) FROM Task t " +
            "LEFT JOIN t.project p " +
            "LEFT JOIN p.members m " +
            "WHERE (t.creator.id = :userId OR t.assignee.id = :userId OR m.id = :userId) " +
            "AND t.status = :status")
    long countByUserAndStatus(@Param("userId") UUID userId, @Param("status") TaskStatus status);
}