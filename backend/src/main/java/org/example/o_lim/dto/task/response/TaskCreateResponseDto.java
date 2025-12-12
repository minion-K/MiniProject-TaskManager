package org.example.o_lim.dto.task.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.tag.response.TagResponseDto;
import org.example.o_lim.entity.Task;
import org.example.o_lim.entity.TaskAssignees;
import org.example.o_lim.entity.TaskTag;
import org.example.o_lim.entity.User;
import java.time.LocalDate;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TaskCreateResponseDto(
        Long projectId,
        String title,
        Long createUserId,
        String content,
        List<String> assignees,
        List<TagResponseDto> tags,
        TaskStatus status,
        PriorityStatus priority,
        LocalDate dueDate
){
    public static  TaskCreateResponseDto from(Task task){
        if(task == null) return null;

        List<String> assignees = task.getAssignee().stream()
                .filter(Objects::nonNull)
                .map(TaskAssignees::getAssignees)
                .filter(Objects::nonNull)
                .map(User::getNickname)
                .toList();

        List<TagResponseDto> tag = new ArrayList<>();
        Set<Long> seenTagIds = new HashSet<>();

        for(TaskTag taskTag: task.getTaskTags()) {
            if(taskTag == null || taskTag.getTag() == null) continue;

            Long tagId = taskTag.getTag().getId();
            if(seenTagIds.contains(tagId)) continue;

            seenTagIds.add(tagId);
            tag.add(TagResponseDto.from(taskTag.getTag()));
        }

        return new TaskCreateResponseDto(
                task.getProject().getId(),
                task.getTitle(),
                task.getCreatedUser().getId(),
                task.getContent(),
                assignees,
                tag,
                task.getStatus(),
                task.getPriority(),
                task.getDueDate()
        );
    }
}
