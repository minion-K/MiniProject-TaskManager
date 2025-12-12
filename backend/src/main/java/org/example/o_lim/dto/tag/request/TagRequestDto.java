package org.example.o_lim.dto.tag.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagRequestDto(
        @NotBlank(message = "태그명은 필수 입니다.")
        @Size(max = 50, message = "태그명은 50자까지 가능합니다.")
        String name,

        @NotBlank(message = "색상은 필수 입니다.")
        @Size(max = 50, message = "컬러명은 50자 까지 가능합니다.")
        String color
) {}
