package org.example.o_lim.dto.auth.response;

import java.util.Set;

public record SignInResponseDto(
        String tokenType,
        String accessToken,
        long expiresAt,
        String username,
        Set<String> roles
) {
}
