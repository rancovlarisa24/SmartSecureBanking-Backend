package com.lrs.SSB.controller;

import com.lrs.SSB.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final OpenAiService openAiService;

    public AiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askAi(@RequestBody UserQuestion userQuestion) {
        String userInput = userQuestion.getQuestion();
        String response = openAiService.askChatGPT(userInput);
        return ResponseEntity.ok(response);
    }
}

