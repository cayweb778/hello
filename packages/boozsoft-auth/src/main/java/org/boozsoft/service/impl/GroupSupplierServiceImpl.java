package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.SysDistRecord;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.SysNotice;
import org.boozsoft.domain.entity.group.GroupSupplier;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.GroupSupplierService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/4/1 10:47 上午
 */
@Service
public class GroupSupplierServiceImpl implements GroupSupplierService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupSupplierRepository supplierRepository;
    @Autowired
    GroupDistRepository groupDistRepository;
    @Autowired
    SysLogRepository sysLogRepository;
    @Autowired
    SysDistReocrdRepository sysDistReocrdRepository;
    @Autowired
    SysNoticeRepository sysNoticeRepository;
    @Autowired
    SupplierRollBackRepository supplierRollBackRepository;


    public Flux<CustomerVo> findAll() {
        return supplierRepository.findAlls();
    }


    /**
     * 集团表中增加
     *
     * @return
     */
    
    public Mono sys_save(GroupSupplier supplier) {
        return supplierRepository.save(supplier).map(o -> R.ok().setResult(o));
    }

    /**
     * 系统日志表增加
     *
     * @return
     */
    
    public Mono syslog_save(String text) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return sysLogRepository.save(
                new SysLog().setOperationDate(time)
                        .setUniqueCode("a001")
                        .setUserName("test")
                        .setAccId("bjxgkj-001")
                        .setIyear("2021")
                        .setOperationCont(text)
        ).map(o -> R.ok().setResult(o));
    }

    /**
     * 集团档案分配表
     *
     * @param databaseUniqueCode 账套唯一码
     * @param recordUniqueCode   档案唯一码
     * @param recordType         档案类型
     * @return
     */
    
    public Mono sysDistRecord_save(String databaseUniqueCode, String recordUniqueCode, String recordType) {
        return  sysDistReocrdRepository.countByDatabaseUniqueCodeAndRecordUniqueCodeAndRecordType(databaseUniqueCode,recordUniqueCode,recordType)
                .map(item->item)
                .flatMap(item2->{
                    if(item2==0){
                        return sysDistReocrdRepository.save(new SysDistRecord().setRecordType(recordType).setRecordUniqueCode(recordUniqueCode).setDatabaseUniqueCode(databaseUniqueCode))
                                .map(o -> Mono.just(o));
                    }
                    return Mono.just("");
                })
                .map(o -> R.ok().setResult(o));
    }


    /**
     * 审批通过-账套表增加
     *
     * @param supplier
     * @return
     */
    
    public Mono approveOkSave_database(GroupSupplier supplier) {
        return supplierRepository.findByUniqueCode(supplier.getUniqueCode())
                .map(x->x.getId())
                .flatMap(id->{
                    return supplierRepository.deleteById(id).then(Mono.just(supplier));
                })
                .map(item->item)
                .flatMap(supplierRepository::save)
                .map(o -> R.ok().setResult(o));
    }

    /**
     * 修改状态：开启 自动分配（集团/已经下发过的账套 全更新）
     *
     * @param accId        账套编码
     * @param uniqueCode   客户唯一码
     * @param customerMono 客户实体类
     * @return
     */
    @Override
    public Mono updateIsAutoSaveApi(String accId, String uniqueCode, GroupSupplier customerMono) {
        System.out.println("暂缓开发，动态切换数据库没实现");
        System.out.println(accId + ">>>");
        return Mono.just(R.ok());
    }

    /**
     * 系统通知信息表
     * 未获取session中数据【写死】
     *
     * @return
     */
    
    public Mono sysnotice_save() {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return sysNoticeRepository.save(
                new SysNotice()
                        .setCorpUniqueCode("abc001")
                        .setUserUniqueCode("user001")
                        .setNoticeType("审核")
                        .setNoticeTime(time)
                        .setNoticeContent("你申请的数据已审批通过，请查收！")
                        .setReceiveRole("001")
                        .setReceiveUser("test")
                        .setNoticeState("0")
        ).map(o -> R.ok().setResult(o));
    }

    
    public Mono findBySuccessStateAndUniqueCode(String state, String unique) {
        return supplierRepository.findBySuccessStateAndUniqueCode(state,unique).map(o -> R.ok().setResult(o));
    }

    /**
     * 根据ID删除系统客户信息
     * @param id
     * @return
     */
    
    public Mono delFindById(String id) {
        return supplierRepository.deleteById(id).then();
    }
}
