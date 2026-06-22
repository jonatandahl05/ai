package com.example.aireliabilitylab.dto;

public class AiRequestDto {

    private String text;

    public AiRequestDto() {
    }

    public AiRequestDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
