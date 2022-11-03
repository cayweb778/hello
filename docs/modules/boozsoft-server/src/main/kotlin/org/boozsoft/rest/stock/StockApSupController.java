package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockApSup;
import org.boozsoft.repo.stock.StockApSupRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 应收款期初供应商
 */
@Slf4j
@RestController
@RequestMapping("/stock_ap_sup")
public class StockApSupController {

    @Autowired
    StockApSupRepository stockApSupRepository;

    @PostMapping("findAll")
    public Mono<R> findAll(){
        return stockApSupRepository.findArCustLeftJoinCustomer().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("save")
    public Mono<R> findAll(@RequestBody List<StockApSup> list){
        return stockApSupRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delSrCustByCustId")
    public Mono<R> delSrCustByCustId(@RequestBody List<String> custId){
        return stockApSupRepository.delSrCustByCustId(custId).then(Mono.just(R.ok()));
    }
    @PostMapping("getArCustCustId")
    public Mono<R> getArCustCustId(){
        return stockApSupRepository.getArCustCustId().collectList().map(a->R.ok().setResult(a));
    }
}
