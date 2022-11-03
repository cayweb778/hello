package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.StockDefineHead;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockDefineHeadRepository extends ReactiveCrudRepository<StockDefineHead, String> {

    Flux<StockDefineHead> findAllByOrderById(Pageable pageable);
    @Query("select * from stock_define_head order by flag desc,define_code asc")
    Flux<StockDefineHead> findAllByOrderByDefineCode();
    @Query("select * from stock_define_head where model = :model order by flag desc,define_code asc")
    Flux<StockDefineHead> findByModelOrderByDefineCode(@Param("model") String model);
    Flux<StockDefineHead> findByFlagOrderById(String flag);
    Mono<Long> countAllBy();
    Flux<StockDefineHead> findByDefineCodeOrderById(String defineCode);
    Flux<StockDefineHead> findByDefineNameOrderById(String defineName);

    /**
     * 获取最大的编码
     */
    @Query("select Max(define_code) as defined_code from stock_define_head ")
    Mono<StockDefineHead> findMaxDefineCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(define_code as int))+1 as define_code from stock_define_head " +
            "where cast(define_code as int) <(select MIN(cast(define_code as int)) as minbreak " +
            "from(select define_code,ROW_NUMBER() over(order by cast(define_code as int)) as sort " +
            "from (select distinct cast(define_code as int) from stock_define_head) temp1) temp2 " +
            "where cast(define_code as int) <> sort) ")
    Mono<StockDefineHead> findBukongDefineCode();

    @Query("select * from stock_define_head where model = :model and flag = '1' order by define_code asc")
    Flux<StockDefineHead> findByModelByFlagOrderByDefineCode(String model);
}
