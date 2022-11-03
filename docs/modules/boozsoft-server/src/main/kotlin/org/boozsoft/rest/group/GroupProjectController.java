package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupProject;
import org.boozsoft.repo.group.GroupProjectRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupProject")
public class GroupProjectController {

    @Autowired
    GroupProjectRepository projectRepository;

    @GetMapping("findByClass")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable,String projectCateCode,String projectClassCode) {
        if (projectClassCode!=null && !projectClassCode.equals("") && !projectClassCode.equals("0") && !projectClassCode.equals("undefined")){
            return projectRepository.findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(pageable,projectClassCode,projectCateCode,"0")
                    .collectList()
                    .flatMap(item-> projectRepository.countByProjectClassCodeAndProjectCateCodeAndIsDel(projectClassCode,projectCateCode,"0")
                            .map(total->R.page(item,pageable,total)));
        }
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(pageable,projectCateCode,"0").collectList()
                .flatMap(item-> projectRepository.countByProjectCateCodeAndIsDel(projectCateCode,"0")
                        .map(total->R.page(item,pageable,total)));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupProject project) {
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
    public Mono delete(@RequestBody GroupProject project){
        return projectRepository.deleteById(project.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<GroupProject> project){
        return projectRepository.deleteAll(project)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByCode(String projectClassCode, String projectCode){
        return projectRepository.findByProjectCodeAndProjectClassCodeOrderById(projectCode,projectClassCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String projectClassCode, String projectName){
        return projectRepository.findByProjectNameAndProjectClassCodeOrderById(projectName,projectClassCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<GroupProject> object){
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
    public Mono<R> findByCateCode(String projectCateCode) {
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(){
        return projectRepository.findAll()
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findDelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDelList() {
        return projectRepository.findAllByIsDelOrderByProjectCode("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    @ApiOperation(value = "根据唯一码查询项目信息", notes = "传入code")
    public Mono<R> findByUniqueCode(String uniqueCode) {
        return projectRepository.findByUniqueCode(uniqueCode)
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCtypeAndOriginId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCtypeAndOriginId(String originId) {
        return projectRepository.findByCtypeAndOriginId("1", originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCtypeAndTenantId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCtypeAndTenantId(String tenantId) {
        return projectRepository.findByCtypeAndTenantId("1", tenantId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByNotSuccessState")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNotSuccessState() {
        return projectRepository.findByNotSuccessState()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findBukongProjectCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findBukongProjectCode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return projectRepository.findBukongProjectCode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findMaxProjectCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findMaxProjectCode(Integer num,Integer sum,Integer qzNum,String qianzhui) {
        return projectRepository.findMaxProjectCode(num,sum,qzNum,qianzhui)
                .map(item->R.ok().setResult(item));
    }

}
