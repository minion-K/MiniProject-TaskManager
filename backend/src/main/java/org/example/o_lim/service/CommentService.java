package org.example.o_lim.service;

import jakarta.validation.Valid;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.comment.request.CommentRequestDto;
import org.example.o_lim.dto.comment.response.CommentDetailResponseDto;
import org.example.o_lim.dto.comment.response.CommentPageResponseDto;
import org.example.o_lim.security.UserPrincipal;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CommentService {
    ResponseDto<CommentDetailResponseDto> createComment(UserPrincipal principal, @Valid CommentRequestDto request, Long taskId);
    ResponseDto<List<CommentDetailResponseDto>> getAllCommentByCreatedAtDesc(Long taskId);
    ResponseDto<CommentDetailResponseDto> deleteComment(UserPrincipal principal, Long taskId, Long commentId);
    ResponseDto<CommentPageResponseDto> getPageCommentByCreatedAtDesc(Long taskId, Pageable pageable);
}
