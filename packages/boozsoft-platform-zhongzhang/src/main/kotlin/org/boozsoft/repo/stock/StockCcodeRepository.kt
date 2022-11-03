package org.boozsoft.repo.stock

import org.boozsoft.domain.entity.stock.StockCcode
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface StockCcodeRepository : ReactiveCrudRepository<StockCcode, String> {
    fun findAllByOrderById(): Flux<StockCcode>
}