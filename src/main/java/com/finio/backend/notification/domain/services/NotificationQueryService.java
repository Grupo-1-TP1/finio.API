package com.finio.backend.notification.domain.services;

import com.finio.backend.notification.domain.model.aggregates.Notification;
import com.finio.backend.notification.domain.model.queries.GetNotificationByIdQuery;
import com.finio.backend.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import java.util.List;
import java.util.Optional;

public interface NotificationQueryService {
    Optional<Notification> handle(GetNotificationByIdQuery query);
    List<Notification> handle(GetNotificationsByUserIdQuery query);
}
