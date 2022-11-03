package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysSettModes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysSettModesRepository extends ReactiveCrudRepository<GroupSysSettModes, String> {

    Flux<GroupSysSettModes> findAllByOrderById(Pageable pageable);
    Mono<GroupSysSettModes> findBySettModesCodeOrderById(String settModesCode);
    Flux<GroupSysSettModes> findByFlagOrderById(String flag);

}
