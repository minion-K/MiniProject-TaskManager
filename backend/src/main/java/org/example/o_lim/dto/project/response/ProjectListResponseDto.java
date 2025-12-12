package org.example.o_lim.dto.project.response;

import org.example.o_lim.entity.Project;

public record ProjectListResponseDto(
        Long id,
        String title,
        String description,
        String adminManagerNickname
) {
    // 전체 조회
    public static ProjectListResponseDto from(Project project) {
        return new ProjectListResponseDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getAdmin().getNickname()
        );
    }
}
