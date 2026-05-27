package com.finio.backend.notification.interfaces.rest;

import com.finio.backend.notification.domain.model.commands.DeleteNotificationCommand;
import com.finio.backend.notification.domain.model.queries.GetNotificationByIdQuery;
import com.finio.backend.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import com.finio.backend.notification.domain.services.NotificationCommandService;
import com.finio.backend.notification.domain.services.NotificationQueryService;
import com.finio.backend.notification.interfaces.rest.resources.CreateNotificationResource;
import com.finio.backend.notification.interfaces.rest.resources.NotificationResource;
import com.finio.backend.notification.interfaces.rest.transform.CreateNotificationCommandFromResourceAssembler;
import com.finio.backend.notification.interfaces.rest.transform.NotificationResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/notifications", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Notifications", description = "Endpoints para el envío y visualización de alertas del sistema")
public class NotificationController {

    private final NotificationCommandService notificationCommandService;
    private final NotificationQueryService notificationQueryService;

    public NotificationController(NotificationCommandService notificationCommandService,
                                  NotificationQueryService notificationQueryService) {
        this.notificationCommandService = notificationCommandService;
        this.notificationQueryService = notificationQueryService;
    }

    @PostMapping
    public ResponseEntity<NotificationResource> createNotification(@RequestBody CreateNotificationResource resource) {
        var command = CreateNotificationCommandFromResourceAssembler.toCommandFromResource(resource);
        var notification = notificationCommandService.handle(command);

        return notification.map(value -> new ResponseEntity<>(
                NotificationResourceFromEntityAssembler.toResourceFromEntity(value),
                HttpStatus.CREATED)
        ).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationResource> getNotificationById(@PathVariable Long notificationId) {
        var query = new GetNotificationByIdQuery(notificationId);
        var notification = notificationQueryService.handle(query);

        return notification.map(value -> ResponseEntity.ok(NotificationResourceFromEntityAssembler.toResourceFromEntity(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResource>> getNotificationsByUserId(@PathVariable Long userId) {
        var query = new GetNotificationsByUserIdQuery(userId);
        var notifications = notificationQueryService.handle(query);

        var resources = notifications.stream()
                .map(NotificationResourceFromEntityAssembler::toResourceFromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        var command = new DeleteNotificationCommand(notificationId);
        var deleted = notificationCommandService.handle(command);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Notification deleted successfully");
    }
}
