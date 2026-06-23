package com.example.aireliabilitylab.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import jakarta.annotation.PostConstruct;

@Service
public class AiClientService {
    
    @Value("${ai.api.key}")
    private String apiKey;

    private final RestClient restClient;

    private static final String SYSTEM_PROMPT = 
    "You are a helpful assistant that analyzes the sentiment of the given text. Respond with a JSON object containing the sentiment (positive, negative, or neutral) and a brief explanation.";

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

    public String analyzeSentiment(String text) {

        Map<String, Object> requestBody = Map.of(

                "model", "gpt-4o-mini","temperature", 0.1, "messages", List.of(Map.of("role", "system","content", SYSTEM_PROMPT),
                        Map.of("role", "user","content", text)
                )
        );
        return restClient.post()

        .uri("/chat/completions")
        .header("Authorization", "Bearer " + apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .body(requestBody)
        .retrieve()
        .body(String.class);

}

        
    









    




    
}
