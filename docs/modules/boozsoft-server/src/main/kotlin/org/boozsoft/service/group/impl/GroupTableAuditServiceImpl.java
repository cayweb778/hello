package org.boozsoft.service.group.impl;

import org.boozsoft.domain.entity.group.GroupTableAudit;
import org.boozsoft.repo.group.GroupTableAuditRepository;
import org.boozsoft.service.group.GroupTableAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * 科目模板
 */
@Service
public class GroupTableAuditServiceImpl implements GroupTableAuditService {


    @Autowired
    GroupTableAuditRepository repository;


    @Override
    public Flux<GroupTableAudit> findAll(Pageable pageable) {
        return repository.findAll();
    }


    @Override
    public Mono<GroupTableAudit> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<GroupTableAudit> save(@RequestParam GroupTableAudit entity) {
        return repository.save(entity);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return repository.deleteById(id);
    }
}