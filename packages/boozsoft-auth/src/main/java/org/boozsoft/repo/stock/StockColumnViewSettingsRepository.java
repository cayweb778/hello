package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockColumnViewSettings;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockColumnViewSettingsRepository extends ReactiveCrudRepository<StockColumnViewSettings, String> {
    Flux<StockColumnViewSettings> findAllByAccountIdAndOwningMenuNameAndColumnTypeNameAndUserIdAndIsTypeOrderByTkeyAsc(String accId,String menuName,String type,String userId,String isType);
    Mono<Void> deleteAllByAccountId(String accId);
}
