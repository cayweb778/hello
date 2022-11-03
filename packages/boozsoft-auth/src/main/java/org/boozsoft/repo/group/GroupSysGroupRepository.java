package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.AccClose;
import org.boozsoft.domain.entity.group.GroupSysGroup;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface GroupSysGroupRepository extends ReactiveCrudRepository<GroupSysGroup, String> {

    Mono<Long> countAllBy();
    Flux<GroupSysGroup> findFirstByGroupCode(String code);
    Flux<GroupSysGroup> findFirstByGroupName(String code);

}
