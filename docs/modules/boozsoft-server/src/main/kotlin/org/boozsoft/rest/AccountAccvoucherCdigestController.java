/**
 * --- 财税达NC代码生成器 ---
 * --- 档案名：AccountAccvoucherCdigest ---
 */

package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.AccountAccvoucherCdigest;
import org.boozsoft.repo.AccountAccvoucherCdigestRepository;
import org.boozsoft.service.impl.AccountAccvoucherCdigestServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/AccvoucherCdigest")
public class AccountAccvoucherCdigestController {

    @Autowired
    AccountAccvoucherCdigestServiceImpl service;
    @Autowired
    AccountAccvoucherCdigestRepository repository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable, String classCode) {
        return service.findAll(pageable).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(classCode) && !classCode.equals("0") && !classCode.equals("undefined")) {
                        return list.stream().filter(item -> classCode.contains(item.getClassCode())).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(R::page);
    }

    @GetMapping("findMaxCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxCcode() {
        return repository.findMaxCcode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findBukongCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongCcode() {
        return repository.findBukongCcode()
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return service.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody AccountAccvoucherCdigest entity) {
        return service.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }

    @PostMapping("saveList")
    @ApiOperation(value = "保存集合（新增或修改）", notes = "传入实体类集合")
    public Mono<R> saveList(@RequestBody List<AccountAccvoucherCdigest> entity) {
        return service.saveAll(entity).collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("findAllByOrderByCcode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByOrderByCcode() {
        return repository.findAllByOrderByCcode().collectList().map(R::ok);
    }

}