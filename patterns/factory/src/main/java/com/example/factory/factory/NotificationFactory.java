package com.example.factory.factory;

import com.example.factory.model.Notification;
import com.example.factory.model.NotificationRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * The Factory centralises all conditional object creation logic.
 *
 * Without a factory, every caller that needs a Notification must know
 * which concrete type to instantiate and how to populate its fields.
 * Add a new type and you're hunting through call sites.
 *
 * With a factory, the creation logic lives in one place. Callers pass
 * a descriptor (NotificationRequest) and receive a fully built object.
 *
 * Java 21+ pattern matching in switch makes this exhaustive — the compiler
 * rejects the code if a new NotificationType is added without a case here.
 */
@Component
public class NotificationFactory {

    public Notification create(NotificationRequest request) {
        Instant now = Instant.now();
        return switch (request.type()) {
            case EMAIL -> new Notification.Email(
                    request.recipient(),
                    request.subject(),
                    request.body(),
                    request.channel() != null ? request.channel() : "noreply@galactic-academy.example.com",
                    now
            );
            case SMS -> new Notification.Sms(
                    request.recipient(),
                    request.subject(),
                    // SMS body is truncated to 160 chars — this rule belongs in the factory
                    request.body().length() > 160 ? request.body().substring(0, 157) + "..." : request.body(),
                    request.channel() != null ? request.channel() : request.recipient(),
                    now
            );
            case PUSH -> new Notification.Push(
                    request.recipient(),
                    request.subject(),
                    request.body(),
                    request.channel() != null ? request.channel() : "",
                    "https://galactic-academy.example.com/icon.png",
                    now
            );
        };
    }
}
