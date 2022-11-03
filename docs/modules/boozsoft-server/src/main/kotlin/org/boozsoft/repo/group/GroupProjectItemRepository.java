package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupProjectItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectItemRepository extends ReactiveCrudRepository<GroupProjectItem, String> {

    Flux<GroupProjectItem> findAllByOrderByItemCode(Pageable pageable);
    Flux<GroupProjectItem> findAllByOrderByItemCode();
    Flux<GroupProjectItem> findByFlagOrderByItemCode(String flag);
    Mono<Long> countAllBy();
    Flux<GroupProjectItem> findByItemCodeOrderByItemCode(String itemCode);
    Flux<GroupProjectItem> findByItemNameOrderByItemCode(String itemName);

}

