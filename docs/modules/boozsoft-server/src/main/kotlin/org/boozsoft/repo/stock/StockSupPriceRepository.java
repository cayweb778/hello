package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.entity.stock.StockSupPrice;
import org.boozsoft.domain.vo.stock.StockCustPriceVo;
import org.boozsoft.domain.vo.stock.StockSupPriceVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockSupPriceRepository extends ReactiveCrudRepository<StockSupPrice, String> {


   /* @Query("select s.stock_num, s.stock_name, s.stock_ggxh, s.stock_class, s.stock_market_unit, s.stock_measurement_type," +
            " (case " +
            "    when s.stock_measurement_type = '单计量' then " +
            "       (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = s.stock_measurement_unit) " +
            "   else " +
            "       (select sml.unit_name as cunitid_name " +
            "        from sys_unit_of_mea_many sml " +
            "        where sml.id = s.stock_measurement_unit) " +
            "  end) as uname, " +
            " sc.stock_cclass_name, scp.* " +
            " from stock_sup_price scp " +
            " left join stock s on s.stock_num=scp.stock_num " +
            " left join  stock_class sc on s.stock_class=sc.unique_stockclass " +
            " left join  sys_unit_of_mea uom on uom.id=s.stock_market_unit " +
            " left join  sys_unit_of_mea_many uoms on uoms.id=s.stock_market_unit " +
            " where 1=1 and scp.sup_id = :supId ")*/

    @Query("select s.stock_num, s.stock_name, s.stock_ggxh, s.stock_class, s.stock_measurement_unit, s.stock_measurement_type, " +
            " (case " +
            "    when s.stock_measurement_type = '单计量' then " +
            "       (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = s.stock_measurement_unit) " +
            "   else " +
            "       (select sml.unit_name as cunitid_name " +
            "        from sys_unit_of_mea_many smm  " +
            "        LEFT JOIN sys_unit_of_mea_list sml ON smm.unit_code = sml.many_code AND sml.is_main = 'true' " +
            "        where smm.id = s.stock_measurement_unit) " +
            "  end) as uname, " +
            " sc.stock_cclass_name , scp.* " +
            " from stock s " +
            " left join stock_sup_price scp on s.stock_num=scp.stock_num " +
            " left JOIN stock_class sc ON s.stock_class = sc.unique_stockclass " +
            " where 1=1 order by s.stock_num ")
    Flux<StockSupPriceVo> findAllByPrice();

    @Modifying
    @Query("delete from stock_sup_price where sup_id = :supId")
    Mono<Void> deleteBycustId(String supId);

    Mono<StockSupPrice> findBySupIdAndStockNum(String supId,String stockNum);

    Flux<StockSupPrice> findAllBySupId(String supId);

    @Query("delete from stock_sup_price where id in (:list)")
    Mono<Void> deleteByIds(List<String> list);

}



