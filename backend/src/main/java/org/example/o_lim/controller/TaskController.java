package org.example.o_lim.controller;

import jakarta.validation.Valid;
import org.example.o_lim.dto.task.request.TaskUpdateStatusRequestDto;
import org.example.o_lim.entity.Project;
import org.example.o_lim.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.task.request.TaskCreateRequestDto;
import org.example.o_lim.dto.task.request.TaskUpdateRequestDto;
import org.example.o_lim.dto.task.response.TaskCreateResponseDto;
import org.example.o_lim.dto.task.response.TaskDetailResponseDto;
import org.example.o_lim.dto.task.response.TaskSearchResponseDto;
import org.example.o_lim.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Tasks.ROOT)
public class TaskController {
    private final TaskService taskService;

    // 생성
    @PostMapping()
    public ResponseEntity<ResponseDto<TaskCreateResponseDto>> createTask(
            @PathVariable("projectId") Project project,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TaskCreateRequestDto request
            ) {
        ResponseDto<TaskCreateResponseDto> response = taskService.createTask(project.getId(), principal, request);

        return ResponseEntity.ok().body(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<TaskSearchResponseDto>>> getAllTasks(
            @PathVariable Long projectId
            ) {
        ResponseDto<List<TaskSearchResponseDto>> response = taskService.getAllTasks(projectId);

        return ResponseEntity.ok().body(response);
    }

    // 단건 조회
    @GetMapping(ApiMappingPattern.Tasks.BY_ID)
    public ResponseEntity<ResponseDto<TaskDetailResponseDto>> getTaskById(
            @PathVariable Long projectId,
            @PathVariable Long taskId
            ) {
        ResponseDto<TaskDetailResponseDto> response = taskService.getTaskById(projectId, taskId);

        return ResponseEntity.ok().body(response);
    }

    // 검색 조회
    @GetMapping(ApiMappingPattern.Tasks.SEARCH)
    public ResponseEntity<ResponseDto<List<TaskDetailResponseDto>>> searchTasks(
            @PathVariable Long projectId,
            @RequestParam(required = false) Long createUserId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) PriorityStatus priority,
            @RequestParam(required = false) LocalDate dueDate
            ) {
        ResponseDto<List<TaskDetailResponseDto>> response = taskService.searchTasks(projectId, createUserId, status, priority, dueDate);

        return ResponseEntity.ok().body(response);
    }

    // 수정
    @PutMapping(ApiMappingPattern.Tasks.BY_ID)
    public ResponseEntity<ResponseDto<TaskDetailResponseDto>> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TaskUpdateRequestDto request
            ) {
        ResponseDto<TaskDetailResponseDto> response = taskService.updateTask(projectId, taskId, principal, request);

        return ResponseEntity.ok().body(response);
    }

     // 담당자 상태 수정
    @PutMapping(ApiMappingPattern.Tasks.UPDATE_BY_STATUS)
    public ResponseEntity<ResponseDto<TaskDetailResponseDto>> updateTaskStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TaskUpdateStatusRequestDto request
            ) {
        ResponseDto<TaskDetailResponseDto> response = taskService.updateTaskStatus(projectId, taskId, principal, request);

        return ResponseEntity.ok().body(response);
    }

    // 삭제
    @DeleteMapping(ApiMappingPattern.Tasks.BY_ID)
    public ResponseEntity<ResponseDto<Void>> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        ResponseDto<Void> response = taskService.deleteTask(projectId, taskId, principal);

        return ResponseEntity.ok().body(response);
    }
}