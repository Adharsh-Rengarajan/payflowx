package com.payflowx.payment_service.event;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.payflowx.payment_service.config.RabbitMQConfig;
import com.payflowx.payment_service.entity.Payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publishPaymentCompleted(Payment payment) {
		NotificationMessage message = NotificationMessage.builder().notificationType("PAYMENT_COMPLETED")
				.paymentId(payment.getPaymentId()).fromAccountId(payment.getFromAccountId())
				.toAccountId(payment.getToAccountId()).amount(payment.getAmount()).currency(payment.getCurrency())
				.status(payment.getStatus().name()).timestamp(LocalDateTime.now()).build();

		rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
		log.info("Published notification for completed payment: {}", payment.getPaymentId());
	}

	public void publishPaymentFailed(Payment payment) {
		NotificationMessage message = NotificationMessage.builder().notificationType("PAYMENT_FAILED")
				.paymentId(payment.getPaymentId()).fromAccountId(payment.getFromAccountId())
				.toAccountId(payment.getToAccountId()).amount(payment.getAmount()).currency(payment.getCurrency())
				.status(payment.getStatus().name()).failureReason(payment.getFailureReason())
				.timestamp(LocalDateTime.now()).build();

		rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_QUEUE, message);
		log.info("Published notification for failed payment: {}", payment.getPaymentId());
	}
}