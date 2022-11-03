package org.boozsoft.rest.stock

import cn.hutool.core.util.IdUtil
import org.boozsoft.domain.entity.stock.ArApCode
import org.boozsoft.repo.stock.ArApCodeRepository
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/ArApCode")
class ArApCodeController {
    @Autowired
    private lateinit var arApCodeRepository: ArApCodeRepository
    @GetMapping("findByBillStyle")
    fun findByBillStyle(billStyle: String): Mono<R<*>> {
        return arApCodeRepository.findAllByBillStyleOrderById(billStyle)
           .collectList()
            .map { o: List<ArApCode> -> R.ok().setResult(o) }
    }

    @PostMapping("saveArApCode")
    fun saveArApCode(@RequestBody arApCodeMono: Mono<ArApCode>): Mono<R<*>> {
        return arApCodeMono
            .map { it.copy(uniqueId=  if (it.uniqueId == null || it.uniqueId == "") IdUtil.objectId()else it.uniqueId) }
            .flatMap { s: ArApCode -> arApCodeRepository.save(s) }
            .map { o: ArApCode -> R.ok().setResult(o) }
    }
}