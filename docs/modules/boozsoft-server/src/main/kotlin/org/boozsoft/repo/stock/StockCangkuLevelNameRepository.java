package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCangkuLevelName;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockCangkuLevelNameRepository extends ReactiveCrudRepository<StockCangkuLevelName, String> {
    @Query("select count(id) from stock_cangku_level_name ")
    Mono<Long> countLevelName();
    Mono<Long> countByLevelName(String name);

    @Query("delete from stock_cangku_level_name where id in (:list) ")
    Mono<Void> delById(List<String> list);
}
