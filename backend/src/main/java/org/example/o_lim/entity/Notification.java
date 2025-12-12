package org.example.o_lim.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.o_lim.entity.base.BaseTimeEntity;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("공지사항 제목")
    @Column(nullable = false, length = 50)
    private String title;

    @Comment("공지사항 내용")
    @Lob
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String content;

    @Comment("프로젝트")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id",
            foreignKey = @ForeignKey(name = "fk_notifications_project_id"))
    private Project project;

    private Notification(String title, String content, Project project) {
        this.title = title;
        this.content = content;
        this.project = project;
    }

    public static Notification create(String title, String content, Project project) {
        return new Notification(title, content, project);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;

    }
}