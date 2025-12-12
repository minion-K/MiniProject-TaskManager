package org.example.o_lim.dto.user.response;

import org.example.o_lim.common.enums.Gender;

public record UserMiniProfileResponseDto(
        String nickname,
        String name,
        String loginId,
        Gender gender
) {}
