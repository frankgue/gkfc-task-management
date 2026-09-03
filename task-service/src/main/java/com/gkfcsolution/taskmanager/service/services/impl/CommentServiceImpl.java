package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.Comment;
import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.NotificationType;
import com.gkfcsolution.taskmanager.domain.repository.CommentRepository;
import com.gkfcsolution.taskmanager.domain.repository.TaskRepository;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import com.gkfcsolution.taskmanager.service.dto.request.CommentRequest;
import com.gkfcsolution.taskmanager.service.dto.response.CommentResponse;
import com.gkfcsolution.taskmanager.service.exception.ResourceNotFoundException;
import com.gkfcsolution.taskmanager.service.mapper.CommentMapper;
import com.gkfcsolution.taskmanager.service.services.CommentService;
import com.gkfcsolution.taskmanager.service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created on 2026 at 12:31
 * File: CommentServiceImpl.java.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 12:31
 */

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

    @Override
    public CommentResponse addComment(CommentRequest request) {
        log.info("Adding comment to task: {}", request.getTaskId());

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + request.getTaskId()));

        User author = getCurrentUser();

        Comment comment = commentMapper.toEntity(request);
        comment.setTask(task);
        comment.setAuthor(author);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment added successfully with id: {}", savedComment.getId());

        // Notifier le créateur et l'assigné de la tâche
        if (task.getCreator() != null && !task.getCreator().getId().equals(author.getId())) {
            notificationService.createNotification(
                    task.getCreator().getId(),
                    "New comment on task: " + task.getTitle(),
                    author.getFullName() + " commented: " + comment.getContent(),
                    NotificationType.COMMENT_ADDED,
                    "/tasks/" + task.getId()
            );
        }

        if (task.getAssignee() != null && !task.getAssignee().getId().equals(author.getId())) {
            notificationService.createNotification(
                    task.getAssignee().getId(),
                    "New comment on task: " + task.getTitle(),
                    author.getFullName() + " commented: " + comment.getContent(),
                    NotificationType.COMMENT_ADDED,
                    "/tasks/" + task.getId()
            );
        }

        return commentMapper.toResponse(savedComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentResponse> getCommentsByTask(UUID taskId) {
        log.debug("Fetching comments for task: {}", taskId);

        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(commentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(UUID id) {
        log.info("Deleting comment: {}", id);

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found: " + id));

        User currentUser = getCurrentUser();
        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully: {}", id);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
