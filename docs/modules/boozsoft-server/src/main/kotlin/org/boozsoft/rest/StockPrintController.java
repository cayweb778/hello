package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.StockPrintBodyRepository;
import org.boozsoft.repo.StockPrintHeadRepository;
import org.boozsoft.repo.StockPrintRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stockPrint")
public class StockPrintController {

    @Autowired
    StockPrintRepository stockPrintRepository;


    @Autowired
    StockPrintHeadRepository stockPrintHeadRepository;



    @Autowired
    StockPrintBodyRepository stockPrintBodyRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> findAll() {
        return Mono.zip(
                        stockPrintRepository.findAll().collectList(),
                        stockPrintHeadRepository.findAll().collectList(),
                        stockPrintBodyRepository.findAll().collectList()
                )
                .map(it->{
                    Map map=new HashMap();
                    map.put("stockPrint",it.getT1());
                    map.put("stockPrintHead",it.getT2());
                    map.put("stockPrintBody",it.getT3());
                    return map;
                })
                .map(R::ok);
    }

    @GetMapping("findAllByCcode")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> findAllByCcode(String ccode) {
        return Mono.zip(
                stockPrintRepository.findAllByCcode(ccode).collectList(),
                stockPrintHeadRepository.findAllByCcode(ccode).collectList(),
                stockPrintBodyRepository.findAllByCcode(ccode).collectList()
        )
                .map(it->{
                    Map map=new HashMap();
                    map.put("stockPrint",it.getT1());
                    map.put("stockPrintHead",it.getT2());
                    map.put("stockPrintBody",it.getT3());
                    return map;
                })
                .map(R::ok);
    }




}
