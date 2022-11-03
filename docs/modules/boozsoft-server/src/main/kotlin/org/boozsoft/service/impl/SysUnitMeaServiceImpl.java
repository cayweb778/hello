package org.boozsoft.service.impl;

import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.service.SysUnitMeaService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class SysUnitMeaServiceImpl implements SysUnitMeaService {
    @Autowired
    SysUnitOfMeaRepository sysUnitOfMeaRepository;


    @Override
    
    public Mono<R> getMenterageList() {
        return sysUnitOfMeaRepository.findAll().collectList().map(o->R.ok().setResult(o));
    }
}
