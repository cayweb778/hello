package org.boozsoft.repo;

import org.boozsoft.domain.entity.ColumnSettings;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ColumnSettingsRepository extends ReactiveCrudRepository<ColumnSettings, String> {
    Flux<ColumnSettings> findAllByAccountIdAndOwningMenuNameAndColumnTypeNameOrderByTkeyAsc(String accId,String menuName,String type);
    Mono<Void> deleteAllByAccountId(String accId);
}
