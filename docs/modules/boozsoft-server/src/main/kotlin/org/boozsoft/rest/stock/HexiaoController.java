package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.repo.stock.StockSaleousingRepository;
import org.boozsoft.repo.stock.StockWarehousingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/hexiao")
public class HexiaoController {

    @Autowired
    private StockSaleousingRepository stockSaleousingRepository;
    @Autowired
    private StockWarehousingRepository stockWarehousingRepository;

    @GetMapping("findByXhd")
    public Mono<R> findByXhd(String year) {
        return stockSaleousingRepository.findByXhd(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBySkWhxXhd")
    public Mono<R> findBySkWhxXhd(String year,String cvencode) {
        return stockSaleousingRepository.findBySkWhxXhd(year,cvencode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCgdhd")
    public Mono<R> findByCgdhd(String year) {
        return stockWarehousingRepository.findByCgdhd(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByFkWhxCgdhd")
    public Mono<R> findByFkWhxCgdhd(String year,String cvencode) {
        return stockWarehousingRepository.findByFkWhxCgdhd(year,cvencode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findSkWhxskdByEndDate")
    public Mono<R> findSkWhxskdByEndDate(String year,String endDate){
        return stockSaleousingRepository.findWhxskdByEndDate(year,endDate)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findFkWhxCgdhdByEndDate")
    public Mono<R> findFkWhxCgdhdByEndDate(String year,String endDate){
        return stockWarehousingRepository.findFkWhxCgdhdByEndDate(year,endDate)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
