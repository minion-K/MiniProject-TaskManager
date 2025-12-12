package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.auth.request.FindIdRequestDto;
import org.example.o_lim.dto.auth.request.PasswordResetRequestDto;
import org.example.o_lim.dto.auth.request.SignInRequestDto;
import org.example.o_lim.dto.auth.request.SignUpRequestDto;
import org.example.o_lim.dto.auth.response.FindIdResponseDto;
import org.example.o_lim.dto.auth.response.PasswordChangeResponseDto;
import org.example.o_lim.dto.auth.response.SignInResponseDto;
import org.example.o_lim.dto.auth.response.SignUpResponseDto;

public interface AuthService {
    ResponseDto<SignUpResponseDto> signUp(@Valid SignUpRequestDto request);
    ResponseDto<SignInResponseDto> signIn(@Valid SignInRequestDto request);
    ResponseDto<FindIdResponseDto> findId(@Valid FindIdRequestDto request);
    ResponseDto<PasswordChangeResponseDto> resetPassword(@Valid PasswordResetRequestDto request);
}
