package org.example.o_lim.dto.project.response;

import org.example.o_lim.common.utils.DateUtils;
import org.example.o_lim.entity.Project;
import java.time.LocalDateTime;

public record ProjectCreateResponseDto(
        Long id,
        Long adminId,
        String title,
        String description,
        String adminManagerNickname,
        String createdAt
) {
    public static ProjectCreateResponseDto from(Project project) {
        return new ProjectCreateResponseDto(
                project.getId(),
                project.getAdmin().getId(),
                project.getTitle(),
                project.getDescription(),
                project.getAdmin().getNickname(),
                DateUtils.toKstString(project.getCreatedAt())
        );
    }
}
