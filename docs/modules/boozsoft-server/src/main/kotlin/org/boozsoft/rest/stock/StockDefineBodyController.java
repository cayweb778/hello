package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockDefineBody;
import org.boozsoft.repo.stock.StockDefineBodyRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stockdefinebody")
public class StockDefineBodyController {

    @Autowired
    StockDefineBodyRepository stockDefineBodyRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable, String defineCode) {
        return stockDefineBodyRepository.findByDefineCodeOrderById(pageable,defineCode)
                .collectList()
                .flatMap(item-> stockDefineBodyRepository.countByDefineCode(defineCode)
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String defineCode) {
        return stockDefineBodyRepository.findByDefineCodeAndFlagOrderById(defineCode,"1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String defineCode) {
        return stockDefineBodyRepository.findByDefineCodeOrderById(defineCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode(String defineCode) {
        return stockDefineBodyRepository.findMaxCcode(defineCode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode(String defineCode) {
        return stockDefineBodyRepository.findBukongCcode(defineCode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String defineCode,String ccode) {
        return stockDefineBodyRepository.findByDefineCodeAndCcodeOrderById(defineCode,ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String defineCode,String cname) {
        return stockDefineBodyRepository.findByDefineCodeAndCnameOrderById(defineCode,cname)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody StockDefineBody object) {
        return stockDefineBodyRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody StockDefineBody object){
        return stockDefineBodyRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<StockDefineBody> object){
        return stockDefineBodyRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody StockDefineBody object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return stockDefineBodyRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<StockDefineBody> object){
        return stockDefineBodyRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
