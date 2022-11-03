package org.boozsoft.rest.stock

import cn.hutool.core.util.IdUtil
import org.boozsoft.domain.entity.stock.StockCcode
import org.boozsoft.domain.entity.stock.StockCcodes
import org.boozsoft.repo.stock.StockCcodeRepository
import org.boozsoft.repo.stock.StockCcodesRepository
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/stockCcode")
class StockCcodeController {
    @Autowired
    private lateinit var stockCcodeRepository: StockCcodeRepository

    @Autowired
    private lateinit var stockCcodesRepository: StockCcodesRepository
    @GetMapping("findAll")
    fun findAll(): Mono<R<*>> {
        return stockCcodeRepository.findAllByOrderById()
            .collectList()
            .map { o: List<StockCcode> -> R.ok().setResult(o) }
    }

    @GetMapping("findByUniqueId")
    fun findByUniqueId(uniqueId: String): Mono<R<*>> {
        return stockCcodesRepository.findByUniqueIdOrderById(uniqueId)
            .collectList()
            .map { o: List<StockCcodes> -> R.ok().setResult(o) }
    }

    @PostMapping("saveStockCcode")
    fun saveStockCcode(@RequestBody stockCcodeMono: Mono<StockCcode>): Mono<R<*>> {
        return stockCcodeMono
            .map { it.copy(uniqueId=  if (it.uniqueId == null || it.uniqueId == "") IdUtil.objectId()else it.uniqueId) }
            .flatMap { s: StockCcode -> stockCcodeRepository.save(s) }
            .map { o: StockCcode -> R.ok().setResult(o) }
    }

    @PostMapping("saveStockCcodes")
    fun saveStockCcodes(@RequestBody stockCcodes: StockCcodes): Mono<R<*>> {
        return stockCcodesRepository.save(stockCcodes)
            .map { o: StockCcodes -> R.ok().setResult(o) }
    }

    @DeleteMapping("deleteByUniqueId")
    fun deleteByUniqueId(uniqueId: String): Mono<R<*>> {
        return stockCcodesRepository.deleteAllByUniqueId(uniqueId)
            .collectList()
            .map { o: List<StockCcodes> -> R.ok().setResult(o) }
    }
}