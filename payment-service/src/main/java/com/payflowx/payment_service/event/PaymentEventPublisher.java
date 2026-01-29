package com.payflowx.payment_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.payflowx.payment_service.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

	private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

	private static final String TOPIC = "payment-events";

	public void publishInitiated(Payment payment) {
		publish("payment.initiated", payment);
	}

	public void publishAuthorized(Payment payment) {
		publish("payment.authorized", payment);
	}

	public void publishCompleted(Payment payment) {
		publish("payment.completed", payment);
	}

	public void publishFailed(Payment payment) {
		publish("payment.failed", payment);
	}

	private void publish(String eventType, Payment payment) {
		PaymentEvent event = PaymentEvent.builder().eventId(UUID.randomUUID().toString()).eventType(eventType)
				.timestamp(LocalDateTime.now())
				.payload(PaymentEvent.PaymentEventPayload.builder().paymentId(payment.getPaymentId())
						.idempotencyKey(payment.getIdempotencyKey()).fromAccountId(payment.getFromAccountId())
						.toAccountId(payment.getToAccountId()).amount(payment.getAmount())
						.currency(payment.getCurrency()).status(payment.getStatus().name())
						.failureCode(payment.getFailureCode()).failureReason(payment.getFailureReason()).build())
				.build();

		kafkaTemplate.send(TOPIC, payment.getPaymentId(), event);
		log.info("Published event: {} for payment: {}", eventType, payment.getPaymentId());
	}
}