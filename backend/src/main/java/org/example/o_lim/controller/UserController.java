package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.user.request.UserProfileUpdateRequest;
import org.example.o_lim.dto.user.response.AllUserInfoResponseDto;
import org.example.o_lim.dto.user.response.UserMiniProfileResponseDto;
import org.example.o_lim.dto.user.response.UserProfileResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Users.ROOT)
public class UserController {
    private final UserService userService;

//    마이페이지
    @GetMapping(ApiMappingPattern.Users.MY_INFO)
    public ResponseEntity<ResponseDto<UserProfileResponseDto>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        ResponseDto<UserProfileResponseDto> response = userService.getMyInfo(principal);

        return ResponseEntity.ok().body(response);
    }

//    회원정보수정
    @PutMapping(ApiMappingPattern.Users.MY_INFO)
    public ResponseEntity<ResponseDto<UserProfileResponseDto>> updateMyInfo(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserProfileUpdateRequest request
            ) {
        ResponseDto<UserProfileResponseDto> response = userService.updateMyInfo(principal, request);

        return ResponseEntity.ok().body(response);
    }

//  모든 유저 정보 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<AllUserInfoResponseDto>>> getAllUsers() {
        ResponseDto<List<AllUserInfoResponseDto>> response = userService.getAllUsers();

        return ResponseEntity.ok().body(response);
    }

//    유저 미니 프로필
    @GetMapping(ApiMappingPattern.Users.MINI_MY_INFO)
    public ResponseEntity<ResponseDto<UserMiniProfileResponseDto>> getUserMiniProfile(
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        ResponseDto<UserMiniProfileResponseDto> response = userService.getUserMiniProfile(principal);

        return ResponseEntity.ok().body(response);
    }
}