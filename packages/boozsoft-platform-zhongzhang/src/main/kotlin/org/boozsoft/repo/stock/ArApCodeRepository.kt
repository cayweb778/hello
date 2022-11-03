package org.boozsoft.repo.stock

import org.boozsoft.domain.entity.stock.ArApCode
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface ArApCodeRepository : ReactiveCrudRepository<ArApCode, String> {
    fun findAllByBillStyleOrderById(billStyle: String): Flux<ArApCode>
}