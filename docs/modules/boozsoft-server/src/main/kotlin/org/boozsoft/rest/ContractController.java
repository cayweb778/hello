package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.account.Contract;
import org.boozsoft.domain.entity.account.ContractBiaodi;
import org.boozsoft.domain.entity.account.ContractBiaodiRollback;
import org.boozsoft.domain.entity.account.ContractRollback;
import org.boozsoft.domain.vo.ContractVo;
import org.boozsoft.repo.ContractBiaodiRepository;
import org.boozsoft.repo.ContractBiaodiRollbackRepository;
import org.boozsoft.repo.ContractRepository;
import org.boozsoft.repo.ContractRollbackRepository;
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
@RequestMapping("/contract")
public class ContractController {

    @Autowired
    SysLogService sysLogService;
    @Autowired
    ContractRepository contractRepository;
    @Autowired
    ContractBiaodiRepository contractBiaodiRepository;
    @Autowired
    ContractRollbackRepository contractRollbackRepository;
    @Autowired
    ContractBiaodiRollbackRepository contractBiaodiRollbackRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findAll(Pageable pageable){
        return contractRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByCode(String conNum){
        return contractRepository.findByConNumOrderById(conNum).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByBiaodi")
    @ApiOperation(value = "查询列表", notes = "传入code")
    
    public Mono<R> findByBiaodi(String conId){
        return contractBiaodiRepository.findByConIdOrderById(conId).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    
    public Mono save(@RequestBody ContractVo vo){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());

        Contract contract = new Contract();
        //合同表逻辑
        Mono<Contract> mono1 = Mono.fromSupplier(
                () -> {
                    BeanUtil.copyProperties(vo,contract);
                    if (vo.getConDate()!=null && !vo.getConDate().equals("")){
                        contract.setConDate(vo.getConDate().substring(0,10));
                    }
                    if (vo.getShengxiaoDate()!=null && !vo.getShengxiaoDate().equals("")){
                        contract.setShengxiaoDate(vo.getShengxiaoDate().substring(0,10));
                    }
                    if (vo.getStartDate()!=null && !vo.getStartDate().equals("")){
                        contract.setStartDate(vo.getStartDate().substring(0,10));
                    }
                    if (vo.getEndDate()!=null && !vo.getEndDate().equals("")){
                        contract.setEndDate(vo.getEndDate().substring(0,10));
                    }
                    return contract;
                }).flatMap(contractRepository::save);
        //合同标的表逻辑
        Mono<List<ContractBiaodi>> mono2 = Mono.fromSupplier(
                () -> Flux.fromIterable(vo.getTable())
                        .map(item -> {
                            if (item.getBiaodiCount() != null && !item.getBiaodiCount().equals("")) {
                                item.setConId(contract.getId());
                            }
                            return item;
                        }))
                .flatMapMany(item->item)
                .collectList()
                .flatMapMany(contractBiaodiRepository::saveAll)
                .collectList();
        //增加
        if (vo.getId()==null || vo.getId().equals("")) {
            return mono1
                    .flatMap(item -> mono2)
                    .map(o-> R.ok().setResult(o));
        }
        //合同回滚表添加记录
        Mono<ContractRollback> mono3 = contractRepository.findById(vo.getId())
                .map(item -> {
                    ContractRollback rollback = new ContractRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("1");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(contractRollbackRepository::save);
        //合同标的回滚表添加记录
        Mono<List<ContractBiaodiRollback>> mono4 = contractBiaodiRepository.findByConIdOrderById(vo.getId())
                .collectList()
                .map(itemList -> {
                    List<ContractBiaodiRollback> list = new ArrayList<ContractBiaodiRollback>();
                    for (ContractBiaodi item : itemList) {
                        ContractBiaodiRollback rollback = new ContractBiaodiRollback();
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
                .flatMapMany(contractBiaodiRollbackRepository::saveAll)
                .collectList();
        return mono3
                .flatMap(item -> mono4)
                .flatMap(item -> mono1)
                .flatMap(item -> mono2)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono delete(@RequestBody Contract object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //合同回滚表添加记录
        Mono mono3 = contractRepository.findById(object.getId())
                .map(item -> {
                    ContractRollback rollback = new ContractRollback();
                    BeanUtils.copyProperties(item, rollback);
                    rollback.setId(null);
                    rollback.setBiandongId(item.getId());
                    rollback.setBiandongDate(time);
                    rollback.setBiandongMethod("2");
                    rollback.setBiandongUniqueCode("a001");
                    rollback.setBiandongName("test");
                    return rollback;
                }).flatMap(contractRollbackRepository::save);
        //合同标的回滚表添加记录
        Mono mono4 = contractBiaodiRepository.findByConIdOrderById(object.getId())
                .collectList()
                .map(itemList -> {
                    List<ContractBiaodiRollback> list = new ArrayList<ContractBiaodiRollback>();
                    for (ContractBiaodi item : itemList) {
                        ContractBiaodiRollback rollback = new ContractBiaodiRollback();
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
                .flatMapMany(contractBiaodiRollbackRepository::saveAll)
                .collectList();
        return mono3
                .defaultIfEmpty(object)
                .flatMap(item -> mono4)
                .defaultIfEmpty(object)
                .flatMap(item -> contractRepository.deleteById(object.getId()))
                .defaultIfEmpty(object)
                .flatMap(item -> contractBiaodiRepository.deleteByConId(object.getId()))
                .defaultIfEmpty(object)
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteBiaodi")
    @ApiOperation(value = "删除", notes = "传入code")
    
    public Mono deleteBiaodi(String id){
        return contractBiaodiRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

}
