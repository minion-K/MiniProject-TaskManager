package org.example.o_lim.service.Impl;

import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.enums.RoleType;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.auth.request.FindIdRequestDto;
import org.example.o_lim.dto.auth.request.PasswordResetRequestDto;
import org.example.o_lim.dto.auth.request.SignInRequestDto;
import org.example.o_lim.dto.auth.request.SignUpRequestDto;
import org.example.o_lim.dto.auth.response.FindIdResponseDto;
import org.example.o_lim.dto.auth.response.PasswordChangeResponseDto;
import org.example.o_lim.dto.auth.response.SignInResponseDto;
import org.example.o_lim.dto.auth.response.SignUpResponseDto;
import org.example.o_lim.entity.Role;
import org.example.o_lim.entity.User;
import org.example.o_lim.provider.JwtProvider;
import org.example.o_lim.repository.RoleRepository;
import org.example.o_lim.repository.UserRepository;
import org.example.o_lim.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

//    회원가입
    @Override
    @Transactional
    public ResponseDto<SignUpResponseDto> signUp(SignUpRequestDto request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        if(!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String encoded = passwordEncoder.encode(request.password());

        User user = User.getUser(request, encoded);
        Role defaultRole = roleRepository.getReferenceById(RoleType.USER);
        user.grantRole(defaultRole);
        userRepository.save(user);

        SignUpResponseDto response = new SignUpResponseDto(
                user.getLoginId(),
                user.getName(),
                user.getNickname(),
                user.getGender()
        );

        return ResponseDto.setSuccess("회원가입을 성공하였습니다.", response);
    }

//    로그인
    @Override
    public ResponseDto<SignInResponseDto> signIn(SignInRequestDto request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.loginId(), request.password())
        );

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String accessToken = jwtProvider.generateJwtToken(request.loginId(), roles);

        Claims claims = jwtProvider.getClaims(accessToken);
        long expiresAt = claims.getExpiration().getTime();

        SignInResponseDto response = new SignInResponseDto(
                "Bearer",
                accessToken,
                expiresAt,
                request.loginId(),
                roles
        );

        return ResponseDto.setSuccess("로그인을 성공하였습니다.", response);
    }

//    아이디 찾기
    @Override
    public ResponseDto<FindIdResponseDto> findId(FindIdRequestDto request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("가입된 이메일이 아닙니다."));

        if(!user.getName().equals(request.name())) {
            throw new IllegalArgumentException("사용자 이름이 일치하지 않습니다.");
        }

        FindIdResponseDto response = new FindIdResponseDto(
                user.getName(),
                user.getLoginId()
        );

        return ResponseDto.setSuccess("ID를 성공적으로 찾았습니다.", response);
    }

//    비밀번호 재설정
    @Override
    @Transactional
    public ResponseDto<PasswordChangeResponseDto> resetPassword(PasswordResetRequestDto request) {
        if(!request.newPassword().equals(request.confirmPassword()))
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("가입된 이메일이 아닙니다."));

        if(!user.getLoginId().equals(request.loginId()))
            throw new IllegalArgumentException("로그인 ID가 일치하지 않습니다.");

        String encoded = passwordEncoder.encode(request.newPassword());
        user.changePassword(encoded);

        userRepository.flush();

        PasswordChangeResponseDto response = new PasswordChangeResponseDto(
                user.getLoginId()
        );

        return ResponseDto.setSuccess("비밀번호가 재설정되었습니다.", response);
    }
}
