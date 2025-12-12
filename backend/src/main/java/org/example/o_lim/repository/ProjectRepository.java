package org.example.o_lim.repository;

import org.example.o_lim.dto.project.response.ProjectTaskCountResponseDto;
import org.example.o_lim.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByTitle(String title);

    List<Project> findByTitleContainingIgnoreCase(String keyword);

    public interface ProjectWithTaskCountProjection {
        Long getProjectId();
        String getTitle();
        String getDescription();
        Long getTaskCount();
    }

    @Query(value = """
        SELECT 
            p.id as id,
            p.title as title,
            p.description as description,
            COUNT(t.id) as taskCount
        FROM 
            projects p
            LEFT JOIN 
                tasks t ON p.id = t.project_id
        GROUP BY
            p.id
        ORDER BY
            COUNT(t.id) DESC
    """, nativeQuery = true)
    List<ProjectTaskCountResponseDto> findProjectTaskCountDesc();
}
