package org.example.o_lim.dto.task.request;

import org.example.o_lim.common.enums.TaskStatus;

public record TaskUpdateStatusRequestDto(
        TaskStatus status
) {}
