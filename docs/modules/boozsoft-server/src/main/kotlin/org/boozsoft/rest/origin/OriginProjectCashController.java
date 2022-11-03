package org.boozsoft.rest.origin;

import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.group.GroupProjectCashCode;
import org.boozsoft.domain.entity.origin.OriginProjectCash;
import org.boozsoft.domain.entity.origin.OriginProjectCashCode;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.boozsoft.repo.*;
import org.boozsoft.repo.group.GroupCodeKemuCountryRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.boozsoft.repo.group.GroupSysOrgRepository;
import org.boozsoft.repo.origin.OriginProjectCashCodeRepository;
import org.boozsoft.repo.origin.OriginProjectCashRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/originProjectCash")
public class OriginProjectCashController {

    @Autowired
    OriginProjectCashRepository projectCashRepository;
    @Autowired
    OriginProjectCashCodeRepository originprojectCashCodeRepository;
    @Autowired
    GroupSysOrgRepository groupSysOrgRepository;
    @Autowired
    GroupProjectCashRepository groupprojectCashRepository;
    @Autowired
    GroupProjectCashCodeRepository groupprojectCashCodeRepository;
    @Autowired
    GroupSysAccountRepository sysAccountRepository;

    @Autowired
    GroupCodeKemuOrgRepository originCodeKemuCountryRepository;

    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map map){
        String orientation = map.get("orientation").toString();
        String orgIyear = map.get("orgIyear").toString();
        return projectCashRepository.findAllByOrderByProjectCode(orientation, orgIyear).collectList()
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
    public Mono save(@RequestBody OriginProjectCash object){
        //对应科目保存  
        Mono<List<OriginProjectCashCode>> mono2 = originprojectCashCodeRepository.saveAll(object.getTable()).collectList();
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
    public Mono editFlag(@RequestBody OriginProjectCash object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return projectCashRepository.save(object).map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<OriginProjectCash> object){
        return projectCashRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("initData")
    @ApiOperation(value = "初始化数据", notes = "初始化数据")
    public Mono<R> initData(String groupId,String year){
        //根据账套查询会计准则  根据会计准则查询对象预制数据 然后插入数据
        Map map = new HashMap();
        return groupSysOrgRepository.findAllByAccCountPartColumn(groupId)
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
                    List<GroupProjectCash> list = (List<GroupProjectCash>)m.get("codeList");
                    return projectCashRepository.deleteAllByOriginIdAndIyear(groupId,year).then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<OriginProjectCash> l = new ArrayList<>();
                    list.forEach(v -> {
                        OriginProjectCash p = new OriginProjectCash();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        p.setOriginId(groupId);
                        p.setIyear(year);
                        l.add(p);
                    });
                    return projectCashRepository.saveAll(l).then(Mono.just(map));
                })
                .flatMap(m -> {
                    List<GroupProjectCashCode> list = (List<GroupProjectCashCode>)m.get("codeList");
                    return originprojectCashCodeRepository.deleteAllByOriginIdAndIyear(groupId,year).then(Mono.just(list));
                })
                .flatMap(list -> {
                    List<OriginProjectCashCode> l = new ArrayList<>();
                    list.forEach(v -> {
                        OriginProjectCashCode p = new OriginProjectCashCode();
                        BeanUtils.copyProperties(v, p);
                        p.setId(null);
                        p.setOriginId(groupId);
                        p.setIyear(year);
                        l.add(p);
                    });
                    return originprojectCashCodeRepository.saveAll(l).then(Mono.just("200"));
                })
                .map(o -> R.ok().setResult(o));
    }


    @GetMapping("getAccountList")
    public Mono<R> getAccountList(String groupId) {
        return sysAccountRepository.findAllByaccGroup(groupId).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("getTypeList")
    @ApiOperation(value = "所属类别list", notes = "所属类别list")
    public Mono<R> getTypeList(String accStandard,String originId,String iyear){
        return projectCashRepository.findAllByTypeGroup(accStandard,originId,iyear).collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByYearAndBend")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByYearAndBend(String year,String originId){
        return originCodeKemuCountryRepository.findAllByIyearAndOrgUniqueOrderByCcode(year,originId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findCodeByProjectCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findCodeByProjectCode(String projectCode,String accStandard,String originId,String iyear){
        return originprojectCashCodeRepository.findByProjectCodeAndAccStandardAndOriginIdAndIyearOrderById(projectCode,accStandard,originId,iyear)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
}
