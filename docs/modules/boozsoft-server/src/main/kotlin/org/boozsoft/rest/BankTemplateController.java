package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.BankTemplate;
import org.boozsoft.domain.entity.account.BankTemplateColumn;
import org.boozsoft.domain.vo.BankTemplateVo;
import org.boozsoft.repo.BankTemplateColumnRepository;
import org.boozsoft.repo.BankTemplateRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/bankTemplate")
public class BankTemplateController {

    @Autowired
    BankTemplateRepository bankTemplateRepository;
    @Autowired
    BankTemplateColumnRepository bankTemplateColumnRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return bankTemplateRepository.findAll()
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByFlag(){
        return bankTemplateRepository.findByFlagOrderById("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByName(String templateName){
        return bankTemplateRepository.findByTemplateNameOrderById(templateName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByColumn")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByColumn(String templateId){
        return bankTemplateColumnRepository.findByTemplateIdOrderById(templateId)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody BankTemplateVo vo){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);

        BankTemplate bankTemplate = new BankTemplate();
        //模板表逻辑
        Mono<BankTemplate> mono1 = Mono.fromSupplier(
                () -> {
                    BeanUtil.copyProperties(vo,bankTemplate);
                    if (bankTemplate.getFlag()==null || bankTemplate.equals("")) {
                        bankTemplate.setFlag("1");
                    }
                    return bankTemplate;
                }).flatMap(bankTemplateRepository::save);
        //模板栏目表逻辑
        Mono<List<BankTemplateColumn>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                        .map(item -> {
                            if (item.getSystemField() != null && !item.getSystemField().equals("")) {
                                item.setTemplateId(bankTemplate.getId());
                            }
                            return item;
                        }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(bankTemplateColumnRepository::saveAll)
                .collectList();
        //增加
        if (vo.getId()==null || vo.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条银行对账单模板,模板名称：【" + vo.getTemplateName() + "】");
            return mono1
                    .flatMap(item -> mono2)
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行对账单模板,模板名称：【" + vo.getTemplateName() + "】");
        return mono1
                .flatMap(item -> mono2)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody BankTemplate object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条银行对账单模板,模板名称：【" + object.getTemplateName() + "】");
        //根据id删除模板表记录
        Mono mono1 = bankTemplateRepository.deleteById(object.getId());
        //根据主表id删除模板栏目表记录
        Mono mono2 = bankTemplateColumnRepository.deleteByTemplateId(object.getId());
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> mono2)
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    
    public Mono editFlag(@RequestBody BankTemplate object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("admin");
        sysLog.setUserName("admin");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        if (object.getFlag().equals("1")){
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】停用了一条银行对账单模板,模板名称：【" + object.getTemplateName() + "】");
            object.setFlag("0");
        } else {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】启用了一条银行对账单模板,模板名称：【" + object.getTemplateName() + "】");
            object.setFlag("1");
        }
        //修改
        Mono mono1 = bankTemplateRepository.save(object);
        //增加系统日志表
        Mono mono2 = sysLogService.save_log(sysLog);
        return mono1
                .flatMap(item -> mono2)
                .map(o -> R.ok().setResult(o));
    }

}
