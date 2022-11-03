package org.boozsoft.service.impl;

import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.repo.*;
import org.boozsoft.service.SupplierService;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/4/1 10:47 上午
 */
@Service
public class SupplierServiceImpl implements SupplierService {
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    DatabaseClient client;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SupplierRepository supplierRepository;
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


    public Flux<CustomerVo> findAllCustomerByDatabase(String test) {
        if (StringUtils.isBlank(test) || "0".equals(test)) {
            // 查询集团表表是否有未生效的单据,并且查询账套客户信息
            return supplierRepository.findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState();
        } else {
            return supplierRepository.findAllByUniqueCustclass(test);
        }
    }
}
