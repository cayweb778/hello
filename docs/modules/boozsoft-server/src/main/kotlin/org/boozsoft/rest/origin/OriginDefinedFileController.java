package org.boozsoft.rest.origin;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginDefinedFile;
import org.boozsoft.repo.origin.OriginDefinedFileRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originDefinedFile")
public class OriginDefinedFileController {

    @Autowired
    OriginDefinedFileRepository definedFileRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String originId) {
        return definedFileRepository.findAllByOriginIdOrderById(pageable,originId)
                .collectList()
                .flatMap(item-> definedFileRepository.countAllByOriginId(originId)
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String originId) {
        return definedFileRepository.findByFlagAndOriginIdOrderById("1",originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String originId) {
        return definedFileRepository.findAllByOriginIdOrderByDefinedCode(originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByModelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelList(String scope, String model,String originId) {
        if (scope.equals("1")) {
            return definedFileRepository.findByScopeAndOriginIdOrderByDefinedCode(scope,originId)
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return definedFileRepository.findByScopeAndModelAndOriginIdOrderByDefinedCode(scope,model,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxDefinedCode(String originId) {
        return definedFileRepository.findMaxDefinedCode(originId)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongDefinedCode(String originId) {
        return definedFileRepository.findBukongDefinedCode(originId)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String definedCode,String originId) {
        return definedFileRepository.findByDefinedCodeAndOriginIdOrderById(definedCode,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String definedName,String originId) {
        return definedFileRepository.findByDefinedNameAndOriginIdOrderById(definedName,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody OriginDefinedFile object) {
        return definedFileRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody OriginDefinedFile object){
        return definedFileRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<OriginDefinedFile> object){
        return definedFileRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody OriginDefinedFile object) {
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
    public Mono excel(@RequestBody List<OriginDefinedFile> object){
        return definedFileRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByModelOrderByDefinedCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByModelOrderByDefinedCode(String model,String originId) {
        return definedFileRepository.findByModelOrderByDefinedCode(model,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
