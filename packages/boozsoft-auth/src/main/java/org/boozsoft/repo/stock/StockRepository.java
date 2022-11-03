package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.Stock;
import org.boozsoft.domain.vo.stock.StockPriceVo;
import org.boozsoft.domain.vo.stock.StockVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockRepository extends ReactiveCrudRepository<Stock, String> {
    Mono<Long> countByStockClass(String stockClass);
    Mono<Long> countByStockNum(String num);
    @Query("select count(id) from stock where stock_barcode=:stockBarcode ")
    Mono<Long> countByStockUnitBarcode(String stockUnitBarcode1);
    @Query("select count(id) from stock where stock_num !=:stockNum and stock_unit_barcode1=:stockUnitBarcode1 ")
    Mono<Long> countByStockUnitBarcode1NotInStockNum(String stockNum,String stockUnitBarcode1);
    @Query("select count(id) from stock where stock_num !=:stockNum and stock_unit_barcode2=:stockUnitBarcode2 ")
    Mono<Long> countByStockUnitBarcode2NotInStockNum(String stockNum,String stockUnitBarcode2);

    Flux<Stock> findAllByStockClass(String stockClass);
    @Query("select * from stock")
    Flux<Stock> findBylimit();
    @Query("select count(id) from stock ")
    Mono<Long> countStock();

    @Query("select stock_num from stock")
    Flux<String> getStockNum();

    @Query("select s.*,sc.stock_cclass_name,(case\n" +
            "            when s.stock_measurement_type = '单计量' then\n" +
            "                (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = s.stock_measurement_unit)\n" +
            "            else\n" +
            "                (select sml.unit_name as cunitid_name\n" +
            "                 from sys_unit_of_mea_many sml\n" +
            "                 where sml.id = s.stock_measurement_unit)\n" +
            "           end) as stock_measurement_name from stock s left join  stock_class sc on s.stock_class=sc.unique_stockclass order by stock_num asc,create_time desc")
    Flux<StockVo> findAlls();

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
            " sc.stock_cclass_name, sp.* " +
            " from stock s " +
            " left JOIN stock_class sc ON s.stock_class = sc.unique_stockclass " +
            " left join  stock_price sp on s.stock_num=sp.stock_num " +
            " where 1=1 ")
    Flux<StockPriceVo> findAllByPrice();

    Flux<Stock> findAllByCreateTimeLessThanAndStockNumLikeOrderByStockNum(String s, String s1);
    Flux<Stock> findAllByCreateTimeLessThanAndStockNameLikeOrderByStockNum(String s, String s1);
    Flux<Stock> findAllByCreateTimeLessThanEqualOrderByStockNum(String s);

    @Query("select count(sbb.id) +\n" +
            "       (select count(sws.id) from stock_warehousings sws where sws.cinvode =:stockId )+\n" +
            "       (select count(sss.id) from stock_saleousings sss where sss.cinvode =:stockId ) as count\n" +
            "from stock_begin_balance sbb\n" +
            "where sbb.stock_id =:stockId ")
    Mono<Long> findByStockIdToBalanceAndSwsAndSss(String stockId);

    Mono<Stock> findByStockNum(String num);

    @Query("select s.*, sc.stock_cclass_name,(case\n" +
            "            when s.stock_measurement_type = '单计量' then\n" +
            "                (select sm.unit_name as cunitid_name from sys_unit_of_mea sm where sm.id = s.stock_measurement_unit)\n" +
            "            else\n" +
            "                (select sml.unit_name as cunitid_name\n" +
            "                 from sys_unit_of_mea_many sml\n" +
            "                 where sml.id = s.stock_measurement_unit)\n" +
            "           end) as stock_measurement_name  " +
            " from  stock s " +
            " left join  stock_class sc on s.stock_class=sc.unique_stockclass " +
            " order by s.create_time desc")
    Flux<StockVo> findAllByXcl();

    @Query("select distinct  scs.base_quantity as xcl,s.* from stock_currentstock scs left join  stock s on  scs.inv_code = s.stock_num order by s.create_time desc")
    Flux<Stock> findCunHuoAllByXcl();

    @Query("select " +
            "(select count(sb.id) from stock_begin_balance sb where sb.stock_id=sk.stock_num)+\n" +
            "(select count(sws.id) from stock_warehousings sws where sws.cinvode=sk.stock_num)+\n" +
            "(select count(sss.id) from stock_saleousings sss where sss.cinvode=sk.stock_num)+\n" +
            "(select count(sc.id) from stock_currentstock sc where sc.inv_code=sk.stock_num)+\n" +
            "(select count(sjs.id) from stock_jhzx_stock sjs where sjs.cinvode=sk.stock_num)+\n" +
            "(select count(stc.id) from stock_taking_cun stc where stc.stock_num=sk.stock_num)+\n" +
            "(select count(scs.id) from stock_changes scs where scs.cinvode=sk.stock_num)\n" +
            "from stock sk\n" +
            "where sk.stock_num =:stockNum \n" +
            "group by sk.stock_num")
    Mono<Long> verifyStockIsData(String stockNum);


    /**
     * 获取存货对应的供应商价格表
     * @return
     */
    @Query("select coalesce(sp.memberprice, '0') memberprice,\n" +
            "       coalesce(sp.max_price, '0')   max_price,\n" +
            "       coalesce(sp.min_price, '0')   min_price,\n" +
            "       coalesce(sp.invscost1, '0')   invscost1,\n" +
            "       coalesce(sp.invscost2, '0')   invscost2,\n" +
            "       coalesce(sp.invscost3, '0')   invscost3,\n" +
            "       coalesce(sp.invscost4, '0')   invscost4,\n" +
            "       coalesce(sp.invscost5, '0')   invscost5,\n" +
            "       coalesce(sp.invscost6, '0')   invscost6,\n" +
            "       coalesce(sp.invscost7, '0')   invscost7,\n" +
            "       coalesce(sp.invscost8, '0')   invscost8,\n" +
            "       coalesce(sp.new_price, '0')   new_price," +
            "       coalesce(sp.regular_price, '0')   regular_price,sup.price_level as sup_price_level\n" +
            "from stock_sup_price sp\n" +
            "         left join stock s on s.stock_num = sp.stock_num\n" +
            "         left join supplier sup on sup.unique_code = s.stock_supplier\n" +
            "where s.stock_num =:stockNum ")
    Flux<StockPriceVo> findByStockSupplierPrice(String stockNum);

    @Query("select  *  from stock  where stock_num in (:stockList)  order by create_time desc")
    Flux<StockVo> findAllByStockNums(List<String> stockList);

    @Query("select  *  from stock  where stock_barcode IN (:stockList) or stock_unit_barcode1 IN (:stockList) or stock_unit_barcode2 IN (:stockList) " +
            " order by create_time desc")
    Flux<StockVo> findAllByStockBarcode(List<String> stockList);
}



