package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.InvoiceType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InvoiceTypeRepository extends ReactiveCrudRepository<InvoiceType, String> {

}

