package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.tag.request.TagRequestDto;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.security.UserPrincipal;
import java.util.List;

public interface TagService {
    ResponseDto<TagResponseDto> createTag(UserPrincipal principal, @Valid TagRequestDto request, Long projectId);
    ResponseDto<List<TagResponseDto>> getAllTag(Long projectId);
    ResponseDto<TagResponseDto> deleteTag(UserPrincipal principal, Long projectId, Long tagId);
}
