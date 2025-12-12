package org.example.o_lim.dto.admin.response;

import org.example.o_lim.common.enums.RoleType;
import java.util.Set;

public record UpdateRoleResponseDto(
        Long userId,
        String loginId,
        Set<RoleType> roles,
        String updatedAt
) {}
