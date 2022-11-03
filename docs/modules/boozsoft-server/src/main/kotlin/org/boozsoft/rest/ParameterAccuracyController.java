package org.boozsoft.rest;

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.ParameterAccuracy;
import org.boozsoft.repo.ParameterAccuracyRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/parameterAccuracy")
public class ParameterAccuracyController {

    @Autowired
    ParameterAccuracyRepository parameterAccuracyRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping()
    @ApiOperation(value = "查询参数精度列表", notes = "")
    public Mono<R> findAllExchangeRateEntry(String tenantId){
        return parameterAccuracyRepository.findAllByTenantIdOrderByParaNum(tenantId)
                .collectList().map(o->R.ok(o));
    }

    @PostMapping()
    @ApiOperation(value = "新增或修改分录", notes = "传入code")
    public Mono<R> exchangeRateEntry(@RequestBody ParameterAccuracy object){
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
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条参数精度数据,所属参数名称：【" + object.getParaName() + "】");
            object.setParaNum(RandomUtil.randomString(6));
            return parameterAccuracyRepository.save(object)
                 /*   .zipWith(sysLogService.save_log(sysLog))*/
                    .map(o-> R.ok(o));

        }
        //修改
        //sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条参数精度数据,所属参数名称：【" + object.getParaName() + "】");
        //回滚表添加记录
     /*   Mono<ExchangeRateEntryRollback> mono1 = exchangeRateEntryRepository.findById(object.getId())
                .map(item -> {
                    ExchangeRateEntryRollback rollback = new ExchangeRateEntryRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(exchangeRateEntryRollbackRepository::save);
        return mono1
                .flatMap(item -> exchangeRateEntryRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog)).map(o-> R.ok());*/
        return Mono.just("").flatMap(item->parameterAccuracyRepository.save(object))/*.map(item -> sysLogService.save_log(sysLog))*/.map(o-> R.ok());
    }


    @DeleteMapping()
    @ApiOperation(value = "删除分录", notes = "传入code")
    public Mono delete(@RequestBody  ParameterAccuracy object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】进行了删除了一条参数精度数据,所属参数名称：【" + object.getParaName() + "】");
       /* //添加回滚表记录
        ExchangeRateEntryRollback rollback = new ExchangeRateEntryRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = parameterAccuracyRepository.save(rollback);*/
        Mono mono1 = Mono.just("");
        return mono1
              /*  .defaultIfEmpty(object)*/
                .flatMap(item -> parameterAccuracyRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
               /* .flatMap(item -> sysLogService.save_log(sysLog))*/
                .then(Mono.just(R.ok()));
    }

}
