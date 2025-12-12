package org.example.o_lim.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FindIdRequestDto(
        @Email @NotBlank(message = "이메일을 입력해주세요")
        String email,

        @NotBlank(message = "이름을 입력해주세요")
        String name
) {}
