package com.example.factory;

import com.example.factory.factory.NotificationFactory;
import com.example.factory.model.Notification;
import com.example.factory.model.NotificationRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class NotificationFactoryTest {

    private final NotificationFactory factory = new NotificationFactory();

    @Test
    void emailRequest_createsEmailNotification() {
        var request = new NotificationRequest(NotificationRequest.NotificationType.EMAIL,
                "luke@jedi.org", "Your Order", "It has been placed.", null);

        Notification result = factory.create(request);

        assertThat(result).isInstanceOf(Notification.Email.class);
        Notification.Email email = (Notification.Email) result;
        assertThat(email.replyTo()).isEqualTo("noreply@galactic-academy.example.com");
    }

    @Test
    void smsRequest_longBody_isTruncatedTo160Chars() {
        String longBody = "A".repeat(200);
        var request = new NotificationRequest(NotificationRequest.NotificationType.SMS,
                "han@falcon.org", "Alert", longBody, "+1-555-0100");

        Notification result = factory.create(request);

        assertThat(result).isInstanceOf(Notification.Sms.class);
        assertThat(((Notification.Sms) result).body()).hasSize(160).endsWith("...");
    }

    @Test
    void pushRequest_setsDefaultIconUrl() {
        var request = new NotificationRequest(NotificationRequest.NotificationType.PUSH,
                "leia@senate.org", "New Message", "You have mail.", "device-token-abc");

        Notification result = factory.create(request);

        assertThat(result).isInstanceOf(Notification.Push.class);
        assertThat(((Notification.Push) result).iconUrl()).contains("galactic-academy.example.com");
    }

    @Test
    void allTypesProduceCorrectSubtype() {
        for (var type : NotificationRequest.NotificationType.values()) {
            var request = new NotificationRequest(type, "test@test.com", "S", "B", "ch");
            Notification n = factory.create(request);
            assertThat(n).isNotNull();
            assertThat(n.recipient()).isEqualTo("test@test.com");
        }
    }
}
