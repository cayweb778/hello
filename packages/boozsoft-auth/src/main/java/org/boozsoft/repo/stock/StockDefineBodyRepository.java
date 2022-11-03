package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.account.DefinedRecord;
import org.boozsoft.domain.entity.stock.StockDefineBody;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockDefineBodyRepository extends ReactiveCrudRepository<StockDefineBody, String> {

    Flux<StockDefineBody> findByDefineCodeOrderById(Pageable pageable, String defineCode);
    Mono<Long> countByDefineCode(String defineCode);
    Flux<StockDefineBody> findByDefineCodeOrderById(String definedCode);
    Flux<StockDefineBody> findByDefineCodeAndFlagOrderById(String defineCode,String flag);
    Flux<StockDefineBody> findByDefineCodeAndCcodeOrderById(String defineCode,String ccode);
    Flux<StockDefineBody> findByDefineCodeAndCnameOrderById(String defineCode,String cname);

    /**
     * 获取最大的编码
     */
    @Query("select Max(ccode) as ccode from stock_define_body where define_code=:defineCode ")
    Mono<StockDefineBody> findMaxCcode(String defineCode);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(ccode as int))+1 as ccode from stock_define_body " +
            "where define_code=:defineCode and cast(ccode as int) <(select MIN(cast(ccode as int)) as minbreak " +
            "from(select ccode,ROW_NUMBER() over(order by cast(ccode as int)) as sort " +
            "from (select distinct cast(ccode as int) from stock_define_body where define_code=:defineCode ) temp1) temp2 " +
            "where cast(ccode as int) <> sort) ")
    Mono<StockDefineBody> findBukongCcode(String defineCode);

}
