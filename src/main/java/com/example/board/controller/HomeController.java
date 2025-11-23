package com.example.board.controller;

import com.example.board.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.board.domain.Post;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Post> posts = postService.list(page);
        model.addAttribute("posts", posts);
        model.addAttribute("currentPage", page);
        return "index";
    }
}
