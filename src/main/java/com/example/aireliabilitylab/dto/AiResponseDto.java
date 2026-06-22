package com.example.aireliabilitylab.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AiResponseDto {


    @NotNull
    @Pattern(regexp = "positive|negative|neutral|unknown")
    private String sentiment;


    @Min(0)
    @Max(100)
    private int score;

    public AiResponseDto() {
    }

    public AiResponseDto(String sentiment, int score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    



    
}
