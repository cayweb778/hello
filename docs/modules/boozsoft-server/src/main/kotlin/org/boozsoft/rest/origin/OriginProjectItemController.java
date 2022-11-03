package org.boozsoft.rest.origin;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginProjectItem;
import org.boozsoft.repo.origin.OriginProjectItemRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originProjectItem")
public class OriginProjectItemController {

    @Autowired
    OriginProjectItemRepository projectItemRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable,String originId) {
        return projectItemRepository.findAllByOriginIdOrderByItemCode(pageable,originId)
                .collectList()
                .flatMap(item-> projectItemRepository.countAllByOriginId(originId)
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll(String originId) {
        return projectItemRepository.findByFlagAndOriginIdOrderByItemCode("1",originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String originId) {
        return projectItemRepository.findAllByOriginIdOrderByItemCode(originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String itemCode,String originId) {
        return projectItemRepository.findByItemCodeAndOriginIdOrderByItemCode(itemCode,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String itemName,String originId) {
        return projectItemRepository.findByItemNameAndOriginIdOrderByItemCode(itemName,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody OriginProjectItem object) {
        return projectItemRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody OriginProjectItem object){
        return projectItemRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<OriginProjectItem> object){
        return projectItemRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody OriginProjectItem object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return projectItemRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<OriginProjectItem> object){
        return projectItemRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}