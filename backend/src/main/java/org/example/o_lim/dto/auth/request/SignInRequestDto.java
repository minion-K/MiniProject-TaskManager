package org.example.o_lim.dto.auth.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequestDto(
        @NotBlank(message = "로그인 ID를 입력해주세요") @Size(min = 6, max = 14)
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요") @Size(min = 8, max = 16)
        String password
) {}
