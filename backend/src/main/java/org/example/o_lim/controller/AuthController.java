package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.auth.request.FindIdRequestDto;
import org.example.o_lim.dto.auth.request.PasswordResetRequestDto;
import org.example.o_lim.dto.auth.request.SignInRequestDto;
import org.example.o_lim.dto.auth.request.SignUpRequestDto;
import org.example.o_lim.dto.auth.response.FindIdResponseDto;
import org.example.o_lim.dto.auth.response.PasswordChangeResponseDto;
import org.example.o_lim.dto.auth.response.SignInResponseDto;
import org.example.o_lim.dto.auth.response.SignUpResponseDto;
import org.example.o_lim.dto.mail.request.SendMailRequestDto;
import org.example.o_lim.service.AuthService;
import org.example.o_lim.service.MailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Auth.ROOT)
public class AuthController {
    private final AuthService authService;
    private final MailService mailService;

//    회원가입
    @PostMapping(ApiMappingPattern.Auth.SIGN_UP)
    public ResponseEntity<ResponseDto<SignUpResponseDto>> signUp(
            @Valid @RequestBody SignUpRequestDto request
            ) {
        ResponseDto<SignUpResponseDto> response = authService.signUp(request);

        return ResponseEntity.ok().body(response);
    }

//    로그인
    @PostMapping(ApiMappingPattern.Auth.SIGN_IN)
    public ResponseEntity<ResponseDto<SignInResponseDto>> signIn(
            @Valid @RequestBody SignInRequestDto request
            ) {
        ResponseDto<SignInResponseDto> response = authService.signIn(request);

        return ResponseEntity.ok().body(response);
    }

//  이메일 전송
    @PostMapping(ApiMappingPattern.Auth.SEND_EMAIL)
    public ResponseEntity<ResponseDto<Void>> sendEmail(
            @Valid @RequestBody SendMailRequestDto request
            ) {
        mailService.sendEmail(request);

        return ResponseEntity.noContent().build();
    }

//    이메일 인증
    @GetMapping(ApiMappingPattern.Auth.VERIFY)
    public ResponseEntity<ResponseDto<Void>> verifyEmail(
            @RequestParam String token
            ) {
        mailService.verifyEmail(token);

        return ResponseEntity.noContent().build();
    }

//    아이디 찾기
    @GetMapping(ApiMappingPattern.Auth.FIND_ID)
    public ResponseEntity<ResponseDto<FindIdResponseDto>> findId(
            @Valid @RequestBody FindIdRequestDto request
            ) {
        ResponseDto<FindIdResponseDto> response = authService.findId(request);

        return ResponseEntity.ok().body(response);
    }

//    비밀번호 재설정
    @PostMapping(ApiMappingPattern.Auth.RESET_PASSWORD)
    public ResponseEntity<ResponseDto<PasswordChangeResponseDto>> resetPassword(
            @Valid @RequestBody PasswordResetRequestDto request
            ) {
        ResponseDto<PasswordChangeResponseDto> response = authService.resetPassword(request);

        return ResponseEntity.ok().body(response);
    }
}