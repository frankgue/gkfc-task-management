package com.gkfcsolution.taskmanager.service.dto.response;

/**
 * Created on 2026 at 10:58
 * File: UserSummaryResponse.java.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 10:58
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String fullName;
}
