package org.example.o_lim.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDto(
        @NotBlank(message = "ID를 입력해주세요")
        String loginId,

        @NotBlank(message = "이메일 인증을 받아주세요") @Email
        String email,

        @NotBlank(message = "새로운 비밀번호를 입력해주세요")
        String newPassword,

        @NotBlank(message = "비밀번호를 확인해주세요")
        String confirmPassword
) {}
