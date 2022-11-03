package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.BudgetSource;
import org.boozsoft.domain.entity.FaEconomyUse;
import org.boozsoft.repo.BudgetSourceRepository;
import org.boozsoft.repo.FaEconomyUseRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sys/faEconomyUse")
public class FaEconomyUseController {

    @Autowired
    private FaEconomyUseRepository groupBudgetSourceRepository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return groupBudgetSourceRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return groupBudgetSourceRepository.findByFlagOrderById("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String settModesCode){
        return groupBudgetSourceRepository.findByBsCodeOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByBsName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBsName(String settModesCode){
        return groupBudgetSourceRepository.findByBsNameOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody FaEconomyUse object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            object.setCreateDate(time);
        }
        return groupBudgetSourceRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody FaEconomyUse object){
        return groupBudgetSourceRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody FaEconomyUse object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag().equals("1")){
            object.setStopDate(time);
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return groupBudgetSourceRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<FaEconomyUse> object){
        return groupBudgetSourceRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }
    @GetMapping("/getTotalData")
    @ApiOperation(value = "获取所有数据总条数", notes = "获取所有数据总条数")
    public Mono getTotalData(){
        return groupBudgetSourceRepository.findAll()
                .collectList()
                .map(v-> v.size())
                .map(v-> R.ok().setResult(v));
    }

}
