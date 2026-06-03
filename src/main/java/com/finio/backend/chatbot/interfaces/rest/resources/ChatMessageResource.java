package com.finio.backend.chatbot.interfaces.rest.resources;

public record ChatMessageResource(Long id, String role, String content) {}