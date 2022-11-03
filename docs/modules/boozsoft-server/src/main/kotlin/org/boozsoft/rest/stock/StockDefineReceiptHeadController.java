package org.boozsoft.rest.stock;

import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockDefineReceiptHead;
import org.boozsoft.repo.stock.StockDefineReceiptHeadRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/stockDefineReceiptHead")
public class StockDefineReceiptHeadController {

    @Autowired
    StockDefineReceiptHeadRepository stockDefineReceiptHeadRepository;

    @GetMapping("stockDefineHeadFindByAll")
    public Mono<R> stockDefineHeadFindByAll(){
        return stockDefineReceiptHeadRepository.findAll().collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("countByDefineName")
    public Mono<R> countByDefineName(String name){
        return stockDefineReceiptHeadRepository.countByDefineName(name).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockDefineHeadfindByName")
    public Mono<R> stockDefineHeadfindByName(String name){
        return stockDefineReceiptHeadRepository.findByDefineName(name).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockDefineHeadSave")
    public Mono<R> stockDefineHeadSave(@RequestBody StockDefineReceiptHead pojo){
        pojo.setDefineName(pojo.getDefineName().trim());
        return stockDefineReceiptHeadRepository.save(pojo).map(a->R.ok().setResult(a));
    }

    @PostMapping("stockDefineHeadDeleteAllById")
    public Mono<R> deleteAllById(String id){
        return stockDefineReceiptHeadRepository.deleteAllById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("stockDefineHeadEditByFlag")
    public Mono<R> eidtByFlag(@RequestBody Map map){
        List<String> list= Arrays.asList(map.get("id").toString().split(","));
        return stockDefineReceiptHeadRepository.eidtByFlag(map.get("flag").toString(),list).then(Mono.just(R.ok()));
    }
}
