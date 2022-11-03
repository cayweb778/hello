package org.boozsoft.repo;

import org.boozsoft.domain.entity.AccvoucherTemp;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccvoucherTempRepository extends ReactiveCrudRepository<AccvoucherTemp, String> {
    Flux<AccvoucherTemp> findAllByUserIdAndTenantIdAndIyear(String userId,String tenantId,String iyear);
    Flux<AccvoucherTemp> deleteAllByUserIdAndTenantIdAndIyear(String userId,String tenantId,String iyear);

}
