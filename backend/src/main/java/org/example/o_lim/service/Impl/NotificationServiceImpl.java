package org.example.o_lim.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.dto.notification.request.NotificationCreatedRequestDto;
import org.example.o_lim.dto.notification.request.NotificationUpdatedRequestDto;
import org.example.o_lim.dto.notification.response.NotificationDetailResponseDto;
import org.example.o_lim.dto.notification.response.NotificationListResponseDto;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.entity.Notification;
import org.example.o_lim.entity.Project;
import org.example.o_lim.repository.NotificationRepository;
import org.example.o_lim.repository.ProjectRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.NotificationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 조회만 가능
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<NotificationDetailResponseDto> createNotification(UserPrincipal principal, Long projectId, NotificationCreatedRequestDto request) {

        validateTitleAndContent(request.title(), request.content());

         Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("PROJECT_NOT_FOUND"));

        Notification saved = notificationRepository.save(Notification.create(request.title(), request.content(), project));

        NotificationDetailResponseDto data = NotificationDetailResponseDto.from(saved);

        return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<NotificationDetailResponseDto> updateNotification(
            UserPrincipal principal, Long notificationId, Long projectId, NotificationUpdatedRequestDto request
            ) {
       validateTitleAndContent(request.title(), request.content());

       if (notificationId == null) throw new IllegalArgumentException("NOTIFICATION_ID_REQUIRED");

       Notification notification = notificationRepository.findByIdAndProjectId(notificationId, projectId)
               .orElseThrow(() -> new IllegalArgumentException("NOTIFICATION_NOT_FOUND"));

       notification.update(request.title(), request.content());
       notificationRepository.flush();

       NotificationDetailResponseDto data = NotificationDetailResponseDto.from(notification);

       return ResponseDto.setSuccess("SUCCESS", data);
    }

    @Override
    public ResponseDto<List<NotificationListResponseDto>> getAllNotifications(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("PROJECT_NOT_FOUND"));

        List<Notification> notifications = notificationRepository.findAllNotificationById(projectId);
        List<NotificationListResponseDto> result = notifications.stream()
                .map(NotificationListResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    public ResponseDto<NotificationDetailResponseDto> getNotificationById(Long notificationId) {
        NotificationDetailResponseDto data = null;

        if (notificationId == null) throw new IllegalArgumentException("NOTIFICATION_ID_REQUIRED");

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("NOTIFICATION_NOT_FOUND"));

        data = NotificationDetailResponseDto.from(notification);

        return ResponseDto.setSuccess("SUCCESS",data);
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<Void> deleteNotification(
            UserPrincipal principal, Long notificationId, Long projectId
            ) {
        if (notificationId == null) throw new IllegalArgumentException("NOTIFICATION_ID_REQUIRED");

        Notification notification = notificationRepository.findByIdAndProjectId(notificationId, projectId)
                .orElseThrow(() -> new IllegalArgumentException("NOTIFICATION_NOT_FOUND"));

        notificationRepository.delete(notification);

        return ResponseDto.setSuccess("SUCCESS",null);
    }

    private void validateTitleAndContent(String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("TITLE_REQUIRED");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("CONTENT_REQUIRED");
        }
    }
}
