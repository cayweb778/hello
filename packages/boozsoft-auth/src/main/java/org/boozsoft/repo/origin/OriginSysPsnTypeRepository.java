package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginSysPsnType;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OriginSysPsnTypeRepository extends ReactiveCrudRepository<OriginSysPsnType, String> {

    Flux<OriginSysPsnType> findAllByOriginIdOrderByPsnTypeCode(String originId);
}
