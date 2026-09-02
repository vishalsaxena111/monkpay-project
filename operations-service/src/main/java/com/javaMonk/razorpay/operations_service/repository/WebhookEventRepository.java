package com.javaMonk.razorpay.operations_service.repository;

import com.javaMonk.razorpay.common_lib.enums.WebhookEventStatus;
import com.javaMonk.razorpay.operations_service.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    List<WebhookEvent> findByStatusAndNextRetryAtBefore(WebhookEventStatus webhookEventStatus, LocalDateTime now);
}
