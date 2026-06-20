package com.finio.backend.chatbot.infrastructure.gateways;

import com.finio.backend.chatbot.domain.model.aggregates.ChatMessage;
import com.finio.backend.chatbot.domain.services.outboundports.AiClientGateway;
import com.finio.backend.chatbot.interfaces.rest.resources.UserFinancialSnapshotResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class OpenAiGatewayImpl implements AiClientGateway {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    @Override
    public String generateResponse(List<ChatMessage> conversationHistory,
                                   UserFinancialSnapshotResource snapshot,
                                   Double savingPercentage) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        List<Map<String, String>> messages = new ArrayList<>();

        // 🤖 1. SYSTEM PROMPT DEFINITIVO (Rol de auditor analítico)
        String systemPrompt = """
                Eres FinioBot, el asesor financiero inteligente integrado en la aplicación móvil 'Finio'. 
                Tu objetivo es ayudar a usuarios universitarios y jóvenes profesionales a resolver dudas sobre su dinero basándote ESTRICTAMENTE en sus datos reales.
                
                Reglas operativas:
                1. Analiza los datos de referencia proporcionados para responder de manera exacta a consultas sobre montos, comercios, categorías o balances. No inventes transacciones.
                2. Si el usuario te pregunta por un gasto específico (ej: "cuánto gasté en Tambo"), busca en la lista de 'Últimos movimientos' o categorías para calcular la respuesta.
                3. Da respuestas concisas, estructuradas y de máximo 2 o 3 párrafos cortos (optimizado para celulares). Usa viñetas para desglosar datos.
                4. Si te piden un consejo de ahorro general, personalízalo usando su categoría donde ha gastado más dinero este mes.
                5. Mantén un tono financiero, amigable y centrado en la optimización del dinero. Si preguntan cosas fuera de finanzas, redirige cortésmente.
                6. ¡REVISA EL HISTORIAL DE LA CONVERSACIÓN! Si te están repreguntando algo, ve directo a la respuesta analítica sin saludos corporativos.
                """;

        messages.add(Map.of("role", "system", "content", systemPrompt));

        // 📊 2. INYECCIÓN DEL HISTORIAL TRANSACCIONAL Y ESTADO REAL DEL USUARIO
        LocalDate today = LocalDate.now();
        String fechaActual = today.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        BigDecimal capacidadAhorroReal = snapshot.totalIncomeThisMonth().subtract(snapshot.totalExpenseThisMonth());

        StringBuilder realContext = new StringBuilder();
        realContext.append("[REPORTE FINANCIERO CONSOLIDADO DEL USUARIO (BASE DE DATOS REAL)]\n");
        realContext.append("- Fecha actual de la consulta: ").append(fechaActual).append("\n");
        realContext.append("- Saldo disponible actual en cuentas: S/. ").append(snapshot.totalBalance()).append("\n");
        realContext.append("- Meta de ahorro establecida en el perfil: ").append(savingPercentage * 100).append("% del ingreso mensual.\n");
        realContext.append("- Flujo de caja del mes actual:\n");
        realContext.append("  * Total Ingresos Registrados: S/. ").append(snapshot.totalIncomeThisMonth()).append("\n");
        realContext.append("  * Total Gastos Registrados: S/. ").append(snapshot.totalExpenseThisMonth()).append("\n");
        realContext.append("  * Balance Neto del Mes (Ingresos - Gastos): S/. ").append(capacidadAhorroReal).append("\n");

        realContext.append("- Consumo mensual distribuido por categorías:\n");
        snapshot.spendingByCategory().forEach((category, amount) ->
                realContext.append("  * ").append(category).append(": S/. ").append(amount).append("\n")
        );

        realContext.append("- Historial de los últimos movimientos realizados:\n");
        if (snapshot.recentTransactions() == null || snapshot.recentTransactions().isEmpty()) {
            realContext.append("  (No hay movimientos recientes registrados en este periodo)\n");
        } else {
            snapshot.recentTransactions().forEach(t ->
                    realContext.append("  * [").append(t.date()).append("] ").append(t.type())
                            .append(" en ").append(t.category()).append(" (").append(t.description())
                            .append("): S/. ").append(t.amount()).append("\n")
            );
        }
        realContext.append("[FIN DEL REPORTE REAL]");

        messages.add(Map.of("role", "system", "content", realContext.toString()));

        // 🔄 3. ACOPLAMIENTO DE LA CONVERSACIÓN ACTIVA (Chat de la UI)
        for (ChatMessage msg : conversationHistory) {
            messages.add(Map.of("role", msg.getRole(), "content", msg.getContent()));
        }

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "messages", messages,
                "temperature", 0.3, // Temperatura baja obligatoria para garantizar rigor matemático y cero alucinaciones
                "max_tokens", 450
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
            if (response != null && response.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString();
            }
            return "No recibí una respuesta válida del servicio de asesoría.";
        } catch (Exception e) {
            return "Error de comunicación con el motor analítico de Finio: " + e.getMessage();
        }
    }
}