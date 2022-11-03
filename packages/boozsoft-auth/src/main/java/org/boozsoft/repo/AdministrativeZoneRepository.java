package org.boozsoft.repo;

import org.boozsoft.domain.entity.AdministrativeZone;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AdministrativeZoneRepository extends ReactiveCrudRepository<AdministrativeZone, String> {

    Flux<AdministrativeZone> findAllByOrderById(Pageable pageable);

}

