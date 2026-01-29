package com.payflowx.notification_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payflowx.notification_service.dto.ApiResponse;
import com.payflowx.notification_service.entity.NotificationLog;
import com.payflowx.notification_service.entity.NotificationLog.NotificationStatus;
import com.payflowx.notification_service.entity.NotificationLog.RecipientType;
import com.payflowx.notification_service.event.NotificationMessage;
import com.payflowx.notification_service.repository.NotificationLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

	private final NotificationLogRepository notificationLogRepository;

	public void processNotification(NotificationMessage message) {
		if ("PAYMENT_COMPLETED".equals(message.getNotificationType())) {
			handlePaymentCompleted(message);
		} else if ("PAYMENT_FAILED".equals(message.getNotificationType())) {
			handlePaymentFailed(message);
		}
	}

	private void handlePaymentCompleted(NotificationMessage message) {
		String senderMessage = String.format("Payment of %s %s to account %s completed successfully.",
				message.getCurrency(), message.getAmount(), message.getToAccountId());
		NotificationLog senderLog = NotificationLog.builder().paymentId(message.getPaymentId())
				.notificationType(message.getNotificationType()).recipientAccountId(message.getFromAccountId())
				.recipientType(RecipientType.SENDER).amount(message.getAmount()).currency(message.getCurrency())
				.message(senderMessage).status(NotificationStatus.SENT).build();
		notificationLogRepository.save(senderLog);
		log.info("NOTIFICATION [SENDER]: {}", senderMessage);

		String receiverMessage = String.format("You received %s %s from account %s.", message.getCurrency(),
				message.getAmount(), message.getFromAccountId());
		NotificationLog receiverLog = NotificationLog.builder().paymentId(message.getPaymentId())
				.notificationType(message.getNotificationType()).recipientAccountId(message.getToAccountId())
				.recipientType(RecipientType.RECEIVER).amount(message.getAmount()).currency(message.getCurrency())
				.message(receiverMessage).status(NotificationStatus.SENT).build();
		notificationLogRepository.save(receiverLog);
		log.info("NOTIFICATION [RECEIVER]: {}", receiverMessage);
	}

	private void handlePaymentFailed(NotificationMessage message) {
		String senderMessage = String.format("Payment of %s %s to account %s failed. Reason: %s", message.getCurrency(),
				message.getAmount(), message.getToAccountId(), message.getFailureReason());
		NotificationLog senderLog = NotificationLog.builder().paymentId(message.getPaymentId())
				.notificationType(message.getNotificationType()).recipientAccountId(message.getFromAccountId())
				.recipientType(RecipientType.SENDER).amount(message.getAmount()).currency(message.getCurrency())
				.message(senderMessage).status(NotificationStatus.SENT).build();
		notificationLogRepository.save(senderLog);
		log.info("NOTIFICATION [SENDER]: {}", senderMessage);
	}

	public ResponseEntity<ApiResponse<List<NotificationLog>>> getAllNotifications() {
		List<NotificationLog> logs = notificationLogRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notifications retrieved", logs));
	}

	public ResponseEntity<ApiResponse<List<NotificationLog>>> getByPaymentId(String paymentId) {
		List<NotificationLog> logs = notificationLogRepository.findByPaymentId(paymentId);
		if (logs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No notifications found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notifications found", logs));
	}

	public ResponseEntity<ApiResponse<List<NotificationLog>>> getByAccountId(String accountId) {
		List<NotificationLog> logs = notificationLogRepository.findByRecipientAccountId(accountId);
		if (logs.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No notifications found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Notifications found", logs));
	}
}
