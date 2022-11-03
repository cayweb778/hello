package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AccAgingRange;
import org.boozsoft.repo.AccAgingRangeRepository;
import org.boozsoft.repo.StockPrintSettingRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/stockPrintSetting")
public class StockPrintSettingController {

    /********************* Mr. Ye *******************/
    @Autowired
    private StockPrintSettingRepository repository;

    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAllByModel(String model){
        return null;
    }


    @PostMapping
    @ApiOperation(value = "添加区间", notes = "传入code")
    public Mono<R> save(@RequestBody AccAgingRange entity) {
        return null;
    }
    /********************* Mr. Ye *******************/

    @GetMapping("findAllByAccIdModel")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAllByAccIdModel(String accId,String model){
        return null;
    }

}
