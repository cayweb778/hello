package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockHeadColumnSettings;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface StockHeadColumnSettingsRepository extends ReactiveCrudRepository<StockHeadColumnSettings, String> {

    Flux<StockHeadColumnSettings> findAllByOwningMenuNameOrderBySerialAsc(String menuName);
    Flux<StockHeadColumnSettings> findAllByOwningMenuNameAndColumnTypeNameAndFieldNameInOrderBySerialAsc(String menuName, String type, List<String> name);

}