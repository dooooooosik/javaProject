package com.example.dreambackend.filter;

import com.example.dreambackend.util.JwtUtil;
import com.example.dreambackend.entity.AppUser;
import com.example.dreambackend.repository.UserRepository;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Authorization 헤더가 없거나 Bearer 형식이 아님");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        try {
            // JWT에서 이메일(또는 사용자 이름) 추출
            final String email = jwtUtil.extractUsername(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                AppUser user = userRepository.findByEmail(email);

                if (user != null && jwtUtil.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user.getId(), null, user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("JWT 인증 성공: 사용자 " + email);
                } else {
                    System.out.println("JWT 인증 실패: 사용자 정보 없음 또는 토큰 유효하지 않음");
                }

            }
        } catch (MalformedJwtException e) {
            System.out.println("잘못된 JWT 형식: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT 처리 중 오류 발생: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
