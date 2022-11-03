package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.ReimRollback;
import org.boozsoft.domain.entity.account.Reimbursement;
import org.boozsoft.repo.ReimRollbackRepository;
import org.boozsoft.repo.ReimbursementRepository;
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
@RequestMapping("/reimbursement")
public class ReimbursementController {

    @Autowired
    ReimbursementRepository reimbursementRepository;
    @Autowired
    ReimRollbackRepository reimRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return reimbursementRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String reimCode){
        return reimbursementRepository.findByReimCodeOrderById(reimCode).map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody Reimbursement object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条报销单,单据编码：【" + object.getReimCode() + "】,单据名称：【" + object.getReimName() + "】");
            return reimbursementRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条报销单,单据编码：【" + object.getReimCode() + "】,单据名称：【" + object.getReimName() + "】");
        //回滚表添加记录
        Mono<ReimRollback> mono1 = reimbursementRepository.findById(object.getId())
                .map(item -> {
                    ReimRollback rollback = new ReimRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(reimRollbackRepository::save);
        return mono1
                .flatMap(item -> reimbursementRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody Reimbursement object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条报销单,单据编码：【" + object.getReimCode() + "】,单据名称：【" + object.getReimName() + "】");
        //添加回滚表记录
        ReimRollback rollback = new ReimRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = reimRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> reimbursementRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
