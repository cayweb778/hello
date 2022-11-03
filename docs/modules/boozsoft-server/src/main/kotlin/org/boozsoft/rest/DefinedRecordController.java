package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.DefinedRecord;
import org.boozsoft.repo.DefinedRecordRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/definedRecord")
public class DefinedRecordController {

    @Autowired
    DefinedRecordRepository definedRecordRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String definedCode) {
        return definedRecordRepository.findByDefinedCodeOrderById(pageable,definedCode)
                .collectList()
                .flatMap(item-> definedRecordRepository.countByDefinedCode(definedCode)
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String definedCode) {
        return definedRecordRepository.findByDefinedCodeAndFlagOrderById(definedCode,"1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String definedCode) {
        return definedRecordRepository.findByDefinedCodeOrderById(definedCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxRecordCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxRecordCode(String definedCode) {
        return definedRecordRepository.findMaxRecordCode(definedCode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongRecordCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongRecordCode(String definedCode) {
        return definedRecordRepository.findBukongRecordCode(definedCode)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String definedCode,String recordCode) {
        return definedRecordRepository.findByDefinedCodeAndRecordCodeOrderById(definedCode,recordCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String definedCode,String recordName) {
        return definedRecordRepository.findByDefinedCodeAndRecordNameOrderById(definedCode,recordName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody DefinedRecord object) {
        return definedRecordRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody DefinedRecord object){
        return definedRecordRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<DefinedRecord> object){
        return definedRecordRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody DefinedRecord object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return definedRecordRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<DefinedRecord> object){
        return definedRecordRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
