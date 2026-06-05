package com.finio.backend.notification.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream serviceAccountStream;

            // 1. Intentamos leer la variable de entorno (Ideal para cuando el backend corre en la nube)
            String firebaseJsonConfig = System.getenv("FIREBASE_CREDENTIALS_JSON");

            if (firebaseJsonConfig != null && !firebaseJsonConfig.isBlank()) {
                // Si la variable existe, convertimos ese bloque de texto JSON a un Stream de Java
                serviceAccountStream = new ByteArrayInputStream(firebaseJsonConfig.getBytes(StandardCharsets.UTF_8));
                System.out.println("🔥 [FIREBASE] Inicializado usando la Variable de Entorno en la nube.");
            } else {
                // 2. Si la variable no existe, buscamos el archivo local (Ideal para cuando programas en tu PC)
                ClassPathResource resource = new ClassPathResource("firebase-service-account.json");

                if (!resource.exists()) {
                    System.err.println("❌ [FIREBASE] No se encontró el archivo local ni la variable de entorno.");
                    return;
                }

                serviceAccountStream = resource.getInputStream();
                System.out.println("🔥 [FIREBASE] Inicializado usando el archivo JSON local.");
            }

            // 3. Inicializamos la app de Firebase con las credenciales obtenidas
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("🔥 [FIREBASE] Conexión establecida con éxito.");
            }

        } catch (IOException e) {
            System.err.println("❌ [FIREBASE] Error crítico al inicializar: " + e.getMessage());
        }
    }
}