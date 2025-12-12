package org.example.o_lim.repository;

import org.example.o_lim.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    public interface CommentWithCreatedAtProjection {
        String getAuthorName();
        String getContent();
        LocalDateTime getCreatedAt();
    }

    @Query(value = """
        SELECT 
            u.name as AuthorName,
            c.content as Content,
            c.created_at as CreatedAt
        FROM comments c
            LEFT JOIN users u
            ON c.author_id = u.id
        WHERE task_id = :taskId
        ORDER BY 
            c.created_at DESC
    """, nativeQuery = true)
    List<CommentWithCreatedAtProjection> getCommentsByCreatedAtDesc(@Param("taskId") Long taskId);
}
