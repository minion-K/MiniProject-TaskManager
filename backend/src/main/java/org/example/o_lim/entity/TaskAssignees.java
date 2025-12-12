package org.example.o_lim.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "task_assignees",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_task_assignees_task_id_assignee_id",
                        columnNames = {"task_id", "assignee_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskAssignees {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id",
            foreignKey = @ForeignKey(name = "fk_task_assignees_task_id"),
            nullable = false
    )
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignee_id",
            foreignKey = @ForeignKey(name = "fk_task_assignees_assignee_id")
    )
    private User assignees;

    // Task 관계
    public TaskAssignees(Task task, User assignee) {
        this.task = task;
        this.assignees = assignee;
    }

    public User getAssignees() {
        return assignees;
    }

    public void setTask(Task task) {
        if (this.task != null) {
            this.task.getAssignee().remove(this);
        }
        this.task = task;
        if (task != null) {
            task.getAssignee().add(this);
        }
    }
}
