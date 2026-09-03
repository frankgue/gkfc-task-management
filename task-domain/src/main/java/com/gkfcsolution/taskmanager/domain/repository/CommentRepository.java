package com.gkfcsolution.taskmanager.domain.repository;

/**
 * Created on 2026 at 09:49
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 09:49
 */
import com.gkfcsolution.taskmanager.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskIdOrderByCreatedAtAsc(UUID taskId);
    List<Comment> findByAuthorId(UUID authorId);
    long countByTaskId(UUID taskId);
}
