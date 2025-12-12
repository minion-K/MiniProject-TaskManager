package org.example.o_lim.dto.comment.response;

import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious,
        String sort
) {
    public static PageMeta from(Page<?> p) {
        String sort = p.getSort().toString();
        return PageMeta.builder()
                .page(p.getNumber())
                .size(p.getSize())
                .totalElements(p.getTotalElements())
                .totalPages(p.getTotalPages())
                .hasNext(p.hasNext())
                .hasPrevious(p.hasPrevious())
                .sort(sort)
                .build();
    }
}
