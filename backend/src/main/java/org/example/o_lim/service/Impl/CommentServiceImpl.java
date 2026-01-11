package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.comment.request.CommentRequestDto;
import org.example.o_lim.dto.comment.response.CommentDetailResponseDto;
import org.example.o_lim.dto.comment.response.CommentPageResponseDto;
import org.example.o_lim.dto.comment.response.PageMeta;
import org.example.o_lim.entity.Comment;
import org.example.o_lim.entity.Task;
import org.example.o_lim.entity.User;
import org.example.o_lim.repository.CommentRepository;
import org.example.o_lim.repository.TaskRepository;
import org.example.o_lim.repository.UserRepository;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.security.util.PrincipalUtils;
import org.example.o_lim.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

//    댓글 생성
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseDto<CommentDetailResponseDto> createComment(UserPrincipal principal, CommentRequestDto request, Long taskId) {
        PrincipalUtils.requiredActive(principal);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 테스크를 찾을 수 없습니다."));

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 사용자를 찾을 수 없습니다."));

        Comment comment = Comment.create(task, user, request.content());
        Comment saved = commentRepository.save(comment);

        CommentDetailResponseDto response  = CommentDetailResponseDto.from(saved);

        return ResponseDto.setSuccess("댓글이 생성되었습니다.", response);
    }

//    전체 조회
    @Override
    public ResponseDto<List<CommentDetailResponseDto>> getAllCommentByCreatedAtDesc(Long taskId) {
        List<CommentRepository.CommentWithCreatedAtProjection> comments = commentRepository.getCommentsByCreatedAtDesc(taskId);

        if (comments == null || comments.isEmpty()) {
            throw new EntityNotFoundException("현재 테스크에 댓글이 없습니다.");
        }

        List<CommentDetailResponseDto> response = comments.stream()
                .map(CommentDetailResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("댓글이 조회되었습니다.", response);
    }

//    댓글 삭제
    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or @commentz.isCommentAuthor(#commentId, authentication)")
    public ResponseDto<CommentDetailResponseDto> deleteComment(UserPrincipal principal, Long taskId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID의 테스크를 찾을 수 없습니다."));

        commentRepository.delete(comment);

        return ResponseDto.setSuccess("댓글이 삭제되었습니다.", null);
    }

//    댓글 페이지네이션
    @Override
    public ResponseDto<CommentPageResponseDto> getPageCommentByCreatedAtDesc(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("현재 테스크에 댓글이 없습니다."));

        Page<Comment> pageResult = commentRepository.findAll(pageable);
        List<CommentDetailResponseDto> content = pageResult.getContent().stream()
                .map(CommentDetailResponseDto::from)
                .toList();

        PageMeta meta = PageMeta.from(pageResult);

        CommentPageResponseDto response = CommentPageResponseDto.builder()
                .content(content)
                .meta(meta)
                .build();

        return ResponseDto.setSuccess("댓글이 조회되었습니다.", response);
    }
}


