package org.boozsoft.rest;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.StockJrjc;
import org.boozsoft.domain.entity.StockJrjcs;
import org.boozsoft.repo.StockJrjcRepository;
import org.boozsoft.repo.StockJrjcsRepository;
import org.boozsoft.repo.stock.StockHeadColumnSettingsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stockJrjcs")
public class StockJrjcsController {
    @Autowired
    StockJrjcsRepository stockJrjcsRepository;
    @Autowired
    StockJrjcRepository stockJrjcRepository;

    @ApiOperation(value = "查询所有", notes = "")
    @GetMapping("/findAll")
    public Mono<R> findAll() {
        return stockJrjcsRepository.findAll().collectList().map(R::ok);
    }



    @ApiOperation(value = "查询所有", notes = "")
    @PostMapping("/save")
    public Mono<R> save(Map params) {
        // 保存主表
        List<StockJrjc> list=null;
        stockJrjcRepository.saveAll(list);

        // 保存从表
        List<StockJrjcs> list1=new ArrayList<>();
        return stockJrjcsRepository.saveAll(list1).collectList().map(R::ok);
    }

}
