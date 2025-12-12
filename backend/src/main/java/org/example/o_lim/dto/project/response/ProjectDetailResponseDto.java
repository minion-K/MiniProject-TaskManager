package org.example.o_lim.dto.project.response;

import org.example.o_lim.common.utils.DateUtils;
import org.example.o_lim.entity.Project;
import java.time.LocalDateTime;

public record ProjectDetailResponseDto (
        Long id,
        Long adminId,
        String adminManagerNickname,
        String title,
        String description,
        String createdAt,
        String updatedAt
) {
    // 단건 조회
    public static ProjectDetailResponseDto from(Project project) {
        return new ProjectDetailResponseDto(
                project.getId(),
                project.getAdmin().getId(),
                project.getAdmin().getNickname(),
                project.getTitle(),
                project.getDescription(),
                DateUtils.toKstString(project.getCreatedAt()),
                DateUtils.toKstString(project.getUpdatedAt())
            );
        }
    }
