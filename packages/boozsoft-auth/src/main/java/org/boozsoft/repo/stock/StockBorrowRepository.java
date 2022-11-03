package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockBorrow;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.boozsoft.domain.vo.stock.StockBorrowVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockBorrowRepository extends ReactiveCrudRepository<StockBorrow, String> {

    @Query("select sb.*,(select sum(cast(sbs.ruku_sum as float)) from stock_borrows sbs where sbs.ccode=sb.ccode) ruku_sum,(select sum(cast(sbs.ljhh_sum as float)) from stock_borrows sbs where sbs.ccode=sb.ccode) ljhh_sum,\n" +
            "       (select sum(cast(sbs.ljzh_sum as float)) from stock_borrows sbs where sbs.ccode=sb.ccode) ljzh_sum,(select sum(cast(sbs.base_quantity as float)) from stock_borrows sbs where sbs.ccode = sb.ccode) base_quantity \n" +
            "from stock_borrow sb\n" +
            "where sb.borrow_style =:borrowStyle \n" +
            "order by sb.ddate desc")
    Flux<StockBorrowVo> findAllOrderByDdateDesc(String borrowStyle);
    Flux<StockBorrow> findAllByBorrowStyleAndIyearAndCcodeLikeOrderByDdateAscCcodeAsc(String type, String year, String s);

    Mono<Void> deleteByCcode(String ccode);

    @Query("update stock_borrow set bcheck=:bcheck,bcheck_time=:bcheckTime,bcheck_user=:bcheckUser where ccode in (:ccode)")
    Mono<Void> updeteStockBorrowBcheckByCcodelist(String bcheck, String bcheckTime, String bcheckUser, List<String> ccode);

    @Query("select dept.dept_name,psn.psn_name,psn2.real_name as cmaker_name,sb.* from stock_borrow sb " +
            "left join sys_department dept on dept.unique_code=sb.cdepcode\n" +
            "left join sys_psn psn on psn.id=sb.cpersoncode\n" +
            "left join _app_group_sys_user psn2 on psn2.id=sb.cmaker where sb.ccode in (select distinct sbs.ccode from stock_borrows sbs where cast(sbs.base_quantity as float)!=cast(sbs.ruku_sum as float)) " +
            "and sb.iyear=:iyear and sb.ddate between :strDate and :endDate and sb.borrow_style=:borrowStyle and sb.bcheck='1' order by sb.ccode")
    Flux<StockBorrowVo> getReferData(String iyear,String strDate,String endDate,String borrowStyle);
}
