package org.example.o_lim.security;

import org.example.o_lim.entity.User;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Collection;
import java.util.List;

@Component
public class UserPrincipalMapper {
    @NonNull
    public UserPrincipal map(@NonNull User user) {
        Collection<SimpleGrantedAuthority> authorities =
                (user.getUserRoles() == null || user.getUserRoles().isEmpty())
                ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
                : user.getUserRoles().stream()
                        .map(r -> {
                            String name = r.getRole().getName().name();
                            String role = name.startsWith("ROLE") ? name : "ROLE_" + name;

                            return new SimpleGrantedAuthority(role);
                        })
                        .toList();

        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getLoginId())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}
