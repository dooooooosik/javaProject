package com.example.dreambackend.controller;
import java.util.Map;

import com.example.dreambackend.service.UserService;
import com.example.dreambackend.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("nickname"); // 프론트엔드의 'nickname'을 username으로 매핑
        String email = requestData.get("email");
        String password = requestData.get("password");

        try {
            userService.registerUser(username, email, password);
            return ResponseEntity.status(201).body(Map.of("success", true, "message", "회원가입이 완료되었습니다!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        // 사용자 인증
        if (userService.loginUser(email, password)) {
            String token = jwtUtil.generateToken(email); // JWT 생성
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "로그인 성공!",
                    "token", token // 프론트엔드에서 사용할 토큰
            ));
        }

        // 인증 실패
        return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "이메일 또는 비밀번호가 잘못되었습니다."
        ));
    }
}
