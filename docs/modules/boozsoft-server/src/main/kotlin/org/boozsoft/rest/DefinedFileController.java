package org.boozsoft.rest;

import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.DefinedFile;
import org.boozsoft.repo.DefinedFileRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/definedFile")
public class DefinedFileController {

    @Autowired
    DefinedFileRepository definedFileRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return definedFileRepository.findAllByOrderById(pageable)
                .collectList()
                .flatMap(item-> definedFileRepository.countAllBy()
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll() {
        return definedFileRepository.findByFlagOrderById("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList() {
        return definedFileRepository.findAllByOrderByDefinedCode()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByModelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelList(String scope, String model) {
        if (scope.equals("1")) {
            return definedFileRepository.findByScopeOrderByDefinedCode(scope)
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return definedFileRepository.findByScopeAndModelOrderByDefinedCode(scope,model)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxDefinedCode() {
        return definedFileRepository.findMaxDefinedCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongDefinedCode() {
        return definedFileRepository.findBukongDefinedCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String definedCode) {
        return definedFileRepository.findByDefinedCodeOrderById(definedCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String definedName) {
        return definedFileRepository.findByDefinedNameOrderById(definedName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody DefinedFile object) {
        return definedFileRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody DefinedFile object){
        return definedFileRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<DefinedFile> object){
        return definedFileRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody DefinedFile object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return definedFileRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<DefinedFile> object){
        return definedFileRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByModelOrderByDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelOrderByDefinedCode(String model) {
        return definedFileRepository.findByModelOrderByDefinedCode(model)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
