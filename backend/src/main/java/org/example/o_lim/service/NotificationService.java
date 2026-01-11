package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.notification.request.NotificationCreatedRequestDto;
import org.example.o_lim.dto.notification.request.NotificationUpdatedRequestDto;
import org.example.o_lim.dto.notification.response.NotificationDetailResponseDto;
import org.example.o_lim.dto.notification.response.NotificationListResponseDto;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.security.UserPrincipal;
import java.util.List;

public interface NotificationService {
    ResponseDto<NotificationDetailResponseDto> createNotification(UserPrincipal principal, Long projectId, @Valid NotificationCreatedRequestDto request);
    ResponseDto<NotificationDetailResponseDto> updateNotification(UserPrincipal principal, Long notificationId, Long projectId, @Valid NotificationUpdatedRequestDto request);
    ResponseDto<List<NotificationListResponseDto>> getAllNotifications(Long projectId);
    ResponseDto<NotificationDetailResponseDto> getNotificationById(Long notificationId, Long projectId);
    ResponseDto<Void> deleteNotification(UserPrincipal principal, Long notificationId, Long projectId);
}
