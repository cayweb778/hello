package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.SettModes;
import org.boozsoft.domain.entity.account.SettModesRollback;
import org.boozsoft.repo.SettModesRepository;
import org.boozsoft.repo.SettModesRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/settModes")
public class SettModesController {

    @Autowired
    SettModesRepository settModesRepository;
    @Autowired
    SettModesRollbackRepository settModesRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return settModesRepository.findAllByOrderBySettModesCode().collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return settModesRepository.findByFlagOrderBySettModesCode("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String settModesCode){
        return settModesRepository.findBySettModesCode(settModesCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody SettModes object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if(object.getCreateDate()==null || object.getCreateDate().equals("")){
            object.setCreateDate(time);
        }
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        return settModesRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody SettModes object){
        return settModesRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody SettModes object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag().equals("1")) {
            object.setFlag("0");
            object.setStopDate(time);
        } else {
            object.setFlag("1");
            object.setStopDate(null);
        }
        return settModesRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SettModes> object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return Flux.fromIterable(object).map(item -> {
                    if (item.getCreateDate()==null || item.getCreateDate().equals("")) {
                        item.setCreateDate(time);
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(settModesRepository::saveAll)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
