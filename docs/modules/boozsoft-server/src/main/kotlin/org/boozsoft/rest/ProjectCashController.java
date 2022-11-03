package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.group.GroupProjectCashCode;
import org.boozsoft.domain.entity.origin.OriginProjectCash;
import org.boozsoft.domain.entity.origin.OriginProjectCashCode;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.group.GroupFaAccountRepository;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.origin.OriginProjectCashCodeRepository;
import org.boozsoft.repo.origin.OriginProjectCashRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/projectCash")
public class ProjectCashController {

    @Autowired
    ProjectCashRepository projectCashRepository;
    @Autowired
    ProjectCashCodeRepository projectCashCodeRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    GroupProjectCashRepository groupprojectCashRepository;
    @Autowired
    GroupSysAccountRepository groupSysAccountRepository;
    @Autowired
    GroupProjectCashCodeRepository groupprojectCashCodeRepository;
    @Autowired
    OriginProjectCashRepository originProjectCashRepository;
    @Autowired
    OriginProjectCashCodeRepository originProjectCashCodeRepository;
    @Autowired
    SysLogService sysLogService;
    @Autowired
    GroupStockAccountRepository groupStockAccountRepository;
    @Autowired
    GroupFaAccountRepository groupFaAccountRepository;

    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map map){
        String year = map.get("year").toString();
        return projectCashRepository.findAllByOrderByProjectCode(year).collectList()
                .map(list->{
                   return list.stream().filter(v->{
                       if(map.containsKey("projectType") && (map.get("projectType").toString().equals("0") || map.get("projectType").toString().length() > 2)){
                           return true;
                       }
                       if(map.containsKey("projectType") && !map.get("projectType").toString().equals(v.getProjectType()) ){
                           return false;
                       }
                       return true;
                   });
                })
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList(){
        return projectCashRepository.findAllByOrderByProjectCode()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String projectCode) {
        return projectCashRepository.findByProjectCodeOrderById(projectCode).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody ProjectCash object){
        //对应科目保存
        Mono<List<ProjectCashCode>> mono2 = projectCashCodeRepository.saveAll(object.getTable()).collectList();
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
    public Mono editFlag(@RequestBody ProjectCash object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return projectCashRepository.save(object).map(item->R.ok().setResult(item));
    }

    @GetMapping("findCodeByYearAndBend")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByYearAndBend(String year){
        return codeKemuRepository.findByIyearOrderByCcode(year)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByProjectCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByProjectCode(String projectCode,String iyear){
        return projectCashCodeRepository.findByProjectCodeAndIyearOrderById(projectCode,iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByIyear")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByIyear(String iyear){
        return projectCashCodeRepository.findByIyearOrderById(iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<ProjectCash> object){
        return projectCashRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("initData")
    @ApiOperation(value = "初始化数据", notes = "初始化数据")
    public Mono<R> initData(String accId,String flg,String year){
        //根据账套查询会计准则  根据会计准则查询对象预制数据 然后插入数据
        Map map = new HashMap();
        //集团账套
        Mono<R> map1 = groupSysAccountRepository.findAllByAccCountPartColumn(accId)
                .flatMap(obj -> {
                    return originProjectCashRepository.findAllByOriginIdAndIyearAndAccStandard(obj.getAccGroup(), year, obj.getUniqueAccStandard()).collectList()
                            .map(list->{
                                map.put("cashList",list);
                                return obj;
                            });
                })
                .flatMap(obj -> {
                    return originProjectCashCodeRepository.findAllByOriginIdAndIyearAndAccStandard(obj.getAccGroup(), year, obj.getUniqueAccStandard()).collectList()
                            .map(list->{
                                map.put("codeList",list);
                                return map;
                            });
                })
                .flatMap(m -> {
                    List<OriginProjectCash> list = (List<OriginProjectCash>)m.get("cashList");
                    return projectCashRepository.deleteAll().then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<ProjectCash> l = new ArrayList<>();
                    list.forEach(v -> {
                        ProjectCash p = new ProjectCash();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        l.add(p);
                    });
                    return projectCashRepository.saveAll(l).then(Mono.just(map));
                })
                .flatMap(m -> {
                    List<OriginProjectCashCode> list = (List<OriginProjectCashCode>)m.get("codeList");
                    return projectCashCodeRepository.deleteAll().then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<ProjectCashCode> l = new ArrayList<>();
                    list.forEach(v -> {
                        ProjectCashCode p = new ProjectCashCode();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        l.add(p);
                    });
                    return projectCashCodeRepository.saveAll(l).then(Mono.just("200"));
                })
                .map(o -> R.ok().setResult(o));
        //独立账套
        Mono<R> map2 = groupSysAccountRepository.findAllByAccCountPartColumn(accId)
                .flatMap(obj -> {
                    return groupprojectCashRepository.findAllByAccStandard(obj.getUniqueAccStandard()).collectList()
                            .map(list->{
                                map.put("cashList",list);
                                return obj;
                            });
                })
                .flatMap(obj -> {
                    return groupprojectCashCodeRepository.findAllByAccStandard(obj.getUniqueAccStandard()).collectList()
                            .map(list->{
                                map.put("codeList",list);
                                return map;
                            });
                })
                .flatMap(m -> {
                    List<GroupProjectCash> list = (List<GroupProjectCash>)m.get("cashList");
                    return projectCashRepository.deleteAll().then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<ProjectCash> l = new ArrayList<>();
                    list.forEach(v -> {
                        ProjectCash p = new ProjectCash();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        l.add(p);
                    });
                    return projectCashRepository.saveAll(l).then(Mono.just(map));
                })
                .flatMap(m -> {
                    List<GroupProjectCashCode> list = (List<GroupProjectCashCode>)m.get("codeList");
                    return projectCashCodeRepository.deleteAll().then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<ProjectCashCode> l = new ArrayList<>();
                    list.forEach(v -> {
                        ProjectCashCode p = new ProjectCashCode();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        p.setIyear(year);
                        l.add(p);
                    });
                    return projectCashCodeRepository.saveAll(l).then(Mono.just("200"));
                })
                .map(o -> R.ok().setResult(o));

        return "1".equals(flg)?map2:map1;
    }

    @GetMapping("isGroup")
    @ApiOperation(value = "是否是集团", notes = "是否是集团")
    public Mono<R> isGroup(String accId, String type){
        //存货 财务 固定资产 
        if(type.contains("ZZ")){
            return groupSysAccountRepository.findFirstByAccIdOrderByAccId(accId)
                    .map(obj-> Objects.isNull(obj.getIndependent())?"1":obj.getIndependent())
                    .map(o -> R.ok().setResult(o));
        }else if(type.contains("CH")){
            return  groupStockAccountRepository.findByStockAccId(accId)
                    .map(obj-> Objects.isNull(obj.getIndependent())?"1":obj.getIndependent())
                    .map(o -> R.ok().setResult(o));
        }else if(type.contains("GZ")){
            return groupFaAccountRepository.findFirstByAccIdOrderByAccId(accId)
                    .map(obj-> Objects.isNull(obj.getIndependent())?"1":obj.getIndependent())
                    .map(o -> R.ok().setResult(o));
        }else{
            return Mono.just( R.ok());
        }
    }


    @GetMapping("getTypeList")
    @ApiOperation(value = "所属类别list", notes = "所属类别list")
    public Mono<R> getTypeList(){
        return projectCashRepository.findAllByTypeGroup().collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("/getTotalData")
    @ApiOperation(value = "获取所有数据总条数", notes = "获取所有数据总条数")
    public Mono getTotalData(){
        return projectCashRepository.findAll()
                .collectList()
                .map(v-> v.size())
                .map(v-> R.ok().setResult(v));
    }


    @PostMapping("/saveMoney")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveMoney(@RequestBody ProjectCash object){
        //对应科目保存
        return projectCashRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }
    @GetMapping("/delAll")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono delAll(){
        //对应科目保存
        return projectCashRepository.updateMoney()
                .then(Mono.just(R.ok()));
    }

    @PostMapping("excelQc")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excelQc(@RequestBody List<ProjectCash> object){
        List<String> collect = object.stream().map(v -> v.getProjectCode()).collect(Collectors.toList());
        return projectCashRepository.findAllByProjectCodes(collect)
                .collectList()
                .map(list->{
                    list.forEach(o->{
                        Optional<ProjectCash> first = object.stream().filter(obj -> o.getProjectCode().equals(obj.getProjectCode())).findFirst();
                        o.setBeiyong1(first.get().getBeiyong1());
                    });
                    return list;
                })
                .flatMap(list-> projectCashRepository.saveAll(list).collectList())
                .map(o-> R.ok().setResult(o));
    }
}
