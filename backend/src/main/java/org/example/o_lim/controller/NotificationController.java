package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.notification.request.NotificationCreatedRequestDto;
import org.example.o_lim.dto.notification.request.NotificationUpdatedRequestDto;
import org.example.o_lim.dto.notification.response.NotificationDetailResponseDto;
import org.example.o_lim.dto.notification.response.NotificationListResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(ApiMappingPattern.Notification.ROOT)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

  // 생성
  @PostMapping
  public ResponseEntity<ResponseDto<NotificationDetailResponseDto>> createNotification(
          @AuthenticationPrincipal UserPrincipal principal,
          @PathVariable Long projectId,
          @Valid @RequestBody NotificationCreatedRequestDto request
            ) {
        ResponseDto<NotificationDetailResponseDto> response = notificationService.createNotification(principal, projectId, request);

       return ResponseEntity.ok().body(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<NotificationListResponseDto>>> getAllNotifications(
            @PathVariable Long projectId
            ) {
        ResponseDto<List<NotificationListResponseDto>> response = notificationService.getAllNotifications(projectId);

        return ResponseEntity.ok().body(response);
    }

   // 단건 조회
    @GetMapping(ApiMappingPattern.Notification.BY_ID)
    public ResponseEntity<ResponseDto<NotificationDetailResponseDto>> getNotificationById(
            @PathVariable Long notificationId
            ) {
        ResponseDto<NotificationDetailResponseDto> response = notificationService.getNotificationById(notificationId);

        return ResponseEntity.ok().body(response);
    }

    // 수정
    @PutMapping(ApiMappingPattern.Notification.BY_ID)
    public ResponseEntity<ResponseDto<NotificationDetailResponseDto>> updateNotification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long notificationId,
            @PathVariable Long projectId,
            @Valid @RequestBody NotificationUpdatedRequestDto request
            ) {
        ResponseDto<NotificationDetailResponseDto> response = notificationService.updateNotification(principal, notificationId, projectId, request);

           return ResponseEntity.ok().body(response);
    }

    // 삭제
    @DeleteMapping(ApiMappingPattern.Notification.BY_ID)
    public ResponseEntity<ResponseDto<Void>> deleteNotification(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long notificationId,
            @PathVariable Long projectId
            ) {
        ResponseDto<Void> response = notificationService.deleteNotification(principal, notificationId, projectId);

        return ResponseEntity.ok().body(response);
    }
}