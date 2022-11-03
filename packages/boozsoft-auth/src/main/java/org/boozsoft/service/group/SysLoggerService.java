package org.boozsoft.service.group;

import org.boozsoft.domain.entity.group.SysLogger;
import org.springbooz.core.tool.result.page.PageResult;
import reactor.core.publisher.Mono;

import org.springframework.data.domain.Pageable;

public interface SysLoggerService {
    Mono<PageResult<SysLogger>> findAll(Pageable pageable);

    Mono<SysLogger> findById(String id);

    Mono<SysLogger> save(SysLogger entity);

    Mono<Void> deleteById(String id);
}