package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.domain.entity.Notification;
import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.NotificationType;
import com.gkfcsolution.taskmanager.service.dto.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Created on 2026 at 09:58
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:58
 */
public interface NotificationService {
    Notification createNotification(UUID userId, String title, String content,
                                    NotificationType type, String link);
    Page<NotificationResponse> getUserNotifications(UUID userId, Pageable pageable);
    long getUnreadCount(UUID userId);
    void markAsRead(UUID notificationId, UUID userId);
    void markAllAsRead(UUID userId);
    void deleteNotification(UUID notificationId, UUID userId);
    void sendTaskAssignedNotification(Task task, User assignee);
    void sendTaskCompletedNotification(Task task, User assignee);
    void sendTaskOverdueNotification(Task task, User assignee);
}
