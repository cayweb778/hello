package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaAssetGroup;
import org.boozsoft.domain.entity.group.GroupFaAssetGroup;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaAssetGroupRepository extends ReactiveCrudRepository<GroupFaAssetGroup, String> {

    Flux<GroupFaAssetGroup> findAllByOrderById(Pageable pageable);
    Mono<GroupFaAssetGroup> findByBsCodeOrderById(String code);
    Mono<GroupFaAssetGroup> findByBsNameOrderById(String bsName);
    Flux<GroupFaAssetGroup> findByFlagOrderById(String flag);
}
