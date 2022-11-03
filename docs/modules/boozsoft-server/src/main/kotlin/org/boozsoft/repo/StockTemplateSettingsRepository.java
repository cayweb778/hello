package org.boozsoft.repo;


import org.boozsoft.domain.entity.StockTemplateSettings;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockTemplateSettingsRepository extends ReactiveCrudRepository<StockTemplateSettings, String> {
    Flux<StockTemplateSettings> findAllByParentIdAndBelongAreaOrderByPaintSortAsc(String value, String value1);
    Flux<StockTemplateSettings> findAllByParentIdOrderByBelongAreaAscPaintSortAsc(String value);

    Flux<StockTemplateSettings> findAllByParentId(String source);
}
