package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BaseInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BaseInfoRepository extends ReactiveCrudRepository<BaseInfo, String> {

    Flux<BaseInfo> findAllByOrderById(Pageable pageable);

}
