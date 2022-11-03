package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.Invoice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface InvoiceRepository extends ReactiveCrudRepository<Invoice, String> {
    Flux<Invoice> findAllByInvoiceHeaderUniqueCode(String headerunique);
}

