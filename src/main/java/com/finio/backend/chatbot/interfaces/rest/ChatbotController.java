package com.finio.backend.chatbot.interfaces.rest;

import com.finio.backend.chatbot.application.internal.commandservices.ChatCommandServiceImpl;
import com.finio.backend.chatbot.domain.model.commands.SendMessageCommand;
import com.finio.backend.chatbot.interfaces.rest.resources.ChatMessageResource;
import com.finio.backend.chatbot.interfaces.rest.transform.ChatMessageResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatbot")
@Tag(name = "Chatbot", description = "AI Assistant Management Endpoints")
public class ChatbotController {

    private final ChatCommandServiceImpl chatCommandService;

    public ChatbotController(ChatCommandServiceImpl chatCommandService) {
        this.chatCommandService = chatCommandService;
    }

    /**
     * Endpoint to send a message to the financial assistant chatbot.
     */
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResource> sendMessage(
            @RequestParam Long userId,
            @RequestParam String sessionId,
            @RequestBody String message) {

        var command = new SendMessageCommand(userId, sessionId, message);
        var aiMessage = chatCommandService.handle(command);

        var resource = ChatMessageResourceFromEntityAssembler.toResourceFromEntity(aiMessage);
        return new ResponseEntity<>(resource, HttpStatus.CREATED);
    }
}