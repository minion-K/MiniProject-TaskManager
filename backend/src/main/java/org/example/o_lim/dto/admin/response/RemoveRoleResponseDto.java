package org.example.o_lim.dto.admin.response;

import org.example.o_lim.common.enums.RoleType;
import java.util.Set;

public record RemoveRoleResponseDto(
        Long userId,
        String loginId,
        String removed,
        Set<RoleType> roles,
        String updatedAt
) {}
