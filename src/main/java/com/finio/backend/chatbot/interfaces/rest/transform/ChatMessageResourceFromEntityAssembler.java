package com.finio.backend.chatbot.interfaces.rest.transform;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.interfaces.rest.resources.ChatMessageResource;

public class ChatMessageResourceFromEntityAssembler {
    public static ChatMessageResource toResourceFromEntity(ChatMessage entity) {
        return new ChatMessageResource(entity.getId(), entity.getRole(), entity.getContent());
    }
}