package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysAccvoucherTemplate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysAccvoucherTemplateRepository extends ReactiveCrudRepository<SysAccvoucherTemplate, String> {
    Mono<SysAccvoucherTemplate> findById(int id);
}

