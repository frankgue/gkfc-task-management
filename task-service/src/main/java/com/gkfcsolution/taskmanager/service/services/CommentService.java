package com.gkfcsolution.taskmanager.service.services;

import com.gkfcsolution.taskmanager.service.dto.request.CommentRequest;
import com.gkfcsolution.taskmanager.service.dto.response.CommentResponse;

import java.util.List;
import java.util.UUID;

/**
 * Created on 2026 at 12:28
 * File: null.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 12:28
 *
 */
public interface CommentService {
    CommentResponse addComment(CommentRequest request);
    List<CommentResponse> getCommentsByTask(UUID taskId);
    void deleteComment(UUID id);
}
