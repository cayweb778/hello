package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupProjectClass;
import org.boozsoft.domain.vo.ProjectClassVo;
import org.boozsoft.repo.group.GroupProjectClassRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/groupProject/class")
public class GroupProjectClassController {

    @Autowired
    GroupProjectClassRepository projectClassRepository;

    @GetMapping("findListByCate")
    public Mono<R> findListByCate() {
        return projectClassRepository.findAllByOrderByProjectClassCode()
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    public Mono<R> findAll() {
        return projectClassRepository.findAllByOrderByProjectClassCode()
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("treeProClass")
    public Mono<Map> treeProClass() {
        return projectClassRepository.findAllByOrderByProjectClassCode()
                .collectList()
                .map(list -> TreeUtils.castTreeList(list, ProjectClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("findByCode")
    public Mono<R> findByCode(String projectClassCode) {
        return projectClassRepository.findByProjectClassCodeOrderById(projectClassCode)
                .collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupProjectClass object){
        if (object.getBend()==null || object.getBend().equals("")){
            object.setBend("1");
        }
        if (object.getParentId()==null || object.getParentId().equals("")){
            object.setParentId("0");
        }
        object.setJici(object.getProjectClassCode().length()/2+"");
        //上级分类是否末级状态修改
        Mono mono = projectClassRepository.findById(object.getParentId())
                .map(item -> {
                    item.setBend("0");
                    return item;
                }).flatMap(projectClassRepository::save);
        if (object.getUniqueCode() == null || object.getUniqueCode().equals("")) {
            object.setUniqueCode(IdUtil.objectId());
        }
        return mono
                .defaultIfEmpty(object)
                .flatMap(item -> projectClassRepository.save(object))
                .defaultIfEmpty(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupProjectClass object){
        Mono mono2 = projectClassRepository.findByParentIdOrderByProjectClassCode(object.getParentId())
                .collectList()
                .flatMap(item -> {
                    if (item.size()>0){
                        return projectClassRepository.findById(object.getParentId())
                                .map(aa -> {
                                    aa.setBend("1");
                                    return aa;
                                }).flatMap(projectClassRepository::save);
                    }
                    return projectClassRepository.findById(object.getParentId())
                            .map(aa -> {
                                aa.setBend("0");
                                return aa;
                            }).flatMap(projectClassRepository::save);
                });
        /*if(object.getParentId()!="0"){
            Mono mono1 = projectClassRepository.deleteByItemCodeAndProjectClassCode(object.getProjectCateCode(), object.getProjectClassCode()+"%");
            return mono1
                    .defaultIfEmpty(object)
                    .flatMap(item -> mono2)
                    .then(Mono.just(R.ok()));
        }*/
        Mono mono1 = projectClassRepository.deleteByProjectClassCode(object.getProjectClassCode()+"%");
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> mono2)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findClassByBend")
    public Mono findClassByBend(){
        return projectClassRepository.findByBendOrderByProjectClassCode("1").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByProjectCateCodeAndNotJiciOrderByProjectClassCode")
    public Mono<Map> findByProjectCateCodeAndNotJiciOrderByProjectClassCode() {
        return projectClassRepository.findByNotJiciOrderByProjectClassCode()
                .collectList()
                .map(list -> TreeUtils.castTreeList(list, ProjectClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findById(String id){
        return projectClassRepository.findById(id)
                .map(o-> R.ok().setResult(o));
    }

}
