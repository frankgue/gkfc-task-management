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
import com.gkfcsolution.taskmanager.domain.entity.Project;
import com.gkfcsolution.taskmanager.service.dto.request.ProjectCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.ProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class})
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", expression = "java(com.gkfcsolution.taskmanager.domain.enums.ProjectStatus.PLANNING)")
    Project toEntity(ProjectCreateRequest request);

    @Mapping(target = "creator", source = "creator")
    @Mapping(target = "members", source = "members")
    @Mapping(target = "status", expression = "java(project.getStatus() != null ? project.getStatus().name() : null)")
    @Mapping(target = "taskCount", expression = "java(project.getTasks() != null ? project.getTasks().size() : 0)")
    @Mapping(target = "completedTaskCount", expression = "java(project.getTasks() != null ? project.getTasks().stream().filter(t -> t.getStatus() != null && t.getStatus().name().equals(\"DONE\")).count() : 0)")
    ProjectResponse toResponse(Project project);
}
