package com.example.aireliabilitylab.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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





    




    
}
