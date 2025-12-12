package org.example.o_lim.repository;

import org.example.o_lim.entity.TaskAssignees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskAssigneesRepository extends JpaRepository<TaskAssignees, Long> {
    @Query("""
        SELECT t FROM TaskAssignees t
        JOIN FETCH t.assignees
        WHERE t.task.id = :taskId
    """)
    List<TaskAssignees> findByTaskIdWithUser(@Param("taskId") Long taskId);
}
