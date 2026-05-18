package com.finio.backend.notification.application.internal.queryservices;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.domain.model.queries.GetNotificationByIdQuery;
import com.finio.backend.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import com.finio.backend.notification.domain.services.NotificationQueryService;
import com.finio.backend.notification.infrastructure.persistence.jpa.NotificationRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }

    @Override
    public List<Notification> handle(GetNotificationsByUserIdQuery query) {
        return notificationRepository.findByUserId(query.userId());
    }
}
