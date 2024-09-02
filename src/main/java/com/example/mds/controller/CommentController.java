package com.example.mds.controller;


import com.example.mds.dto.comment.request.CommentCreateRequest;
import com.example.mds.dto.comment.request.CommentUpdateRequest;
import com.example.mds.entity.Comment;
import com.example.mds.entity.Member;
import com.example.mds.entity.Post;
import com.example.mds.service.CommentService;
import com.example.mds.service.MemberService;
import com.example.mds.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "댓글 컨트롤러", description = "Comment Controller")
@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
    private final PostService postService;
    private final CommentService commentService;
    private final MemberService memberService;

    @Operation(summary = "댓글 생성")
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

    @Operation(summary = "댓글 삭제")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId,  Principal principal) {
        Comment comment = this.commentService.getComment(commentId);
        if (!comment.getAuthor().getEmail().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 삭제할 권한이 없습니다.");
        }
        this.commentService.deleteCommentById(commentId);
        return "redirect:/community/all";
    }

}
