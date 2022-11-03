package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.group.Bank;
import org.boozsoft.repo.BankRepository;
import org.boozsoft.service.BankService;

import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BankServiceImpl implements BankService {

    @Autowired
    BankRepository bankRepository;

    @Override
    
    public Mono sys_save(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    
    public Mono acc_save(Bank bank) {
        return bankRepository.save(bank);
    }
}
