package com.example.dreambackend.controller;

import com.example.dreambackend.service.GPTService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dream")
@CrossOrigin(origins = "*")

public class DreamController {
    private final GPTService gptService;

    public DreamController(GPTService gptService) {
        this.gptService = gptService;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, String>> searchDream(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            Map<String, String> errorResponse = Map.of("error", "키워드를 입력해주세요.");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            String interpretation = gptService.getDreamInterpretation(keyword.trim());
            Map<String, String> response = Map.of("result", interpretation);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = Map.of("error", "서버 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

}
