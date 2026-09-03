package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.Notification;
import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.NotificationType;
import com.gkfcsolution.taskmanager.domain.repository.NotificationRepository;
import com.gkfcsolution.taskmanager.service.dto.response.NotificationResponse;
import com.gkfcsolution.taskmanager.service.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Created on 2026 at 17:02
 * File: NotificationServiceImpl.java.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 17:02
 */
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(UUID userId, String title, String content, NotificationType type, String link) {
        log.info("Creating notification for user: {}, type: {}", userId, type);

        Notification notification = Notification.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .type(type)
                .read(false)
                .link(link)
                .createdAt(LocalDateTime.now())
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<NotificationResponse> getUserNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Override
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification.setReadAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsReadByUserId(userId);
    }

    @Override
    public void deleteNotification(UUID notificationId, UUID userId) {
        notificationRepository.deleteByIdAndUserId(notificationId, userId);
    }

    @Override
    public void sendTaskAssignedNotification(Task task, User assignee) {
        String title = "Task Assigned: " + task.getTitle();
        String content = String.format("You have been assigned to task '%s'", task.getTitle());
        createNotification(assignee.getId(), title, content, NotificationType.TASK_ASSIGNED,
                "/tasks/" + task.getId());
    }

    @Override
    public void sendTaskCompletedNotification(Task task, User assignee) {
        String title = "Task Completed: " + task.getTitle();
        String content = String.format("Task '%s' has been completed", task.getTitle());
        createNotification(assignee.getId(), title, content, NotificationType.TASK_COMPLETED,
                "/tasks/" + task.getId());
    }

    @Override
    public void sendTaskOverdueNotification(Task task, User assignee) {
        String title = "Task Overdue: " + task.getTitle();
        String content = String.format("Task '%s' is overdue", task.getTitle());
        createNotification(assignee.getId(), title, content, NotificationType.TASK_OVERDUE,
                "/tasks/" + task.getId());
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .type(notification.getType())
                .read(notification.isRead())
                .link(notification.getLink())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}
