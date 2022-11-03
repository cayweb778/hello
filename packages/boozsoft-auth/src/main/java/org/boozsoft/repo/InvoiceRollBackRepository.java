package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.rollback.InvoiceRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceRollBackRepository extends ReactiveCrudRepository<InvoiceRollback, String> {
}

