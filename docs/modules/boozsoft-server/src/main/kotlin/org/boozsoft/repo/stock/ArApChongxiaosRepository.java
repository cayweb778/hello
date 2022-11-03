package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApChongxiaos;
import org.boozsoft.domain.vo.stock.ArApHzhcdsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApChongxiaosRepository extends ReactiveCrudRepository<ArApChongxiaos, String> {

    Flux<ArApChongxiaos> findAllByBusStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String billStyle, String ccode);
    @Query("delete from ar_ap_hzhcds where ccode=:ccode and bus_style=:billStyle ")
    Mono<ArApChongxiaos> deleteByCcodeAndBillType(String ccode,String billStyle);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_ap_ysyf b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findYsyfByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join stock_saleousing b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='YSD' || a.sourcetype='XHD' || a.sourcetype='XSFP') " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_begin_balance b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='QCYSD' || a.sourcetype='QCXHD' || a.sourcetype='QCXSFP') " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findSSByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join stock_saleousing b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='YSD' || a.sourcetype='XHD' || a.sourcetype='XSFP') " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_ap_ysyf b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and a.sourcetype='SKD' " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_begin_balance b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='QCYSD' || a.sourcetype='QCXHD' || a.sourcetype='QCXSFP' || a.sourcetype='QCSKD') " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findYSByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join stock_warehousing b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='YFD' || a.sourcetype='CGDHD' || a.sourcetype='CGFP') " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_begin_balance b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='QCYFD' || a.sourcetype='QCCGDHD' || a.sourcetype='QCCGFP') " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findFFByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join stock_warehousing b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='YFD' || a.sourcetype='CGDHD' || a.sourcetype='CGFP') " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_ap_ysyf b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and a.sourcetype='FKD' " +
            "order by a.line_id " +
            "union all " +
            "select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_chongxiaos a left join ar_begin_balance b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bus_style=:billStyle and (a.sourcetype='QCYSD' || a.sourcetype='QCXHD' || a.sourcetype='QCXSFP' || a.sourcetype='QCSKD' || a.sourcetype='QCFKD') " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findYFByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    Flux<ArApChongxiaos> findByIyearAndBusStyle(String iyear,String billStyle);

}



