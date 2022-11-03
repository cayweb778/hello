package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.InvoiceOcrRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceOcrRecordRepository extends ReactiveCrudRepository<InvoiceOcrRecord, String> {
}

