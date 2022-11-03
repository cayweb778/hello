package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.vo.stock.StockCustPriceVo;
import org.boozsoft.domain.vo.stock.StockPriceVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockCustPriceRepository extends ReactiveCrudRepository<StockCustPrice, String> {


    @Query("select s.stock_num, s.stock_name, s.stock_ggxh, s.stock_class, s.stock_market_unit, s.stock_measurement_type," +
            " sc.stock_cclass_name, " +
            " (CASE " +
            "  WHEN s.stock_measurement_type = '单计量' THEN " +
            "  ( SELECT sm.unit_name FROM sys_unit_of_mea sm WHERE sm.ID = s.stock_market_unit ) " +
            "  ELSE ( SELECT sm.unit_name FROM sys_unit_of_mea_list sm WHERE sm.ID = s.stock_market_unit ) " +
            " END " +
            " ) AS uname," +
            " scp.* " +
            " from stock_cust_price scp " +
            " left join stock s on s.stock_num=scp.stock_num " +
            " left join  stock_class sc on s.stock_class=sc.unique_stockclass " +
            " where 1=1 and scp.cust_id = :custId ")
    Flux<StockCustPriceVo> findAllByPrice(String custId);

    @Modifying
    @Query("delete from stock_cust_price where cust_id = :custId")
    Mono<Void> deleteBycustId(String custId);


    Flux<StockCustPrice> findAllByCustIdAndStockNumOrderByOperatorTimeDesc(String custId,String stockId);

}



