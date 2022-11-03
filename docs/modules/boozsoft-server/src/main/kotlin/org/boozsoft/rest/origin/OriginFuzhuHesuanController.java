package org.boozsoft.rest.origin;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginFuzhuHesuan;
import org.boozsoft.repo.origin.OriginFuzhuHesuanRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originFuzhuHesuan")
public class OriginFuzhuHesuanController {

    @Autowired
    OriginFuzhuHesuanRepository fuzhuHesuanRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String originId) {
        return fuzhuHesuanRepository.findAllByOriginIdOrderByCdfine(pageable, originId)
                .collectList()
                .flatMap(item -> fuzhuHesuanRepository.countAllByOriginId(originId)
                        .map(total -> R.page(item, pageable, total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String originId) {
        return fuzhuHesuanRepository.findByFlagAndOriginIdOrderByCdfine("1",originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String originId) {
        return fuzhuHesuanRepository.findAllByOriginIdOrderByCdfine(originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String ccode,String originId) {
        return fuzhuHesuanRepository.findByCcodeAndOriginIdOrderByCdfine(ccode,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String cname,String originId) {
        return fuzhuHesuanRepository.findByCnameAndOriginIdOrderByCdfine(cname,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCankaoDuixiang")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCankaoDuixiang(String cankaoDuixiang,String originId) {
        return fuzhuHesuanRepository.findByCankaoDuixiangAndOriginIdOrderByCdfine(cankaoDuixiang,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody OriginFuzhuHesuan object) {
        return fuzhuHesuanRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody OriginFuzhuHesuan object){
        return fuzhuHesuanRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<OriginFuzhuHesuan> object){
        return fuzhuHesuanRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody OriginFuzhuHesuan object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return fuzhuHesuanRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<OriginFuzhuHesuan> object){
        return fuzhuHesuanRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
