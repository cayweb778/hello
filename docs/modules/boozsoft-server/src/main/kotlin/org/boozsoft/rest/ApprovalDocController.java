package org.boozsoft.rest;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.ApprovalDoc;
import org.boozsoft.domain.entity.account.ApprovalDocRollback;
import org.boozsoft.repo.ApprovalDocRepository;
import org.boozsoft.repo.ApprovalDocRollbackRepository;
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
@RequestMapping("/approval")
public class ApprovalDocController {

    @Autowired
    ApprovalDocRepository approvalDocRepository;
    @Autowired
    ApprovalDocRollbackRepository approvalDocRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return approvalDocRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String docCode){
        return approvalDocRepository.findByDocCodeOrderById(docCode).map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody ApprovalDoc object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        if (object.getPizhunDate()!=null && !object.getPizhunDate().equals("")){
            object.setPizhunDate(object.getPizhunDate().substring(0,10));
        }
        if (object.getStartDate()!=null && !object.getStartDate().equals("")){
            object.setStartDate(object.getStartDate().substring(0,10));
        }
        if (object.getEndDate()!=null && !object.getEndDate().equals("")){
            object.setEndDate(object.getEndDate().substring(0,10));
        }
        if (object.getAwardDate()!=null && !object.getAwardDate().equals("")){
            object.setAwardDate(object.getAwardDate().substring(0,10));
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条批文信息,批文文号：【" + object.getDocCode() + "】,批文名称：【" + object.getDocName() + "】");
            return approvalDocRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o-> R.ok().setResult(o));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条批文信息,批文文号：【" + object.getDocCode() + "】,批文名称：【" + object.getDocName() + "】");
        //回滚表添加记录
        Mono<ApprovalDocRollback> mono1 = approvalDocRepository.findById(object.getId())
                .map(item -> {
                    ApprovalDocRollback rollback = new ApprovalDocRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(approvalDocRollbackRepository::save);
        return mono1
                .flatMap(item -> approvalDocRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody ApprovalDoc object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条批文信息,批文文号：【" + object.getDocCode() + "】,批文名称：【" + object.getDocName() + "】");
        //回滚表添加记录
        ApprovalDocRollback rollback = new ApprovalDocRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono<ApprovalDocRollback> mono1 = approvalDocRollbackRepository.save(rollback);
        return mono1
                .flatMap(item -> approvalDocRepository.deleteById(object.getId()))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
