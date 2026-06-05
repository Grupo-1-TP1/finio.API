package com.finio.backend.notification.application.internal.eventhandlers;

import com.finio.backend.finance.domain.model.events.BudgetLimitReachedEvent;
import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.domain.model.aggregates.NotificationType; // <-- Tu enum real
import com.finio.backend.notification.domain.services.outboundports.NotificationGateway;
import com.finio.backend.notification.infrastructure.persistence.jpa.NotificationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BudgetNotificationHandler {

    private final NotificationGateway notificationGateway;
    private final NotificationRepository notificationRepository;

    public BudgetNotificationHandler(NotificationGateway notificationGateway, NotificationRepository notificationRepository) {
        this.notificationGateway = notificationGateway;
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    @Transactional
    public void on(BudgetLimitReachedEvent event) {
        String title;
        String body;

        if ("EXCEEDED_100".equals(event.alertType())) {
            title = "🚨 ¡Presupuesto Excedido!";
            body = "Has superado el 100% de tu presupuesto establecido para la categoría " + event.categoryName() + ".";
        } else if ("WARNING_80".equals(event.alertType())) {
            title = "⚠️ ¡Alerta de Presupuesto!";
            body = "Atención: Ya has consumido más del 80% de tu presupuesto asignado a " + event.categoryName() + ".";
        } else {
            return;
        }

        Notification localNotification = new Notification(
                event.userId(),
                title,
                body,
                NotificationType.BUDGET_ALERT //
        );
        notificationRepository.save(localNotification);

        notificationGateway.sendPushNotification(event.userId(), title, body);
    }
}