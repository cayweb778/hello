package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.account.Receipt;
import org.boozsoft.domain.entity.account.ReceiptHeader;
import org.boozsoft.domain.entity.account.ReceiptHeaderRollback;
import org.boozsoft.domain.entity.account.ReceiptRollback;
import org.boozsoft.domain.vo.ReceiptVo;
import org.boozsoft.repo.ReceiptHeaderRepository;
import org.boozsoft.repo.ReceiptHeaderRollbackRepository;
import org.boozsoft.repo.ReceiptRepository;
import org.boozsoft.repo.ReceiptRollbackRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/receipt")
public class ReceiptController {

    @Autowired
    ReceiptHeaderRepository receiptHeaderRepository;
    @Autowired
    ReceiptRepository receiptRepository;
    @Autowired
    ReceiptHeaderRollbackRepository receiptHeaderRollbackRepository;
    @Autowired
    ReceiptRollbackRepository receiptRollbackRepository;

    @Autowired
    SysLogService sysLogService;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return receiptHeaderRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByReceCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByReceCode(String receCode) {
        return receiptHeaderRepository.findByReceCodeOrderById(receCode).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findReceiptByUniqueCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findReceiptByUniqueCode(String uniqueCode){
        return receiptRepository.findByUniqueCodeOrderById(uniqueCode).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody ReceiptVo vo){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        ReceiptHeader header = new ReceiptHeader();
        // 表头的逻辑
        Mono<ReceiptHeader> mono1 = Mono.fromSupplier(
                () -> {
                    BeanUtil.copyProperties(vo,header);
                    if (header.getReceDate()!=null && !header.getReceDate().equals("")) {
                        header.setReceDate(header.getReceDate().substring(0, 10));
                    }
                    return header;
                })
                .flatMap(receiptHeaderRepository::save);
        //表体逻辑
        Mono<List<Receipt>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                        .map(item -> {
                            if (item.getStockName() != null && !item.getStockName().equals("")) {
                                item.setUniqueCode(header.getId());
                            }
                            return item;
                        }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(receiptRepository::saveAll)
                .collectList();
        //增加
        if (vo.getId()==null || vo.getId().equals("")) {
            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】增加了一条财务收据,收据编码：【" + vo.getReceCode() + "】,收据名称：【" + vo.getReceName() + "】");
            return mono1
                    .flatMap(item -> mono2)
                    .flatMap(item -> sysLogService.save_log(sysLog))
                    .map(item -> R.ok().setResult(item));
        }
        //修改
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】修改了一条财务收据,收据编码：【" + vo.getReceCode() + "】,收据名称：【" + vo.getReceName() + "】");
        //表头回滚表添加记录
        Mono<ReceiptHeaderRollback> mono3 = receiptHeaderRepository.findById(vo.getId())
                .map(item -> {
                    ReceiptHeaderRollback rollback = new ReceiptHeaderRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(receiptHeaderRollbackRepository::save);
        //表体回滚表添加记录
        Mono<List<ReceiptRollback>> mono4 = receiptRepository.findByUniqueCodeOrderById(vo.getId())
                .collectList()
                .map(itemList -> {
                    List<ReceiptRollback> list = new ArrayList<ReceiptRollback>();
                    for (Receipt item : itemList) {
                        ReceiptRollback rollback = new ReceiptRollback();
                        BeanUtils.copyProperties(item, rollback);
                        rollback.setId(null);
                        rollback.setBiandongId(item.getId());
                        rollback.setBiandongDate(time);
                        rollback.setBiandongMethod("1");
                        rollback.setBiandongUniqueCode("a001");
                        rollback.setBiandongName("test");
                        list.add(rollback);
                    }
                    return list;
                })
                .flatMapMany(receiptRollbackRepository::saveAll)
                .collectList();
        return mono3
                .flatMap(item -> mono4)
                .flatMap(item -> mono1)
                .flatMap(item -> mono2)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody ReceiptHeader object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName("test");
        sysLog.setAccId("bjxgkj-001");
        sysLog.setIyear(year);
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】删除了一条财务收据,收据编码：【" + object.getReceCode() + "】,收据名称：【" + object.getReceName() + "】");
        //表头回滚表添加记录
        Mono mono3 = receiptHeaderRepository.findById(object.getId())
                .map(item -> {
                    ReceiptHeaderRollback rollback = new ReceiptHeaderRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("2");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(receiptHeaderRollbackRepository::save);
        //表体回滚表添加记录
        Mono mono4 = receiptRepository.findByUniqueCodeOrderById(object.getId()).collectList()
                .map(itemList -> {
                    List<ReceiptRollback> list = new ArrayList<ReceiptRollback>();
                    for (Receipt item : itemList) {
                        ReceiptRollback rollback = new ReceiptRollback();
                        BeanUtils.copyProperties(item, rollback);
                        rollback.setId(null);
                        rollback.setBiandongId(item.getId());
                        rollback.setBiandongDate(time);
                        rollback.setBiandongMethod("2");
                        rollback.setBiandongUniqueCode("a001");
                        rollback.setBiandongName("test");
                        list.add(rollback);
                    }
                    return list;
                })
                .flatMapMany(receiptRollbackRepository::saveAll)
                .collectList();
        return mono3
                .defaultIfEmpty(object)
                .flatMap(item -> mono4)
                .defaultIfEmpty(object)
                .flatMap(item -> receiptHeaderRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> receiptRepository.deleteByUniqueCode(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> sysLogService.save_log(sysLog))
                .then(Mono.just(R.ok()));
    }

}
