package com.example.factory.service;

import com.example.factory.factory.NotificationFactory;
import com.example.factory.model.Notification;
import com.example.factory.model.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationFactory factory;
    private final List<Notification> sent = new ArrayList<>();

    public NotificationService(NotificationFactory factory) {
        this.factory = factory;
    }

    public Notification send(NotificationRequest request) {
        Notification notification = factory.create(request);

        // Pattern matching switch — exhaustive over the sealed hierarchy
        switch (notification) {
            case Notification.Email e  -> log.info("[EMAIL] To: {} | ReplyTo: {} | Subject: {}", e.recipient(), e.replyTo(), e.subject());
            case Notification.Sms s   -> log.info("[SMS]   To: {} | Phone: {} | Body: {}", s.recipient(), s.phoneNumber(), s.body());
            case Notification.Push p  -> log.info("[PUSH]  To: {} | Token: {} | Subject: {}", p.recipient(), p.deviceToken(), p.subject());
        }

        sent.add(notification);
        return notification;
    }

    public List<Notification> getSent() {
        return List.copyOf(sent);
    }
}
