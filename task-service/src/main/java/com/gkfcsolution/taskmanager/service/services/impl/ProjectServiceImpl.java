package com.gkfcsolution.taskmanager.service.services.impl;

import com.gkfcsolution.taskmanager.domain.entity.Project;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.ProjectStatus;
import com.gkfcsolution.taskmanager.domain.repository.ProjectRepository;
import com.gkfcsolution.taskmanager.domain.repository.UserRepository;
import com.gkfcsolution.taskmanager.service.dto.request.ProjectCreateRequest;
import com.gkfcsolution.taskmanager.service.dto.response.ProjectResponse;
import com.gkfcsolution.taskmanager.service.exception.BusinessException;
import com.gkfcsolution.taskmanager.service.exception.ResourceNotFoundException;
import com.gkfcsolution.taskmanager.service.mapper.ProjectMapper;
import com.gkfcsolution.taskmanager.service.services.ProjectService;

import java.util.List;
import java.util.UUID;

/**
 * Created on 2026 at 11:38
 * File: ProjectServiceImpl.java.java
 * Project: gkfc-task-management
 *
 * @author Frank GUEKENG
 * @date 02/09/2026
 * @time 11:38
 */

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(ProjectCreateRequest request, User creator) {
        log.info("Creating new project: {}", request.getName());

        Project project = projectMapper.toEntity(request);
        project.setCreator(creator);
        project.setStatus(ProjectStatus.PLANNING);

        // Ajouter les membres
        if (request.getMemberIds() != null && !request.getMemberIds().isEmpty()) {
            request.getMemberIds().forEach(memberId -> {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + memberId));
                project.addMember(member);
            });
        }

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully: {}", savedProject.getId());

        return projectMapper.toResponse(savedProject);
    }

    @Transactional(readOnly = true)
    @Override
    public ProjectResponse getProjectById(UUID id) {
        log.debug("Fetching project with id: {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        return projectMapper.toResponse(project);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ProjectResponse> getProjectsByUser(UUID userId) {
        log.debug("Fetching projects for user: {}", userId);

        return projectRepository.findByMembers_Id(userId).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponse updateProjectStatus(UUID id, String status) {
        log.info("Updating project {} status to: {}", id, status);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));

        try {
            ProjectStatus newStatus = ProjectStatus.valueOf(status.toUpperCase());
            project.setStatus(newStatus);
            Project updatedProject = projectRepository.save(project);

            return projectMapper.toResponse(updatedProject);

        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + status);
        }
    }

    @Override
    public void deleteProject(UUID id) {
        log.info("Deleting project with id: {}", id);

        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Project not found: " + id);
        }

        projectRepository.deleteById(id);
        log.info("Project deleted successfully: {}", id);
    }
}
