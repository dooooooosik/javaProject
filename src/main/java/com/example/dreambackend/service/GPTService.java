package com.example.dreambackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class GPTService {
    private static final Logger logger = LoggerFactory.getLogger(GPTService.class);
    private final RestTemplate restTemplate;

    // OpenAI API 키 주입
    @Value("${openai.api.key}")
    private String apiKey;

    // 캐시: keyword -> 해석 결과
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public GPTService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getDreamInterpretation(String keyword) {
        // 캐시에서 결과 조회
        if (cache.containsKey(keyword)) {
            logger.info("캐시에서 결과 반환: {}", keyword);
            return cache.get(keyword);
        }

        // OpenAI API URL
        String gptApiUrl = "https://api.openai.com/v1/chat/completions";

        // 요청 본문
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a dream interpretation expert."),
                        Map.of("role", "user", "content", "꿈 해몽 키워드: " + keyword)
                ),
                "max_tokens", 500
        );

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // 재시도 로직 (최대 3회)
        int retryCount = 3;
        for (int i = 0; i < retryCount; i++) {
            try {
                ResponseEntity<Map> response = restTemplate.postForEntity(gptApiUrl, request, Map.class);

                // 2xx 응답 처리
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                        if (message != null) {
                            String result = (String) message.get("content");
                            cache.put(keyword, result); // 결과를 캐시에 저장
                            return result;
                        }
                    }
                    return "GPT 응답에서 결과를 찾을 수 없습니다.";
                }
                // 429 Too Many Requests 처리
                else if (response.getStatusCode().value() == 429) {
                    Map<String, Object> errorBody = response.getBody();
                    String errorMessage = (String) ((Map<String, Object>) errorBody.get("error")).get("message");
                    logger.warn("429 Too Many Requests - {}", errorMessage);
                    TimeUnit.SECONDS.sleep(5); // 5초 대기 후 재시도
                }
                // 기타 에러 처리
                else {
                    logger.error("GPT API 호출 실패: {}", response.getStatusCode());
                }
            } catch (Exception e) {
                logger.error("GPT 호출 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        return "해몽 결과를 가져오는 데 실패했습니다. 관리자에게 문의하세요.";
    }
}
