package org.example.o_lim.dto.comment.response;

import org.example.o_lim.common.utils.DateUtils;
import org.example.o_lim.entity.Comment;
import org.example.o_lim.repository.CommentRepository;

public record CommentDetailResponseDto(
        String authorName,
        String content,
        String createdAt
) {
    public static CommentDetailResponseDto from(Comment comment) {
        return new CommentDetailResponseDto(
                comment.getAuthor().getName(),
                comment.getContent(),
                DateUtils.toKstString(comment.getCreatedAt()));
    }

    public static CommentDetailResponseDto from(CommentRepository.CommentWithCreatedAtProjection c) {
        return new CommentDetailResponseDto(
                c.getAuthorName(),
                c.getContent(),
                DateUtils.toKstString(c.getCreatedAt()));
    }
}
