package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupUserOperatAuthColumn;
import org.reactivestreams.Publisher;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface GroupUserOperatAuthColumnRepository extends ReactiveCrudRepository<GroupUserOperatAuthColumn, String> {
    Flux<GroupUserOperatAuthColumn> findAllByPlatformMarkOrderByIdAsc(String mark);
}
