package org.example.o_lim.dto.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDto(
        @NotBlank(message = "내용은 필수입니다.")
        String content
) {}
