package org.example.o_lim.dto.admin.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.o_lim.common.enums.RoleType;

public record AddRoleRequestDto(
        @NotNull(message = "userId는 필수입니다.")
        @Positive(message = "userId는 양수여야 합니다.")
        Long userId,

        @NotNull(message = "role은 필수입니다.")
        String role
) {}
