package org.example.o_lim.dto.tag.response;

import org.example.o_lim.entity.Tag;

public record TagResponseDto(
        String name,
        String color
) {
    public static TagResponseDto from(Tag tag) {
        return new TagResponseDto(
                tag.getName(),
                tag.getColor()
        );
    }
}
