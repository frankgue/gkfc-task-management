package com.gkfcsolution.taskmanager.service.dto.request;

/**
 * Created on 2026 at 09:53
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:53
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    private String title;
    private String description;
    private String priority;
    private LocalDateTime dueDate;
    private Double estimatedHours;
    private UUID assigneeId;
    private UUID projectId;
    private List<String> tags;
}
