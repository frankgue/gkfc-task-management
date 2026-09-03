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
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.service.dto.response.UserSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserSummaryResponse toSummary(User user);
}
