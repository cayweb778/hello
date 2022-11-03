package org.boozsoft.repo.bank;

import org.boozsoft.domain.entity.bank.BankInvoice;
import org.boozsoft.domain.entity.invoice.Invoice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BankInvoiceRepository extends ReactiveCrudRepository<BankInvoice, String> {
    Flux<BankInvoice> findAllByInvoiceHeaderUniqueCode(String headerunique);
}

