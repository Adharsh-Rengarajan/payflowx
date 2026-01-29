package com.payflowx.notification_service.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.payflowx.notification_service.event.NotificationMessage;
import com.payflowx.notification_service.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

	private final NotificationService notificationService;

	@RabbitListener(queues = "payment-notifications")
	public void consume(NotificationMessage message) {
		log.info("Received notification: {} for payment: {}", message.getNotificationType(), message.getPaymentId());
		notificationService.processNotification(message);
	}
}