package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.service.dto.request.TaskCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskFilterRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
public interface TaskService {
    TaskResponse createTask(TaskCreateRequest request);
    TaskResponse getTaskById(UUID id);

    Page<TaskResponse> searchTasks(TaskFilterRequest filter, Pageable pageable);
    TaskResponse updateTask(UUID id, TaskUpdateRequest request);
    TaskResponse changeTaskStatus(UUID id, String status);
    TaskResponse assignTask(UUID taskId, UUID userId);
    void deleteTask(UUID id);
    List<TaskResponse> getOverdueTasks();
    long countTasksByStatus(String status);
}
