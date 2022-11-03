package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FaAssetType;
import org.boozsoft.domain.entity.FaProperty;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.boozsoft.domain.vo.SysDepartmentVo;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.repo.FaPropertyRepository;
import org.boozsoft.repo.group.GroupFaPropertyRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/FaProperty")
public class FaPropertyController {

    @Autowired
    FaPropertyRepository groupFaPropertyRepository;

    @GetMapping("treeDept")

    public Mono<Map> treeDept(String id, String flag) {
        if (StrUtil.isNotBlank(id) && !id.equals("0")) {
            return groupFaPropertyRepository.findByIdOrderByDeptCode(id)
                    .collectList()
//                    .map(list->TreeUtils.castTreeList(list,SysDepartmentVo.class))
                    .map(list -> {
                        if (StrUtil.isNotBlank(flag)) {
                            return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        } else {
            return groupFaPropertyRepository.findAllDeptCodeOrDeptNameByFlag()
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, SysDepartmentVo.class))
                    .map(list -> {
                        if (StrUtil.isNotBlank(flag)) {
                            return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                        }
                        return list;
                    })
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        }
    }

    @GetMapping("treeDeptByFlag")
    public Mono<Map> treeDeptByFlag() {
        return groupFaPropertyRepository.findByFlagOrderByPeopertyId("1")
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
        return groupFaPropertyRepository.findAllByOrderByPeopertyId().collectList().map(a->R.ok().setResult(a));
    }


    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody FaProperty entity) {
        entity.setStatus("1");
        return groupFaPropertyRepository.save(entity).map(o -> R.ok().setResult(o));
    }



    @PostMapping("deleteBy")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteBy(@RequestBody Map params) {
        return groupFaPropertyRepository.deleteById(Flux.fromIterable((List)params.get("ids")))
                .then(Mono.just(R.ok()));
    }


    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<FaProperty> object){
        object.stream().forEach(v->{
            v.setUniqueCustclass(UUID.randomUUID().toString());
            v.setStatus("1");
        });
        return groupFaPropertyRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }
    @GetMapping("/getTotalData")
    @ApiOperation(value = "获取所有数据总条数", notes = "获取所有数据总条数")
    public Mono getTotalData(){
        return groupFaPropertyRepository.findAll()
                .collectList()
                .map(v-> v.size())
                .map(v-> R.ok().setResult(v));
    }


}
