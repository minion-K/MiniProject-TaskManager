package org.example.o_lim.security.util;

import lombok.RequiredArgsConstructor;
import org.example.o_lim.entity.Comment;
import org.example.o_lim.repository.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("commentz")
public class CommentChecker {
    private final CommentRepository commentRepository;

    public boolean isCommentAuthor(Long commentId, Authentication principal) {
        if (commentId == null || principal == null) return false;

        String loginId = principal.getName();

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) return false;

        return comment.getAuthor().getLoginId().equals(loginId);
    }
}
