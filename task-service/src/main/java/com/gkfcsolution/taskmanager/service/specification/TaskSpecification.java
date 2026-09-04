// task-service/src/main/java/com/gkfcsolution/taskmanager/service/specification/TaskSpecification.java
package com.gkfcsolution.taskmanager.service.specification;

import com.gkfcsolution.taskmanager.domain.entity.Task;
import com.gkfcsolution.taskmanager.domain.entity.User;
import com.gkfcsolution.taskmanager.domain.enums.TaskStatus;
import com.gkfcsolution.taskmanager.domain.enums.UserRole;
import com.gkfcsolution.taskmanager.service.dto.request.TaskFilterRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> buildSpecification(TaskFilterRequest filter, User currentUser) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ✅ Si l'utilisateur n'est pas ADMIN, filtrer par ses droits
            if (currentUser.getRole() != UserRole.ADMIN) {
                // L'utilisateur peut voir les tâches où il est :
                // - créateur
                // - assigné
                // - membre du projet
                Predicate createdByUser = criteriaBuilder.equal(root.get("creator"), currentUser);
                Predicate assignedToUser = criteriaBuilder.equal(root.get("assignee"), currentUser);
                Predicate memberOfProject = criteriaBuilder.isMember(currentUser, root.get("project").get("members"));

                predicates.add(criteriaBuilder.or(createdByUser, assignedToUser, memberOfProject));
            }

            // Filtre par statut
            if (filter.getStatus() != null && !filter.getStatus().isEmpty()) {
                try {
                    TaskStatus status = TaskStatus.valueOf(filter.getStatus().toUpperCase());
                    predicates.add(criteriaBuilder.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {}
            }

            // Filtre par priorité
            if (filter.getPriority() != null && !filter.getPriority().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), filter.getPriority()));
            }

            // Filtre par projet
            if (filter.getProjectId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("project").get("id"), filter.getProjectId()));
            }

            // Filtre par assigné
            if (filter.getAssigneeId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("assignee").get("id"), filter.getAssigneeId()));
            }

            // Filtre par créateur
            if (filter.getCreatorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("creator").get("id"), filter.getCreatorId()));
            }

            // Filtre par date d'échéance
            if (filter.getDueDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), filter.getDueDateFrom()));
            }
            if (filter.getDueDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), filter.getDueDateTo()));
            }

            // Filtre des tâches en retard
            if (filter.getOverdue() != null && filter.getOverdue()) {
                predicates.add(criteriaBuilder.lessThan(root.get("dueDate"), LocalDateTime.now()));
                predicates.add(criteriaBuilder.notEqual(root.get("status"), TaskStatus.DONE));
            }

            // Recherche par titre ou description
            if (filter.getSearchTerm() != null && !filter.getSearchTerm().isEmpty()) {
                String searchPattern = "%" + filter.getSearchTerm().toLowerCase() + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern)
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}