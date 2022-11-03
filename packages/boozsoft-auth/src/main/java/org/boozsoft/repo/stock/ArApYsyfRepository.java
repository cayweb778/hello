package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.ArApYsyf;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApYsyfRepository extends ReactiveCrudRepository<ArApYsyf, String> {

    @Query("select * from ar_ap_ysyf where bill_style=:billStyle and iyear=:iyear order by ddate asc,ccode asc ")
    Flux<ArApYsyf> findByBillStyleAndIyearOrderByCcode(String billStyle,String iyear);

    @Query("select * from ar_ap_ysyf where bill_style=:billStyle and (ddate between :ddate1 and :ddate2) order by ddate asc,ccode asc ")
    Flux<ArApYsyf> findByBillStyleAndDdateOrderByCcode(@Param("billStyle") String billStyle,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);
    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from ar_ap_ysyf t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and not exists(select * from ar_ap_ysyf t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<ArApYsyf> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from ar_ap_ysyf where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle ")
    Mono<ArApYsyf> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);

    Flux<ArApYsyf> findByCcode(String ccode);

    @Query("select * from ar_ap_ysyf where (write_off_status!='1' or write_off_status is null) " +
            "and bcheck='1' and iyear=:iyear and cvencode=:cvencode order by ddate asc,ccode asc ")
    Flux<ArApYsyf> findWhxskd(@Param("iyear") String iyear, @Param("cvencode") String cvencode);

    @Query("select * from ar_ap_ysyf where (write_off_status!='1' or write_off_status is null) " +
            "and bill_style=:billStyle and bcheck='1' and iyear=:iyear and ddate<=:endDate order by ddate asc,ccode asc ")
    Flux<ArApYsyf> findWhxskdByEndDate(@Param("billStyle") String billStyle,@Param("iyear") String iyear, @Param("endDate") String endDate);

    Flux<ArApYsyf> findAllByIyearAndDdateLessThanOrderByCcode(String year, String expirationDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='SKD' and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findSkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='SKD' and bcheck='1' and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findYshSkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='SKD' and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findWshSkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='SKD' and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and bus_style='SKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findSkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='SKD' and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and bus_style='SKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findYshSkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='SKD' and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and bus_style='SKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findWshSkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='FKD' and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and bus_style='FKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findFkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='FKD' and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and bus_style='FKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findYshFkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_ap_ysyf where bill_style='FKD' and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode " +
            "union all " +
            "select cvencode_js as cvencode,sum(cast(COALESCE(ys_isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and bus_style='FKD' group by cvencode_js) as tmp group by cvencode")
    Flux<ArApYsyf> findWshFkdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select * from ar_ap_ysyf where bill_style='SKD' and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findSkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from ar_ap_ysyf where bill_style='SKD' and bcheck='1' and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findYshSkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from ar_ap_ysyf where bill_style='SKD' and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findWshSkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='FKD' and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findFkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='FKD' and bcheck='1' and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findYfhFkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum, " +
            "sum(cast(COALESCE(idiscount,'0') as decimal)) as idiscount,sum(cast(COALESCE(hx_idiscount,'0') as decimal)) as hx_idiscount " +
            "from ar_ap_ysyf where bill_style='FKD' and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode")
    Flux<ArApYsyf> findWfhFkdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from ar_ap_ysyf where bill_style='FKD' and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findFkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from ar_ap_ysyf where bill_style='FKD' and bcheck='1' and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findYfhFkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from ar_ap_ysyf where bill_style='FKD' and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) and cvencode=:cvencode order by ccode ")
    Flux<ArApYsyf> findWfhFkdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    Flux<ArApYsyf> findAllByBillStyleAndDdateLikeOrderByCcode(String billStyle, String ddate);

}
