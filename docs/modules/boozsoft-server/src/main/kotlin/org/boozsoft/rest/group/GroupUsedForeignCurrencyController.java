/**
 * --- 财税达NC代码生成器 ---
 * --- 档案名：GroupUsedForeignCurrency ---
 */

package org.boozsoft.rest.group;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.UsedForeignCurrency;
import org.boozsoft.domain.entity.organize.OrgUsedForeignCurrency;
import org.boozsoft.service.group.impl.GroupUsedForeignCurrencyServiceImpl;
import org.boozsoft.domain.entity.group.GroupUsedForeignCurrency;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/UsedForeignCurrency")
public class GroupUsedForeignCurrencyController {

    @Autowired
    GroupUsedForeignCurrencyServiceImpl service;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        List<GroupUsedForeignCurrency> datalist=new ArrayList<>();
        GroupUsedForeignCurrency a1=new GroupUsedForeignCurrency();
        a1.setBeiyong1("美国");
        a1.setForeignCode("PYG");
        a1.setForeignName("美元");
        a1.setForeignSimpName("1PAB=100 centesimos（分）");
        datalist.add(a1);
        GroupUsedForeignCurrency a2=new GroupUsedForeignCurrency();
        a2.setBeiyong1("欧洲货币联盟");
        a2.setForeignCode("SYP");
        a2.setForeignName("欧元");
        a2.setForeignSimpName("1XOF=100 centimes（分）");
        datalist.add(a2);
        GroupUsedForeignCurrency a3=new GroupUsedForeignCurrency();
        a3.setBeiyong1("英国");
        a3.setForeignCode("VND");
        a3.setForeignName("英镑");
        a3.setForeignSimpName("1ZMK=100 nywee（恩韦）");
        datalist.add(a3);
        GroupUsedForeignCurrency a4=new GroupUsedForeignCurrency();
        a4.setBeiyong1("日本");
        a4.setForeignCode("PAB");
        a4.setForeignName("日元");
        a4.setForeignSimpName("1LBP=100 piastres（皮阿斯特）");
        datalist.add(a4);
        return service.findAll(pageable).collectList()
                .flatMap(list->list.size()>0?Mono.just(list):service.saveList(datalist).collectList())
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("org/findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllOrg(Pageable pageable) {
        return service.findAllOrg(pageable).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("acc/findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllAcc(Pageable pageable) {
        List<UsedForeignCurrency> datalist=new ArrayList<>();
        UsedForeignCurrency a1=new UsedForeignCurrency();
        a1.setBeiyong1("美国");
        a1.setForeignCode("PYG");
        a1.setForeignName("美元");
        a1.setForeignSimpName("1PAB=100 centesimos（分）");
        datalist.add(a1);
        UsedForeignCurrency a2=new UsedForeignCurrency();
        a2.setBeiyong1("欧洲货币联盟");
        a2.setForeignCode("SYP");
        a2.setForeignName("欧元");
        a2.setForeignSimpName("1XOF=100 centimes（分）");
        datalist.add(a2);
        UsedForeignCurrency a3=new UsedForeignCurrency();
        a3.setBeiyong1("英国");
        a3.setForeignCode("VND");
        a3.setForeignName("英镑");
        a3.setForeignSimpName("1ZMK=100 nywee（恩韦）");
        datalist.add(a3);
        UsedForeignCurrency a4=new UsedForeignCurrency();
        a4.setBeiyong1("日本");
        a4.setForeignCode("PAB");
        a4.setForeignName("日元");
        a4.setForeignSimpName("1LBP=100 piastres（皮阿斯特）");
        datalist.add(a4);

        return service.findAllAcc(pageable).collectList()
            .flatMap(list->list.size()>0?Mono.just(list):service.saveAccList(datalist).collectList())
            .map(o -> R.ok().setResult(o));
    }

    @GetMapping("acc/findAvailables")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAvailablesAcc() {
        return service.findAvailablesAcc().collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("org/findAvailables")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAvailablesOrg(String uniqueCode) {
        return service.findAvailablesOrg(uniqueCode).collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findById(@RequestBody Map params) {
        return service.findById(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }
    @GetMapping("org/findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findByIdOrg(@RequestBody Map params) {
        return service.findByIdOrg(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }

    @GetMapping("acc/findById")
    @ApiOperation(value = "查询Id", notes = "传入Id")
    public Mono<R> findByIdAcc(@RequestBody Map params) {
        return service.findByIdAcc(params.get("id").toString()).map(o -> R.ok().setResult(o));
    }


    @PostMapping("save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> save(@RequestBody GroupUsedForeignCurrency entity) {
        return service.save(entity).map(o -> R.ok().setResult(o));
    }

    @PostMapping("org/save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> saveOrg(@RequestBody OrgUsedForeignCurrency entity) {
        return service.saveOrg(entity).map(o -> R.ok().setResult(o));
    }

    @PostMapping("acc/save")
    @ApiOperation(value = "保存（新增或修改）", notes = "传入实体类")
    public Mono<R> saveAcc(@RequestBody UsedForeignCurrency entity) {
        return service.saveAcc(entity).map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteById(@RequestBody Map params) {
        return service.deleteById(params.get("id").toString()).thenReturn(R.ok());
    }

    @DeleteMapping("org/deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteByIdOrg(@RequestBody Map params) {
        return service.deleteByIdOrg(params.get("id").toString()).thenReturn(R.ok());
    }

    @DeleteMapping("acc/deleteById")
    @ApiOperation(value = "通过ID删除", notes = "传入Id")
    public Mono<R> deleteByIdAcc(@RequestBody Map params) {
        return service.deleteByIdAcc(params.get("id").toString()).thenReturn(R.ok());
    }

    @GetMapping("duplicate")
    @ApiOperation(value = "查询重复", notes = "传入code")
    public Mono duplicate(String id, String value, String type) {
        return (type.equals("code") || type.equals("name")) ? service.duplicateCheckQuery(value, type).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                })
                : Mono.just(R.ok().setResult(-1));
    }

    @GetMapping("org/duplicate")
    @ApiOperation(value = "查询重复", notes = "传入code")
    public Mono duplicateOrg(String id, String value, String type,String uniqueCode) {
        return (type.equals("code") || type.equals("name")) ? service.duplicateCheckQueryOrg(value, type,uniqueCode).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                })
                : Mono.just(R.ok().setResult(-1));
    }

    @GetMapping("acc/duplicate")
    @ApiOperation(value = "查询重复", notes = "传入code")
    public Mono duplicateAcc(String id, String value, String type) {
        return (type.equals("code") || type.equals("name")) ? service.duplicateCheckQueryAcc(value, type).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                })
                : Mono.just(R.ok().setResult(-1));
    }





}