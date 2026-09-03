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

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {

    @NotBlank(message = "Project name is required")
    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Set<UUID> memberIds;
}
