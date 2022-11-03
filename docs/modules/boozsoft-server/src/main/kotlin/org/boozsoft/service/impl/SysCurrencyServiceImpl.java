package org.boozsoft.service.impl;

import org.boozsoft.repo.CurrencyRepository;
import org.boozsoft.service.SysCurrencyService;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class SysCurrencyServiceImpl implements SysCurrencyService {
    @Autowired
    CurrencyRepository currencyRepository;


    @Override
    
    public Mono<R> getCurrencyList() {
        return currencyRepository.findAll().collectList().map(o->R.ok().setResult(o));
    }
}
