package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockChange;

import org.boozsoft.domain.vo.stock.StockChangeVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockChangeRepository extends ReactiveCrudRepository<StockChange, String> {
    Flux<StockChange> findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(String type, String Iyaer);
    Mono<StockChange> findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(String type, String Iyaer);

    Mono<StockChange> findByCcodeAndBillStyle(String ccode, String type);

    @Query("delete from stock_xy_csource where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> delXyCsourceByCcodeAndBillType(String ccode, String billStyle);

    @Query("delete from stock_xy_csource where xyccode=:xyccode and xy_bill_style=:xybillStyle and bill_style=:billStyle and ccode=:ccode ")
    Mono<Void> delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(String xyccode, String xybillStyle, String billStyle, String ccode);

    @Query("delete from stock_change where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndBillType(String ccode, String billStyle);

    @Query("select icost from stock_change where bill_style='CGDHD' and cvencode=:supUnique  order by ddate desc limit 1 ")
    Mono<Long> findByStockWareRecentlySupMoney(String supUnique);

    @Query("select * from stock_change where bill_style=:type and iyear=:iyear  order by ddate desc ")
    Flux<StockChange> findAllTypeAndIyear(String type, String iyear);

    Mono<StockChange> findByCcode(String ccode);

    @Query("select psn3.real_name as buname,psn2.real_name as cmaker_name,sd.dept_name,sp.psn_name, sw.* " +
            "from stock_change sw " +
            " left JOIN sys_psn sp on sp.id = sw.cpersoncode " +
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker " +
            " left join _app_group_sys_user psn3 on psn3.id=sw.bcheck_user " +
            " left JOIN sys_department sd on sd.unique_code = sw.cdepcode " +
            "where sw.ddate between :strDate and :endDate " +
            "order by sw.ccode asc")
    Flux<StockChangeVo> findMainList(String strDate, String endDate);


}



