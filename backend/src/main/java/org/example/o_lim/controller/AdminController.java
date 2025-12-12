package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.admin.request.AddRoleRequestDto;
import org.example.o_lim.dto.admin.request.RemoveRoleRequestDto;
import org.example.o_lim.dto.admin.request.UpdateRoleRequestDto;
import org.example.o_lim.dto.admin.response.AddRoleResponseDto;
import org.example.o_lim.dto.admin.response.RemoveRoleResponseDto;
import org.example.o_lim.dto.admin.response.UpdateRoleResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiMappingPattern.Admin.ROOT)
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

//    권한 추가
    @PostMapping(ApiMappingPattern.Admin.ADD)
    public ResponseEntity<ResponseDto<AddRoleResponseDto>> addRoles(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddRoleRequestDto request
            ) {
        ResponseDto<AddRoleResponseDto> response = adminService.addRoles(principal, request);

        return ResponseEntity.ok().body(response);
    }

//    권한 삭제
    @DeleteMapping(ApiMappingPattern.Admin.REMOVE)
    public ResponseEntity<ResponseDto<RemoveRoleResponseDto>> removeRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RemoveRoleRequestDto request
            ) {
        ResponseDto<RemoveRoleResponseDto> response = adminService.removeRole(principal, request);

        return ResponseEntity.ok().body(response);
    }

//    권한 수정
    @PutMapping(ApiMappingPattern.Admin.REPLACE)
    public ResponseEntity<ResponseDto<UpdateRoleResponseDto>> replaceRoles(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateRoleRequestDto request
            ) {
        ResponseDto<UpdateRoleResponseDto> response = adminService.replaceRoles(principal, request);

        return ResponseEntity.ok().body(response);
    }
}
