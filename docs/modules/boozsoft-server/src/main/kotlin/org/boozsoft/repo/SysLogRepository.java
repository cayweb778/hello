package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysLog;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysLogRepository extends ReactiveCrudRepository<SysLog, String> {

    Flux<SysLog> findAllByUserNameAndOperationDateBetweenOrderByOperationDateDesc(Pageable pageable, String userName, String between1, String between2);

    Mono<Long> countAllByUserNameAndOperationDateBetween(String userName, String between1, String between2);

}
