package org.boozsoft.repo.bank;

import org.boozsoft.domain.entity.invoice.rollback.InvoiceRollback;
import org.boozsoft.domain.entity.bank.rollback.BankInvoiceRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankInvoiceRollBackRepository extends ReactiveCrudRepository<BankInvoiceRollback, String> {
}

