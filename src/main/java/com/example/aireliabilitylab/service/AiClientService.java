package com.example.aireliabilitylab.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.aireliabilitylab.dto.AiResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Service
public class AiClientService {
    
    @Value("${openai.api.key}")
    private String apiKey;

    private final RestClient restClient;

    private final Validator validator;

    private static final String SYSTEM_PROMPT = """
You are a sentiment analysis engine.

Return ONLY this JSON structure:
{
  "sentiment": "positive",
  "score": 95
}

Rules:
- sentiment must be one of: positive, negative, neutral
- score must be a number between 0 and 100
- do not include explanation
- do not include markdown
- do not include any extra fields
""";

    public AiClientService(Validator validator) {
        this.validator = validator;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); // 2 seconds
        factory.setReadTimeout(8000); // 8 seconds

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.openai.com/v1")
                .build();
    }
    
    @PostConstruct
    public void validateApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("CRITICAL: API key is missing.");
        }
    }

    public AiResponseDto analyzeSentiment(String text) {

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.1,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", text)
                )
        );

        int maxRetries = 3;
        long delay = 1000; // initial delay in milliseconds
    
        String openAiResponse;
    
        openAiResponse = null;

for (int attempt = 1; attempt <= maxRetries; attempt++) {
    try {
        openAiResponse = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        break;

    } catch (HttpClientErrorException.TooManyRequests e) {
        System.out.println("429 Too Many Requests. Attempt " + attempt + " of " + maxRetries);
        System.out.println("Waiting " + delay + " ms before retrying...");

        try {
            Thread.sleep(delay);
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            return new AiResponseDto("unknown", 0);
        }

        delay *= 2;

    } catch (Exception e) {
        e.printStackTrace();
        return new AiResponseDto("unknown", 0);
    }
}

if (openAiResponse == null) {
    return new AiResponseDto("unknown", 0);
}
    
        try {
            ObjectMapper objectMapper = new ObjectMapper();
    
            JsonNode root = objectMapper.readTree(openAiResponse);
    
            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
    
            System.out.println("AI content: " + content);
    
            AiResponseDto responseDto = objectMapper.readValue(content, AiResponseDto.class);

            Set<ConstraintViolation<AiResponseDto>> violations = validator.validate(responseDto);

            if (!violations.isEmpty()) {
                System.out.println("Validation errors: " + violations);
                return new AiResponseDto("unknown", 0);
            }

            return responseDto;
        } catch (Exception e) {
            e.printStackTrace();
            return new AiResponseDto("unknown", 0);
        }
    }
  
                

    




    
}
