package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.ArBeginBalance;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArBeginBalanceRepository extends ReactiveCrudRepository<ArBeginBalance, String> {

    Flux<ArBeginBalance> findAllByOrderByCcode();
    Flux<ArBeginBalance> findAllByBillStyleAndIyearOrderByCcode(String billStyle,String iyear);
    Flux<ArBeginBalance> findAllByIyearAndDdateLessThanAndBillStyleOrderByCcode(String year,String date,String bill);

    @Query("select * from ar_begin_balance where bcheck='1' and iyear=:iyear and (hx_flag!='1' or hx_flag is null) " +
            "and cvencode_js=:cvencode order by ccode ")
    Flux<ArBeginBalance> findByCvencode(@Param("cvencode") String cvencode,@Param("iyear") String iyear);

    @Query("select * from ar_begin_balance where bcheck='1' and (hx_flag!='1' or hx_flag is null) " +
            "and bill_style=:billStyle and iyear=:iyear order by ccode ")
    Flux<ArBeginBalance> findWhxList(@Param("billStyle") String billStyle,@Param("iyear") String iyear);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from ar_begin_balance t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and not exists(select * from ar_begin_balance t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<ArBeginBalance> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from ar_begin_balance where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui ")
    Mono<ArBeginBalance> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

    Mono<ArBeginBalance> findByCcode(String ccode);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear group by cvencode_js")
    Flux<ArBeginBalance> findYszzYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' group by cvencode_js")
    Flux<ArBeginBalance> findYshYszzYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) group by cvencode_js")
    Flux<ArBeginBalance> findWshYszzYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findYszzYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findYshYszzYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findWshYszzYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bus_style='SKD' group by cvencode_js")
    Flux<ArBeginBalance> findSkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' and bus_style='SKD' group by cvencode_js")
    Flux<ArBeginBalance> findYshSkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) and bus_style='SKD' group by cvencode_js")
    Flux<ArBeginBalance> findWshSkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bus_style='FKD' group by cvencode_js")
    Flux<ArBeginBalance> findFkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' and bus_style='FKD' group by cvencode_js")
    Flux<ArBeginBalance> findYshFkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) and bus_style='FKD' group by cvencode_js")
    Flux<ArBeginBalance> findWshFkdYueList(String iyear);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bus_style='SKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findSkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' and bus_style='SKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findYshSkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) and bus_style='SKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findWshSkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bus_style='FKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findFkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and bcheck='1' and bus_style='FKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findYshFkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select cvencode_js,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum_benbi " +
            "from ar_begin_balance where iyear=:iyear and (bcheck !='1' or bcheck is null) and bus_style='FKD' and cvencode_js=:cvencode group by cvencode_js")
    Flux<ArBeginBalance> findWshFkdYueByCvencodeList(String iyear,String cvencode);

    @Query("select * from ar_begin_balance where (hx_flag!='1' or hx_flag is null) " +
            "and bill_style=:billStyle and iyear=:iyear order by ccode ")
    Flux<ArBeginBalance> findWhxByBillStyleList(@Param("billStyle") String billStyle,@Param("iyear") String iyear);

    @Query("select * from ar_begin_balance where (hx_flag!='1' or hx_flag is null) " +
            "and ar_style=:arStyle and iyear=:iyear order by ccode ")
    Flux<ArBeginBalance> findWhxByArStyleList(@Param("arStyle") String arStyle,@Param("iyear") String iyear);

}



