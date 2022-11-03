package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.ArApHzhcds;
import org.boozsoft.domain.vo.stock.ArApHzhcdsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArApHzhcdsRepository extends ReactiveCrudRepository<ArApHzhcds, String> {

    Flux<ArApHzhcds> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String billStyle, String ccode);
    @Query("delete from ar_ap_hzhcds where ccode=:ccode and bill_style=:billStyle ")
    Mono<ArApHzhcds> deleteByCcodeAndBillType(String ccode,String billStyle);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_hzhcds a left join ar_ap_ysyf b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bill_style=:billStyle " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findYsyfByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    @Query("select a.*,b.hx_isum as ydhx_isum " +
            "from ar_ap_hzhcds a left join stock_saleousing b on a.sourcecode = b.ccode " +
            "where a.ccode=:ccode and a.bill_style=:billStyle " +
            "order by a.line_id ")
    Flux<ArApHzhcdsVo> findSaleousingByBillStyleAndCcodeOrderByLineId(String billStyle, String ccode);

    Flux<ArApHzhcds> findByIyearAndBusStyle(String iyear,String billStyle);

}



