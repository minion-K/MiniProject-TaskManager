package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.project.request.ProjectCreateRequestDto;
import org.example.o_lim.dto.project.request.ProjectUpdateRequestDto;
import org.example.o_lim.dto.project.response.*;
import org.example.o_lim.security.UserPrincipal;

import java.util.List;

public interface ProjectService {
    ResponseDto<ProjectCreateResponseDto> createProject(UserPrincipal principal, @Valid ProjectCreateRequestDto dto);
    ResponseDto<ProjectDetailResponseDto> getProjectById(Long projectId);
    ResponseDto<List<ProjectListResponseDto>> getAllProjects();
    ResponseDto<ProjectUpdateResponseDto> updateProject(UserPrincipal principal, Long projectId, @Valid ProjectUpdateRequestDto dto);
    ResponseDto<Void> deleteProject(UserPrincipal principal, Long projectId);
    ResponseDto<List<ProjectTaskCountResponseDto>> getTaskCountDesc();
    ResponseDto<List<ProjectListResponseDto>> getProjectByKeyword(String keyword);
}
