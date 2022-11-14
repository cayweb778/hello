package org.boozsoft.repo.stock;

import org.springframework.data.repository.query.Param;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.domain.vo.stock.StockAccSheetVo;
import org.boozsoft.domain.vo.stock.StockWarehousingsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.boozsoft.domain.vo.stock.StockKctzVo;

import java.util.List;

public interface StockWarehousingsRepository extends ReactiveCrudRepository<StockWarehousings, String> {
    Flux<StockWarehousings> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String type, String code);
    Flux<StockWarehousings> findAllByBillStyleAndIyearOrderByBcheckDescDdateAscCcodeAscLineIdAsc(String type, String iyear);

    Flux<StockWarehousings> findAllByCcodeAndBillStyle(String ccode,String type);

    @Query("delete from stock_warehousings where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndSourcetype(String ccode, String billStyle);

    @Query("select * from stock_warehousings where line_code=:linecode ")
    Mono<StockWarehousings> findByLineCode(String linecode);

    @Query(" select s.stock_num, s.stock_barcode,s.stock_name, s.stock_ggxh, s.stock_class, " +
            " uom.unit_name cunitname,uom.unit_type utype," +
            " uoms.unit_name as cunitnames, um.unit_type utypes," +
            " uoms1.unit_name as fnames1, uoms1.conversion_rate as rate1, " +
            " uoms2.unit_name as fnames2, uoms2.conversion_rate as rate2, " +
            " sc.* from stock_warehousings sc  " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " LEFT JOIN sys_unit_of_mea uom ON uom.ID = sc.cunitid" +
            " LEFT JOIN sys_unit_of_mea_list uoms ON uoms.ID = sc.cunitid" +
            " LEFT JOIN sys_unit_of_mea_many um ON um.unit_code = uoms.many_code" +
            " LEFT JOIN sys_unit_of_mea_list uoms1 ON uoms1.ID = sc.cunitid_f1" +
            " LEFT JOIN sys_unit_of_mea_list uoms2 ON uoms2.ID = sc.cunitid_f2" +
            " where bill_style =:billStyle and ccode=:ccode")
    Flux<StockWarehousingsVo> findAllByBillStyleAndCcode(String billStyle, String ccode);

    @Query("update stock_warehousings set bcheck=:bcheck,bcheck_time=:bcheckTime,bcheck_user=:bcheckUser where ccode in (:list) ")
    Mono<Void> editBcheck(String bcheck, String bcheckTime, String bcheckUser, List<String> list);

    @Query("delete from stock_warehousings where ccode in (:ccode) ")
    Mono<Void> delAllList(List<String> list);

    @Query("select sup.cust_name,sk.stock_name,psn.real_name as cmaker_name,psn2.real_name as buname, sws.*\n" +
            "from stock_warehousings sws\n" +
            "left join supplier sup on sup.id=sws.cvencode\n" +
            "left join stock sk on sk.stock_num=sws.cinvode\n" +
            "left join _app_group_sys_user psn on psn.id=sws.cmaker\n" +
            "left join _app_group_sys_user psn2 on psn2.id=sws.bcheck_user\n" +
            "left join stock_cangku ck on ck.id=sws.cwhcode\n"+
            "where sws.bill_style =:type \n" +
            "  and sws.iyear =:iyear \n" +
            "order by sws.ddate, sws.ccode, sws.line_code")
    Flux<StockWarehousingsVo> findAllByBillStyleAndIyear(String type, String iyear);

    @Query(" select " +
            " sc.bcheck, sc.ddate,  sc.bill_style, sc.ccode, sc.dpdate, sc.dvdate, sc.base_quantity as bq, sc.price as price, sc.icost  as icost, sc.cwhcode,sup.cust_name as comcode,cus.cust_name as comcode2, sc.cmaker_time," +
            " sc.citemcode, sc.batch_id," +
            "  (case  " +
            "     when sw.unit_type = 'cust' then (select sm.cust_name from customer sm where sm.id = sw.unit_value)\n" +
            "     when sw.unit_type = 'supplier' then (select sm.cust_name from supplier sm where sm.id = sw.unit_value)\n" +
            "     else (sw.unit_value) " +
            "   end) as comcode3 " +
            " from stock_warehousings sc  " +
            " left join stock_warehousing sw on sw.ccode = sc.ccode " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " left join supplier sup on sup.id=sc.cvencode" +
            " left join customer cus on cus.id=sc.cvencode " +
            " where s.stock_num =:ch and sc.ddate BETWEEN :strDate AND :endDate and sc.bill_style in ('CGRKD', 'DBRKD', 'XTZHRKD', 'QTRKD', 'PYRKD','RKTZD')")
    Flux<StockKctzVo> findAllByCinvodeAndDate(String ch, String strDate, String endDate);

    @Query("select s.stock_ggxh,\n" +
            "       s.stock_name,\n" +
            "       (case\n" +
            "            when s.stock_measurement_type = '单计量' then (select sm.unit_name\n" +
            "                                                        from sys_unit_of_mea sm\n" +
            "                                                        where sm.id = sws.cg_unit_id)\n" +
            "            else (select sm.unit_name\n" +
            "                  from sys_unit_of_mea_list sm\n" +
            "                  where sm.id = sws.cg_unit_id) end) as unit_name,\n" +
            "       sws.*\n" +
            "from stock_warehousings sws\n" +
            "         left join stock s on s.stock_num = sws.cinvode\n" +
            "where sws.ccode in (:list)\n" +
            "order by sws.ddate, sws.ccode asc, sws.bill_style desc")
    Flux<StockWarehousingsVo> findAllByListCcode(List<String> list);

    @Query("select count(id) from stock_warehousings where iyear=:iyear and bcheck!='1' and bill_style='PYRKD'")
    Mono<Long> getPYRKDAndNoBcheck1(String iyear);

    @Query(" select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.cwhcode, " +
            " sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid,  sbb.price, sbb.icost, sbb.bcheck," +
            " sbb.cinvode, sbb.batch_id as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.sub_quantity2 bq2, sbb.dpdate, sbb.dvdate, '1' as types, sbb.isum_chuku  as isum " +
            " from stock_warehousings sbb  " +
            " where sbb.iyear=:iyear  and sbb.cwhcode =:ck  and sbb.bill_style in ('CGRKD', 'QTRKD','CLLYRKD') ")
    Flux<StockAccSheetVo> findAllByIyearAndCk(String iyear,String ck);

    @Query(" select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.cwhcode, " +
            " sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid,  sbb.price, sbb.icost, sbb.bcheck," +
            " sbb.cinvode, sbb.batch_id as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.sub_quantity2 bq2, sbb.dpdate, sbb.dvdate, '1' as types, sbb.isum_chuku  as isum " +
            " from stock_warehousings sbb " +
            " where sbb.iyear=:iyear and sbb.cwhcode=:ck and sbb.cinvode in (:list) and sbb.bill_style in ('CGRKD', 'QTRKD','CLLYRKD','CGDHD')  ")
    Flux<StockAccSheetVo> findAllByIyearAndCkAndList(String iyear,String ck,List<String> list);

    @Query(" select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.cwhcode, " +
            " sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid,  sbb.price, sbb.icost, " +
            " sbb.cinvode, sbb.batch_id as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.sub_quantity2 bq2, sbb.bcheck as types, sbb.ccode, sbb.sourcecode, sbb.sourcetype,sbb.bill_style " +
            " from stock_warehousings sbb  " +
            " where sbb.iyear=:iyear  and  sbb.cwhcode=:ck and sbb.bill_style in ('CGRKD', 'CGDHD','QTRKD','QC')")
    Flux<StockAccSheetVo> findAllByIyearAndCkZaitu( String iyear, String ck);

    @Query("select s.stock_ggxh,s.stock_class,psn3.real_name as buname,psn2.real_name as cmaker_name,sup.cust_name,sup.cust_code,sw.* from stock_warehousings sw\n" +
            "left join supplier sup on sup.id=sw.cvencode\n" +
            "left join stock s on s.stock_num=sw.cinvode\n" +
            "left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
            "left join _app_group_sys_user psn3 on psn3.id=sw.bcheck_user\n" +
            "where sw.bill_style=:type \n" +
            "order by sw.ccode asc")
    Flux<StockWarehousingsVo> findAllMainsList(String type);

    @Query("delete from stock_warehousings where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndBillType(String obj, String dbckd);

    @Query("select s.stock_ggxh,s.stock_class,psn3.real_name as buname,psn2.real_name as cmaker_name,sup.cust_name,sw.*" +
            "from stock_warehousings sw\n" +
            "left join supplier sup on sup.id=sw.cvencode\n" +
            "left join stock s on s.stock_num=sw.cinvode\n" +
            "left join stock_cangku ck on ck.id=sw.cwhcode\n"+
            "left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
            "left join _app_group_sys_user psn3 on psn3.id=sw.bcheck_user\n" +
            "where sw.bill_style=:type and sw.ddate between :strDate and :endDate \n" +
            "order by sw.ccode asc")
    Flux<StockWarehousingsVo> findAllByTypeList(String type, String strDate, String endDate);

    @Query("select sw.* from stock_warehousings sw " +
            "where sw.bill_style in (:tlist)  and  sw.bcheck = '1' and sw.ddate like :riqi \n" +
            "order by sw.ccode asc")
    Flux<StockWarehousings> findAllByBillStyleAndDdate(String riqi, List<String> tlist);

    @Query("select * from stock_warehousings  where bill_style = 'CGDHD' and  bcheck = '1'  and ddate like :riqi ")
    Flux<StockWarehousings> findAllDdate(String riqi);

    Mono<Void> deleteByLineCode(String lineCode);

    @Query(" select " +
            " sc.bcheck, sc.ddate,  sc.bill_style, sc.ccode, sc.dpdate, sc.dvdate, sc.base_quantity as bq, sc.price as price, sc.icost  as icost, sc.cwhcode,sup.cust_name as comcode,cus.cust_name as comcode2, sc.cmaker_time," +
            " sc.citemcode, sc.batch_id," +
            "  (case  " +
            "     when sw.unit_type = 'cust' then (select sm.cust_name from customer sm where sm.id = sw.unit_value)\n" +
            "     when sw.unit_type = 'supplier' then (select sm.cust_name from supplier sm where sm.id = sw.unit_value)\n" +
            "     else (sw.unit_value) " +
            "   end) as comcode3 " +
            " from stock_warehousings sc  " +
            " left join stock_warehousing sw on sw.ccode = sc.ccode " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " left join supplier sup on sup.id=sc.cvencode " +
            " left join customer cus on cus.id=sc.cvencode " +
            " where s.stock_num =:ch and sc.batch_id =:pc and sc.ddate BETWEEN :strDate AND :endDate and sc.bill_style in ('CGRKD', 'DBRKD', 'XTZHRKD', 'QTRKD', 'PYRKD','CLLYRKD')")
    Flux<StockKctzVo> findAllByCinvodeAndDateAndPc(String ch, String pc, String strDate, String endDate);

    @Query("select temp.* from (select  (case\n" +
            "                            when s.stock_measurement_type = '单计量' then '1'\n" +
            "                            else (select sm.conversion_rate\n" +
            "                                  from sys_unit_of_mea_list sm\n" +
            "                                  where sm.id = sws.cg_unit_id) end) as conversion_rate\n" +
            "               from stock_warehousings sws\n" +
            "                        left join stock s on s.stock_num = sws.cinvode\n" +
            "               where sws.cg_unit_id =:cgUnitId \n" +
            "                 and sws.ccode =:ccode \n" +
            "               order by sws.ddate, sws.ccode asc, sws.bill_style desc) as temp group by temp.conversion_rate")
    Mono<StockWarehousingsVo> getUnitRate(String cgUnitId,String ccode);

    @Query("select (cast(temp.base_quantity as float)-cast(temp.isum_jiesuan as float))/cast(temp.conversion_rate as float) as remain_cnumber," +
            "   (cast(temp.isum_jiesuan as float))/cast(temp.conversion_rate as float) as use_cnumber, temp.* from (select s.stock_ggxh,\n" +
            "       s.stock_name,\n" +
            "        (case when sws.cg_unit_id=s.stock_unit_id then s.stock_barcode else\n" +
            "           ((case when sws.cg_unit_id=s.stock_unit_id1 then s.stock_unit_barcode1 else\n" +
            "               (case when sws.cg_unit_id=s.stock_unit_id2 then s.stock_unit_barcode2 else '' end ) end )) end ) stock_barcode,\n" +
            "       (case when sws.cg_unit_id=s.stock_unit_id then s.stock_ggxh else\n" +
            "           ((case when sws.cg_unit_id=s.stock_unit_id1 then s.stock_unit_ggxh1 else\n" +
            "               (case when sws.cg_unit_id=s.stock_unit_id2 then s.stock_unit_ggxh2 else '' end ) end )) end ) stock_ggxh,\n" +
            "       (case when sws.cg_unit_id=s.stock_unit_id then s.stock_unit_name else\n" +
            "           ((case when sws.cg_unit_id=s.stock_unit_id1 then s.stock_unit_name1 else\n" +
            "               (case when sws.cg_unit_id=s.stock_unit_id2 then s.stock_unit_name2 else '' end ) end )) end ) unit_name," +
            "   (case when s.stock_measurement_type = '单计量' then '1' else (select sm.conversion_rate from sys_unit_of_mea_list sm where sm.id = sws.cg_unit_id) end) as conversion_rate," +
            "       sws.*\n" +
            "from stock_warehousings sws\n" +
            "         left join stock s on s.stock_num = sws.cinvode\n" +
            "where sws.cvencode=:cvencode and sws.ddate between :startDate and :endDate and sws.bill_style in (:billStyle) and sws.bcheck='1') as temp where (cast(temp.isum_jiesuan as float))/cast(temp.conversion_rate as float)>0 ")
    Flux<StockWarehousingsVo> findAllByBillStyleAndDdateAndCvencode(String cvencode,List<String> billStyle,String startDate, String endDate);


    Flux<StockWarehousings> findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(String year,  String stockId, String batchId, String start, String end,List<String> bills);
    Flux<StockWarehousings> findAllByIyearAndCinvodeAndBillStyleIn(String year,String stockId,List<String> bills);

    Flux<StockWarehousings> findAllByIyearAndCinvodeAndBillStyle(String year,String stockId,String bill);
    Flux<StockWarehousings> findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(String year,  String stockId, String batchId, String start, String end,String bill);


    @Query(" select sbb.* from stock_warehousings sbb  where  sbb.cinvode=:ch and sbb.iyear=:year and sbb.ddate <=:date  and cwhcode = :ck and sbb.bill_style in ('CGRKD','QTRKD','RKTZD','HZHCD','LZHCD')")
    Flux<StockWarehousings> findAllByCwhcodeAndCinvodeAndDdate(String ck,String ch, String year, String date);

    @Query(" select sbb.* from stock_warehousings sbb  where  sbb.cinvode=:ch and sbb.batch_id=:batchId and sbb.iyear=:year and sbb.ddate <=:date  and sbb.bcheck = '1' and cwhcode = :ck and sbb.bill_style in ('CGRKD','QTRKD')")
    Flux<StockWarehousings> findAllByCwhcodeAndCinvodeAndDdateAndBatchId(String ck,String ch, String year, String date, String batchId);

    @Query(" select sbb.* from stock_warehousings sbb  where  sbb.cinvode in (:chs) and sbb.iyear=:year and sbb.ddate <=:date   and sbb.bill_style in ('CGRKD', 'QTRKD','RKTZD')")
    Flux<StockWarehousings> findAllByCinvodesAndDdate(List<String> chs, String year, String date);

    Mono<StockWarehousings> findByCcodeAndLineCode(String ccode,String lineCode);

    @Query(" select sc.* from stock_warehousings sc where  sc.iyear=:year and sc.ddate <=:date and sc.bill_style in ('CGRKD','QTRKD','RKTZD')")
    Flux<StockWarehousings> findAllByYearAndDate(String year, String date);


    @Query("select * from stock_warehousings  where bill_style = :type and  bcheck = '1'  and cinvode in (:cinvodes) ")
    Flux<StockWarehousings> findPriceByCinvodeList(String type,List<String> cinvodes);


    @Query(" select sc.* from stock_warehousings sc where  sc.iyear=:year and sc.sourcetype ='DBRKD' and sc.sourcecode in (:syccodes)")
    Flux<StockWarehousings> findAllBySourcecodeList(String year, List<String> syccodes);

    @Query("delete from stock_warehousings where sourcecode=:sourcecode and bill_style in ('HZHCD','LZHCD')  ")
    Mono<Void> delByHCD(String sourcecode);
}



