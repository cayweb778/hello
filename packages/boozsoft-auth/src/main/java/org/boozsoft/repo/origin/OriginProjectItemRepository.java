package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginProjectItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectItemRepository extends ReactiveCrudRepository<OriginProjectItem, String> {

    Flux<OriginProjectItem> findAllByOriginIdOrderByItemCode(Pageable pageable,String originId);
    Flux<OriginProjectItem> findAllByOriginIdOrderByItemCode(String originId);
    Flux<OriginProjectItem> findByFlagAndOriginIdOrderByItemCode(String flag,String originId);
    Mono<Long> countAllByOriginId(String originId);
    Flux<OriginProjectItem> findByItemCodeAndOriginIdOrderByItemCode(String itemCode,String originId);
    Flux<OriginProjectItem> findByItemNameAndOriginIdOrderByItemCode(String itemName,String originId);

}

