package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.CodeCompare;
import org.boozsoft.repo.CodeCompareRepository;
import org.boozsoft.util.RandomNum;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/codeCompare")
public class CodeCompareController {

    @Autowired
    CodeCompareRepository codeCompareRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(){
        return codeCompareRepository.findAllByOrderBySourceCode()
                .collectList().map(R::page);
    }

    @GetMapping("findByIyear")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByIyear(String iyear){
        return codeCompareRepository.findByIyearOrderBySourceCode(iyear)
                .collectList().map(R::page);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody CodeCompare object){
        List<CodeCompare> list=new ArrayList<>();
        String sameSource=object.getId()==null? RandomNum.uuid():object.getId();
        for (int i = 0; i < object.getSourceCode().split(",").length; i++) {
            CodeCompare vo=new CodeCompare();
            vo.setSourceCode(object.getSourceCode().split(",")[i]);
            vo.setIyear(object.getIyear());
            vo.setTargetCode(object.getTargetCode());
            vo.setSameSource(sameSource);
            list.add(vo);
        }
        return codeCompareRepository.delFindBySameSource(Arrays.asList(object.getSameSource()))
                .then(codeCompareRepository.saveAll(list).collectList()
                .map(o -> R.ok().setResult(o)));
    }

    @DeleteMapping
    @ApiOperation(value = "根据id删除数据", notes = "传入code")
    public Mono delete(String id){
        return codeCompareRepository.delFindBySameSource(Arrays.asList(id.split(",")))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<CodeCompare> object){
        return codeCompareRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
