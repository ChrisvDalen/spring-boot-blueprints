package com.example.factory.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
        @NotNull NotificationType type,
        @NotBlank String recipient,
        @NotBlank String subject,
        @NotBlank String body,
        String channel  // type-specific: email address / phone number / device token
) {
    public enum NotificationType { EMAIL, SMS, PUSH }
}
