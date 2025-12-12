package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.tag.request.TagRequestDto;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.entity.Project;
import org.example.o_lim.entity.Tag;
import org.example.o_lim.repository.ProjectRepository;
import org.example.o_lim.repository.TagRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.TagService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ProjectRepository projectRepository;

//    태그 생성
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<TagResponseDto> createTag(UserPrincipal principal, TagRequestDto request,Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 프로젝트를 찾을 수 없습니다."));

        if (tagRepository.existsByNameAndProjectId(request.name(), projectId)) {
            throw new IllegalArgumentException("이미 존재하는 태그명입니다: " + request.name());
        }

        if (tagRepository.existsByColorAndProjectId(request.color(), projectId)) {
            throw new IllegalArgumentException("이미 존재하는 색상입니다: " + request.color());
        }

        Tag tags = Tag.create(project, request.name(), request.color());
        Tag saved = tagRepository.save(tags);

        TagResponseDto response = TagResponseDto.from(saved);

        return ResponseDto.setSuccess("태그가 생성되었습니다.", response);
    }

//    전체 조회
    @Override
    public ResponseDto<List<TagResponseDto>> getAllTag(Long projectId) {
        List<Tag> tags = tagRepository.findByProjectId(projectId);

        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException("현재 프로젝트가 없습니다.");
        }

        List<TagResponseDto> response = tags.stream()
                .map(TagResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("태그가 조회되었습니다.", response);
    }

//    태그 삭제
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseDto<TagResponseDto> deleteTag(UserPrincipal principal, Long projectId, Long tagId) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 태그가 찾을 수 없습니다."));

        if (!tag.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("해당 ID의 프로젝트를 찾을 수 없습니다.");
        }

        tagRepository.delete(tag);

        return ResponseDto.setSuccess("태그가 삭제되었습니다.", null);
    }
}