package org.example.o_lim.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.constants.ApiMappingPattern;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.comment.request.CommentRequestDto;
import org.example.o_lim.dto.comment.response.CommentDetailResponseDto;
import org.example.o_lim.dto.comment.response.CommentPageResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.CommentService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiMappingPattern.Comments.ROOT)
public class CommentController {
    private final CommentService commentService;

//    댓글 생성
    @PostMapping
    public ResponseEntity<ResponseDto<CommentDetailResponseDto>> createComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CommentRequestDto request,
            @PathVariable Long taskId
            ) {
        ResponseDto<CommentDetailResponseDto> response = commentService.createComment(principal, request, taskId);

        return ResponseEntity.ok().body(response);
    }

//    댓글 조회
    @GetMapping
    public ResponseEntity<ResponseDto<List<CommentDetailResponseDto>>> getAllCommentsByCreatedAtDesc(
            @PathVariable Long taskId
            ) {
        ResponseDto<List<CommentDetailResponseDto>> response = commentService.getAllCommentByCreatedAtDesc(taskId);

        return ResponseEntity.ok().body(response);
    }

//    댓글 삭제
    @DeleteMapping(ApiMappingPattern.Comments.BY_ID)
    public ResponseEntity<ResponseDto<CommentDetailResponseDto>> deleteComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long taskId,
            @PathVariable Long commentId
            ) {
        ResponseDto<CommentDetailResponseDto> response = commentService.deleteComment(principal, taskId, commentId);

        return  ResponseEntity.ok().body(response);
    }

//    댓글 페이지네이션
    @GetMapping(ApiMappingPattern.Comments.PAGE)
    public ResponseEntity<ResponseDto<CommentPageResponseDto>> getPageCommentsByCreatedAtDesc(
            @PathVariable Long taskId,
            @PageableDefault(
                    page = 0,
                    size = 5,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
            ) {
        ResponseDto<CommentPageResponseDto> response = commentService.getPageCommentByCreatedAtDesc(taskId, pageable);

        return ResponseEntity.ok().body(response);
    }
}