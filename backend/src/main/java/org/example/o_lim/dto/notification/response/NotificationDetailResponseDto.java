package org.example.o_lim.dto.notification.response;

import org.example.o_lim.common.utils.DateUtils;
import org.example.o_lim.entity.Notification;

public record NotificationDetailResponseDto (
        Long id,
        String title,
        String content,
        String createdAt,
        String updatedAt
) {
    public static NotificationDetailResponseDto from (Notification notification) {
        return new NotificationDetailResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getContent(),
                DateUtils.toKstString(notification.getCreatedAt()),
                DateUtils.toKstString(notification.getUpdatedAt())
        );
    }
}