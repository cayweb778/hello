/**
 *  --- 财税达NC代码生成器 ---
 *  --- 档案名：SysLogger ---
 */

package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupTableAudit;
import org.boozsoft.domain.entity.group.SysLogger;
import org.boozsoft.service.group.impl.GroupTableAuditServiceImpl;
import org.boozsoft.service.group.impl.SysLoggerServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/TableAudit")
public class GroupTableAuditController {

    @Autowired
    GroupTableAuditServiceImpl service;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return service.findAll(pageable).collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(String id) {
        return service.findById(id).map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody GroupTableAudit entity) {
        return service.save(entity).map(o -> R.ok().setResult(o));
    }


    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }


}