package org.example.o_lim.dto.user.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.o_lim.repository.UserRepository;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AllUserInfoResponseDto(
    String name,
    String nickname,
    String loginId,
    String role
) {
    public static AllUserInfoResponseDto from(UserRepository.UserWithRolesProjection u) {
        return new AllUserInfoResponseDto(
            u.getUsername(),
            u.getUserNickname(),
            u.getUserLoginId(),
            u.getRoleName()
        );
    }
}
