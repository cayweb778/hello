package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysTradeNature;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysTradeNatureRepository extends ReactiveCrudRepository<SysTradeNature, String> {
    @Query("SELECT * FROM _app_group_sys_trade_nature WHERE id= :id")
    Mono<SysTradeNature> findById(Long id);
}

