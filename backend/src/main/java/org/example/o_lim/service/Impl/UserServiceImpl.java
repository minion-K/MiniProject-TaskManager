package org.example.o_lim.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.enums.Gender;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.user.request.UserProfileUpdateRequest;
import org.example.o_lim.dto.user.response.AllUserInfoResponseDto;
import org.example.o_lim.dto.user.response.UserMiniProfileResponseDto;
import org.example.o_lim.dto.user.response.UserProfileResponseDto;
import org.example.o_lim.entity.User;
import org.example.o_lim.repository.UserRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.security.util.PrincipalUtils;
import org.example.o_lim.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

//    마이페이지
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<UserProfileResponseDto> getMyInfo(UserPrincipal principal) {
        PrincipalUtils.requiredActive(principal);

        User user = userRepository.findByLoginId(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        UserProfileResponseDto response
                = new UserProfileResponseDto(
                        user.getLoginId(),
                        user.getName(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getGender()
        );

        return ResponseDto.setSuccess("조회 성공", response);
    }

//    회원 정보 수정
    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<UserProfileResponseDto> updateMyInfo(UserPrincipal principal, UserProfileUpdateRequest request) {
        PrincipalUtils.requiredActive(principal);

        User user = userRepository.findByLoginId(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        if(request.nickname() == null && request.email() == null && request.gender() == null) {
            throw new IllegalArgumentException("수정할 정보를 입력해주세요");
        }

        String newNickname = (request.nickname() != null && !request.nickname().isBlank()) ? request.nickname() : null;
        String newEmail = (request.email() != null && !request.email().isBlank()) ? request.email() : null;
        Gender newGender = request.gender();

        boolean changedNickname = newNickname != null && !Objects.equals(user.getNickname(), request.nickname());
        boolean changedEmail = newEmail != null && !Objects.equals(user.getEmail(), request.email());
        boolean changedGender = newGender != null  || user.getGender() != null;

        changedGender = changedGender && !Objects.equals(user.getGender(), request.gender());

        if(!changedNickname && !changedEmail && !changedGender) {
            throw new IllegalArgumentException("변경된 개인정보가 없습니다.");
        }

        if(changedNickname) user.setNickname(newNickname);
        if(changedEmail) user.setEmail(newEmail);
        if(changedGender) user.setGender(newGender);

        UserProfileResponseDto response =
                new UserProfileResponseDto(
                        user.getLoginId(),
                        user.getName(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getGender()
                );

        return ResponseDto.setSuccess("개인정보가 수정되었습니다.", response);
    }

//    모든 유저 조회(권한 정보 포함)
    public ResponseDto<List<AllUserInfoResponseDto>> getAllUsers() {
        List<UserRepository.UserWithRolesProjection> result  = userRepository.findAllUsersWithRole_Native();

        List<AllUserInfoResponseDto> response = result.stream()
                .map(AllUserInfoResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("조회를 성공하였습니다.", response);
    }

//    유저 미니 프로필
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseDto<UserMiniProfileResponseDto> getUserMiniProfile(UserPrincipal principal) {
        PrincipalUtils.requiredActive(principal);

        User user = userRepository.findByLoginId(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        UserMiniProfileResponseDto response = new UserMiniProfileResponseDto(
                user.getNickname(),
                user.getName(),
                user.getLoginId(),
                user.getGender()
        );

        return ResponseDto.setSuccess("조회를 성공하였습니다.", response);
    }
}
