package com.gkfcsolution.taskmanager.service.dto.response;

/**
 * Created on 2026 at 12:08
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 12:08
 */

import com.gkfcsolution.taskmanager.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String content;
    private NotificationType type;
    private boolean read;
    private String link;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
}
