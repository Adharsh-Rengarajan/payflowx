package com.payflowx.payment_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflowx.payment_service.client.AuthorizationClient;
import com.payflowx.payment_service.dto.ApiResponse;
import com.payflowx.payment_service.dto.AuthorizationRequest;
import com.payflowx.payment_service.dto.AuthorizationResponse;
import com.payflowx.payment_service.dto.PaymentRequest;
import com.payflowx.payment_service.dto.PaymentResponse;
import com.payflowx.payment_service.entity.Payment;
import com.payflowx.payment_service.entity.Payment.PaymentStatus;
import com.payflowx.payment_service.event.NotificationPublisher;
import com.payflowx.payment_service.event.PaymentEventPublisher;
import com.payflowx.payment_service.repository.PaymentRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final AuthorizationClient authorizationClient;
	private final PaymentEventPublisher eventPublisher;
	private final NotificationPublisher notificationPublisher;

	private final AtomicLong counter = new AtomicLong(0);

	@PostConstruct
	public void initializeCounter() {
		Long maxId = paymentRepository.findMaxId().orElse(0L);
		counter.set(maxId);
		log.info("Payment counter initialized to: {}", maxId);
	}

	@Transactional
	public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(PaymentRequest request) {
		Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(request.getIdempotencyKey());
		if (existingPayment.isPresent()) {
			log.info("Duplicate payment request with idempotencyKey: {}", request.getIdempotencyKey());
			return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment already processed",
					PaymentResponse.fromEntity(existingPayment.get())));
		}

		String paymentId = generatePaymentId();

		Payment payment = Payment.builder().paymentId(paymentId).idempotencyKey(request.getIdempotencyKey())
				.fromAccountId(request.getFromAccountId()).toAccountId(request.getToAccountId())
				.amount(request.getAmount()).currency(request.getCurrency()).description(request.getDescription())
				.status(PaymentStatus.INITIATED).build();

		payment = paymentRepository.save(payment);
		log.info("Payment initiated: {}", paymentId);
		eventPublisher.publishInitiated(payment);

		AuthorizationRequest authRequest = new AuthorizationRequest(request.getFromAccountId(),
				request.getToAccountId(), request.getAmount(), request.getCurrency());

		try {
			ApiResponse<AuthorizationResponse> authApiResponse = authorizationClient.authorize(authRequest);
			AuthorizationResponse authResponse = authApiResponse.getData();

			if (authResponse.isAuthorized()) {
				payment.setStatus(PaymentStatus.AUTHORIZED);
				payment.setAuthorizedAt(LocalDateTime.now());
				payment = paymentRepository.save(payment);
				log.info("Payment authorized: {}", paymentId);
				eventPublisher.publishAuthorized(payment);

				payment.setStatus(PaymentStatus.COMPLETED);
				payment.setCompletedAt(LocalDateTime.now());
				payment = paymentRepository.save(payment);
				log.info("Payment completed: {}", paymentId);
				eventPublisher.publishCompleted(payment);
				notificationPublisher.publishPaymentCompleted(payment);

				return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment completed successfully",
						PaymentResponse.fromEntity(payment)));
			} else {
				payment.setStatus(PaymentStatus.FAILED);
				payment.setFailureCode(authResponse.getRejectionCode());
				payment.setFailureReason(authResponse.getRejectionReason());
				payment.setFailedAt(LocalDateTime.now());
				payment = paymentRepository.save(payment);
				log.info("Payment failed: {} - {}", paymentId, authResponse.getRejectionCode());
				eventPublisher.publishFailed(payment);
				notificationPublisher.publishPaymentFailed(payment);

				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.success(
						HttpStatus.BAD_REQUEST.value(), "Payment failed", PaymentResponse.fromEntity(payment)));
			}
		} catch (Exception e) {
			log.error("Authorization service error for payment: {}", paymentId, e);
			payment.setStatus(PaymentStatus.FAILED);
			payment.setFailureCode("AUTHORIZATION_SERVICE_ERROR");
			payment.setFailureReason("Unable to reach authorization service");
			payment.setFailedAt(LocalDateTime.now());
			payment = paymentRepository.save(payment);
			eventPublisher.publishFailed(payment);
			notificationPublisher.publishPaymentFailed(payment);

			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
					.body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.value(), "Payment processing failed", null));
		}
	}

	public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(String paymentId) {
		Optional<Payment> paymentOpt = paymentRepository.findByPaymentId(paymentId);
		if (paymentOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Payment not found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment found",
				PaymentResponse.fromEntity(paymentOpt.get())));
	}

	private String generatePaymentId() {
		String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
		long sequence = counter.incrementAndGet();
		return String.format("PAY-%s-%06d", date, sequence);
	}
}