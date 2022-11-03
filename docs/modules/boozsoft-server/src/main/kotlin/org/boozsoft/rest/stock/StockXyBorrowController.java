package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockBorrowsSource;
import org.boozsoft.repo.stock.StockBorrowsSourceRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @description: 借入借用单  下游表
 * @author: miao
 * @date: 2022/10/14 14:12
 * @param:
 * @return: null
 **/
@Slf4j
@RestController
@RequestMapping("/stock_xyborrow")
public class StockXyBorrowController {

    @Autowired
    StockBorrowsSourceRepository sourceRepository;

    @PostMapping("stockXyBorrowSave")
    @Transactional
    public Mono<R> save(@RequestBody StockBorrowsSource pojo) {
        return sourceRepository.save(pojo).map(R::ok);
    }

    @PostMapping("deleteByCcodeAndXyccode")
    @Transactional
    public Mono<R> deleteByCcodeAndXyccode(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        String xyccode=map.get("xyccode").toString();
        return sourceRepository.deleteByCcodeAndXyccode(ccode,xyccode).then(Mono.just(R.ok()));
    }

    @PostMapping("findByCcodeAndXyBillStyle")
    public Mono<R> findByCcodeAndXyBillStyle(@RequestBody Map map) {
        String ccode=map.get("ccode").toString();
        String xytype=map.get("xytype").toString();
        return sourceRepository.findByCcodeAndXyBillStyle(ccode,xytype).collectList().map(R::ok);
    }
}
