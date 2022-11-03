/**
 * --- 财税达NC代码生成器 ---
 * --- 档案名：AccountAccvoucherCdigestClass ---
 */

package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AccountAccvoucherCdigestClass;
import org.boozsoft.repo.AccountAccvoucherCdigestClassRepository;
import org.boozsoft.service.impl.AccountAccvoucherCdigestClassServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.Map;

@RestController
@RequestMapping("/AccvoucherCdigestClass")
public class AccountAccvoucherCdigestClassController {

    @Autowired
    AccountAccvoucherCdigestClassServiceImpl service;
    @Autowired
    AccountAccvoucherCdigestClassRepository repository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return service.findAll(pageable).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findMaxClassCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxClassCode() {
        return repository.findMaxClassCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongClassCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongClassCode() {
        return repository.findBukongClassCode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return service.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody AccountAccvoucherCdigestClass entity) {
        return service.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }


}