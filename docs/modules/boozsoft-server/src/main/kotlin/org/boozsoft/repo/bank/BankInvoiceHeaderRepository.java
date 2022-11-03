package org.boozsoft.repo.bank;

import org.boozsoft.domain.entity.bank.BankInvoiceHeader;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BankInvoiceHeaderRepository extends ReactiveCrudRepository<BankInvoiceHeader, String> {
    Mono<Long> countByFapiaoCodeAndFapiaoNumber(String fapiaoCode, String fapiaoNumber);
}

