package com.example.dreambackend.service;

import com.example.dreambackend.entity.Post;
import com.example.dreambackend.entity.AppUser;
import com.example.dreambackend.repository.PostRepository;
import com.example.dreambackend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 좋아요 증가 메서드
    public Post incrementLikeCount(Long userId, Long postId) {
        // 게시글을 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 게시글 ID: " + postId));

        // 좋아요 수 증가
        post.setLikeCount(post.getLikeCount() + 1);
        return postRepository.save(post);
    }





    // **게시글 생성**
    public Post createPost(Long userId, String title, String content) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID: " + userId));

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUser(user); // 작성자 설정
        post.setLikeCount(0); // 초기 좋아요 수는 0으로 설정
        return postRepository.save(post);
    }

    // **모든 게시글 조회**
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // **ID로 게시글 조회**
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + id));
    }

    // **특정 유저의 게시글 조회**
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // **게시글 수정**
    public Post updatePost(Long userId, Long postId, String title, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId)
        );

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(title);
        post.setContent(content);
        post.setUpdatedAt(new Date());
        return postRepository.save(post);
    }

    // **게시글 삭제**
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId)
        );

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalStateException("해당 게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    // **게시글 좋아요 추가**
    public Post likePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId)
        );

        post.setLikeCount(post.getLikeCount() + 1); // 좋아요 수 증가
        return postRepository.save(post);
    }
}
