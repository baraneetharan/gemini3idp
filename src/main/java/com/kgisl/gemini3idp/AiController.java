package com.kgisl.gemini3idp;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ai")
public class AiController {

    private ChatClient chatClient;

    public AiController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Autowired
    private GoogleGenAiChatModel chatModel;

    @GetMapping("/clinical")
    public String chat() {
        var pdfData = new ClassPathResource("/Clinical 20251223_1.pdf");

        var userMessage = UserMessage.builder()
                .text("You are a very professional document summarization specialist. Please summarize the given document.")
                .media(List.of(new Media(new MimeType("application", "pdf"), pdfData)))
                .build();

        var response = this.chatModel.call(new Prompt(List.of(userMessage)));
        System.out.println(response.getResult().getOutput().getText());
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/hospital1")
    public String hospital1chat() {
        var pdfData = new ClassPathResource("/Hospital 20251223_1.pdf");

        var userMessage = UserMessage.builder()
                .text("You are a very professional document summarization specialist. Please summarize the given document.")
                .media(List.of(new Media(new MimeType("application", "pdf"), pdfData)))
                .build();

        var response = this.chatModel.call(new Prompt(List.of(userMessage)));
        System.out.println(response.getResult().getOutput().getText());
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/hospital2")
    public String hospital2chat() {
        var pdfData = new ClassPathResource("/Hospital 20251223_2.pdf");

        var userMessage = UserMessage.builder()
                .text("You are a very professional document summarization specialist. Please summarize the given document.")
                .media(List.of(new Media(new MimeType("application", "pdf"), pdfData)))
                .build();

        var response = this.chatModel.call(new Prompt(List.of(userMessage)));
        System.out.println(response.getResult().getOutput().getText());
        return response.getResult().getOutput().getText();
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "message", required = false) String message) {
        
        Map<String, String> response = new HashMap<>();
        
        try {
            // Create a temporary file to store the uploaded content
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
            file.transferTo(tempFile);
            tempFile.deleteOnExit(); // Ensure the file is deleted when the JVM exits
            
            // Create a resource from the temporary file
            Resource fileResource = new FileSystemResource(tempFile);
            
            // Get the content type or default to application/octet-stream
            String contentType = file.getContentType();
            String type = "application";
            String subtype = "octet-stream";
            
            if (contentType != null && contentType.contains("/")) {
                String[] parts = contentType.split("/");
                if (parts.length == 2) {
                    type = parts[0];
                    subtype = parts[1];
                }
            }
            
            // Create the user message with file content
            UserMessage userMessage = UserMessage.builder()
                    .text(message != null ? message : "You are a very professional document summarization specialist. Please summarize the given document.")
                    .media(List.of(new Media(
                        new MimeType(type, subtype), 
                        fileResource)))
                    .build();
            
            // Get response from the AI model
            var aiResponse = this.chatModel.call(new Prompt(List.of(userMessage)));
            String responseText = aiResponse.getResult().getOutput().getText();
            
            // Log the response
            System.out.println("AI Response: " + responseText);
            
            // Build the response
            response.put("status", "success");
            response.put("response", responseText);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("response", "Error processing file: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

}