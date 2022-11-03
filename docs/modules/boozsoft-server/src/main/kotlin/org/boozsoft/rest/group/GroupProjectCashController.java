package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.group.GroupProjectCashCode;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.boozsoft.repo.GroupProjectCashCodeRepository;
import org.boozsoft.repo.GroupProjectCashRepository;
import org.boozsoft.repo.ProjectCashCodeRepository;
import org.boozsoft.repo.ProjectCashRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.codekemu.GroupCodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeKemuCountryRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupProjectCash")
public class GroupProjectCashController {

    @Autowired
    GroupProjectCashRepository projectCashRepository;
    @Autowired
    GroupProjectCashCodeRepository projectCashCodeRepository;
    @Autowired
    GroupCodeKemuRepository codeKemuRepository;

    @Autowired
    GroupCodeKemuCountryRepository groupCodeKemuCountryRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(){
        return projectCashRepository.findAllByOrderByProjectCode().collectList().map(R::page);
    }

    @GetMapping("findListByAccStandard")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findListByAccStandard(String accStandard){
        return projectCashRepository.findByAccStandardOrderById(accStandard).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String projectCode) {
        return projectCashRepository.findByProjectCodeOrderById(projectCode).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupProjectCash object){
        //对应科目保存 
        Mono<List<GroupProjectCashCode>> mono2 = projectCashCodeRepository.saveAll(object.getTable()).collectList();
        return projectCashRepository.save(object)
                .flatMap(item -> mono2)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody ProjectCash object){
        return projectCashRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupProjectCash object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return projectCashRepository.save(object).map(item->R.ok().setResult(item));
    }

    @GetMapping("findCodeByYearAndBend")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByYearAndBend(String accStandard){
        return groupCodeKemuCountryRepository.findAllByUniqueAccStandardOrderByCcode(accStandard)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByProjectCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByProjectCode(String projectCode,String iyear){
        return projectCashCodeRepository.findByProjectCodeAndAccStandardOrderById(projectCode,iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByIyear")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByIyear(String iyear){
        return projectCashCodeRepository.findByAccStandardOrderById(iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("check")
    @ApiOperation(value = "校验", notes = "校验")
    public Mono<R> check(String iyear){
        return null;
    }

    @GetMapping("getTypeList")
    @ApiOperation(value = "所属类别list", notes = "所属类别list")
    public Mono<R> getTypeList(String accStandard){
        return projectCashRepository.findAllByTypeGroup(accStandard).collectList()
                .map(o -> R.ok().setResult(o));
    }

}
