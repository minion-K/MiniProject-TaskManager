package org.example.o_lim.service.Impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.o_lim.common.enums.PriorityStatus;
import org.example.o_lim.common.enums.TaskStatus;
import org.example.o_lim.dto.ResponseDto;
import org.example.o_lim.dto.tag.request.TagRequestDto;
import org.example.o_lim.dto.task.request.TaskCreateRequestDto;
import org.example.o_lim.dto.task.request.TaskUpdateRequestDto;
import org.example.o_lim.dto.task.request.TaskUpdateStatusRequestDto;
import org.example.o_lim.dto.task.response.TaskCreateResponseDto;
import org.example.o_lim.dto.task.response.TaskDetailResponseDto;
import org.example.o_lim.dto.task.response.TaskSearchResponseDto;
import org.example.o_lim.entity.*;
import org.example.o_lim.repository.*;
import org.example.o_lim.security.UserPrincipal;
import org.example.o_lim.service.TaskService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final TaskTagRepository taskTagRepository;

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseDto<TaskCreateResponseDto> createTask(
            Long projectId, UserPrincipal principal, TaskCreateRequestDto request
            ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        boolean isTask = taskRepository.existsByProjectIdAndTitle(project.getId(), request.title());

        if (isTask) {
            throw new IllegalArgumentException("해당 프로젝트 내 직무가 중복됩니다.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

        Task task = Task.create(
                project,
                request.title(),
                request.content(),
                user,
                request.status(),
                request.priority(),
                request.dueDate()
        );

        List<Long> assigneeIds = request.assigneeIds() != null ? request.assigneeIds() : Collections.emptyList();
        List<User> assignees = userRepository.findAllById(assigneeIds);

        for (User assignee : assignees) {
            task.addAssignee(assignee);
        }
        taskRepository.save(task);
        taskTagRepository.flush();

        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(request.tagIds());

            for (Tag tag : tags) {
                if (!tag.getProject().getId().equals(project.getId())) {
                    throw new EntityNotFoundException("해당 프로젝트의 태그가 존재하지 않습니다." + tag.getId());
                }

                TaskTag taskTag = TaskTag.create(task, tag);
                taskTagRepository.save(taskTag);
                task.addTaskTag(taskTag);
            }
            taskTagRepository.flush();
        }

        if(request.newTags() != null && !request.newTags().isEmpty()) {
            for(TagRequestDto newTagDto: request.newTags()) {

                String name = newTagDto.name() != null ? newTagDto.name().trim() : "";
                String color = newTagDto.color() != null ? newTagDto.color().trim() : "";

                if(name.isEmpty() || color.isEmpty()) continue;

                if(tagRepository.existsByNameAndProjectId(newTagDto.name(), project.getId())) {
                    throw new IllegalArgumentException("이미 존재하는 태그명입니다." + newTagDto.name());
                }

                if(tagRepository.existsByColorAndProjectId(newTagDto.color(), project.getId())) {
                    throw new IllegalArgumentException("이미 존재하는 색상입니다." + newTagDto.color());
                }

                Tag newTag = Tag.create(project, newTagDto.name(), newTagDto.color());
                tagRepository.save(newTag);

                TaskTag taskTag = TaskTag.create(task, newTag);
                taskTagRepository.save(taskTag);
                task.addTaskTag(taskTag);
            }
            taskTagRepository.flush();
        }

        Task saveTask = taskRepository.findByIdWithTaskTags(task.getId())
                .orElseThrow(() -> new EntityNotFoundException("저장된 직무를 찾을 수 없습니다."));

        return ResponseDto.setSuccess("SUCCESS", TaskCreateResponseDto.from(saveTask));

    }

    @Override
    public ResponseDto<List<TaskSearchResponseDto>> getAllTasks(Long projectId) {
        List<Task> tasks = taskRepository.findAllTaskById(projectId);

        List<TaskSearchResponseDto> result = tasks.stream()
                .map(TaskSearchResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", result);
    }

    @Override
    public ResponseDto<TaskDetailResponseDto> getTaskById(Long projectId, Long taskId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getProject().getId().equals(project.getId()))
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트 내에 존재하지 않는 직무입니다."));

        return ResponseDto.setSuccess("SUCCESS", TaskDetailResponseDto.from(task));
    }

    @Override
    public ResponseDto<List<TaskDetailResponseDto>> searchTasks(
            Long projectId, Long createUserId, TaskStatus status, PriorityStatus priority,
            LocalDate dueDate
            ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        List<Task> tasks = taskRepository.searchTasks(projectId, createUserId, status, priority, dueDate);

        List<TaskDetailResponseDto> dto = tasks.stream()
                .map(TaskDetailResponseDto::from)
                .toList();

        return ResponseDto.setSuccess("SUCCESS", dto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseDto<TaskDetailResponseDto> updateTask(
            Long projectId, Long taskId, UserPrincipal principal, TaskUpdateRequestDto request
            ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 직무가 존재하지 않습니다."));

        if (request.title() == null
                && request.content() == null
                && request.status() == null
                && request.priority() == null
                && request.dueDate() == null
                && (request.tagId() == null || request.tagId().isEmpty())
                && (request.newTags() == null || request.newTags().isEmpty())
                && (request.addAssigneeIds() == null || request.addAssigneeIds().isEmpty())
                && (request.removeAssigneeIds() == null || request.removeAssigneeIds().isEmpty())
        ) {
                throw new IllegalArgumentException("수정할 정보를 입력해주세요.");
        }

        String newTitle = (request.title() != null && !request.title().isBlank()) ? request.title() : null;
        String newContent = (request.content() != null && !request.content().isBlank()) ? request.content() : null;
        Set<Long> addAssignees = request.addAssigneeIds() == null ? Collections.emptySet() : new HashSet<>(request.addAssigneeIds());
        Set<Long> removeAssignees = request.removeAssigneeIds() == null ? Collections.emptySet() : new HashSet<>(request.removeAssigneeIds());

        boolean changedAssignee = !addAssignees.isEmpty() || !removeAssignees.isEmpty();

        TaskStatus newStatus = task.getStatus();
        String statusStr = request.status();
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                newStatus = TaskStatus.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 status 값입니다.");
            }
        }

        PriorityStatus newPriority = task.getPriority();
        String priorityStr = request.priority();
        if (priorityStr != null && !priorityStr.isBlank()) {
            try {
                newPriority = PriorityStatus.valueOf(priorityStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 priority 값입니다.");
            }
        }

        String dueDateStr = request.dueDate();
        LocalDate newDueDate = null;
        boolean changedDueDate = false;
        if (dueDateStr == null) {
            if (task.getDueDate() != null) { // null로 초기화(제거) 요청
                newDueDate = null;
                changedDueDate = true;
            }
        } else if (dueDateStr.isBlank()) {
            newDueDate = task.getDueDate();
        } else {
            try {
                LocalDate parsedDate = LocalDate.parse(dueDateStr);
                if (!parsedDate.equals(task.getDueDate())) {
                    newDueDate = parsedDate;
                    changedDueDate = true;
                } else {
                    newDueDate = task.getDueDate();
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("dueDate 형식이 올바르지 않습니다.");
            }
        }

        // ====== 태그: "교체" -> "추가/삭제" 방식으로 변경 ======
        // 현재 태그
        Set<Long> existingTagIds = task.getTaskTags().stream()
                .map(tt -> tt.getTag().getId())
                .collect(java.util.stream.Collectors.toSet());

        // 요청으로 들어온 추가/삭제 목록
        Set<Long> toAdd = request.addTagIds() == null ? new java.util.HashSet<>() : new java.util.HashSet<>(request.addTagIds());
        Set<Long> toRemove = request.removeTagIds() == null ? new java.util.HashSet<>() : new java.util.HashSet<>(request.removeTagIds());

        // 새 태그 생성(newTags): 이름/색상 유효성 + 중복 검사 후 생성, 생성된 ID를 toAdd에 합치기
        if (request.newTags() != null && !request.newTags().isEmpty()) {
            for (TagRequestDto newTagDto : request.newTags()) {
                String name = newTagDto.name() != null ? newTagDto.name().trim() : "";
                String color = newTagDto.color() != null ? newTagDto.color().trim() : "";
                if (name.isEmpty() || color.isEmpty()) continue;

                if (tagRepository.existsByNameAndProjectId(name, project.getId())) {
                    throw new IllegalArgumentException("이미 존재하는 태그명입니다.");
                }
                if (tagRepository.existsByColorAndProjectId(color, project.getId())) {
                    throw new IllegalArgumentException("이미 존재하는 색상입니다.");
                }

                Tag newTag = Tag.create(project, name, color);
                tagRepository.save(newTag);
                toAdd.add(newTag.getId());
            }
        }

        // 동일 ID가 add/remove 양쪽에 들어온 경우 제거
        if (!toAdd.isEmpty() && !toRemove.isEmpty()) {
            Set<Long> intersection = new java.util.HashSet<>(toAdd);
            intersection.retainAll(toRemove);
            if (!intersection.isEmpty()) {
                // 일반적으로는 충돌을 에러로 처리하거나, 여기서는 "무시" 정책 선택
                toAdd.removeAll(intersection);
                toRemove.removeAll(intersection);
            }
        }

        // 유효성: 프로젝트에 속한 태그인지 확인
        for (Long tagId : toAdd) {
            boolean exists = tagRepository.existsByIdAndProjectId(tagId, project.getId());
            if (!exists) throw new EntityNotFoundException("프로젝트에 속하지 않는 태그(ID=" + tagId + ") 입니다.");
        }
        for (Long tagId : toRemove) {
            // 삭제 대상이 현재 달려있지 않다면 그냥 무시(혹은 에러 처리도 가능)
            // 여기서는 무시 정책 선택
        }

        // 변경사항 판단
        boolean changedTitle = newTitle != null && !java.util.Objects.equals(task.getTitle(), newTitle);
        boolean changedContent = newContent != null && !java.util.Objects.equals(task.getContent(), newContent);
        boolean changedStatus = !task.getStatus().equals(newStatus);
        boolean changedPriority = !task.getPriority().equals(newPriority);
        boolean changedTag = (!toAdd.isEmpty() || !toRemove.isEmpty());

        if (!changedTitle && !changedContent && !changedAssignee && !changedStatus
                && !changedPriority && !changedTag && !changedDueDate) {
            throw new IllegalArgumentException("변경된 정보가 없습니다.");
        }

        // ====== 실제 반영 ======
        if (changedTitle) task.setTitle(newTitle);
        if (changedContent) task.setContent(newContent);
        if (changedAssignee) {
            if (!removeAssignees.isEmpty()) {

                List<Long> removeAssigneeIds = new ArrayList<>(removeAssignees);

                List<User> usersToRemove = userRepository.findAllById(removeAssigneeIds);

                Set<Long> foundIds = usersToRemove.stream()
                            .map(User::getId)
                            .collect(Collectors.toSet());

                List<Long> notFoundIds = removeAssigneeIds.stream()
                            .filter(id -> !foundIds.contains(id))
                            .collect(Collectors.toList());

                if(!notFoundIds.isEmpty()) {
                    throw new EntityNotFoundException("존재하지 않는 유저가 포함되어 있습니다. ID: " +notFoundIds);
                }

                Set<Long> currentAssigneeIds = task.getAssignee().stream()
                        .map(TaskAssignees::getAssignees)
                        .map(User::getId)
                        .collect(Collectors.toSet());

                List<Long> notAssignedIds = removeAssignees.stream()
                        .filter(id -> !currentAssigneeIds.contains(id))
                        .collect(Collectors.toList());

                    if (!notAssignedIds.isEmpty()) {
                        throw new EntityNotFoundException("해당 작업에 할당되지 않은 담당자(ID= " + notAssignedIds + ")가 포함되어 있습니다.");
                    }

                usersToRemove.forEach(task::removeAssignee);
            }

           if (!addAssignees.isEmpty()) {
                List<User> usersToAdd = userRepository.findAllById(addAssignees);
               if (usersToAdd.size() != addAssignees.size()) {

                   Set<Long> foundIds = usersToAdd.stream()
                           .map(User::getId)
                           .collect(Collectors.toSet());

                   List<Long> notFoundIds = addAssignees.stream()
                           .filter(id -> !foundIds.contains(id))
                           .collect(Collectors.toList());
                   throw new EntityNotFoundException("존재하지 않는 유저가 포함되어 있습니다. ID: " + notFoundIds);
               }
               usersToAdd.forEach(task::addAssignee);
            }
            taskRepository.flush();
        }

        if (changedStatus) task.setStatus(newStatus);
        if (changedPriority) task.setPriority(newPriority);
        if (changedTag) {
            // 추가
            for (Long tagId : toAdd) {
                if (!existingTagIds.contains(tagId)) {
                    Tag tag = tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException("태그(ID=" + tagId + ")를 찾을 수 없습니다."));
                    task.addTag(tag); // <-- Task 엔티티에 addTag(Tag) 유틸 메서드가 있다고 가정
                    existingTagIds.add(tagId);
                }
            }
            // 삭제
            if (!toRemove.isEmpty()) {
                // TaskTag 컬렉션에서 제거
                task.getTaskTags().removeIf(tt -> toRemove.contains(tt.getTag().getId()));
            }
            taskRepository.flush(); // FK/조인테이블 반영 보장
        }

        if (changedDueDate) task.setDueDate(newDueDate);

        return ResponseDto.setSuccess("SUCCESS", TaskDetailResponseDto.from(task));
    }

    @Override
    @Transactional
    @PreAuthorize("@authz.isChange(#taskId, authentication)")
    public ResponseDto<TaskDetailResponseDto> updateTaskStatus(
            Long projectId, Long taskId, UserPrincipal principal, TaskUpdateStatusRequestDto request
            ) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        Task task = taskRepository.findByIdWithAssignees(taskId)
                .orElseThrow(() -> new EntityNotFoundException("해당 직무가 존재하지 않습니다."));

        task.updateStatus(request.status());

        return ResponseDto.setSuccess("SUCCESS", TaskDetailResponseDto.from(task));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseDto<Void> deleteTask(Long projectId, Long taskId, UserPrincipal principal) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 프로젝트가 존재하지 않습니다."));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 직무가 존재하지 않습니다."));

        taskRepository.delete(task);

        return ResponseDto.setSuccess("SUCCESS",null);
    }
}


