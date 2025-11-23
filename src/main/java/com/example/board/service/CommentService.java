package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.domain.Member;
import com.example.board.domain.Post;
import com.example.board.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentService(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    public List<Comment> listByPost(Long postId) {
        Post post = postService.get(postId);
        return commentRepository.findByPostOrderByCreatedAtAsc(post);
    }

    public void write(Long postId, String content, Member author) {
        Post post = postService.get(postId);
        Comment comment = new Comment(content, author, post);
        commentRepository.save(comment);
    }

    public void delete(Long commentId, Member requester) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        if (!comment.getAuthor().getId().equals(requester.getId())) {
            throw new SecurityException("본인 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }
}
