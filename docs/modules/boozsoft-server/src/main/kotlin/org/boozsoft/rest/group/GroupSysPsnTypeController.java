package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysPsnType;
import org.boozsoft.repo.group.GroupSysPsnTypeRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupSys/psnType")
public class GroupSysPsnTypeController {

    @Autowired
    GroupSysPsnTypeRepository sysPsnTypeRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return sysPsnTypeRepository.findAllByOrderByPsnTypeCode().collectList().map(R::page);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id){
        Mono mono = sysPsnTypeRepository.findById(id);
        return mono;
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysPsnType object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        return sysPsnTypeRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysPsnType object) {
        return sysPsnTypeRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<GroupSysPsnType> object) {
        return Flux.fromIterable(object).map(item -> {
                    if (item.getUniqueCode() == null || item.getUniqueCode().equals("")) {
                        item.setUniqueCode(IdUtil.objectId());
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(sysPsnTypeRepository::saveAll)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
