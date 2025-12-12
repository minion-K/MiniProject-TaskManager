package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.project.request.ProjectCreateRequestDto;
import org.example.o_lim.dto.project.request.ProjectUpdateRequestDto;
import org.example.o_lim.dto.project.response.*;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Projects.ROOT)
public class ProjectController {
    private final ProjectService projectService;

    // 생성
    @PostMapping
    public ResponseEntity<ResponseDto<ProjectCreateResponseDto>> createProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ProjectCreateRequestDto request
            ) {
        ResponseDto<ProjectCreateResponseDto> response = projectService.createProject(principal, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 단건 조회
    @GetMapping(ApiMappingPattern.Projects.BY_ID)
    public ResponseEntity<ResponseDto<ProjectDetailResponseDto>> getProjectById(
            @PathVariable Long projectId
            ) {
        ResponseDto<ProjectDetailResponseDto> response = projectService.getProjectById(projectId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<ProjectListResponseDto>>> getAllProjects() {
        ResponseDto<List<ProjectListResponseDto>> response = projectService.getAllProjects();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // task 순 조회
    @GetMapping(ApiMappingPattern.Projects.SEARCH_BY_TASK_DESC)
    public ResponseEntity<ResponseDto<List<ProjectTaskCountResponseDto>>> getTaskCountDesc() {
        ResponseDto<List<ProjectTaskCountResponseDto>> response = projectService.getTaskCountDesc();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 검색 조회
    @GetMapping(ApiMappingPattern.Projects.SEARCH)
    public ResponseEntity<ResponseDto<List<ProjectListResponseDto>>> getProjectByKeyword(
            @RequestParam("keyword") String keyword
            ) {
        ResponseDto<List<ProjectListResponseDto>> response = projectService.getProjectByKeyword(keyword);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 수정
    @PutMapping(ApiMappingPattern.Projects.BY_ID)
    public ResponseEntity<ResponseDto<ProjectUpdateResponseDto>> updateProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectUpdateRequestDto request
            ) {
        ResponseDto<ProjectUpdateResponseDto> response = projectService.updateProject(principal, projectId, request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 삭제
    @DeleteMapping(ApiMappingPattern.Projects.BY_ID)
    public ResponseEntity<ResponseDto<Void>> deleteProject(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId
            ) {
        ResponseDto<Void> response = projectService.deleteProject(principal, projectId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}