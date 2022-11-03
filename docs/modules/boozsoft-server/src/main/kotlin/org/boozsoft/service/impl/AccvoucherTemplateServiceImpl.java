package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import org.boozsoft.repo.SysAccvoucherTemplateRepository;
import org.boozsoft.service.AccvoucherTemplateService;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * @author LZ
 * @version 1.0
 * @title 操作人员
 * @company 财税达软件科技
 * @date 2021/4/1 10:47 上午
 */
@Service
public class AccvoucherTemplateServiceImpl implements AccvoucherTemplateService {
    @Autowired
    SysAccvoucherTemplateRepository sysAccvoucherTemplateRepository;


    
    public Mono<SysAccvoucherTemplate> findByAccTemplateId(int id) {
        return sysAccvoucherTemplateRepository.findById(id);
    }
}
