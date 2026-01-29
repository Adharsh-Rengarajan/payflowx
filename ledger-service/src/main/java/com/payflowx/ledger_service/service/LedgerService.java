package com.payflowx.ledger_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflowx.ledger_service.dto.ApiResponse;
import com.payflowx.ledger_service.entity.LedgerEntry;
import com.payflowx.ledger_service.entity.LedgerEntry.EntryStatus;
import com.payflowx.ledger_service.entity.LedgerEntry.EntryType;
import com.payflowx.ledger_service.event.PaymentEvent;
import com.payflowx.ledger_service.repository.LedgerEntryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerService {

	private final LedgerEntryRepository ledgerEntryRepository;

	@Transactional
	public void handlePaymentInitiated(PaymentEvent event) {
		if (ledgerEntryRepository.existsByPaymentId(event.getPayload().getPaymentId())) {
			log.info("Ledger entries already exist for payment: {}", event.getPayload().getPaymentId());
			return;
		}

		LedgerEntry debitEntry = LedgerEntry.builder().paymentId(event.getPayload().getPaymentId())
				.accountId(event.getPayload().getFromAccountId()).entryType(EntryType.DEBIT)
				.amount(event.getPayload().getAmount()).currency(event.getPayload().getCurrency())
				.status(EntryStatus.PENDING).build();

		LedgerEntry creditEntry = LedgerEntry.builder().paymentId(event.getPayload().getPaymentId())
				.accountId(event.getPayload().getToAccountId()).entryType(EntryType.CREDIT)
				.amount(event.getPayload().getAmount()).currency(event.getPayload().getCurrency())
				.status(EntryStatus.PENDING).build();

		ledgerEntryRepository.save(debitEntry);
		ledgerEntryRepository.save(creditEntry);

		log.info("Created PENDING ledger entries for payment: {}", event.getPayload().getPaymentId());
	}

	@Transactional
	public void handlePaymentCompleted(PaymentEvent event) {
		List<LedgerEntry> entries = ledgerEntryRepository.findByPaymentIdAndStatus(event.getPayload().getPaymentId(),
				EntryStatus.PENDING);

		for (LedgerEntry entry : entries) {
			entry.setStatus(EntryStatus.CONFIRMED);
			entry.setConfirmedAt(LocalDateTime.now());
			ledgerEntryRepository.save(entry);
		}

		log.info("Confirmed ledger entries for payment: {}", event.getPayload().getPaymentId());
	}

	@Transactional
	public void handlePaymentFailed(PaymentEvent event) {
		List<LedgerEntry> entries = ledgerEntryRepository.findByPaymentIdAndStatus(event.getPayload().getPaymentId(),
				EntryStatus.PENDING);

		for (LedgerEntry entry : entries) {
			entry.setStatus(EntryStatus.CANCELLED);
			ledgerEntryRepository.save(entry);
		}

		log.info("Cancelled ledger entries for payment: {}", event.getPayload().getPaymentId());
	}

	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getEntriesByPaymentId(String paymentId) {
		List<LedgerEntry> entries = ledgerEntryRepository.findByPaymentId(paymentId);
		if (entries.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No ledger entries found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Ledger entries found", entries));
	}

	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getEntriesByAccountId(String accountId) {
		List<LedgerEntry> entries = ledgerEntryRepository.findByAccountId(accountId);
		if (entries.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(ApiResponse.error(HttpStatus.NOT_FOUND.value(), "No ledger entries found", null));
		}
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Ledger entries found", entries));
	}

	public ResponseEntity<ApiResponse<List<LedgerEntry>>> getAllEntries() {
		List<LedgerEntry> entries = ledgerEntryRepository.findAll();
		return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "All ledger entries", entries));
	}
}