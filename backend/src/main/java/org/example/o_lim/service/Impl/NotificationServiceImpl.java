package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
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
    public ResponseDto<NotificationDetailResponseDto> createNotification(
            UserPrincipal principal, Long projectId, NotificationCreatedRequestDto request
            ) {
        validateTitleAndContent(request.title(), request.content());

         Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("PROJECT_NOT_FOUND"));

        Notification saved = notificationRepository.save(Notification.create(request.title(), request.content(), project));

        NotificationDetailResponseDto response = NotificationDetailResponseDto.from(saved);

        return ResponseDto.setSuccess("SUCCESS", response);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<NotificationDetailResponseDto> updateNotification(
            UserPrincipal principal, Long notificationId,
            Long projectId, NotificationUpdatedRequestDto request
            ) {
       validateTitleAndContent(request.title(), request.content());

       Notification notification = notificationRepository.findByIdAndProjectId(notificationId, projectId)
               .orElseThrow(() -> new EntityNotFoundException("해당 공지사항을 찾을 수 없습니다."));

       notification.update(request.title(), request.content());
       notificationRepository.flush();

       NotificationDetailResponseDto response = NotificationDetailResponseDto.from(notification);

       return ResponseDto.setSuccess("SUCCESS", response);
    }

    @Override
    public ResponseDto<List<NotificationListResponseDto>> getAllNotifications(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        List<Notification> notifications = notificationRepository.findAllNotificationById(projectId);
        List<NotificationListResponseDto> response = notifications.stream()
                .map(NotificationListResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", response);
    }

    @Override
    public ResponseDto<NotificationDetailResponseDto> getNotificationById(
            Long notificationId, Long projectId
            ) {
        Notification notification = notificationRepository.findByIdAndProjectId(notificationId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 공지사항을 찾을 수 없습니다."));

        NotificationDetailResponseDto response = NotificationDetailResponseDto.from(notification);

        return ResponseDto.setSuccess("SUCCESS",response);
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseDto<Void> deleteNotification(
            UserPrincipal principal, Long notificationId, Long projectId
            ) {
        Notification notification = notificationRepository.findByIdAndProjectId(notificationId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 공지사항을 찾을 수 없습니다."));

        notificationRepository.delete(notification);

        return ResponseDto.setSuccess("SUCCESS",null);
    }

    private void validateTitleAndContent(String title, String content) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("공지사항 제목을 입력해주세요.");
        }

        if (!StringUtils.hasText(content)) {
            throw new IllegalArgumentException("공지사항 내용을 입력해주세요.");
        }
    }
}
