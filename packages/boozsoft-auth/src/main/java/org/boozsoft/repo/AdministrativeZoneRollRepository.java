package org.boozsoft.repo;

import org.boozsoft.domain.entity.AdministrativeZoneRoll;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AdministrativeZoneRollRepository extends ReactiveCrudRepository<AdministrativeZoneRoll, String> {

    Flux<AdministrativeZoneRoll> findAllByOrderById(Pageable pageable);

}

