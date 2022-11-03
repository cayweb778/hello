package org.boozsoft.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.*;
import org.boozsoft.repo.*;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/exchangeRate")
public class ExchangeRateController {

    @Autowired
    UsedForeignCurrencyRepository usedForeignCurrencyRepository;
    @Autowired
    ExchangeRateEntryRepository exchangeRateEntryRepository;
    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    @Autowired
    ExchangeRateEntryRollbackRepository exchangeRateEntryRollbackRepository;
    @Autowired
    ExchangeRateRollbackRepository exchangeRateRollbackRepository;
    @Autowired
    CurrencyRepository currencyRepository;
    @Autowired
    VoucherTypeRepository voucherTypeRepository;
    @Autowired
    VoucherTypeRollbackRepository voucherTypeRollbackRepository;
    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")

    public Mono<R> findAll(Pageable pageable) {
        return voucherTypeRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("currency")
    @ApiOperation(value = "查询有效货币信息", notes = "")
    public Mono<R> currencyList() {
        return currencyRepository.findAllByOrderByNumAsc().collectList().map(R::page);
    }

    @GetMapping("foreignCurrency")
    @ApiOperation(value = "查询常用外币信息", notes = "")
    public Mono<R> foreignCurrency(String accId) {
        return usedForeignCurrencyRepository.findAllByAccountIdOrderByForeignCodeAsc(accId).collectList().cache().map(R::page);
    }

    @PostMapping("foreignCurrency")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> foreignCurrency(@RequestBody UsedForeignCurrency object) {
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
        if (object.getId() == null || object.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】向【" + object.getAccountId() + "】里增加了一条常用外币,外币编码：【" + object.getForeignCode() + "】,外币简称：【" + object.getForeignName() + "】");
            return usedForeignCurrencyRepository.save(object)
                    .zipWith(sysLogService.save_log(sysLog))
                    .map(o -> R.ok());

        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】对【" + object.getAccountId() + "】修改了一条常用外币,外币编码：【" + object.getForeignCode() + "】,外币简称：【" + object.getForeignName() + "】");
        //回滚表添加记录
/*        Mono<VoucherTypeRollback> mono1 = voucherTypeRepository.findById(object.getId())
                .map(item -> {
                    VoucherTypeRollback rollback = new VoucherTypeRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(voucherTypeRollbackRepository::save);
        return mono1
                .flatMap(item -> voucherTypeRepository.save(object))
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o-> R.ok().setResult(o));
                */
        return usedForeignCurrencyRepository.save(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(o -> R.ok());
    }

    @DeleteMapping("foreignCurrency")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody UsedForeignCurrency object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】对【" + object.getAccountId() + "】删除了一条常用外币,外币编码：【" + object.getForeignCode() + "】,外币简称：【" + object.getForeignName() + "】");
        //添加回滚表记录
          /*  VoucherTypeRollback rollback = new VoucherTypeRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = usedForeignCurrencyRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> voucherTypeRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));*/
        return usedForeignCurrencyRepository.delete(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

    @GetMapping
    @ApiOperation(value = "查询汇率基础信息", notes = "")
    public Mono<R> findAllExchange(String accId, String iyear) {
        return exchangeRateRepository.findFirstByAccountIdAndIyear(accId, iyear).map(o -> R.ok().setResult(o));
    }

    @GetMapping("all")
    @ApiOperation(value = "查询所有汇率基础信息", notes = "")
    public Mono<R> findAllExchange(String iyear) {
        return exchangeRateRepository.findAllByIyear(iyear).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @Transactional
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> exchangeRate(@RequestBody ExchangeRate object) {
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        String year = new SimpleDateFormat("yyyy").format(new Date());
//        //系统操作日志
//        SysLog sysLog = new SysLog();
//        sysLog.setOperationDate(time);
//        sysLog.setUniqueCode("a001");
//        sysLog.setUserName("test");
//        sysLog.setAccId("bjxgkj-001");
//        sysLog.setIyear(year);
        //增加
        if (object.getId() == null || object.getId().equals("")) {
            object.setId(null);
//            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】对【"+object.getAccountId()+"】增加了一条汇率信息,汇率类型为：【" + object.getRateStyle() + "】");
            return exchangeRateRepository.save(object)
//                    .zipWith(sysLogService.save_log(sysLog))
                    .flatMap(dbEntity -> {
                        if (StrUtil.isNotBlank(dbEntity.getBeiyong2()) && dbEntity.getBeiyong2().equals("1")) {//初始化
                            return generatePeriod(dbEntity).flatMap(list -> exchangeRateEntryRepository.saveAll(list).collectList()).thenReturn(dbEntity);
                        }
                        return Mono.just(dbEntity);
                    })
                    .map(o -> R.ok(o));

        }
        //修改
//        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】对【"+object.getAccountId()+"】增加了一条汇率信息,汇率类型为：【" + object.getRateStyle() + "】");
        //回滚表添加记录
//        Mono<ExchangeRateRollback> mono1 = exchangeRateRepository.findById(object.getId())
//                .map(item -> {
//                    ExchangeRateRollback rollback = new ExchangeRateRollback();
//                    BeanUtils.copyProperties(item, rollback);
//                    rollback.setId(null);
//                    rollback.setBiandongId(item.getId());
//                    rollback.setBiandongDate(time);
//                    rollback.setBiandongMethod("1");
//                    rollback.setBiandongUniqueCode("a001");
//                    rollback.setBiandongName("test");
//                    return rollback;
//                }).flatMap(exchangeRateRollbackRepository::save);
        return /*mono1
                .flatMap(item -> */exchangeRateRepository.save(object)/*)
                .flatMap(item -> sysLogService.save_log(sysLog))*/.map(o -> R.ok());
    }

    @DeleteMapping
    @Transactional
    @ApiOperation(value = "删除信息与分录", notes = "传入code")
    public Mono deleteInfo(@RequestBody ExchangeRate object) {
        return exchangeRateRepository.findById(object.getId())
                .flatMap(
                        item -> exchangeRateEntryRepository.findAllByUniqueCodeAndCurrencyCodeOrderByIperiodAsc(item.getId(), item.getForeignCode())
                                .collectList().flatMap(exchangeRateEntryRepository::deleteAll).thenReturn(item).flatMap(exchangeRateRepository::delete)
                )
                .then(Mono.just(R.ok()));
    }

    private Mono<List<ExchangeRateEntry>> generatePeriod(ExchangeRate dbEntity) {
        List<ExchangeRateEntry> list = new ArrayList<>();
        if (dbEntity.getRateStyle().equals("MONTH")) {
            for (int i = 1; i <= 12; i++) {
                list.add(new ExchangeRateEntry(dbEntity.getId(), (dbEntity.getIyear() + "-" + (i < 10 ? "0" + i : i)), "0.22", DateUtil.now(), dbEntity.getForeignCode()));
            }
        } else {
            for (int i = 1; i <= 31; i++) {
                list.add(new ExchangeRateEntry(dbEntity.getId(), (dbEntity.getIyear() + "-01-" + (i < 10 ? "0" + i : i)), "0.22", DateUtil.now(), dbEntity.getForeignCode()));
            }
        }
        return Mono.just(list);
    }

    @GetMapping("entry")
    @ApiOperation(value = "查询汇率分录", notes = "")
    public Mono<R> findAllExchangeRateEntry(String uniqueCode, String date, String currencyCode) {
        return exchangeRateEntryRepository.findAllByUniqueCodeAndCurrencyCodeAndIperiodLikeOrderByIperiodAsc(uniqueCode, currencyCode, date + "%")
                .filter(entity -> date.length() == 4 ? entity.getIperiod().length() == 7 : entity.getIperiod().length() == 10)
                .collectList().map(R::page);
    }

    @PostMapping("entry")
    @ApiOperation(value = "新增或修改分录", notes = "传入code")
    public Mono<R> exchangeRateEntry(@RequestBody ExchangeRateEntry object) {
       /* String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);*/
        //增加
        if (object.getId() == null || object.getId().equals("")) {
            /*   sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条汇率分录数据,所属期间：【" + object.getIperiod() + "】");*/
            return exchangeRateEntryRepository.save(object)
                    /* .zipWith(sysLogService.save_log(sysLog))*/
                    .map(o -> R.ok());

        }
        //修改
        /*sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条汇率分录数据,汇率期间：【" + object.getIperiod() + "】");*/
        //回滚表添加记录
   /*     Mono<ExchangeRateEntryRollback> mono1 = exchangeRateEntryRepository.findById(object.getId())
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
                }).flatMap(exchangeRateEntryRollbackRepository::save);*/
        return /*mono1
                .flatMap(item ->*/ exchangeRateEntryRepository.save(object)/*)*/
                /*.flatMap(item -> sysLogService.save_log(sysLog))*/.map(o -> R.ok());
    }


    @DeleteMapping("entry")
    @ApiOperation(value = "删除分录", notes = "传入code")
    public Mono delete(@RequestBody ExchangeRateEntry object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】进行了删除了一条汇率分录数据,日期【" + object.getIperiod() + "】,外币编码：【" + object.getCurrencyCode() + "】");
        //添加回滚表记录
        ExchangeRateEntryRollback rollback = new ExchangeRateEntryRollback();
        BeanUtils.copyProperties(object, rollback);
        rollback.setId(null);
        rollback.setBiandongId(object.getId());
        rollback.setBiandongMethod("2");
        rollback.setBiandongUniqueCode("a001");
        rollback.setBiandongName("test");
        Mono mono1 = exchangeRateEntryRollbackRepository.save(rollback);
        return mono1
                .defaultIfEmpty(object)
                .flatMap(item -> exchangeRateEntryRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
