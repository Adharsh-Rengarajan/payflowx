package com.payflowx.payment_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.payflowx.payment_service.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

	Optional<Payment> findByPaymentId(String paymentId);

	@Query("SELECT MAX(p.id) FROM Payment p")
	Optional<Long> findMaxId();

	boolean existsByIdempotencyKey(String idempotencyKey);
}