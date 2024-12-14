package com.example.dreambackend.controller;

import com.example.dreambackend.entity.Post;
import com.example.dreambackend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(Authentication authentication, @RequestBody PostRequest request) {
        Long userId = Long.valueOf(authentication.getName());
        Post post = postService.createPost(userId, request.getTitle(), request.getContent());
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts().stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(new PostResponse(post));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(Authentication authentication, @PathVariable Long id) {
        Long userId = Long.valueOf(authentication.getName());
        postService.deletePost(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody PostRequest request
    ) {
        Long userId = Long.valueOf(authentication.getName());
        Post updatedPost = postService.updatePost(userId, id, request.getTitle(), request.getContent());
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/myposts")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        Long userId = extractUserIdFromAuthentication(authentication);
        List<PostResponse> posts = postService.getPostsByUserId(userId).stream()
                .map(PostResponse::new)
                .toList();
        return ResponseEntity.ok(posts);
    }

    // 좋아요 증가 API
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Integer>> likePost(Authentication authentication, @PathVariable Long id) {
        Long userId = Long.valueOf(authentication.getName());
        Post post = postService.incrementLikeCount(userId, id);

        Map<String, Integer> response = new HashMap<>();
        response.put("likeCount", post.getLikeCount());
        return ResponseEntity.ok(response);
    }





    private Long extractUserIdFromAuthentication(Authentication authentication) {
        return Long.valueOf(authentication.getName());
    }
}

class PostRequest {
    private String title;
    private String content;

    public PostRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class PostResponse {
    private Long id; // 게시글 ID
    private String content;
    private String title;
    private String username;
    private int likeCount;

    // 생성자 수정
    public PostResponse(Post post) {
        this.id = post.getId(); // 게시글 ID 추가
        this.content = post.getContent();
        this.title = post.getTitle();
        this.username = post.getUser().getUsername();
        this.likeCount = post.getLikeCount();
    }

    // Getter 추가
    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public int getLikeCount() {
        return likeCount;
    }
}
