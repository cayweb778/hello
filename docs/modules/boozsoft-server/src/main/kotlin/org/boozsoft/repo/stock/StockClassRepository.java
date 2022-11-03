package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockClass;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockClassRepository extends ReactiveCrudRepository<StockClass, String> {
    @Query("select  COALESCE(max(stock_grade_code), '0') from stock_class where parent_id=:parentId ")
    Mono<String> findByMaxParentId(String parentId);
    Mono<Long> countByParentId(String parentId);
    Mono<Long> countByStockClass(String cusClass);
    Mono<Long> countByParentIdAndStockCclassName(String parentId,String cusCclassName);
    Mono<Long> countByStockCclassName(String cusCclassName);

    Mono<StockClass> findByUniqueStockclass(String uniqueCustclass);
    @Query("select * from stock_class order by stock_grade_code")
    Flux<StockClass> findAll();

    @Query("select * from stock_class where stock_bend=:bend and flag='1' order by stock_grade_code")
    Flux<StockClass> findAllByStockBend(String bend);


    @Modifying
    @Query("delete from stock_class where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    Flux<StockClass> findAllByFlag(String flag);

}

