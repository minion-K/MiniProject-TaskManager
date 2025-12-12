package org.example.o_lim.security.util;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.entity.TaskAssignees;
import org.example.o_lim.repository.TaskAssigneesRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.List;

@RequiredArgsConstructor
@Component("authz")
public class TaskStatusChecker {
    private final TaskAssigneesRepository taskAssigneesRepository;

    public boolean isChange(Long taskId, Authentication principal) {
        if (principal == null || taskId == null) return false;

        String loginId = principal.getName();

        List<TaskAssignees> results = taskAssigneesRepository.findByTaskIdWithUser(taskId).stream()
                .filter(assignee -> assignee.getAssignees().getLoginId().equals(loginId))
                .toList();

        return !results.isEmpty();
    }
}
