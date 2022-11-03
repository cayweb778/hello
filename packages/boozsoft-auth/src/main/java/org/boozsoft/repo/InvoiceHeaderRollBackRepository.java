package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.rollback.InvoiceHeaderRollback;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceHeaderRollBackRepository extends ReactiveCrudRepository<InvoiceHeaderRollback, String> {
}

