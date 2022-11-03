package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysPsnType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysPsnTypeRepository extends ReactiveCrudRepository<SysPsnType, String> {

    Flux<SysPsnType> findAllByOrderByPsnTypeCode();

    Flux<SysPsnType> findByUniqueCode(String uniqueCode);
    @Modifying
    @Query("delete from sys_psn_type where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);
}
