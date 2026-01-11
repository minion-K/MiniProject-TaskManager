package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.example.o_lim.common.enums.RoleType;
import org.example.o_lim.common.utils.DateUtils;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.admin.request.AddRoleRequestDto;
import org.example.o_lim.dto.admin.request.RemoveRoleRequestDto;
import org.example.o_lim.dto.admin.request.UpdateRoleRequestDto;
import org.example.o_lim.dto.admin.response.AddRoleResponseDto;
import org.example.o_lim.dto.admin.response.RemoveRoleResponseDto;
import org.example.o_lim.dto.admin.response.UpdateRoleResponseDto;
import org.example.o_lim.entity.Role;
import org.example.o_lim.entity.User;
import org.example.o_lim.repository.RoleRepository;
import org.example.o_lim.repository.UserRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.AdminService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@PreAuthorize("hasRole('ADMIN')")
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

//    권한 추가
    @Override
    @Transactional
    public ResponseDto<AddRoleResponseDto> addRoles(
            UserPrincipal principal, @Valid AddRoleRequestDto request
            ) {
        User user = userRepository.findWithRolesById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        RoleType roleType;

        try {
            roleType = RoleType.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("요청한 권한 값이 올바르지 않습니다.");
        }

        Role role = roleRepository.findByName(roleType)
                .orElseThrow(() -> new EntityNotFoundException("해당 권한이 존재하지 않습니다."));

        if(user.getRoleTypes().contains(roleType)) {
           throw new IllegalArgumentException("해당 권한을 이미 보유 중입니다.");
        }

        user.grantRole(role);
        userRepository.flush();

        AddRoleResponseDto response = new AddRoleResponseDto(
                user.getId(),
                user.getLoginId(),
                request.role(),
                Set.copyOf(user.getRoleTypes()),
                DateUtils.toKstString(user.getUpdatedAt())
        );

        return ResponseDto.setSuccess("권한이 추가되었습니다.", response);
    }

//    권한 삭제
    @Override
    @Transactional
    public ResponseDto<RemoveRoleResponseDto> removeRole(
            UserPrincipal principal, @Valid RemoveRoleRequestDto request
            ) {
        User user = userRepository.findWithRolesById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        RoleType roleType;

        try {
            roleType = RoleType.valueOf(request.role());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("요청한 권한 값이 올바르지 않습니다.");
        }

        Role role = roleRepository.findById(roleType)
                .orElseThrow(() -> new EntityNotFoundException("해당 권한을 찾을 수 없습니다."));

        if(!user.getRoleTypes().contains(roleType)) {
            throw new IllegalArgumentException("해당 권한을 보유하고 있지 않습니다.");
        }

        user.revokeRole(role);
        userRepository.flush();

        if(user.getUserRoles().isEmpty()) {
            user.grantRole(roleRepository.getReferenceById(RoleType.USER));
        }

        RemoveRoleResponseDto response = new RemoveRoleResponseDto(
                user.getId(),
                user.getLoginId(),
                request.role(),
                Set.copyOf(user.getRoleTypes()),
                DateUtils.toKstString(user.getUpdatedAt())
        );

        return ResponseDto.setSuccess("권한이 제거되었습니다.", response);
    }

//    권한 수정
    @Override
    @Transactional
    public ResponseDto<UpdateRoleResponseDto> replaceRoles(
            UserPrincipal principal, @Valid UpdateRoleRequestDto request
            ) {
        User user = userRepository.findWithRolesById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        user.getUserRoles().clear();
        userRepository.flush();

        request.roles().forEach(roleType -> {
            Role role = roleRepository.findById(roleType)
                    .orElseThrow(() -> new EntityNotFoundException("해당 권한을 찾을 수 없습니다."));
            user.grantRole(role);
        });

        UpdateRoleResponseDto response = new UpdateRoleResponseDto(
                user.getId(),
                user.getLoginId(),
                Set.copyOf(user.getRoleTypes()),
                DateUtils.toKstString(user.getUpdatedAt())
        );

        return ResponseDto.setSuccess("권한이 수정되었습니다.", response);
    }
}
