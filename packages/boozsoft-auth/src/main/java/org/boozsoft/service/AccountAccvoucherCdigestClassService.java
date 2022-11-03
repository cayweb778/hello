package org.boozsoft.service;

import org.boozsoft.domain.entity.account.AccountAccvoucherCdigestClass;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;

public interface AccountAccvoucherCdigestClassService {
    Flux<AccountAccvoucherCdigestClass> findAll(Pageable pageable);

    Mono<AccountAccvoucherCdigestClass> findById(String id);

    Mono<AccountAccvoucherCdigestClass> save(AccountAccvoucherCdigestClass entity);

    Mono<Void> deleteById(String id);
}