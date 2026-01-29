package com.payflowx.ledger_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflowx.ledger_service.entity.LedgerEntry;
import com.payflowx.ledger_service.entity.LedgerEntry.EntryStatus;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
	List<LedgerEntry> findByPaymentId(String paymentId);

	List<LedgerEntry> findByAccountId(String accountId);

	List<LedgerEntry> findByPaymentIdAndStatus(String paymentId, EntryStatus status);

	boolean existsByPaymentId(String paymentId);
}