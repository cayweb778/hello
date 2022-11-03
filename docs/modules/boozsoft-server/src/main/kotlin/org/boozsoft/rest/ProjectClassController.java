package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.ProjectClass;
import org.boozsoft.domain.entity.account.ProjectClassRollback;
import org.boozsoft.domain.vo.ProjectClassVo;
import org.boozsoft.repo.ProjectClassRepository;
import org.boozsoft.repo.ProjectClassRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/project/class")
public class ProjectClassController {

    @Autowired
    ProjectClassRepository projectClassRepository;

    @GetMapping("findListByCate")
    public Mono<R> findListByCate() {
        return projectClassRepository.findAllByOrderByProjectClassCode()
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    public Mono<R> findAll(String itemCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")){
            return projectClassRepository.findByProjectCateCodeOrderByProjectClassCode(itemCode)
                    .collectList().map(o -> R.ok().setResult(o));
        }
        return projectClassRepository.findAllByOrderByProjectClassCode()
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("treeProClass")
    public Mono<Map> treeProClass(String projectCateCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")){
            return projectClassRepository.findByProjectCateCodeOrderByProjectClassCode(projectCateCode)
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, ProjectClassVo.class))
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        }
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

    @GetMapping("findByProjectCateCodeAndNotJiciOrderByProjectClassCode")
    public Mono<Map> findByProjectCateCodeAndNotJiciOrderByProjectClassCode(String projectCateCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")){
            return projectClassRepository.findByProjectCateCodeAndNotJiciOrderByProjectClassCode(projectCateCode)
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, ProjectClassVo.class))
                    .map(list -> CollectOfUtils.mapof(
                            "code", 0,
                            "message", "ok",
                            "type", "success",
                            "result", list
                    ));
        }
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

    @GetMapping("findByCode")
    public Mono<R> findByCode(String projectClassCode, String projectCateCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")){
            return projectClassRepository.findByProjectClassCodeAndProjectCateCodeOrderByProjectClassCode(projectClassCode,projectCateCode)
                    .collectList().map(o -> R.ok().setResult(o));
        }
        return projectClassRepository.findByProjectClassCodeOrderByProjectClassCode(projectClassCode)
                .collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody ProjectClass object){
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
        if (object.getId()==null || object.getId().equals("")) {
            if (object.getUniqueCode() == null || object.getUniqueCode().equals("")) {
                object.setUniqueCode(IdUtil.objectId());
            }
            return mono
                    .defaultIfEmpty(object)
                    .flatMap(item->projectClassRepository.save(object))
                    .defaultIfEmpty(object)
                    .map(o -> R.ok().setResult(o));
//            return Mono.zip(mono
//                    ,projectClassRepository.save(object).map(item->"").switchIfEmpty(Mono.just(""))
//                    ,sysLogService.save_log(sysLog))
//                    .map(o -> R.ok().setResult(o));
        }
        return mono
                .defaultIfEmpty(object)
                .flatMap(item -> projectClassRepository.save(object))
                .defaultIfEmpty(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody ProjectClass object) {
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
        if(object.getProjectCateCode()!=null && !object.getProjectCateCode().equals("")){
            Mono mono1 = projectClassRepository.deleteByItemCodeAndProjectClassCode(object.getProjectCateCode(), object.getProjectClassCode()+"%");
            return mono1
                    .defaultIfEmpty(object)
                    .flatMap(item -> mono2)
                    .then(Mono.just(R.ok()));
        }
        Mono mono1 = projectClassRepository.deleteByProjectClassCode(object.getProjectClassCode()+"%");
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> mono2)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findClassByBend")
    public Mono findClassByBend(String itemCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")){
            return projectClassRepository.findByBendAndProjectCateCodeOrderByProjectClassCode("1",itemCode).collectList().map(o -> R.ok().setResult(o));
        }
        return projectClassRepository.findByBendOrderByProjectClassCode("1").collectList().map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteByProjectCateCode")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteByProjectCateCode(String projectCateCode) {
        return projectClassRepository.deleteByProjectCateCode(projectCateCode)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findById")
    public Mono<R> findById(String id) {
        return projectClassRepository.findById(id).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    public Mono<R> findByUniqueCode(String uniqueCode) {
        return projectClassRepository.findByUniqueCode(uniqueCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }


}
