package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.SysNotice;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.repo.GroupDistRepository;
import org.boozsoft.repo.SysNoticeRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.CustomerService;
import org.boozsoft.service.ProjectService;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
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
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    ProjectRepositoryBase projectRepository;

    @GetMapping("findByClass")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable,String projectCateCode,String projectClassCode) {
        if (projectClassCode != null && !projectClassCode.equals("") && !projectClassCode.equals("0") && !projectClassCode.equals("undefined")) {
            return projectRepository.findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(projectClassCode, projectCateCode, "0", pageable)
                    .collectList()
                    .flatMap(item -> projectRepository.countByProjectClassCodeAndProjectCateCodeAndIsDel(projectClassCode, projectCateCode,"0")
                            .map(total -> R.page(item, pageable, total)));
        }
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0",pageable).collectList()
                .flatMap(item-> projectRepository.countByProjectCateCodeAndIsDel(projectCateCode,"0")
                        .map(total->R.page(item,pageable,total)));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(String itemCode,String projectClassCtl){
        if (projectClassCtl!=null && projectClassCtl.equals("1")) {
            return projectRepository.findAllByItemCodeAndIsDelOrderByProjectCode(itemCode,"0")
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return projectRepository.findAllByIsDelOrderByProjectCode("0")
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody Project project) {
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
    public Mono delete(@RequestBody Project project){
        return projectRepository.deleteById(project.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<Project> project){
        return projectRepository.deleteAll(project)
                .then(Mono.just(R.ok()));
    }

    /*@GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByCode(String projectCode,String itemCode,String projectClassCtl){
        if (projectClassCtl.equals("1")) {
            return projectRepository.findByProjectCodeAndItemCodeOrderByProjectCode(projectCode,itemCode)
                    .map(item -> R.ok().setResult(item));
        }
        return projectRepository.findByProjectCodeOrderByProjectCode(projectCode)
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String projectName,String itemCode,String projectClassCtl){
        if (projectClassCtl.equals("1")) {
            return projectRepository.findByProjectNameAndItemCodeOrderByProjectCode(projectName,itemCode)
                    .map(item -> R.ok().setResult(item));
        }
        return projectRepository.findByProjectNameOrderByProjectCode(projectName)
                .map(item -> R.ok().setResult(item));
    }*/

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByCode(String projectClassCode, String projectCode){
        return projectRepository.findByProjectCodeAndProjectClassCodeOrderByProjectCode(projectCode,projectClassCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String projectClassCode, String projectName){
        return projectRepository.findByProjectNameAndProjectClassCodeOrderByProjectCode(projectName,projectClassCode)
                .collectList()
                .map(item -> R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<Project> object){
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
    public Mono<R> findByCateCode(String projectCateCode,String itemCode,String projectClassCtl) {
        if (projectClassCtl!=null && projectClassCtl.equals("1")) {
            /*return projectRepository.findByProjectCateCodeAndItemCodeAndIsDelOrderByProjectCode(projectCateCode,itemCode,"0")
                    .collectList().map(o -> R.ok().setResult(o));*/
            return projectRepository.findByItemCodeAndIsDelOrderByProjectCode(itemCode,"0")
                    .collectList()
                    .map(o -> R.ok().setResult(o));
        }
        return projectRepository.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCateCode,"0")
                .collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAll")
    public Mono<R> findAll() {
        return projectRepository.findAllByIsDelOrderByProjectCode("0").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findDelList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDelList() {
        return projectRepository.findAllByIsDelOrderByProjectCode("1")
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
