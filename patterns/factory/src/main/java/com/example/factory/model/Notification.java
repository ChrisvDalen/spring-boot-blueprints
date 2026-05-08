package com.example.factory.model;

import java.time.Instant;

/**
 * Sealed hierarchy — every notification type is known at compile time.
 * Pattern matching (Java 21+) makes exhaustive switch possible without a default branch.
 */
public sealed interface Notification permits Notification.Email, Notification.Sms, Notification.Push {

    String recipient();
    String subject();
    String body();
    Instant createdAt();

    record Email(
            String recipient,
            String subject,
            String body,
            String replyTo,
            Instant createdAt
    ) implements Notification {}

    record Sms(
            String recipient,
            String subject,
            String body,
            String phoneNumber,
            Instant createdAt
    ) implements Notification {}

    record Push(
            String recipient,
            String subject,
            String body,
            String deviceToken,
            String iconUrl,
            Instant createdAt
    ) implements Notification {}
}
