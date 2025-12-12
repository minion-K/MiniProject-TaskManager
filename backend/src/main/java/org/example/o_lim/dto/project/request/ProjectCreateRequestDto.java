package org.example.o_lim.dto.project.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequestDto(
//        @NotNull(message = "프로젝트 작성자는 필수 입력값입니다.")
//        Long admin_id,

        @NotBlank(message = "프로젝트 제목은 비워둘 수 없습니다.")
        @Size(max = 150, message = "제목은 최대 150자까지 입력 가능합니다.")
        String title,

        @NotBlank(message = "프로젝트 설명은 비워둘 수 없습니다.")
        @Size(max = 255, message = "설명은 최대 255자까지 입력 가능합니다.")
        String description
    ) {}
