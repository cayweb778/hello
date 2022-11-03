package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.boozsoft.domain.entity.group.SysLogger;
import org.boozsoft.repo.group.GroupFaPropertyRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groupFaProperty")
public class GroupFaPropertyController {

    @Autowired
    GroupFaPropertyRepository groupFaPropertyRepository;

    @GetMapping("findAll")
    public Mono<R> findAll(){
        return groupFaPropertyRepository.findAllByOrderByPeopertyId().collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody GroupFaProperty entity) {
        return groupFaPropertyRepository.save(entity).map(o -> R.ok().setResult(o));
    }



    @PostMapping("deleteBy")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteBy(@RequestBody Map params) {
        return groupFaPropertyRepository.deleteById(Flux.fromIterable((List)params.get("ids")))
                .then(Mono.just(R.ok()));
    }


}
