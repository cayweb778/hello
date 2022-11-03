package org.boozsoft.repo.stock;

import cn.hutool.core.util.ArrayUtil;
import org.boozsoft.domain.entity.stock.StockTaking;
import org.boozsoft.domain.entity.stock.StockTakings;
import org.boozsoft.domain.vo.stock.StockTakingsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface StockTakingsRepository extends ReactiveCrudRepository<StockTakings, String> {

    Flux<StockTakings> findByCwhcodeAndCinvodeAndIyearAndBatchIdAndDpdateAndDvdate(String cwhcode,String cinvode,String iyear,String batchId,String dpdate,String dvdate);

    @Query(" select s.stock_num, s.stock_barcode,s.stock_name, s.stock_ggxh, s.stock_class, s.stock_property_batch, " +
            " s.stock_measurement_type umtype, " +
            " s.stock_measurement_unit utcode, " +
            " st.id as pdid, st.base_quantity as bq, st.sub_quantity1 as sq1,  st.sub_quantity2 as sq2, " +
            " st.* from stock_takings st  " +
            " left join stock s on st.cinvode = s.stock_num where st.ccode =:ccode order by st.ccode " )
    Flux<StockTakingsVo> findAllByPrice(String ccode);

    @Query("delete from stock_takings where ccode=:ccode")
    Mono<Void> deleteByCcode(String ccode);

    Flux<StockTakings> findByCcode(String ccode);

    @Query("select sts.* from stock_takings sts left join stock_taking st on sts.ccode = st.ccode" +
            " where sts.id=:id and sts.ccode = :ccode ")
    Mono<StockTakings> findByCurrentId(String id, String ccode);

    @Query("delete from stock_takings where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("update stock_takings set quantity_pd = base_quantity, sub_pandian1 = sub_quantity1, sub_pandian2 = sub_quantity2, " +
            " quantity_uk = '0.00', quantity_uk1 = '0.00', quantity_uk2 = '0.00' " +
            " where ccode=:ccode")
    Mono<Void> updateByCcode(String ccode);

    @Query("update stock_takings set quantity_pd = '0.00', sub_pandian1 = '0.00', sub_pandian2 = '0.00', " +
            " quantity_uk = '0.00', quantity_uk1 = '0.00', quantity_uk2 = '0.00' " +
            " where ccode=:ccode")
    Mono<Void> updateByCcodeZero(String ccode);

    Flux<StockTakings> findAllByCcodeIn(List<String> codes);
    @Query(" select  st.*, s.stock_measurement_type uimtype, s.stock_measurement_unit uimid, " +
            " s.stock_barcode sb ,s.stock_unit_barcode1 sb1 ,s.stock_unit_barcode2 sb2" +
            " from stock_takings st  " +
            " left join stock s on st.cinvode = s.stock_num where st.ccode =:ccode " )
    Flux<StockTakingsVo> findByCcodes(String ccode);

}



