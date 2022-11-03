/**
 *  --- 财税达NC代码生成器 ---
 *  --- 档案名：SysLogger ---
 */

package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.repo.group.SysLoggerRepository;
import org.boozsoft.service.group.impl.SysLoggerServiceImpl;
import org.boozsoft.domain.entity.group.SysLogger;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.Map;

@RestController
@RequestMapping("/SysLogger")
public class SysLoggerController {

    @Autowired
    SysLoggerServiceImpl service;

    @Autowired
    SysLoggerRepository repository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return service.findAll(pageable).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByYearAndMonth")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByYearAndMonth(Pageable pageable,@RequestParam("year") String year,@RequestParam("month") String month) {
        return repository.findAllByIyearAndImonth(pageable,year,month).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByYearAndMonthAndDay")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByYearAndMonthAndDay(Pageable pageable,@RequestParam("year") String year,@RequestParam("month") String month,@RequestParam("month") String day) {
        return repository.findAllByIyearAndImonthAndIday(pageable,year,month,day).collectList().map(o -> R.ok().setResult(o));
    }



    @GetMapping("findAllByQuJian")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByQuJian(Pageable pageable,@RequestParam("start") String start,@RequestParam("end") String end) {

        return null;
//        return repository.findAllByIyearAndImonthAndIday(pageable,year,month,day).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(String id) {
        return service.findById(id).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody SysLogger entity) {
        entity.setIyear(entity.getCreattime().split("-")[0]);
        entity.setImonth(entity.getCreattime().split("-")[1]);
        entity.setIday(entity.getCreattime().split("-")[2]);
        return service.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }

    @DeleteMapping("tableAuditSave")
    @ApiOperation(value = "表审计保存", notes = "表审计保存")
    public Mono<R> tableAuditSave(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }

}