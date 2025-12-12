package org.example.o_lim.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.o_lim.common.enums.Gender;

public record SignUpRequestDto(
        @NotBlank(message = "이름을 입력해주세요") @Size(max = 50)
        String name,

        @NotBlank(message = "로그인 ID를 입력해주세요") @Size(min = 6, max = 14)
        String loginId,

        @NotBlank(message = "비밀번호를 입력해주세요.") @Size(min = 8, max = 16)
        String password,

        @NotBlank(message = "비밀번호 확인란을 입력해주세요") @Size(min = 8, max = 16)
        String confirmPassword,

        @NotBlank(message = "이메일을 입력해주세요.") @Email @Size(max = 255)
        String email,

        @NotBlank(message = "닉네임을 입력해주세요.") @Size(max = 50)
        String nickname,

        Gender gender
) {}
