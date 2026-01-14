package com.kgisl.gemini3idp;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {
    private ChatClient chatClient;

    public AiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String getResponseFromAI(String prompt) {
        // Logic to call ai:
        return chatClient.prompt(prompt).call().content();

    }
}