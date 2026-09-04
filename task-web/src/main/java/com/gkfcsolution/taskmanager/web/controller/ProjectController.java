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
import com.gkfcsolution.taskmanager.service.dto.request.ProjectCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.ProjectResponse;
import com.gkfcsolution.taskmanager.service.services.ProjectService;
import com.gkfcsolution.taskmanager.service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "API for managing projects")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new project")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectCreateRequest request,
            Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        ProjectResponse response = projectService.createProject(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get project by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID id) {
        ProjectResponse response = projectService.getProjectById(id);
        return ResponseEntity.ok(response);
    }
/*
    @GetMapping("/creator")
    @Operation(summary = "Get all projects for current user")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<List<ProjectResponse>> getMyProjectsByCreator(Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        log.debug("Current User : " + currentUser.getId() + " - " + currentUser.getEmail());
        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser.getId());
        return ResponseEntity.ok(projects);
    }*/

    @GetMapping
    @Operation(summary = "Get all projects for current user")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        var currentUser = userService.getCurrentUser(authentication);
        log.debug("Current User : " + currentUser.getId() + " - " + currentUser.getEmail());
        List<ProjectResponse> projects = projectService.getProjectsByUser(currentUser.getId());
        return ResponseEntity.ok(projects);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update project status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<ProjectResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        ProjectResponse response = projectService.updateProjectStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a project")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
