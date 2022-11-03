package org.boozsoft.service.impl;

import org.boozsoft.repo.SysZoneRepository;
import org.boozsoft.service.SysZoneService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class SysZoneServiceImpl implements SysZoneService {
    @Autowired
    SysZoneRepository sysZoneRepository;


    @Override
    
    public Mono<R> findAll() {
        return sysZoneRepository.findAll().collectList().map(o->R.ok().setResult(o));
    }
}
