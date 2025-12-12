package org.example.o_lim.dto.user.response;

import org.example.o_lim.common.enums.Gender;

public record UserProfileResponseDto(
        String loginId,
        String name,
        String email,
        String nickname,
        Gender gender
) {}
