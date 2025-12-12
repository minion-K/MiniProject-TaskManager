package org.example.o_lim.security;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.entity.User;
import org.example.o_lim.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserPrincipalMapper principalMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String loginId = (username == null) ? "" : username.trim();

        if(loginId.isEmpty()) throw new UsernameNotFoundException("사용자를 찾을 수 없습니다. [ " + username + " ]");

        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. [ " + username + " ]"));

        return principalMapper.map(user);
    }
}
