package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaAssetGroup;
import org.boozsoft.domain.entity.FaEconomyUse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaAssetGroupRepository extends ReactiveCrudRepository<FaAssetGroup, String> {

    Flux<FaAssetGroup> findAllByOrderById(Pageable pageable);
    Mono<FaAssetGroup> findByBsCodeOrderById(String code);
    Mono<FaAssetGroup> findByBsNameOrderById(String bsName);
    Flux<FaAssetGroup> findByFlagOrderById(String flag);
}
