package org.boozsoft.service.impl;

import org.boozsoft.repo.AccountAccvoucherCdigestRepository;
import org.boozsoft.service.AccountAccvoucherCdigestService;
import org.boozsoft.domain.entity.account.AccountAccvoucherCdigest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

import java.util.List;


/**
 * 科目模板
 */
@Service
public class AccountAccvoucherCdigestServiceImpl implements AccountAccvoucherCdigestService {


    @Autowired
    AccountAccvoucherCdigestRepository repository;


    @Override
    public Flux<AccountAccvoucherCdigest> findAll(Pageable pageable) {
        return repository.findAllByOrderByCcode();
    }


    @Override
    public Mono<AccountAccvoucherCdigest> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<AccountAccvoucherCdigest> save(@RequestParam AccountAccvoucherCdigest entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<AccountAccvoucherCdigest> saveAll(@RequestParam List<AccountAccvoucherCdigest> entity) {
        return repository.saveAll(entity);
    }
}