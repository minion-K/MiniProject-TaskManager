package org.example.o_lim.security.util;

import lombok.NoArgsConstructor;
import org.example.o_lim.security.UserPrincipal;
import org.springframework.security.access.AccessDeniedException;

@NoArgsConstructor
public class PrincipalUtils {

    public static void requiredActive(UserPrincipal principal) throws AccessDeniedException {
        if(principal == null) {
            throw new AccessDeniedException("인증 필요");
        }

        if(!principal.isAccountNonLocked() || !principal.isEnabled() || !principal.isAccountNonExpired()) {
            throw new AccessDeniedException("비활성화 된 계정");
        }
    }
}
