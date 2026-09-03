package com.gkfcsolution.taskmanager.service.validator;

/**
 * Created on 2026 at 09:59
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:59
 */

import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.TaskStatus;
import com.gkfcsolution.taskmanager.domain.enums.UserRole;
import com.gkfcsolution.taskmanager.service.dto.request.TaskCreateRequest;
import com.gkfcsolution.taskmanager.service.exception.BusinessException;
import com.gkfcsolution.taskmanager.service.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TaskValidator {

    public void validateForCreation(TaskCreateRequest request) {
        log.debug("Validating task creation request: {}", request.getTitle());

        // Vérification du titre
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new BusinessException("Task title is required");
        }

        // Vérification de la date d'échéance
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Due date cannot be in the past");
        }

        // Vérification des heures estimées
        if (request.getEstimatedHours() != null && request.getEstimatedHours() <= 0) {
            throw new BusinessException("Estimated hours must be greater than 0");
        }
    }

    public void validateForUpdate(Task task, User currentUser) {
        log.debug("Validating task update for task: {}", task.getId());

        // Seul le créateur, l'assigné ou un admin peut modifier
        if (!canModify(task, currentUser)) {
            throw new UnauthorizedException("You don't have permission to modify this task");
        }
    }

    public void validateStatusChange(Task task, User currentUser) {
        log.debug("Validating status change for task: {}", task.getId());

        // Seul le créateur, l'assigné ou un admin peut changer le statut
        if (!canModify(task, currentUser)) {
            throw new UnauthorizedException("You don't have permission to change task status");
        }

        // Vérification : une tâche terminée ne peut pas être réouverte
        if (task.getStatus() == TaskStatus.DONE) {
            throw new BusinessException("Completed task cannot be reopened");
        }
    }

    public void validateAssignment(Task task, User currentUser) {
        log.debug("Validating assignment for task: {}", task.getId());

        // Seul le créateur, un project manager ou un admin peut assigner
        if (!canAssign(task, currentUser)) {
            throw new UnauthorizedException("You don't have permission to assign this task");
        }
    }

    public void validateDeletion(Task task, User currentUser) {
        log.debug("Validating deletion for task: {}", task.getId());

        // Seul le créateur ou un admin peut supprimer
        if (!(task.getCreator().getId().equals(currentUser.getId()) ||
                currentUser.getRole() == UserRole.ADMIN)) {
            throw new UnauthorizedException("You don't have permission to delete this task");
        }
    }

    private boolean canModify(Task task, User user) {
        if (user.getRole() == UserRole.ADMIN) return true;
        if (task.getCreator().getId().equals(user.getId())) return true;
        if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) return true;
        return false;
    }

    private boolean canAssign(Task task, User user) {
        if (user.getRole() == UserRole.ADMIN) return true;
        if (user.getRole() == UserRole.PROJECT_MANAGER) return true;
        if (task.getCreator().getId().equals(user.getId())) return true;
        return false;
    }
}
