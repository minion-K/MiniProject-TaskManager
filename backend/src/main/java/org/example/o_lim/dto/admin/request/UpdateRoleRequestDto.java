package org.example.o_lim.dto.admin.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.o_lim.common.enums.RoleType;
import java.util.Set;

public record UpdateRoleRequestDto(
        @NotNull(message = "userId는 필수입니다.")
        @Positive(message = "userId는 양수여야 합니다.")
        Long userId,

        @NotEmpty(message = "roles는 비어있을 수 없습니다.")
        Set<@NotNull(message = "roles 항목은 null일 수 없습니다.") RoleType> roles
) {}
