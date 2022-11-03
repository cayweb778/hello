package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaEconomyUse;
import org.boozsoft.domain.entity.group.GroupFaEconomyUse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaEconomyUseRepository extends ReactiveCrudRepository<GroupFaEconomyUse, String> {

    Flux<GroupFaEconomyUse> findAllByOrderById(Pageable pageable);
    Mono<GroupFaEconomyUse> findByBsCodeOrderById(String code);
    Mono<GroupFaEconomyUse> findByBsNameOrderById(String bsName);
    Flux<GroupFaEconomyUse> findByFlagOrderById(String flag);
}
