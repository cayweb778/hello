package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.group.GroupProjectCashCode;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectCashCodeRepository extends ReactiveCrudRepository<GroupProjectCashCode, String> {

    Flux<GroupProjectCashCode> findByProjectCodeAndAccStandardOrderById(String projectCode,String iyear);
    Flux<GroupProjectCashCode> findByAccStandardOrderById(String iyear);
    Flux<GroupProjectCashCode> findAllByAccStandard(String uniqueAccStandard);
}
