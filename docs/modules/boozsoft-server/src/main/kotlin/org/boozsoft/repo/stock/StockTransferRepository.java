package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.entity.stock.StockTaking;
import org.boozsoft.domain.entity.stock.StockTransfer;
import org.boozsoft.domain.vo.stock.StockTakingVo;
import org.boozsoft.domain.vo.stock.StockTransferVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTransferRepository extends ReactiveCrudRepository<StockTransfer, String> {

    Flux<StockTransfer> findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(String type, String iyear);

    Mono<StockTransfer> findFirstByBillStyleAndIyearOrderByDdateDesc(String type, String iyear);

    Mono<StockTransfer> findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(String type, String s);

    Mono<StockTransfer> findByCcode(String code);

    @Query("select psn3.psn_name as buname,psn2.psn_name as kuname,sd.dept_name,ck.ck_name,rk.ck_name rkname,sp.psn_name, sw.* " +
            "from stock_transfer sw " +
            " left join stock_cangku ck on ck.id=sw.cwhcode "+
            " LEFT JOIN stock_cangku rk ON rk.ID = sw.cwhcoderk "+
            " left JOIN sys_psn sp on sp.id = sw.cpersoncode " +
            " left join sys_psn psn2 on psn2.id=sw.cwhcode_user " +
            " left join sys_psn psn3 on psn3.id=sw.bcheck_user " +
            " left JOIN sys_department sd on sd.unique_code = sw.cdepcode " +
            "where sw.ddate between :strDate and :endDate " +
            "order by sw.ccode asc")
    Flux<StockTransferVo> findMainList(String strDate, String endDate);

}



