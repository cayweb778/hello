package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.InvoiceCheckRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceCheckRecordRepository extends ReactiveCrudRepository<InvoiceCheckRecord, String> {

}

