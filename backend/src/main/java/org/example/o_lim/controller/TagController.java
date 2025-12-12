package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.tag.request.TagRequestDto;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Tags.ROOT)
public class TagController {
    private final TagService tagService;

//    태그 생성
    @PostMapping
    public ResponseEntity<ResponseDto<TagResponseDto>> createTag(
             @AuthenticationPrincipal UserPrincipal principal,
             @Valid @RequestBody TagRequestDto request,
             @PathVariable Long projectId
             ) {
        ResponseDto<TagResponseDto> response = tagService.createTag(principal, request, projectId);

        return ResponseEntity.ok().body(response);
    }

//    전체 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<TagResponseDto>>> getAllTags(
            @PathVariable Long projectId
            ) {
        ResponseDto<List<TagResponseDto>> response = tagService.getAllTag(projectId);

        return ResponseEntity.ok().body(response);
    }

//    태그 삭제
    @DeleteMapping(ApiMappingPattern.Tags.BY_ID)
    public ResponseEntity<ResponseDto<TagResponseDto>> deleteTag(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long projectId,
            @PathVariable Long tagId
            ) {
        ResponseDto<TagResponseDto> response = tagService.deleteTag(principal, projectId, tagId);

        return ResponseEntity.ok().body(response);
    }
}