package org.example.o_lim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.o_lim.entity.base.BaseTimeEntity;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    태스크 외래키
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comments_task_id"))
    private Task task;

//    작성자명
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_comments_author_id"))
    private User author;

//    댓글 내용
    @Column(name = "content", nullable = false)
    private String content;

//    생성자
    public Comment(Task task, User author, String content) {
        this.task = task;
        this.author = author;
        this.content = content;
    }

    public static Comment create(Task task, User author, String content) {
        return new Comment(task,author, content);
    }
}
