package org.boozsoft.repo.stock;

import io.swagger.annotations.ApiModelProperty;
import org.boozsoft.domain.entity.stock.StockBeginBalance;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.domain.vo.stock.StockAccSheetVo;
import org.boozsoft.domain.vo.stock.StockBalanceColumnsVo;
import org.boozsoft.domain.vo.stock.StockBeginBalanceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockBeginBalanceRepository extends ReactiveCrudRepository<StockBeginBalance, String> {
    @Query("select COALESCE(max(iine_id)+1, 0) as floor_num from stock_begin_balance")
    Mono<Long> getStockBalanceMaxIineId();

    @Query("select count(id) batch_number,\n" +
            "       (select count(id) dvdate from stock_begin_balance where dvdate is not null and dvdate!='' and iyear=:iyear),\n" +
            "       (select count(sub_quantity1) sub_quantity1 from stock_begin_balance where sub_quantity1 >0 and iyear=:iyear),\n" +
            "       (select count(sub_quantity2) sub_quantity2 from stock_begin_balance where sub_quantity2 >0 and iyear=:iyear)\n" +
            "from stock_begin_balance\n" +
            "where batch_number is not null and batch_number!='' and iyear=:iyear")
    Mono<StockBalanceColumnsVo> getStockBalanceColumns(String iyear);

    @Query("select (select appu.username from _app_group_sys_user appu where appu.id = sb.cmaker )username,\n" +
            "       (select appu.username from _app_group_sys_user appu where appu.id = sb.bcheck_user )bcheck_user_name,st.stock_num,\n" +
            "       st.stock_name,\n" +
            "       st.stock_ggxh,st.stock_barcode,st.stock_unit_name,st.stock_unit_name1,st.stock_unit_name2,ck.ck_name as cwhcode_join," +
            "( case when st.stock_unit_id = st.stock_purchase_unit then sb.base_quantity else ( case when  st.stock_unit_id1 = st.stock_purchase_unit then sb.sub_quantity1 else ( case when st.stock_unit_id2 = st.stock_purchase_unit then sb.sub_quantity2 else 0 end) end) end) as cnumber,\n" +
            " ( case when st.stock_purchase_unit = st.stock_unit_id then st.stock_unit_name else ( case when st.stock_purchase_unit = st.stock_unit_id1 then st.stock_unit_name1 else ( case when st.stock_purchase_unit = st.stock_unit_id2 then st.stock_unit_name2 else '' end) end) end) as cg_unit_id," +
            "sb.*\n" +
            "from stock_begin_balance sb\n" +
            "         left join stock st on st.stock_num = sb.stock_id left join stock_cangku ck on ck.id = sb.stock_cangku_id left join _app_group_sys_user appu on appu.id = sb.cmaker \n" +
            "where sb.iyear =:iyear order by sb.bcheck desc,sb.batch_number asc")
    Flux<StockBeginBalanceVo> findAllStockBalance(String iyear);

    @Query("update stock_begin_balance set bcheck=:bcheck,bcheck_user=:bcheckUser,bcheck_time=:bcheckTime where id in (:id)")
    Mono<Void> editAuditStockBalance(String bcheck, String bcheckUser, String bcheckTime, List<String> id);

    @Query("delete from stock_begin_balance where id in (:id)")
    Mono<Void> delStockBalance(List<String> id);

    Flux<StockBeginBalance> findAllByStockId(String stockId);


    @Query("select count(id) +\n" +
            "       (select count(id) from stock_saleousings where cinvode =:cinvode and bill_style = 'CGDHD' and iyear =:iyear ) as aaa\n" +
            "from stock_warehousings\n" +
            "where cinvode =:cinvode and bill_style = 'CGDHD' and iyear =:iyear and bdocum_style='1' ")
    Mono<Long> findByStock_CKAndTH(String cinvode,String iyear);

    @Query("select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.stock_cangku_id as cwhcode, " +
            " sbb.cunitid_type as cunitidType, sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid, sbb.price, sbb.icost, sbb.bcheck," +
            " sbb.stock_id as cinvode, sbb.batch_number as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.dpdate, sbb.dvdate, sbb.sub_quantity2 bq2, '0' as types " +
            " from stock_begin_balance sbb " +
            "where  bcheck = '1' and iyear=:iyear and stock_cangku_id = :ck")
    Flux<StockAccSheetVo> findAllByIyearAndCk(String ck, String iyear);

    @Query("select sbb.stock_id as cinvode, sbb.batch_number as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.sub_quantity2 bq2, '0' as types " +
            " from stock_begin_balance sbb " +
            "where  bcheck = '1' and iyear=:iyear")
    Flux<StockBeginBalance> findAllByIyear(String iyear);

    Flux<StockBeginBalance> findAllByStockIdAndBatchNumber(String stockId, String pcNum);


    Flux<StockBeginBalance> findAllByIyearAndBcheckAndStockIdAndBatchNumberAndDpdateAndDvdateOrderByDvdateDesc(String year,String check,String stockId,String batchId,String start,String end);
    Flux<StockBeginBalance> findAllByIyearAndBcheckAndStockId(String year,String check,String stockId);
    Flux<StockBeginBalance> findAllByIyearAndBcheckAndCwhcode1(String year,String check,String stockId);

    @Query(" select * from stock_begin_balance sbb where   sbb.stock_id=:ch  and sbb.iyear=:year and sbb.bcheck = '1' and sbb.batch_number=:batchId  and stock_cangku_id = :ck ")
    Flux<StockBeginBalance> findAllByCwhcodeAndCinvodeAndDdateAndBatchId(String ck, String ch, String year, String batchId);

    @Query(" select * from stock_begin_balance sbb where   sbb.stock_id=:ch  and sbb.iyear=:year and sbb.bcheck = '1'  and stock_cangku_id = :ck ")
    Flux<StockBeginBalance> findAllByCwhcodeAndCinvode(String ck, String ch, String year);

    @Query(" select * from stock_begin_balance sbb where  sbb.stock_id in (:chs)  and sbb.iyear=:year and sbb.bcheck = '1' ")
    Flux<StockBeginBalance> findAllByCinvodes(List<String> chs, String year);


    @Query("select distinct temp.*,\n" +
            "                temp.batch_id as batch_number,\n" +
            "                temp.cwhcode as stock_cangku_id,\n" +
            "                ck.stock_num as stock_id,\n" +
            "                ck.stock_name,\n" +
            "                ck.stock_ggxh,\n" +
            "                ck.stock_unit_id,\n" +
            "                ck.stock_unit_id1,\n" +
            "                ck.stock_unit_id2,\n" +
            "                ck.stock_unit_name,\n" +
            "                ck.stock_unit_name1,\n" +
            "                ck.stock_unit_name2,\n" +
            "                (select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id)  rate,\n" +
            "                (select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id1) rate1,\n" +
            "                coalesce((select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id2),\n" +
            "                         '0')                                                                            rate2\n" +
            "from (select sbb.stock_id        as cinvode,\n" +
            "             sbb.stock_cangku_id as cwhcode,\n" +
            "             sbb.batch_number    as batch_id,\n" +
            "             sbb.dpdate,\n" +
            "             sbb.dvdate\n" +
            "      from stock_begin_balance sbb\n" +
            "      where 1 = 1\n" +
            "        and sbb.stock_cangku_id = :cwhcode\n" +
            "        and sbb.stock_id = :stockId\n" +
            "        and sbb.iyear = :year\n" +
            "      union all\n" +
            "      select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
            "      from stock_warehousings sws\n" +
            "      where 1 = 1\n" +
            "        and sws.iyear = :year\n" +
            "        and sws.cwhcode1 = :cwhcode\n" +
            "        and sws.cinvode = :stockId\n" +
            "        and sws.bill_style != 'CGDD'\n" +
            "      union all\n" +
            "      select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
            "      from stock_saleousings sws\n" +
            "      where 1 = 1\n" +
            "        and sws.iyear = :year\n" +
            "        and sws.cwhcode1 = :cwhcode\n" +
            "        and sws.cinvode = :stockId\n" +
            "        and sws.bill_style != 'XSDD') as temp\n" +
            "        left join stock ck on ck.stock_num = temp.cinvode\n" +
            "where temp.cinvode is not null\n" +
            "  and temp.cwhcode is not null\n" +
            "  and temp.batch_id is not null")
    Flux<StockBeginBalance> findAllByIyearAndCwhcodeAndStockId(@Param("year") String year, @Param("cwhcode")String cwhcode,@Param("stockId") String stockId);


    @Query("select distinct temp.*,\n" +
            "                temp.batch_id as batch_number,\n" +
            "                temp.cwhcode as stock_cangku_id,\n" +
            "                ck.stock_num as stock_id,\n" +
            "                ck.stock_name,\n" +
            "                ck.stock_ggxh,\n" +
            "                ck.stock_unit_id,\n" +
            "                ck.stock_unit_id1,\n" +
            "                ck.stock_unit_id2,\n" +
            "                ck.stock_unit_name,\n" +
            "                ck.stock_unit_name1,\n" +
            "                ck.stock_unit_name2,\n" +
            "                (select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id)  rate,\n" +
            "                (select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id1) rate1,\n" +
            "                coalesce((select mx.conversion_rate from sys_unit_of_mea_list mx where mx.id = ck.stock_unit_id2),\n" +
            "                         '0')                                                                            rate2\n" +
            "from (select sbb.stock_id        as cinvode,\n" +
            "             sbb.stock_cangku_id as cwhcode,\n" +
            "             sbb.batch_number    as batch_id,\n" +
            "             sbb.dpdate,\n" +
            "             sbb.dvdate\n" +
            "      from stock_begin_balance sbb\n" +
            "      where 1 = 1\n" +
            "        and sbb.stock_cangku_id = :cwhcode\n" +
            "        and sbb.iyear = :year\n" +
            "      union all\n" +
            "      select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
            "      from stock_warehousings sws\n" +
            "      where 1 = 1\n" +
            "        and sws.iyear = :year\n" +
            "        and sws.cwhcode1 = :cwhcode\n" +
            "        and sws.bill_style != 'CGDD'\n" +
            "      union all\n" +
            "      select distinct sws.cinvode, sws.cwhcode, sws.batch_id, sws.dpdate, sws.dvdate\n" +
            "      from stock_saleousings sws\n" +
            "      where 1 = 1\n" +
            "        and sws.iyear = :year\n" +
            "        and sws.cwhcode1 = :cwhcode\n" +
            "        and sws.bill_style != 'XSDD') as temp\n" +
            "        left join stock ck on ck.stock_num = temp.cinvode\n" +
            "where temp.cinvode is not null\n" +
            "  and temp.cwhcode is not null\n" +
            "  and temp.batch_id is not null")
    Flux<StockBeginBalance> findAllByIyearAndCwhcode(@Param("year") String year, @Param("cwhcode")String cwhcode);

}

