package org.boozsoft.repo.bank;

import org.boozsoft.domain.entity.bank.rollback.BankInvoiceHeaderRollback;
import org.boozsoft.domain.entity.invoice.rollback.InvoiceHeaderRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankInvoiceHeaderRollBackRepository extends ReactiveCrudRepository<BankInvoiceHeaderRollback, String> {
}

