package org.boozsoft.repo.stock

import org.boozsoft.domain.entity.stock.StockCcodes
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface StockCcodesRepository : ReactiveCrudRepository<StockCcodes, String> {
    fun findByUniqueIdOrderById(uniqueId: String): Flux<StockCcodes>
    fun deleteAllByUniqueId(uniqueId: String): Flux<StockCcodes>
}