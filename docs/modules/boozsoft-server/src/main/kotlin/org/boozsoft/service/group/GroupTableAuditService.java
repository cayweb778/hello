package org.boozsoft.service.group;

import org.boozsoft.domain.entity.group.GroupTableAudit;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupTableAuditService {
    Flux<GroupTableAudit> findAll(Pageable pageable);

    Mono<GroupTableAudit> findById(String id);

    Mono<GroupTableAudit> save(GroupTableAudit entity);

    Mono<Void> deleteById(String id);
}