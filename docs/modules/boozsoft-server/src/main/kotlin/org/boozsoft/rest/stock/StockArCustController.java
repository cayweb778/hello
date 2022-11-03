package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockArCust;
import org.boozsoft.repo.stock.StockArCustRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 应收款期初客户
 */
@Slf4j
@RestController
@RequestMapping("/stock_ar_cust")
public class StockArCustController {

    @Autowired
    StockArCustRepository stockArCustRepository;

    @PostMapping("findAll")
    public Mono<R> findAll(){
        return stockArCustRepository.findArCustLeftJoinCustomer().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("save")
    public Mono<R> findAll(@RequestBody List<StockArCust> list){
        return stockArCustRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("delSrCustByCustId")
    public Mono<R> delSrCustByCustId(@RequestBody List<String> custId){
        return stockArCustRepository.delSrCustByCustId(custId).then(Mono.just(R.ok()));
    }
    @PostMapping("getArCustCustId")
    public Mono<R> getArCustCustId(){
        return stockArCustRepository.getArCustCustId().collectList().map(a->R.ok().setResult(a));
    }
}
