package org.example.o_lim.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.example.o_lim.common.enums.Gender;

public record UserProfileUpdateRequest(
        @Size(max = 50)
        String nickname,

        @Email @Size(max = 255)
        String email,

        Gender gender
) {}
