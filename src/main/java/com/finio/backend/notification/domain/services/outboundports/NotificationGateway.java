package com.finio.backend.notification.domain.services.outboundports;

/**
 * Outbound port for sending push notifications to the mobile client.
 */
public interface NotificationGateway {
    void sendPushNotification(Long userId, String title, String body);
}