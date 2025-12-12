package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.admin.request.AddRoleRequestDto;
import org.example.o_lim.dto.admin.request.RemoveRoleRequestDto;
import org.example.o_lim.dto.admin.request.UpdateRoleRequestDto;
import org.example.o_lim.dto.admin.response.AddRoleResponseDto;
import org.example.o_lim.dto.admin.response.RemoveRoleResponseDto;
import org.example.o_lim.dto.admin.response.UpdateRoleResponseDto;
import org.example.o_lim.security.UserPrincipal;

public interface AdminService {
    ResponseDto<AddRoleResponseDto> addRoles(UserPrincipal principal, @Valid AddRoleRequestDto request);
    ResponseDto<RemoveRoleResponseDto> removeRole(UserPrincipal principal, @Valid RemoveRoleRequestDto request);
    ResponseDto<UpdateRoleResponseDto> replaceRoles(UserPrincipal principal, @Valid UpdateRoleRequestDto request);
}
