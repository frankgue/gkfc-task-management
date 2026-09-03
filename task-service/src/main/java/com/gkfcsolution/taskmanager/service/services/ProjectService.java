package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.service.dto.request.ProjectCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.ProjectResponse;

import java.util.List;
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
public interface ProjectService {
    ProjectResponse createProject(ProjectCreateRequest request, User creator);
    ProjectResponse getProjectById(UUID id);
    List<ProjectResponse> getProjectsByUser(UUID userId);
    ProjectResponse updateProjectStatus(UUID id, String status);
    void deleteProject(UUID id);
}
