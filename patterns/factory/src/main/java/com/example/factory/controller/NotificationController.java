package com.example.factory.controller;

import com.example.factory.model.Notification;
import com.example.factory.model.NotificationRequest;
import com.example.factory.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Factory pattern demo — sealed hierarchy + exhaustive pattern matching")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Send a notification", description = "Factory creates the right Notification subtype based on the type field")
    public Notification send(@Valid @RequestBody NotificationRequest request) {
        return notificationService.send(request);
    }

    @GetMapping
    @Operation(summary = "List all sent notifications")
    public List<Notification> list() {
        return notificationService.getSent();
    }
}
