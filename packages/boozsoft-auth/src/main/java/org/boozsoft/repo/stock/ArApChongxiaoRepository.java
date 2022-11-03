package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.ArApChongxiao;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApChongxiaoRepository extends ReactiveCrudRepository<ArApChongxiao, String> {

    @Query("select * from ar_ap_chongxiao where bus_style=:billStyle and iyear=:iyear order by ddate asc,ccode asc ")
    Flux<ArApChongxiao> findByBillStyleAndIyearOrderByCcode(String billStyle, String iyear);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from ar_ap_chongxiao t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and not exists(select * from ar_ap_chongxiao t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<ArApChongxiao> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from ar_ap_chongxiao where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui ")
    Mono<ArApChongxiao> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}



