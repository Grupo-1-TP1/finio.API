package com.finio.backend.notification.infrastructure.gateways;

import com.finio.backend.notification.domain.services.outboundports.NotificationGateway;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class FirebaseNotificationGatewayImpl implements NotificationGateway {

    @Override
    public void sendPushNotification(Long userId, String title, String body) {

        String userTopic = "user_" + userId;

        com.google.firebase.messaging.Notification firebaseNotification =
                com.google.firebase.messaging.Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build();

        Message message = Message.builder()
                .setTopic(userTopic)
                .setNotification(firebaseNotification)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("🚀 [FIREBASE PUSH SENT] ID de respuesta de Google: " + response);
        } catch (Exception e) {
            System.err.println("❌ Error enviando push vía Firebase: " + e.getMessage());
        }
    }
}