package com.example.dreambackend.repository;

import com.example.dreambackend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 🔥 기존의 findByAuthorId를 findByUserId로 변경
    List<Post> findByUserId(Long userId);
}
