package com.gkfcsolution.taskmanager.service.dto.response;

/**
 * Created on 2026 at 12:27
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 12:27
 */

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
public class CommentResponse {
    private UUID id;
    private String content;
    private UserSummaryResponse author;
    private UUID taskId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID parentCommentId;
    private boolean hasReplies;
}
