package org.example.o_lim.dto.task.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.entity.Task;
import org.example.o_lim.entity.TaskTag;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskSearchResponseDto(
        Long projectId,
        Long taskId,
        String title,
        Long createUserId,
        List<String> assignees,
        TaskStatus status,
        PriorityStatus priority,
        List<TagResponseDto> tags,
        LocalDate dueDate
){
    public static  TaskSearchResponseDto from(Task task) {
        if(task == null) return null;

        List<TagResponseDto> tagDtos = task.getTaskTags().stream()
                .filter(Objects::nonNull)
                .map(TaskTag::getTag)
                .filter(Objects::nonNull)
                .map(TagResponseDto::from)
                .toList();

        List<String> assigneeNicknames = task.getAssignee().stream()
                .filter(Objects::nonNull)
                .map(a -> a.getAssignees())
                .filter(Objects::nonNull)
                .map(user -> user.getNickname())
                .toList();

        return new TaskSearchResponseDto(
                task.getProject().getId(),
                task.getId(),
                task.getTitle(),
                task.getCreatedUser().getId(),
                assigneeNicknames,
                task.getStatus(),
                task.getPriority(),
                tagDtos,
                task.getDueDate()
        );
    }
}

