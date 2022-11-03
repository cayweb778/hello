package org.boozsoft.service

import org.boozsoft.domain.entity.account.AccountAccvoucherCdigestClass
import org.springframework.data.domain.Pageable
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface AccountAccvoucherCdigestClassService {
    fun findAll(pageable: Pageable?): Flux<AccountAccvoucherCdigestClass?>?
    fun findById(id: String?): Mono<AccountAccvoucherCdigestClass?>?
    fun save(entity: AccountAccvoucherCdigestClass): Mono<AccountAccvoucherCdigestClass?>?
    fun deleteById(id: String?): Mono<Void?>?
}