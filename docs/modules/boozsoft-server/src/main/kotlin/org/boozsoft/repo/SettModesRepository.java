package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SettModes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SettModesRepository extends ReactiveCrudRepository<SettModes, String> {

    Flux<SettModes> findAllByOrderBySettModesCode(Pageable pageable);
    Flux<SettModes> findAllByOrderBySettModesCode();
    Flux<SettModes> findBySettModesCode(String settModesCode);
    Flux<SettModes> findByFlagOrderBySettModesCode(String flag);

    @Query("select * from sys_sett_modes where tenant_id = :databasenum")
    Flux<SettModes> findByTenantId(String databasenum);
    Mono<Void> deleteAllByTenantId(String databasenum);
}
