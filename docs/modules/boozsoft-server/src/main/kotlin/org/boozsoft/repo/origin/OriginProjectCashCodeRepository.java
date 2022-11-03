package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.origin.OriginProjectCash;
import org.boozsoft.domain.entity.origin.OriginProjectCashCode;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectCashCodeRepository extends ReactiveCrudRepository<OriginProjectCashCode, String> {

    Flux<OriginProjectCashCode> findAllByOriginIdAndIyearAndAccStandard(String accGroup, String year,String uniqueAccStandard);

    Mono<Void> deleteAllByOriginIdAndIyear(String groupId, String year);

    Flux<OriginProjectCashCode> findByProjectCodeAndAccStandardAndOriginIdAndIyearOrderById(String projectCode, String accStandard, String originId, String iyear);

}
