package org.boozsoft.repo.stock;

import cn.hutool.core.util.ArrayUtil;
import io.swagger.annotations.ApiModelProperty;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.entity.stock.StockSaleousings;
import org.boozsoft.domain.entity.stock.StockWarehousings;
import org.boozsoft.domain.vo.StockSaleousingsCostVo;
import org.boozsoft.domain.vo.stock.StockAccSheetVo;
import org.boozsoft.domain.vo.stock.StockKctzVo;
import org.boozsoft.domain.vo.stock.StockSaleousingsUseVo;
import org.boozsoft.domain.vo.stock.StockSaleousingsVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface StockSaleousingsRepository extends ReactiveCrudRepository<StockSaleousings, String> {

    Flux<StockSaleousings> findAllByCcode(String ccode);
    Flux<StockSaleousings> findAllByIyearAndCcode(String year,String ccode);
    Flux<StockSaleousings> findAllByBillStyleAndCcodeOrderByDdateAscCcodeAscLineIdAsc(String billStyle, String ccode);

    Flux<StockSaleousings> findAllByBillStyleAndIyearOrderByDdateAscCcodeAscLineIdAsc(String type, String year);

    Flux<StockSaleousings> findAllByBillStyleAndLineCodeInOrderByDdateAscCcodeAscLineIdAsc(String type, List<String> codes);

    Mono<Long> countAllByBillStyleAndSourcecode(String style, String code);

    @Query("delete from stock_saleousings where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndSourcetype(String ccode, String billStyle);

    Flux<StockSaleousings> findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc(String type, String start,String end);

    Flux<StockSaleousings> findAllByBillStyleAndBcheckAndDdateBetweenOrderByDdateAscCcodeAscLineIdAsc(String type, String start,String end,String check);


    @Query(" select s.stock_num, s.stock_barcode,s.stock_name, s.stock_ggxh, s.stock_class, " +
            " uom.unit_name cunitname,uom.unit_type utype," +
            " uoms.unit_name as cunitnames, um.unit_type utypes," +
            " uoms1.unit_name as fnames1, uoms1.conversion_rate as rate1, " +
            " uoms2.unit_name as fnames2, uoms2.conversion_rate as rate2, " +
            " sc.* from stock_saleousings sc  " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " LEFT JOIN sys_unit_of_mea uom ON uom.ID = sc.cunitid" +
            " LEFT JOIN sys_unit_of_mea_list uoms ON uoms.ID = sc.cunitid" +
            " LEFT JOIN sys_unit_of_mea_many um ON um.unit_code = uoms.many_code" +
            " LEFT JOIN sys_unit_of_mea_list uoms1 ON uoms1.ID = sc.cunitid_f1" +
            " LEFT JOIN sys_unit_of_mea_list uoms2 ON uoms2.ID = sc.cunitid_f2" +
            " where bill_style =:billStyle and ccode=:ccode")
    Flux<StockSaleousingsVo> findAllByBillStyleAndCcode(String billStyle, String ccode);

    Flux<StockSaleousings> findAllByBillStyleAndCcodeIn(String billStyle, List<String> ccode);
    Flux<StockSaleousings> findAllByBillStyleAndSourcecodeIn(String billStyle, List<String> ccode);

    @Query(" select " +
            " sc.bcheck, sc.ddate,  sc.bill_style, sc.ccode, sc.dpdate, sc.dvdate, sc.base_quantity as bq1, sc.price as price1, sc.icost as icost1, sc.cwhcode,sup.cust_name as comcode,cus.cust_name as comcode2, sc.cmaker_time," +
            " sc.citemcode, sc.batch_id " +
            " from stock_saleousings sc  " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " left join supplier sup on sup.id=sc.cvencode " +
            " left join customer cus on cus.id=sc.cvencode_js " +
            " where s.stock_num =:ch and sc.ddate BETWEEN :strDate AND :endDate and sc.bill_style in ('XSCKD', 'DBCKD', 'QTCKD', 'PKCKD', 'XTZHCKD','CLLYCKD','CKTZD')")
    Flux<StockKctzVo> findAllByCinvodeAndDate(String ch, String strDate, String endDate);

    @Query(" select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.cwhcode, " +
            " sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid,  sbb.price, sbb.icost,  sbb.bcheck," +
            " sbb.cinvode, sbb.batch_id as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.dpdate, sbb.dvdate, sbb.sub_quantity2 bq2, '2' as types, sbb.isum_xiaohuo as isum " +
            " from stock_saleousings sbb  " +
            " where sbb.iyear=:iyear and sbb.bcheck = '1' and cwhcode = :ck and sbb.bill_style in ('XSCKD', 'DBCKD', 'XTZHCKD', 'QTCKD', 'PYCKD','CLLYCKD','CKTZD')")
    Flux<StockAccSheetVo> findAllByIyearAndCk(String ck, String iyear);


    @Query(" select sbb.iyear as iyear, sbb.cwhcode1, sbb.cwhcode2, sbb.cwhcode3, sbb.cwhcode4, sbb.cwhcode5, sbb.cwhcode6, sbb.cwhcode, " +
            " sbb.cunitid_f1 as cunitidF1, sbb.cunitid_f2 as cunitidF2, sbb.cunitid as cunitid,  sbb.price, sbb.icost, " +
            " sbb.cinvode, sbb.batch_id as batchid, sbb.base_quantity bq, sbb.sub_quantity1 bq1, sbb.sub_quantity2 bq2, sbb.bcheck as types, sbb.ccode, sbb.sourcecode, sbb.sourcetype,sbb.bill_style " +
            " from stock_saleousings sbb  " +
            " where sbb.iyear=:iyear and bcheck != '1' and cwhcode = :ck and sbb.bill_style in ('CLLYCKD', 'XSCKD','QTCKD','XHD','QCXHD')")
    Flux<StockAccSheetVo> findAllByIyearAndCkZaitu(String ck, String iyear);
    @Query("select s.stock_ggxh,\n" +
            "       s.stock_name,\n" +
            "       (case\n" +
            "            when s.stock_measurement_type = '单计量' then (select sm.unit_name\n" +
            "                                                        from sys_unit_of_mea sm\n" +
            "                                                        where sm.id = s.stock_measurement_unit)\n" +
            "            else (select sm.unit_name\n" +
            "                  from sys_unit_of_mea_list sm\n" +
            "                  where sm.id = s.stock_measurement_unit) end) as unit_name,\n" +
            "       sws.*\n" +
            "from stock_saleousings sws\n" +
            "         left join stock s on s.stock_num = sws.cinvode\n" +
            "where sws.ccode in (:list)\n" +
            "order by sws.ddate, sws.ccode asc, sws.bill_style desc")
    Flux<StockSaleousingsUseVo> findAllByListCcode(List<String> lists);

    @Query("delete from stock_saleousings where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndBillType(String obj, String dbckd);

    @Query("select cus.cust_name, dept.dept_name, psn2.psn_name as cmaker_name,sa.base_quantity as squantity, sa.*\n" +
            "from stock_saleousings sa\n" +
            "         left join sys_department dept on dept.unique_code = sa.cdepcode\n" +
            "         left join sys_psn psn2 on psn2.id = sa.cmaker\n" +
            "         left join customer cus on cus.id = sa.cvencode" +
        "             inner join stock_saleousing ss on ss.ccode = sa.ccode and (ss.delivery_id='' or ss.delivery_id isnull )\n" +
            "where sa.bill_style =:billStyle \n" +
            "  and sa.bcheck = '1'\n" +
            "  and sa.iyear =:iyear ")
    Flux<StockSaleousingsVo> findAllByBillStyleAndIyear(String billStyle,String iyear);
    @Query("select sw.* from stock_saleousings sw " +
            "where sw.bill_style in (:tlist) and  sw.bcheck = '1' and sw.ddate like :riqi \n" +
            "order by sw.ccode asc")
    Flux<StockSaleousings> findAllByBillStyleAndDdate(String riqi, List<String> tlist);

    @Query(" select " +
            " sc.bcheck, sc.ddate,  sc.bill_style, sc.ccode, sc.dpdate, sc.dvdate, sc.base_quantity as bq1, sc.price as price1, sc.icost as icost1, sc.cwhcode,sup.cust_name as comcode,cus.cust_name as comcode2, sc.cmaker_time," +
            " sc.citemcode, sc.batch_id " +
            " from stock_saleousings sc  " +
            " left join stock s on sc.cinvode = s.stock_num " +
            " left join supplier sup on sup.id=sc.cvencode " +
            " left join customer cus on cus.id=sc.cvencode_js " +
            " where s.stock_num =:ch and sc.batch_id =:pc and sc.ddate BETWEEN :strDate AND :endDate and sc.bill_style in ('XSCKD', 'DBCKD', 'QTCKD', 'PKCKD', 'XTZHCKD','CLLYCKD')")
    Flux<StockKctzVo> findAllByCinvodeAndDateAndPc(String ch,  String pc,String strDate, String endDate);

    Mono<Void> deleteByLineCodeIn(List<String> dels);

    Flux<StockSaleousings> findAllByDdateBetweenAndBillStyleAndBcheckOrderByCcodeAsc(String strDate, String endDate, String billStyle,String check);

    Flux<StockSaleousings> findAllByDdateBetweenAndBillStyleAndCwhcodeAndBcheckOrderByCcodeAsc(String strDate, String endDate, String billStyle,String cwhcode,String check);

    Flux<StockSaleousings> findAllByTotalsourceidInOrderByDdateAscCcodeAscLineIdAsc(List<String> codes);

    Flux<StockSaleousings> findAllByCcodeIn(List<String> codes);

    Flux<StockSaleousings>  findAllByIyearAndCinvodeAndBillStyleIn(String year, String s1, List<String> asList);

    Flux<StockSaleousings> findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleInOrderByDvdateDesc(String year, String s1, String s2, String s3, String s4, List<String> asList);

    Flux<StockSaleousings> findAllByIyearAndCinvodeAndBillStyle(String year, String s, String xhd);

    Flux<StockSaleousings> findAllByIyearAndCinvodeAndBatchIdAndDpdateAndDvdateAndBillStyleOrderByDvdateDesc(String year, String s, String s1, String s2, String s3, String xhd);

    @Query(" select sbb.* from stock_saleousings sbb  where  sbb.cinvode=:ch and sbb.iyear=:year and sbb.ddate <=:date  and cwhcode = :ck and sbb.bill_style in ('XSCKD','QTCKD','CKTZD')")
    Flux<StockSaleousings> findAllByCwhcodeAndCinvodeAndDdate(String ck,String ch, String year, String date);

    @Query(" select sbb.* from stock_saleousings sbb  where  sbb.cinvode=:ch and sbb.batch_id=:batchId and sbb.iyear=:year and sbb.ddate <=:date and sbb.bcheck = '1' and cwhcode = :ck and sbb.bill_style in ('CKTZD')")
    Flux<StockSaleousings> findAllByCwhcodeAndCinvodeAndDdateAndBatchId(String ck,String ch, String year, String date, String batchId);

    @Query(" select sbb.* from stock_saleousings sbb  where  sbb.cinvode in (:chs) and sbb.iyear=:year and sbb.ddate <=:date  and sbb.bill_style in ('CGCKD', 'QTCKD','CKTZD')")
    Flux<StockSaleousings> findAllByCinvodesAndDdate(List<String> chs, String year, String date);

    @Query("select sw.*, s.stock_valuation_type as buname from stock_saleousings sw " +
            " left join stock s on s.stock_num = sw.cinvode " +
            " where sw.bill_style = :style  and sw.ddate BETWEEN :strriqi AND :endriqi ")
    Flux<StockSaleousingsCostVo> findAllByBillStyleAndDdateDist(String strriqi, String endriqi, String style);

    @Query("select sw.*, s.stock_valuation_type as buname from stock_saleousings sw " +
            " left join stock s on s.stock_num = sw.cinvode " +
            " where sw.bill_style = :style  and sw.ddate <= :riqi")
    Flux<StockSaleousingsCostVo> findAllByBillStyleAndYearAndDdates(String style,String riqi);

    @Query("select sw.* from stock_saleousings sw where sw.bill_style in ('CLLYD','QTCKD','XSCKD') and sw.icost <'0' and iyear=:year and sw.ddate <= :riqi ")
    Flux<StockSaleousings> findAllByBillStyleAndYearAndDdate(String year,String riqi);
}



