package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.BaseInfo;
import org.boozsoft.domain.entity.account.BaseInfoColumn;
import org.boozsoft.domain.vo.BaseInfoVo;
import org.boozsoft.repo.BaseInfoColumnRepository;
import org.boozsoft.repo.BaseInfoRepository;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/base/info")
public class BaseInfoController {

    @Autowired
    BaseInfoRepository baseInfoRepository;
    @Autowired
    BaseInfoColumnRepository baseInfoColumnRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return baseInfoRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    
    public Mono findById(String id){
        Mono mono = baseInfoRepository.findById(id);
        return mono;
    }

    @GetMapping("findColumnByTable")
    @ApiOperation(value = "查询", notes = "传入code")
    
    public Mono<R> findColumnByTable(String baseTable) {
        return baseInfoColumnRepository.findByBaseTableOrderById(baseTable)
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findColumnAndIsflagByTable")
    @ApiOperation(value = "查询", notes = "传入code")
    
    public Mono<R> findColumnAndIsflagByTable(String baseTable) {
        return baseInfoColumnRepository.findByBaseTableAndIsflagOrderById(baseTable,"1")
                .collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody BaseInfoVo vo){
        // 基础档案的逻辑
        Mono<BaseInfo> mono1 = Mono.fromSupplier(
                () -> {
                    BaseInfo baseInfo = new BaseInfo();
                    baseInfo.setId(vo.getId());
                    baseInfo.setBaseName(vo.getBaseName());
                    baseInfo.setBaseTable(vo.getBaseTable());
                    return baseInfo;
                })
                .flatMap(baseInfoRepository::save);
        // 栏目的逻辑
        Mono<List<BaseInfoColumn>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                        .map(item -> {
                            if (item.getCname() != null && !item.getCname().equals("")) {
                                item.setBaseTable(vo.getBaseTable());
                            }
                            return item;
                        }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(baseInfoColumnRepository::saveAll)
                .collectList();
        return mono1.zipWith(mono2).map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody BaseInfo baseInfo){
        return baseInfoRepository.deleteById(baseInfo.getId())
                .zipWith(baseInfoColumnRepository.deleteByBaseTable(baseInfo.getBaseTable()))
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteColumn")
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono deleteColumn(String id) {
        return baseInfoColumnRepository.deleteById(id).then(Mono.just(R.ok()));
    }

}
