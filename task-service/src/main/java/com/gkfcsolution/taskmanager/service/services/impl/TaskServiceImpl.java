// task-service/src/main/java/com/gkfcsolution/taskmanager/service/services/impl/TaskServiceImpl.java
package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.TaskStatus;
import com.gkfcsolution.taskmanager.domain.enums.UserRole;
import com.gkfcsolution.taskmanager.domain.repository.TaskRepository;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import com.gkfcsolution.taskmanager.service.dto.request.TaskCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskFilterRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.TaskResponse;
import com.gkfcsolution.taskmanager.service.exception.BusinessException;
import com.gkfcsolution.taskmanager.service.exception.ResourceNotFoundException;
import com.gkfcsolution.taskmanager.service.exception.UnauthorizedException;
import com.gkfcsolution.taskmanager.service.mapper.TaskMapper;
import com.gkfcsolution.taskmanager.service.services.NotificationService;
import com.gkfcsolution.taskmanager.service.services.TaskService;
import com.gkfcsolution.taskmanager.service.specification.TaskSpecification;
import com.gkfcsolution.taskmanager.service.validator.TaskValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final TaskMapper taskMapper;
    private final TaskValidator validator;

    @Override
    public TaskResponse createTask(TaskCreateRequest request) {
        log.info("Creating new task: {}", request.getTitle());

        // Validation
        validator.validateForCreation(request);

        // Récupération du créateur
        User creator = getCurrentUser();

        // Récupération de l'assigné (optionnel)
        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getAssigneeId()));
        }

        // Création de la tâche
        Task task = taskMapper.toEntity(request);
        task.setCreator(creator);
        task.setStatus(TaskStatus.TODO);

        if (assignee != null) {
            task.setAssignee(assignee);
        }

        // Sauvegarde
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with id: {}", savedTask.getId());

        // TODO: Envoyer notification à l'assigné
        // ✅ Envoyer notification SEULEMENT SI un assignee existe
        if (savedTask.getAssignee() != null) {
            notificationService.sendTaskAssignedNotification(savedTask, savedTask.getAssignee());
        } else {
            log.debug("ℹ️ No assignee, notification skipped for task: {}", savedTask.getId());
        }

        return taskMapper.toResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        log.debug("Fetching task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));

        // Vérification des permissions
        User currentUser = getCurrentUser();
        if (!canAccessTask(task, currentUser)) {
            throw new UnauthorizedException("You don't have permission to view this task");
        }

        return taskMapper.toResponse(task);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> searchTasks(TaskFilterRequest filter, Pageable pageable) {
        log.debug("Searching tasks with filters: {}", filter);

        User currentUser = getCurrentUser();
        var specification = TaskSpecification.buildSpecification(filter, currentUser);

        return taskRepository.findAll(specification, pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    public TaskResponse updateTask(UUID id, TaskUpdateRequest request) {
        log.info("Updating task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));

        // Vérification des permissions
        validator.validateForUpdate(task, getCurrentUser());

        // Mise à jour
        taskMapper.updateEntity(request, task);

        // Gestion de l'assignation
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignee(assignee);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", updatedTask.getId());

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public TaskResponse changeTaskStatus(UUID id, String status) {
        log.info("Changing task {} status to: {}", id, status);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));

        // Vérification des permissions
        validator.validateStatusChange(task, getCurrentUser());

        try {
            TaskStatus newStatus = TaskStatus.valueOf(status.toUpperCase());

            // Si la tâche est terminée
            if (newStatus == TaskStatus.DONE && task.getStatus() != TaskStatus.DONE) {
                task.setStatus(newStatus);
                task.setActualHours(task.getEstimatedHours());
                // TODO: notificationService.notifyTaskCompleted(task);
            } else {
                task.setStatus(newStatus);
            }

            Task updatedTask = taskRepository.save(task);
            log.info("Task status changed to: {}", newStatus);
            return taskMapper.toResponse(updatedTask);

        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + status);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasksForCurrentUser(TaskFilterRequest filter, Pageable pageable) {
        log.debug("Getting tasks for current user with filters: {}", filter);

        User currentUser = getCurrentUser();

        // ✅ Utiliser la nouvelle méthode du repository
        return taskRepository.findTasksByUserAsCreatorAssigneeOrMember(currentUser.getId(), pageable)
                .map(taskMapper::toResponse);
    }

    @Override
    public TaskResponse assignTask(UUID taskId, UUID userId) {
        log.info("Assigning task {} to user {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Vérification des permissions
        validator.validateAssignment(task, getCurrentUser());

        task.setAssignee(user);
        if (task.getStatus() == TaskStatus.TODO) {
            task.setStatus(TaskStatus.IN_PROGRESS);
        }

        Task updatedTask = taskRepository.save(task);

        // TODO: notificationService.notifyTaskAssigned(task);

        return taskMapper.toResponse(updatedTask);
    }

    @Override
    public void deleteTask(UUID id) {
        log.info("Deleting task with id: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + id));

        // Vérification des permissions
        validator.validateDeletion(task, getCurrentUser());

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        log.info("Fetching overdue tasks");

        User currentUser = getCurrentUser();
        List<Task> overdueTasks = taskRepository.findOverdueTasks(LocalDateTime.now());

        // Filtrer les tâches selon les permissions de l'utilisateur
        return overdueTasks.stream()
                .filter(task -> canAccessTask(task, currentUser))
                .map(taskMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTasksByStatus(String status) {
        try {
            TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
            return taskRepository.count();
        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Récupère l'utilisateur courant depuis le contexte de sécurité
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Vérifie si un utilisateur peut accéder à une tâche
     */
    private boolean canAccessTask(Task task, User user) {
        // Admin peut tout voir
        if (user.getRole() == UserRole.ADMIN) {
            return true;
        }

        // Le créateur peut voir sa tâche
        if (task.getCreator() != null && task.getCreator().getId().equals(user.getId())) {
            return true;
        }

        // L'assigné peut voir sa tâche
        if (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) {
            return true;
        }

        // Les membres du projet peuvent voir les tâches du projet
        if (task.getProject() != null && task.getProject().getMembers() != null) {
            return task.getProject().getMembers().stream()
                    .anyMatch(member -> member.getId().equals(user.getId()));
        }

        return false;
    }
}