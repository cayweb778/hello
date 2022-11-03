package org.boozsoft.service.impl;

import org.boozsoft.domain.entity.account.AccountAccvoucherCdigestClass;
import org.boozsoft.repo.AccountAccvoucherCdigestClassRepository;
import org.boozsoft.service.AccountAccvoucherCdigestClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;


/**
 * 科目模板
 */
@Service
public class AccountAccvoucherCdigestClassServiceImpl implements AccountAccvoucherCdigestClassService {


    @Autowired
    AccountAccvoucherCdigestClassRepository repository;


    @Override
    public Flux<AccountAccvoucherCdigestClass> findAll(Pageable pageable) {
        return repository.findAllByOrderByClassCode();
    }


    @Override
    public Mono<AccountAccvoucherCdigestClass> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<AccountAccvoucherCdigestClass> save(@RequestParam AccountAccvoucherCdigestClass entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}