package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockApBeginBalance;
import org.boozsoft.domain.entity.stock.StockArBeginBalance;
import org.boozsoft.domain.vo.stock.StockArBeginBalanceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockApBeginBalanceRepository extends ReactiveCrudRepository<StockApBeginBalance, String> {
    @Query("select COALESCE(max(line_id)+1, 1) as floor_num from stock_ap_begin_balance where iyear=:iyear")
    Mono<Long> getStockArBalanceMaxIineId(String iyear);

    @Query("select c.cust_name,pro.project_name as citem_name,ar.* from stock_ap_begin_balance ar left join customer c on c.id=ar.cust_id left join project pro on pro.unique_code = ar.citem_id where ar.iyear=:iyear order by ar.bcheck,ar.bcheck_time ")
    Flux<StockArBeginBalanceVo> findByStockArBalance(String iyear);

    @Query("update stock_ap_begin_balance set bcheck=:bcheck,bcheck_user=:bcheckUser,bcheck_time=:bcheckTime where id in (:id)")
    Mono<Void> editAuditStockArBalance(String bcheck, String bcheckUser, String bcheckTime, List<String> id);

    @Query("delete from stock_ap_begin_balance where id in (:id)")
    Mono<Void> delStockArBalance(List<String> id);
    Mono<Long> countByCustIdAndIyearAndBcheck(String custId,String Iyear,String bcheck);
}



