package org.boozsoft.rest.origin;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.origin.OriginProject;
import org.boozsoft.repo.origin.OriginProjectRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/originProject")
public class OriginProjectController {

    @Autowired
    OriginProjectRepository projectRepository;

    @GetMapping("findByClass")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable,String projectCateCode,String projectClassCode,String originId) {
        if (projectClassCode!=null && !projectClassCode.equals("") && !projectClassCode.equals("0") && !projectClassCode.equals("undefined")){
            return projectRepository.findByProjectClassCodeAndProjectCateCodeAndOriginIdOrderByProjectCode(pageable,projectClassCode,projectCateCode,originId)
                    .collectList()
                    .flatMap(item-> projectRepository.countByProjectClassCodeAndProjectCateCodeAndOriginId(projectClassCode,projectCateCode,originId)
                            .map(total->R.page(item,pageable,total)));
        }
        return projectRepository.findByProjectCateCodeAndOriginIdOrderByProjectCode(pageable,projectCateCode,originId).collectList()
                .flatMap(item-> projectRepository.countByProjectCateCodeAndOriginId(projectCateCode,originId)
                        .map(total->R.page(item,pageable,total)));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody OriginProject project) {
        if (project.getUniqueCode() == null || project.getUniqueCode().equals("")) {
            project.setUniqueCode(IdUtil.objectId());
        }
        if (project.getStartDate() != null && !project.getStartDate().equals("")) {
            project.setStartDate(project.getStartDate().substring(0, 10));
        }
        if (project.getEndDate() != null && !project.getEndDate().equals("")) {
            project.setEndDate(project.getEndDate().substring(0, 10));
        }
        return projectRepository.save(project)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody OriginProject project){
        return projectRepository.deleteById(project.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<OriginProject> project){
        return projectRepository.deleteAll(project)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByCode(String projectClassCode, String projectCode,String originId){
        return projectRepository.findByProjectCodeAndProjectClassCodeAndOriginIdOrderById(projectCode,projectClassCode,originId)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String projectClassCode, String projectName,String originId){
        return projectRepository.findByProjectCodeAndProjectClassCodeAndOriginIdOrderById(projectName,projectClassCode,originId)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<OriginProject> object){
        return Flux.fromIterable(object).map(item -> {
                    if (item.getUniqueCode() == null || item.getUniqueCode().equals("")) {
                        item.setUniqueCode(IdUtil.objectId());
                    }
                    return item;
                })
                .collectList()
                .flatMapMany(projectRepository::saveAll)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByCateCode")
    @ApiOperation(value = "根据项目大类查询项目信息", notes = "传入code")
    public Mono<R> findByCateCode(String projectCateCode,String originId) {
        return projectRepository.findByProjectCateCodeAndOriginIdOrderByProjectCode(projectCateCode,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String originId){
        return projectRepository.findAllByOriginIdOrderByProjectCode(originId)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    @ApiOperation(value = "根据唯一码查询项目信息", notes = "传入code")
    public Mono<R> findByUniqueCode(String uniqueCode,String originId) {
        return projectRepository.findByUniqueCodeAndOriginId(uniqueCode,originId)
                .map(o -> R.ok().setResult(o));
    }

}
