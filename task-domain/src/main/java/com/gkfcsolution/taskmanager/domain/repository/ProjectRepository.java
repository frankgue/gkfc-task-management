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
import com.gkfcsolution.taskmanager.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByCreatorId(UUID creatorId);
    List<Project> findByMembers_Id(UUID userId);
    @Query("SELECT p FROM Project p JOIN p.members m WHERE m.id = :userId")
    List<Project> findProjectsByMemberId(@Param("userId") UUID userId);
    // ✅ PROJETS OÙ L'UTILISATEUR EST CREATEUR OU MEMBRE (UNION)
    @Query("SELECT DISTINCT p FROM Project p WHERE p.creator.id = :userId OR :userId IN (SELECT m.id FROM p.members m)")
    List<Project> findProjectsByCreatorOrMember(@Param("userId") UUID userId);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.creator.id = :userId")
    long countByCreatorId(@Param("userId") UUID userId);
}