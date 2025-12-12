package org.example.o_lim.dto.comment.response;

import lombok.Builder;
import java.util.List;

@Builder
public record CommentPageResponseDto(
        List<CommentDetailResponseDto> content,
        PageMeta meta
) {}
