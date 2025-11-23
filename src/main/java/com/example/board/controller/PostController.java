package com.example.board.controller;

import com.example.board.domain.Member;
import com.example.board.domain.Post;
import com.example.board.service.MemberService;
import com.example.board.service.PostService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    public PostController(PostService postService, MemberService memberService) {
        this.postService = postService;
        this.memberService = memberService;
    }

    @GetMapping("/write")
    public String writeForm() {
        return "posts/write";
    }

    public static class PostForm {
        @NotBlank @Size(min = 1, max = 200)
        public String title;
        @NotBlank
        public String content;
    }

    @PostMapping("/write")
    public String write(@ModelAttribute @Validated PostForm form,
                        @AuthenticationPrincipal User user) {
        Member author = memberService.findByUsername(user.getUsername());
        Post post = postService.write(form.title, form.content, author);
        return "redirect:/posts/" + post.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Post post = postService.get(id);
        model.addAttribute("post", post);
        return "posts/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id,
                           @AuthenticationPrincipal User user,
                           Model model) {
        Post post = postService.get(id);
        Member me = memberService.findByUsername(user.getUsername());
        if (!post.getAuthor().getId().equals(me.getId())) {
            model.addAttribute("error", "본인 게시글만 수정할 수 있습니다.");
            return "posts/detail";
        }
        model.addAttribute("post", post);
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute @Validated PostForm form,
                       @AuthenticationPrincipal User user,
                       Model model) {
        try {
            Member me = memberService.findByUsername(user.getUsername());
            postService.edit(id, form.title, form.content, me);
            return "redirect:/posts/" + id;
        } catch (SecurityException e) {
            model.addAttribute("error", e.getMessage());
            Post post = postService.get(id);
            model.addAttribute("post", post);
            return "posts/detail";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal User user,
                         Model model) {
        try {
            Member me = memberService.findByUsername(user.getUsername());
            postService.delete(id, me);
            return "redirect:/";
        } catch (SecurityException e) {
            model.addAttribute("error", e.getMessage());
            Post post = postService.get(id);
            model.addAttribute("post", post);
            return "posts/detail";
        }
    }
}
