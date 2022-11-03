package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.bankbus.BankBill;
import org.boozsoft.domain.entity.account.bankbus.BankBillRollback;
import org.boozsoft.repo.bankbus.BankBillRepository;
import org.boozsoft.repo.bankbus.BankBillRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/bankbill")
public class BankBillController {

    @Autowired
    BankBillRepository bankBillRepository;
    @Autowired
    BankBillRollbackRepository bankBillRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return bankBillRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String billNum){
        return bankBillRepository.findByBillNumOrderById(billNum).map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody BankBill object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);

        if (object.getBillDate()!=null && !object.getBillDate().equals("")){
            object.setBillDate(object.getBillDate().substring(0,10));
        }
        if (object.getDueDate()!=null && !object.getDueDate().equals("")){
            object.setDueDate(object.getDueDate().substring(0,10));
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条银行汇票,汇票编号：【" + object.getBillNum() + "】,出票人全称：【" + object.getBillName() + "】");
            return bankBillRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行汇票,汇票单号：【" + object.getBillNum() + "】,出票人全称：【" + object.getBillName() + "】");
        //回滚表添加记录
        Mono<BankBillRollback> mono1 = bankBillRepository.findById(object.getId())
                .map(item -> {
                    BankBillRollback rollback = new BankBillRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(bankBillRollbackRepository::save);
        return mono1
                .flatMap(item -> bankBillRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody BankBill object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条银行汇票,汇票单号：【" + object.getBillNum() + "】,出票人全称：【" + object.getBillName() + "】");
        //回滚表添加记录
        BankBillRollback rollback = new BankBillRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = bankBillRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> bankBillRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
