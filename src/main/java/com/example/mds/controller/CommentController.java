package com.example.mds.controller;


import com.example.mds.dto.comment.request.CommentCreateRequest;
import com.example.mds.dto.comment.request.CommentUpdateRequest;
import com.example.mds.entity.Comment;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.service.CommentService;
import com.example.mds.service.MemberService;
import com.example.mds.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Long id,
                                @Valid CommentCreateRequest commentCreateRequest,
                                BindingResult bindingResult, Principal principal){
        Post post = this.postService.getPost(id);
        Member member = this.memberService.getMember(principal.getName());
        if(bindingResult.hasErrors()){
            model.addAttribute("post", post);
            return "communityDetail";
        }
        this.commentService.create(post, commentCreateRequest.getContent(), member);
        return String.format("redirect:/community/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentUpdateRequest commentUpdateRequest, @PathVariable("id") Long id,
                                Principal principal){
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 수정할 권한이 없습니다.");
        }
        commentUpdateRequest.setContent(comment.getContent());
        return "";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentUpdateRequest commentUpdateRequest, BindingResult bindingResult,
                                @PathVariable("id") Long id, Principal principal){
        if (bindingResult.hasErrors()){
            return "";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 수정할 권한이 없습니다.");
        }
        this.commentService.modify(comment, commentUpdateRequest.getContent());
        return String.format("redirect:/community/%s", comment.getPost().getId());
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,  Principal principal) {
        Comment comment = this.commentService.getComment(commentId);
        if (!comment.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 수정할 권한이 없습니다.");
        }
        this.commentService.deleteCommentById(commentId);
        return "redirect:/community/all";
    }

}
