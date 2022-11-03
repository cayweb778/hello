package org.boozsoft.repo;


import org.boozsoft.domain.entity.StockTemplateSetting;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTemplateSettingRepository extends ReactiveCrudRepository<StockTemplateSetting, String> {
    Flux<StockTemplateSetting> findAllByTemplateBelongOrderByTemplateSortAsc(String belong);
    Mono<Long> countAllByTemplateBelongAndTemplateName(String belong,String name);
    Mono<StockTemplateSetting> findFirstByTemplateBelongOrderByTemplateSortDesc(String belong);
}
