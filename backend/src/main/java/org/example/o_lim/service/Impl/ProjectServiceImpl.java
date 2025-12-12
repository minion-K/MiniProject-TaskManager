package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.project.request.ProjectCreateRequestDto;
import org.example.o_lim.dto.project.request.ProjectUpdateRequestDto;
import org.example.o_lim.dto.project.response.*;
import org.example.o_lim.entity.Project;
import org.example.o_lim.entity.User;
import org.example.o_lim.repository.ProjectRepository;
import org.example.o_lim.repository.UserRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.ProjectService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<ProjectCreateResponseDto> createProject(
            UserPrincipal principal, ProjectCreateRequestDto dto
            ) {
        User admin = userRepository.findById(principal.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        boolean exists = projectRepository.existsByTitle(dto.title());
        if (exists) {
            throw new IllegalArgumentException("해당 프로젝트명이 이미 존재합니다.");
        }

        Project project = Project.create(
                admin,
                dto.title(),
                dto.description()
        );
        projectRepository.save(project);

        return ResponseDto.setSuccess("SUCCESS", ProjectCreateResponseDto.from(project));
    }

    @Override
    public ResponseDto<ProjectDetailResponseDto> getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        return ResponseDto.setSuccess("SUCCESS", ProjectDetailResponseDto.from(project));
    }

    @Override
    public ResponseDto<List<ProjectListResponseDto>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectListResponseDto> result = projects.stream()
                .map(ProjectListResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<ProjectUpdateResponseDto> updateProject(
            UserPrincipal principal, Long projectId, ProjectUpdateRequestDto dto
            ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        project.update(dto.title(), dto.description());

        return ResponseDto.setSuccess("SUCCESS", ProjectUpdateResponseDto.from(project));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<Void> deleteProject(UserPrincipal principal, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        projectRepository.delete(project);

        return ResponseDto.setSuccess("SUCCESS", null);
    }

    @Override
    public ResponseDto<List<ProjectTaskCountResponseDto>> getTaskCountDesc() {
        List<ProjectTaskCountResponseDto> result = projectRepository.findProjectTaskCountDesc();

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    public ResponseDto<List<ProjectListResponseDto>> getProjectByKeyword(String keyword) {
        List<Project> projects = projectRepository.findByTitleContainingIgnoreCase(keyword);

        if (projects.isEmpty()) {
            throw new IllegalArgumentException("검색 결과가 없습니다: " + keyword);
        }

        List<ProjectListResponseDto> result = projects.stream()
                .map(ProjectListResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }
}
