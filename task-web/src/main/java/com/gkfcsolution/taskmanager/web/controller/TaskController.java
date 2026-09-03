package com.gkfcsolution.taskmanager.web.controller;

import com.gkfcsolution.taskmanager.service.dto.request.TaskCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskFilterRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.TaskResponse;
import com.gkfcsolution.taskmanager.service.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Endpoints for managing tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all tasks with filters")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
            @ModelAttribute TaskFilterRequest filter,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        // ✅ Utiliser searchTasks au lieu de getAllTasks
        Page<TaskResponse> tasks = taskService.searchTasks(filter, pageable);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing task")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROJECT_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign task to a user")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable UUID id,
            @RequestParam UUID userId) {
        TaskResponse response = taskService.assignTask(id, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change task status")
    public ResponseEntity<TaskResponse> changeStatus(
            @PathVariable UUID id,
            @RequestParam String status) {
        TaskResponse response = taskService.changeTaskStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get all overdue tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER')")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    // ✅ AJOUTER L'ENDPOINT POUR COMPTER LES TÂCHES PAR STATUT
    @GetMapping("/count")
    @Operation(summary = "Count tasks by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROJECT_MANAGER', 'MEMBER')")
    public ResponseEntity<Long> countTasksByStatus(@RequestParam String status) {
        long count = taskService.countTasksByStatus(status);
        return ResponseEntity.ok(count);
    }
}