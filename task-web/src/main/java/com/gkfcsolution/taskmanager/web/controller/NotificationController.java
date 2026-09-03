package com.gkfcsolution.taskmanager.web.controller;

/**
 * Created on 2026 at 10:01
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 10:01
 */
import com.gkfcsolution.taskmanager.service.dto.response.NotificationResponse;
import com.gkfcsolution.taskmanager.service.services.NotificationService;
import com.gkfcsolution.taskmanager.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API for managing notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get user notifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        var currentUser = userService.getCurrentUser(authentication);
        Page<NotificationResponse> notifications = notificationService.getUserNotifications(currentUser.getId(), pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread/count")
    @Operation(summary = "Get unread notification count")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID id,
            Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        notificationService.markAsRead(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        notificationService.markAllAsRead(currentUser.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable UUID id,
            Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        notificationService.deleteNotification(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
