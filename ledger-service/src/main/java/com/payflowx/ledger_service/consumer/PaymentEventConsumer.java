package com.payflowx.ledger_service.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.payflowx.ledger_service.event.PaymentEvent;
import com.payflowx.ledger_service.service.LedgerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

	private final LedgerService ledgerService;

	@KafkaListener(topics = "payment-events", groupId = "ledger-service-group")
	public void consume(PaymentEvent event) {
		log.info("Received event: {} for payment: {}", event.getEventType(), event.getPayload().getPaymentId());

		switch (event.getEventType()) {
		case "payment.initiated":
			ledgerService.handlePaymentInitiated(event);
			break;
		case "payment.completed":
			ledgerService.handlePaymentCompleted(event);
			break;
		case "payment.failed":
			ledgerService.handlePaymentFailed(event);
			break;
		default:
			log.info("Ignoring event type: {}", event.getEventType());
		}
	}
}