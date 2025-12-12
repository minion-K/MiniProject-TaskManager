package org.example.o_lim.dto.task.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.comment.response.CommentDetailResponseDto;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.entity.*;
import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskDetailResponseDto(
        Long id,
        Long projectId,
        String title,
        String content,
        Long createUserId,
        List<String> assignees,
        TaskStatus status,
        PriorityStatus priority,
        List<TagResponseDto> tags,
        LocalDate dueDate,
        List<CommentDetailResponseDto> comments
) {
    public static  TaskDetailResponseDto from(Task task){
        if(task == null) return null;

        List<TagResponseDto> tag = new ArrayList<>();
        Set<Long> seenTagIds = new HashSet<>();

        for(TaskTag taskTag: task.getTaskTags()) {
            if(taskTag == null || taskTag.getTag() == null) continue;

            Long tagId = taskTag.getTag().getId();
            if(seenTagIds.contains(tagId)) continue;

            seenTagIds.add(tagId);
            tag.add(TagResponseDto.from(taskTag.getTag()));
        }

        List<Comment> comments
                = task.getComments() != null ? task.getComments() : Collections.emptyList();

        List<CommentDetailResponseDto> commentDtos = comments.stream()
                .filter(Objects::nonNull)
                .map(CommentDetailResponseDto::from)
                .toList();

        List<String> assigneeNicknames = task.getAssignee().stream()
                .filter(Objects::nonNull)
                .map(TaskAssignees::getAssignees)
                .filter(Objects::nonNull)
                .map(User::getNickname)
                .toList();

        return new TaskDetailResponseDto(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getContent(),
                task.getCreatedUser().getId(),
                assigneeNicknames,
                task.getStatus(),
                task.getPriority(),
                tag,
                task.getDueDate(),
                commentDtos
        );
    }
}
