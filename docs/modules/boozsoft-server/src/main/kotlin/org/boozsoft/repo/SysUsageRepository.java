package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysUsage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysUsageRepository extends ReactiveCrudRepository<SysUsage, String> {
    Mono<SysUsage> findByServerName(String name);
}

