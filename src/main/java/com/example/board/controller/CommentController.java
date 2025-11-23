package com.example.board.controller;

import com.example.board.domain.Member;
import com.example.board.service.CommentService;
import com.example.board.service.MemberService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final MemberService memberService;

    public CommentController(CommentService commentService, MemberService memberService) {
        this.commentService = commentService;
        this.memberService = memberService;
    }

    public static class CommentForm {
        @NotBlank @Size(min = 1, max = 1000)
        public String content;
    }

    @PostMapping("/{postId}/write")
    public String write(@PathVariable Long postId,
                        @ModelAttribute @Validated CommentForm form,
                        @AuthenticationPrincipal User user) {
        Member author = memberService.findByUsername(user.getUsername());
        commentService.write(postId, form.content, author);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{commentId}/delete")
    public String delete(@PathVariable Long commentId,
                         @RequestParam Long postId,
                         @AuthenticationPrincipal User user) {
        Member me = memberService.findByUsername(user.getUsername());
        commentService.delete(commentId, me);
        return "redirect:/posts/" + postId;
    }
}
