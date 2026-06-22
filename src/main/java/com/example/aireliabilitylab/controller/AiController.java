package com.example.aireliabilitylab.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.aireliabilitylab.dto.AiRequestDto;
import com.example.aireliabilitylab.dto.AiResponseDto;
import com.example.aireliabilitylab.service.AiClientService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiClientService aiClientService;

    public AiController(AiClientService aiClientService) {
        this.aiClientService = aiClientService;
    }

    @PostMapping("/sentiment")
    public AiResponseDto analyzeSentiment(@Valid @RequestBody AiRequestDto request) {
        return aiClientService.analyzeSentiment(request.getText());
    }
}