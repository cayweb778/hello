package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaAssetsMinusMaster;
import org.boozsoft.domain.entity.fa.FaAssetsMinusSub;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaAssetsMinusSubRepository extends ReactiveCrudRepository<FaAssetsMinusSub, String> {

    Flux<FaAssetsMinusSub> findAllBySuperiorIdOrderByAssetsCode(String id);
}
