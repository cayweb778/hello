package org.boozsoft.service.$$platformName$$.impl;

import org.boozsoft.service.$$platformName$$.$$RecordName$$Service;
import org.boozsoft.domain.entity.$$platformName$$.$$RecordName$$;
import org.boozsoft.repo.$$platformName$$.$$RecordName$$Repository;
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
public class $$RecordName$$ServiceImpl implements $$RecordName$$Service {


    @Autowired
    $$RecordName$$Repository repository;


    @Override
    public Flux<$$RecordName$$> findAll(Pageable pageable) {
        return repository.findAll();
    }


    @Override
    public Mono<$$RecordName$$> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<$$RecordName$$> save(@RequestParam $$RecordName$$ entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}