/**
 *  --- 财税达NC代码生成器 ---
 *  --- 档案名：$$RecordName$$ ---
 */

package org.boozsoft.rest.$$platformName$$;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.service.$$platformName$$.impl.$$RecordName$$ServiceImpl;
import org.boozsoft.domain.entity.$$platformName$$.$$RecordName$$;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("$$path$$")
public class $$RecordName$$Controller {

    @Autowired
    $$RecordName$$ServiceImpl service;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return service.findAll(pageable).collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return service.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody $$RecordName$$ entity) {
        return service.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }


}