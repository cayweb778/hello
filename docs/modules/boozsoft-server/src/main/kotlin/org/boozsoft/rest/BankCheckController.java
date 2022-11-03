package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.bankbus.BankCheck;
import org.boozsoft.domain.entity.account.bankbus.BankCheckRollback;
import org.boozsoft.repo.bankbus.BankCheckRepository;
import org.boozsoft.repo.bankbus.BankCheckRollbackRepository;
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
@RequestMapping("/bankcheck")
public class BankCheckController {

    @Autowired
    BankCheckRepository bankCheckRepository;
    @Autowired
    BankCheckRollbackRepository bankCheckRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return bankCheckRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String checkNum){
        return bankCheckRepository.findByCheckNumOrderById(checkNum).map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody BankCheck object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);

        if (object.getCheckDate()!=null && !object.getCheckDate().equals("")){
            object.setCheckDate(object.getCheckDate().substring(0,10));
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条银行支票,支票编号：【" + object.getCheckNum() + "】,支票银行：【" + object.getCheckName() + "】");
            return bankCheckRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条银行支票,支票编号：【" + object.getCheckNum() + "】,支票银行：【" + object.getCheckName() + "】");
        //回滚表添加记录
        Mono<BankCheckRollback> mono1 = bankCheckRepository.findById(object.getId())
                .map(item -> {
                    BankCheckRollback rollback = new BankCheckRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(bankCheckRollbackRepository::save);
        return mono1
                .flatMap(item -> bankCheckRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody BankCheck object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条银行支票,支票编号：【" + object.getCheckNum() + "】,支票银行：【" + object.getCheckName() + "】");
        //回滚表添加记录
        BankCheckRollback rollback = new BankCheckRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongDate(time);
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = bankCheckRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> bankCheckRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
