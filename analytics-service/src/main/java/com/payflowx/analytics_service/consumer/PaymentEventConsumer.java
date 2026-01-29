package com.payflowx.analytics_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.payflowx.analytics_service.event.PaymentEvent;
import com.payflowx.analytics_service.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

	private final AnalyticsService analyticsService;

	@KafkaListener(topics = "payment-events", groupId = "analytics-service-group")
	public void consume(PaymentEvent event) {
		log.info("Received event: {} for payment: {}", event.getEventType(), event.getPayload().getPaymentId());

		switch (event.getEventType()) {
		case "payment.initiated":
			analyticsService.handlePaymentInitiated(event);
			break;
		case "payment.authorized":
			analyticsService.handlePaymentAuthorized(event);
			break;
		case "payment.completed":
			analyticsService.handlePaymentCompleted(event);
			break;
		case "payment.failed":
			analyticsService.handlePaymentFailed(event);
			break;
		default:
			log.info("Ignoring event type: {}", event.getEventType());
		}
	}
}