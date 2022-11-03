package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysMultiSet;
import org.boozsoft.domain.entity.account.SettModes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SettMultiSetRepository extends ReactiveCrudRepository<SysMultiSet, String> {


    Mono<Void> deleteByKm(String km);

    Flux<SysMultiSet> findAllByKm(String km);

}
