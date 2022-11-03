package org.boozsoft.service;

import org.boozsoft.domain.entity.account.AccountAccvoucherCdigest;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountAccvoucherCdigestService {
    Flux<AccountAccvoucherCdigest> findAll(Pageable pageable);

    Mono<AccountAccvoucherCdigest> findById(String id);

    Mono<AccountAccvoucherCdigest> save(AccountAccvoucherCdigest entity);

    Mono<Void> deleteById(String id);

    Flux<AccountAccvoucherCdigest> saveAll(List<AccountAccvoucherCdigest> entity);
}