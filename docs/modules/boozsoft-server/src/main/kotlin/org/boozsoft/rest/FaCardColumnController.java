package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FaCardColumn;
import org.boozsoft.domain.entity.FaProperty;
import org.boozsoft.domain.vo.SysDepartmentVo;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.repo.FaCardColumnRepository;
import org.boozsoft.repo.FaPropertyRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/faCardColumn")
public class FaCardColumnController {

    @Autowired
    FaCardColumnRepository groupFaPropertyRepository;

    @GetMapping("treeDeptByFlag")
    public Mono<Map> treeDeptByFlag() {
        return groupFaPropertyRepository.findByFlagOrderById("1")
                .collectList()
                .map(list->TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }


    @GetMapping("findAll")
    public Mono<R> findAll(){
        return groupFaPropertyRepository.findAllByOrderById().collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody FaCardColumn entity) {
        return groupFaPropertyRepository.save(entity).map(o -> R.ok().setResult(o));
    }



    @PostMapping("deleteBy")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteBy(@RequestBody Map params) {
        return groupFaPropertyRepository.deleteById(Flux.fromIterable((List)params.get("ids")))
                .then(Mono.just(R.ok()));
    }


}
