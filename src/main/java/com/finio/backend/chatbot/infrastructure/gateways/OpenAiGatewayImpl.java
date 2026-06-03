package com.finio.backend.chatbot.infrastructure.gateways;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.domain.services.outboundports.AiClientGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
public class OpenAiGatewayImpl implements AiClientGateway {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    @Override
    public String generateResponse(List<ChatMessage> conversationHistory) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, String>> messages = new ArrayList<>();

        String systemPrompt = """
        Eres FinioBot, el asesor financiero inteligente integrado en la aplicación móvil 'Finio'. 
        Tu objetivo es ayudar a usuarios universitarios y jóvenes profesionales a gestionar sus finanzas, ahorrar y tomar decisiones inteligentes con su dinero.
    
        Reglas operativas estrictas:
        1. Preséntate siempre como el asistente de Finio.
        2. Da respuestas concisas, amigables y de máximo 2 o 3 párrafos cortos (optimizado para lectura en pantallas de celulares).
        3. Utiliza viñetas o listas cuando des consejos para facilitar la lectura.
        4. Haz referencia a las funcionalidades de la app (como el registro de Gastos e Ingresos, control de Cuentas, asignación de Presupuestos manual por Categoría de gastos, creación de Metas de ahorro, Gastos frecuentes, asignación de Presupuestos con Machine Learning) cuando sea oportuno.
        5. Si te preguntan cosas fuera del ámbito de las finanzas, la economía o el ahorro, redirige cortésmente la conversación hacia la gestión del dinero.
        """;

        // System Prompt para darle contexto de negocio a la IA
        messages.add(Map.of(
                "role", "system",
                "content", systemPrompt
        ));

        // Mapeamos el historial que vino de MySQL al formato JSON de OpenAI
        for (ChatMessage msg : conversationHistory) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini", // El modelo más costo-eficiente para estudiantes
                "messages", messages,
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString();
            }
            return "No recibí una respuesta válida de la IA.";
        } catch (Exception e) {
            return "Lo siento. Tengo problemas para conectarme con OpenAI en este momento: " + e.getMessage();
        }
    }
}