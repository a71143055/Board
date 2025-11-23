package com.example.board.service;

import com.example.board.domain.Member;
import com.example.board.domain.Post;
import com.example.board.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private static final int PAGE_SIZE = 10;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Page<Post> list(int page) {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(Math.max(page, 0), PAGE_SIZE));
    }

    public Post write(String title, String content, Member author) {
        Post post = new Post(title, content, author);
        return postRepository.save(post);
    }

    public Post get(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    }

    public void edit(Long id, String title, String content, Member editor) {
        Post post = get(id);
        if (!post.getAuthor().getId().equals(editor.getId())) {
            throw new SecurityException("본인 게시글만 수정할 수 있습니다.");
        }
        post.setTitle(title);
        post.setContent(content);
    }

    public void delete(Long id, Member requester) {
        Post post = get(id);
        if (!post.getAuthor().getId().equals(requester.getId())) {
            throw new SecurityException("본인 게시글만 삭제할 수 있습니다.");
        }
        postRepository.delete(post);
    }
}
