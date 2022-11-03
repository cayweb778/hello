package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.ArApExpense;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApExpenseRepository extends ReactiveCrudRepository<ArApExpense, String> {

    @Query("select * from ar_ap_expense where bill_style=:billStyle and iyear=:iyear order by ddate asc,ccode asc ")
    Flux<ArApExpense> findByBillStyleAndIyearOrderByCcode(String billStyle, String iyear);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from ar_ap_expense t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and not exists(select * from ar_ap_expense t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<ArApExpense> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from ar_ap_expense where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui ")
    Mono<ArApExpense> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}



