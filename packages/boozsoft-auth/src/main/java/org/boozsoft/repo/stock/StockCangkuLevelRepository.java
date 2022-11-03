package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCangkuLevel;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockCangkuLevelRepository extends ReactiveCrudRepository<StockCangkuLevel, String> {

    @Query("select * from stock_cangku_level order by level_order asc")
    Flux<StockCangkuLevel> findAllOrderLevelOrder();
    @Query("delete from stock_cangku_level where id in (:list)")
    Mono<Void> delByIdList(List<String> list);
    Mono<StockCangkuLevel> findById(String id);
    Mono<StockCangkuLevel> findByLevelOrder(String a);

    Mono<Long> countByLevel2(String level2);
    Mono<Long> countByLevel2AndLevel3(String level2,String level3);
    Mono<Long> countByLevel2AndLevel3AndLevel4(String level2,String level3,String level4);
    Mono<Long> countByLevel2AndLevel3AndLevel4AndLevel5(String level2,String level3,String level4,String level5);
    Mono<Long> countByLevel2AndLevel3AndLevel4AndLevel5AndLevel6(String level2,String level3,String level4,String level5,String level6);
}
