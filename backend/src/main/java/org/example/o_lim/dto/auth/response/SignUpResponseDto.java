package org.example.o_lim.dto.auth.response;

import org.example.o_lim.common.enums.Gender;

public record SignUpResponseDto(
        String loginId,
        String name,
        String nickname,
        Gender gender
) {
}
