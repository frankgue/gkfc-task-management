package com.gkfcsolution.taskmanager.service.mapper;

/**
 * Created on 2026 at 12:29
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 12:29
 */

import com.gkfcsolution.taskmanager.domain.entity.Comment;
import com.gkfcsolution.taskmanager.service.dto.request.CommentRequest;
import com.gkfcsolution.taskmanager.service.dto.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class})
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Comment toEntity(CommentRequest request);

    @Mapping(target = "author", source = "author")
    @Mapping(target = "taskId", expression = "java(comment.getTask() != null ? comment.getTask().getId() : null)")
    @Mapping(target = "hasReplies", expression = "java(false)")
    CommentResponse toResponse(Comment comment);
}
