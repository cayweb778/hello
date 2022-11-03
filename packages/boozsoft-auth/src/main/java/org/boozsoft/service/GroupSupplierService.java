package org.boozsoft.service;

import org.boozsoft.domain.entity.group.GroupSupplier;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.SupplierVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSupplierService {
    Flux<CustomerVo> findAll();
    Mono sys_save(GroupSupplier sysCustomer);
    // 系统日志表，以后可能会传写参
    Mono syslog_save(String text);
    // 档案分配表
    Mono sysDistRecord_save(String databaseUniqueCode,String recordUniqueCode,String recordType);
    // 审批通过-账套表中增加
    Mono approveOkSave_database(GroupSupplier customer);
    // 修改状态：开启 自动分配（集团/已经下发过的账套 全更新）
    Mono updateIsAutoSaveApi(String accId,String uniqueCode,GroupSupplier customerMono);
    // 系统通知信息表
    Mono sysnotice_save();
    Mono delFindById(String id);
    Mono findBySuccessStateAndUniqueCode(String state,String unique);
}
