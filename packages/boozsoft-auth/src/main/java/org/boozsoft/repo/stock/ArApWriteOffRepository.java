package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.ArApWriteOff;
import org.boozsoft.domain.vo.stock.ArApWriteOffVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApWriteOffRepository extends ReactiveCrudRepository<ArApWriteOff, String> {

    Flux<ArApWriteOff> findByBillStyleAndIyearOrderByCcode(String billStyle, String iyear);

    Flux<ArApWriteOff> findByCcodeOrderByHxCcode(String ccode);

    @Query("select a.bill_style,a.ddate,a.ccode,a.cvencode,a.cvencode_js,a.isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from stock_saleousing a " +
            "left join ar_ap_write_off b on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and (a.bcloser !='1' or a.bcloser is null) and (a.bill_style='XHD' or a.bill_style='YSD' or a.bill_style='XSFP') " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findYskhxmxb(@Param("iyear") String iyear);

    @Query("select a.ar_style as bill_style,a.ddate,a.ccode,a.cvencode_xs as cvencode,a.cvencode_js,a.ar_isum_benbi as isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_begin_balance a " +
            "left join ar_ap_write_off b on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and a.bus_style='YSD' " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findYskhxmxbYue(@Param("iyear") String iyear);

    @Query("select a.bill_style,a.ddate,a.ccode,a.cvencode,a.cvencode_js,a.isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from stock_warehousing a " +
            "left join ar_ap_write_off b on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and (a.bcloser !='1' or a.bcloser is null) and (a.bill_style='CGDHD' or a.bill_style='YFD') " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findYfkhxmxb(@Param("iyear") String iyear);

    @Query("select a.ar_style as bill_style,a.ddate,a.ccode,a.cvencode_xs as cvencode,a.cvencode_js,a.ar_isum_benbi as isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_begin_balance a " +
            "left join ar_ap_write_off b on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and a.bus_style='YFD' " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findYfkhxmxbYue(@Param("iyear") String iyear);

    @Query("select a.bill_style,a.ddate,a.ccode,a.cvencode,a.cvencode_js,a.isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_ap_write_off b " +
            "left join stock_saleousing a on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and (a.bcloser !='1' or a.bcloser is null) and (a.bill_style='XHD' or a.bill_style='YSD') " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findQuxiaohexiaoList(@Param("iyear") String iyear);

    @Query("select a.ar_style as bill_style,a.ddate,a.ccode,a.cvencode_xs as cvencode,a.cvencode_js,a.ar_isum_benbi as isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_ap_write_off b " +
            "left join ar_begin_balance a on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and a.bus_style='YSD' " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findQuxiaohexiaoYueList(@Param("iyear") String iyear);

    @Query("select a.bill_style,a.ddate,a.ccode,a.cvencode,a.cvencode_js,a.isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_ap_write_off b " +
            "left join stock_warehousing a on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and (a.bcloser !='1' or a.bcloser is null) and (a.bill_style='CGDHD' or a.bill_style='YFD') " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findQuxiaohexiaoYfdList(@Param("iyear") String iyear);

    @Query("select a.ar_style as bill_style,a.ddate,a.ccode,a.cvencode_xs as cvencode,a.cvencode_js,a.ar_isum_benbi as isum,a.hx_isum, " +
            "b.bill_style as hx_style,b.ddate as hx_date,b.ccode as hx_code,b.hx_money,b.idiscount,b.skd_qichu,b.ysd_qichu " +
            "from ar_ap_write_off b " +
            "left join ar_begin_balance a on a.ccode = b.hx_ccode " +
            "where a.iyear=:iyear and a.bcheck = '1' " +
            "and a.bus_style='YFD' " +
            "order by a.ccode,b.ccode asc")
    Flux<ArApWriteOffVo> findQuxiaohexiaoYfdYueList(@Param("iyear") String iyear);

    Flux<ArApWriteOff> findByCcodeAndHxCcode(String ccode,String hxCcode);

    Flux<ArApWriteOff> findByIyearAndBillStyleOrderByCcode(String iyear,String billStyle);

    Mono<ArApWriteOff> deleteByCxCcodeAndCxStyle(String ccode,String billStyle);

}
