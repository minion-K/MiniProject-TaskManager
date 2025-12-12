package org.example.o_lim.dto.task.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.tag.request.TagRequestDto;
import java.time.LocalDate;
import java.util.List;

public record TaskCreateRequestDto(
        @NotBlank(message = "직무명은 필수입니다.")
        @Size(max = 200, message = "직무명은 최대 200자 까지입니다.")
        String title,

        @NotBlank(message = "직무내용은 필수입니다.")
        String content,

        List<Long> assigneeIds,

        List<Long> tagIds,

        List<TagRequestDto> newTags,

        TaskStatus status,

        PriorityStatus priority,

        LocalDate dueDate
){}
