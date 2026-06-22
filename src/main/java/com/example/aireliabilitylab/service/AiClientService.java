package com.example.aireliabilitylab.service;

import org.springframework.stereotype.Service;

import com.example.aireliabilitylab.dto.AiResponseDto;

@Service
public class AiClientService {
    
        public AiResponseDto analyzeSentiment(String text) {

            return new AiResponseDto("posetive", 90);
        }
    
}
