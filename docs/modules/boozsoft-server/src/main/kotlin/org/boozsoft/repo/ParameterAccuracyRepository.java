package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ParameterAccuracy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ParameterAccuracyRepository extends ReactiveCrudRepository<ParameterAccuracy, String> {

    Flux<ParameterAccuracy> findAllByTenantIdOrderByParaNum(String tendId);
}

