package org.boozsoft.rest.stock;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.stock.StockDefineHead;
import org.boozsoft.repo.stock.StockDefineHeadRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/stockdefinehead")
public class StockDefineHeadController {

    @Autowired
    StockDefineHeadRepository stockDefineHeadRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return stockDefineHeadRepository.findAllByOrderById(pageable)
                .collectList()
                .flatMap(item-> stockDefineHeadRepository.countAllBy()
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll() {
        return stockDefineHeadRepository.findByFlagOrderById("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList() {
        return stockDefineHeadRepository.findAllByOrderByDefineCode()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByModelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelList(String model) {
        return stockDefineHeadRepository.findByModelOrderByDefineCode(model)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxDefineCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxDefineCode() {
        return stockDefineHeadRepository.findMaxDefineCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongDefineCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongDefineCode() {
        return stockDefineHeadRepository.findBukongDefineCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String defineCode) {
        return stockDefineHeadRepository.findByDefineCodeOrderById(defineCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String defineName) {
        return stockDefineHeadRepository.findByDefineNameOrderById(defineName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody StockDefineHead object) {
        return stockDefineHeadRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody StockDefineHead object){
        return stockDefineHeadRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<StockDefineHead> object){
        return stockDefineHeadRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody StockDefineHead object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return stockDefineHeadRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<StockDefineHead> object){
        return stockDefineHeadRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByModelByFlagOrderByDefineCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelByFlagOrderByDefineCode(String model) {
        return stockDefineHeadRepository.findByModelByFlagOrderByDefineCode(model)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
