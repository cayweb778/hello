package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BaseInfoColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BaseInfoColumnRepository extends ReactiveCrudRepository<BaseInfoColumn, String> {

    Flux<BaseInfoColumn> findByBaseTableOrderById(String baseTable);

    Flux<BaseInfoColumn> findByBaseTableAndIsflagOrderById(String baseTable,String isflag);

    Mono<BaseInfoColumn> deleteByBaseTable(String baseTable);

}
