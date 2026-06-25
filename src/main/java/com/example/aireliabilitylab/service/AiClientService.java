package com.example.aireliabilitylab.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.aireliabilitylab.dto.AiResponseDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Service
public class AiClientService {
    
    @Value("${openai.api.key}")
    private String apiKey;

    private final RestClient restClient;

    private static final String SYSTEM_PROMPT = """
    You are a sentiment analysis engine. Return ONLY valid JSON. Do not use markdown.Do not include explanations.
    Schema:
    {
    
      "sentiment": "positive|negative|neutral",
    
      "score": 0-100
    
    }
    """;

    public AiClientService(){
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
            throw new IllegalStateException("API key is not set. Please provide a valid API key.");
        }
    }

    public AiResponseDto analyzeSentiment(String text) {

        Map<String, Object> requestBody = Map.of(

                "model", "gpt-4o-mini","temperature", 0.1, "messages", List.of(Map.of("role", "system","content", SYSTEM_PROMPT),
                        Map.of("role", "user","content", text)
                )
        );

        String openAiResponse = restClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);
        try {
            JsonNode root = new ObjectMapper().readTree(openAiResponse);

            String sentiment = root.path("choices").get(0).path("message").path("content").asText();

            return new ObjectMapper().readValue(sentiment, AiResponseDto.class);
            
        } catch (Exception e) {
            return new AiResponseDto("unknown", 0);
        }
                        



}

        
    









    




    
}
