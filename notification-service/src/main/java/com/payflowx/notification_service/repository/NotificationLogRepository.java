package com.payflowx.notification_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflowx.notification_service.entity.NotificationLog;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
	List<NotificationLog> findByPaymentId(String paymentId);

	List<NotificationLog> findByRecipientAccountId(String accountId);
}