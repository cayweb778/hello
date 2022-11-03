package org.boozsoft.service.group.impl;

import org.boozsoft.service.group.SysLoggerService;
import org.boozsoft.domain.entity.group.SysLogger;
import org.boozsoft.repo.group.SysLoggerRepository;
import org.springbooz.core.tool.result.page.PageResult;
import org.springbooz.core.tool.result.page.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;


/**
 * 科目模板
 */
@Service
public class SysLoggerServiceImpl implements SysLoggerService {


    @Autowired
    SysLoggerRepository repository;


    @Override
    public Mono<PageResult<SysLogger>> findAll(Pageable pageable) {
        return PageUtil.of(repository.findAllBy(pageable),repository.count(), pageable);
    }


    @Override
    public Mono<SysLogger> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<SysLogger> save(@RequestParam SysLogger entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}