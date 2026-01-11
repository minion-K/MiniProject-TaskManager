package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.task.request.TaskCreateRequestDto;
import org.example.o_lim.dto.task.request.TaskUpdateRequestDto;
import org.example.o_lim.dto.task.request.TaskUpdateStatusRequestDto;
import org.example.o_lim.dto.task.response.TaskCreateResponseDto;
import org.example.o_lim.dto.task.response.TaskDetailResponseDto;
import org.example.o_lim.dto.task.response.TaskSearchResponseDto;
import org.example.o_lim.security.UserPrincipal;
import java.time.LocalDate;
import java.util.List;

public interface TaskService {
    ResponseDto<TaskCreateResponseDto> createTask(Long projectId, UserPrincipal principal, @Valid TaskCreateRequestDto request);
    ResponseDto<List<TaskSearchResponseDto>> getAllTasks(Long projectId);
    ResponseDto<TaskDetailResponseDto> getTaskById(Long projectId, Long taskId);
    ResponseDto<List<TaskDetailResponseDto>> searchTasks(
            Long projectId, Long createUserId, TaskStatus status, PriorityStatus priority, LocalDate dueDate);
    ResponseDto<TaskDetailResponseDto> updateTask(
            Long projectId, Long taskId, UserPrincipal principal,
            @Valid TaskUpdateRequestDto request
    );
    ResponseDto<TaskDetailResponseDto> updateTaskStatus(
            Long projectId, Long taskId, UserPrincipal principal,
            @Valid TaskUpdateStatusRequestDto request
    );
    ResponseDto<Void> deleteTask(Long projectId, Long taskId, UserPrincipal principal);

}
