package org.example.o_lim.dto.project.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.o_lim.entity.Project;
import org.example.o_lim.repository.ProjectRepository;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectTaskCountResponseDto(
        Long id,
        String title,
        String description,
        Long taskCount
) {
    public static ProjectTaskCountResponseDto from(Project project, long taskCount) {
        if (project == null) return null;

        return new ProjectTaskCountResponseDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                taskCount
        );
    }

    public static ProjectTaskCountResponseDto from(ProjectRepository.ProjectWithTaskCountProjection p) {
        return new ProjectTaskCountResponseDto(
                p.getProjectId(),
                p.getTitle(),
                p.getDescription(),
                p.getTaskCount()
        );
    }
}
