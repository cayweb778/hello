package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaAssetsMinusMaster;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaAssetsMinusMasterRepository extends ReactiveCrudRepository<FaAssetsMinusMaster, String> {
    Mono<FaAssetsMinusMaster> findFirstByHandleDateLikeOrderByCzIdDesc(String date);

    Flux<FaAssetsMinusMaster> findAllByManageCodeOrderByCreateDateAscCzIdAsc(String code);

    Flux<FaAssetsMinusMaster> findAllByManageCodeAndCreateDateLikeOrderByCreateDateAscCzIdAsc(String code, String s);
}
