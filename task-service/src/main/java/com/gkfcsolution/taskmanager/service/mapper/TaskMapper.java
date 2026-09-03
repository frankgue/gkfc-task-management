package com.gkfcsolution.taskmanager.service.mapper;

/**
 * Created on 2026 at 09:55
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:55
 */
import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.service.dto.request.TaskCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.request.TaskUpdateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.TaskResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class})
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "actualHours", ignore = true)
    Task toEntity(TaskCreateRequest request);

    @Mapping(target = "status", expression = "java(task.getStatus() != null ? task.getStatus().name() : null)")
    @Mapping(target = "priority", expression = "java(task.getPriority() != null ? task.getPriority().name() : null)")
    @Mapping(target = "projectId", expression = "java(task.getProject() != null ? task.getProject().getId() : null)")
    @Mapping(target = "projectName", expression = "java(task.getProject() != null ? task.getProject().getName() : null)")
    @Mapping(target = "commentCount", expression = "java(task.getComments() != null ? task.getComments().size() : 0)")
    @Mapping(target = "assignee", source = "assignee")
    @Mapping(target = "creator", source = "creator")
    TaskResponse toResponse(Task task);

    List<TaskResponse> toResponseList(List<Task> tasks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "actualHours", ignore = true)
    void updateEntity(TaskUpdateRequest request, @MappingTarget Task task);
}
