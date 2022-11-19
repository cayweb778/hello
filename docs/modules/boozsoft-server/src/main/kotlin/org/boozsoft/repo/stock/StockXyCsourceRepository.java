package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockXyCsource;
import org.boozsoft.domain.vo.stock.StockWarehousings2Vo;
import org.boozsoft.domain.vo.stock.StockXySourceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockXyCsourceRepository extends ReactiveCrudRepository<StockXyCsource, String> {

    Mono<StockXyCsource> findByIyearAndCcodeAndCcodeDate(String year,String code,String date);
    Mono<StockXyCsource> findByCcodeAndCcodeDate(String year,String code,String date);
    Mono<StockXyCsource> findByIyearAndCcodeAndXyBillStyle(String year,String code,String xyCode);
    Flux<StockXyCsource> findAllByBillStyleAndCcodeIn(String type,List<String> code);
    Flux<StockXyCsource> findAllByBillStyleAndCcode(String type,String code);

    Flux<StockXyCsource> findAllByBillStyleAndIyear(String billStyle,String iyear);
    @Query("select * from stock_xy_csource where bill_style=:billStyle and iyear=:iyear and ccode=:ccode and xy_bill_style not in ('HZHCD','LZHCD','CGJSD')")
    Flux<StockXyCsource> findAllByBillStyleAndIyearAndCcode(String billStyle,String iyear,String ccode);

    Mono<StockXyCsource> findByXyBillStyleAndXyccodeAndIyear(String xyStyle,String xyCode,String iyear);

    Mono<StockXyCsource> findByXyBillStyleAndXyccodeAndIyearAndCcode(String xyStyle,String xyCode,String iyear,String ccode);

    Flux<StockXyCsource> findByCcodeAndXyccode(String code,String xycode);
    Flux<StockXyCsource> findByXyccodeAndXyBillStyle(String code,String xycode);
    Flux<StockXyCsource> findAllByCcode(String code);

    @Query("select xy.bill_style as xy_bill_style\n" +
            "     , xy.xyccode \n" +
            "     , xy.ccode_date as xyccode_date\n" +
            "     , s.stock_name\n" +
            "     , s.stock_num\n" +
            "     , s.stock_ggxh\n" +
            "     , sw.base_quantity\n" +
            "     , sw.cmemo,\n" +
            "       sw.cunitid,\n" +
            "       sw.cwhcode\n" +
            " from stock_xy_csource xy\n" +
            "         left join stock_warehousings sw on sw.ccode = xy.ccode\n" +
            "         left join stock s on s.stock_num = sw.cinvode\n" +
            "where xy.xy_bill_style =:xytype and xy.bill_style=:sytype and xy.ccode=:sytccode order by sw.ccode ")
    Flux<StockXySourceVo> findByXyDataSourrceMap(String xytype,String sytype,String sytccode);

    @Query("select xy.bill_style as xy_bill_style\n" +
            "     , xy.ccode as xyccode\n" +
            "     , xy.ccode_date as xyccode_date\n" +
            "     , s.stock_name\n" +
            "     , s.stock_num\n" +
            "     , s.stock_ggxh\n" +
            "     , sw.base_quantity\n" +
            "     , sw.cmemo,\n" +
            "       sw.cunitid,\n" +
            "       sw.cwhcode\n" +
            " from stock_xy_csource xy\n" +
            "         left join stock_warehousings sw on sw.ccode = xy.ccode\n" +
            "         left join stock s on s.stock_num = sw.cinvode\n" +
            "where xy.bill_style =:sytype and xy.xy_bill_style =:xytype and xy.xyccode=:xyccode order by sw.ccode ")
    Flux<StockXySourceVo> findBySyDataSourrceMap(String xytype,String sytype,String xyccode);


    @Query("select xy.xy_bill_style\n" +
            "     , xy.xyccode\n" +
            "     , xy.xyccode_date\n" +
            "     , sw.cmemo\n" +
            "     , sw.cwhcode\n" +
            "     , dept.dept_name\n" +
            "     , psn.psn_name\n" +
            " from stock_xy_csource xy\n" +
            "         left join stock_saleousing sw on sw.ccode = xy.xyccode\n" +
            "    left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            "where xy.bill_style=:billStyle and xy.ccode =:code order by sw.ccode ")
    Flux<StockXySourceVo> findByXyOutDataSourrce(String billStyle,String code);

    @Query("select sw.bill_style as xy_bill_style\n" +
            "     , sw.ccode as xyccode \n" +
            "     , sw.ddate as xyccode_date\n" +
            "     , sw.cmemo,\n" +
            "     ,  sw.cwhcode\n" +
            "     , dept.dept_name\n" +
            "     , psn.psn_name\n" +
            "from  stock_saleousing sw \n" +
            "    left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            "where sw.bill_style =:billStyle and sw.ccode =:code")
    Flux<StockXySourceVo> findByPlLyOutDataSourrce(String billStyle,String code);

    @Query("select xy.bill_style as xy_bill_style\n" +
            "     , xy.ccode as xyccode\n" +
            "     , xy.ccode_date as xyccode_date\n" +
            "     , sw.cmemo\n" +
            "     , sw.cwhcode\n" +
            "     , dept.dept_name\n" +
            "     , psn.psn_name\n" +
            " from stock_xy_csource xy\n" +
            "         left join stock_saleousing sw on sw.ccode = xy.xyccode\n" +
            "    left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            "where xy.xy_bill_style=:billStyle and xy.xyccode =:code order by sw.ccode ")
    Flux<StockXySourceVo> findByLyOutDataSourrce(String billStyle,String code);

    @Query("select count(sws.ccode)\n" +
            "from stock_xy_csource xy\n" +
            "left join stock_warehousings sws on sws.ccode=xy.xyccode and sws.sourcedetail_id=:sourcedetailId \n" +
            "where xy.iyear =:iyear \n" +
            "  and xy.bill_style =:billStyle ")
    Mono<Long> verifySyLineCodeExistXyData(String billStyle,String iyear,String sourcedetailId);


    @Query("select xy.xy_bill_style\n" +
            "     , xy.xyccode\n" +
            "     , xy.ccode_date as xyccode_date\n" +
            "     , sw.squantity as base_quantity\n" +
            "     , sw.isum\n" +
            "     , sw.cmemo\n" +
            "     , sw.cwhcode\n" +
            "     , dept.dept_name\n" +
            "     , psn.psn_name,sw.bdocum_style\n" +
            "from stock_xy_csource xy\n" +
            "         left join stock_warehousing sw on sw.ccode = xy.xyccode\n" +
            "         left join sys_department dept on dept.unique_code = sw.cdepcode\n" +
            "         left join sys_psn psn on psn.unique_code = sw.cpersoncode\n" +
            "where xy.xy_bill_style =:xytype \n" +
            "  and xy.bill_style =:sytype \n" +
            "  and xy.ccode =:sytccode \n" +
            "order by sw.ccode ")
    Flux<StockXySourceVo> findByXyDataMainSourrceMap(String xytype,String sytype,String sytccode);

    @Query("select xy.xy_bill_style\n" +
            "     , xy.xyccode, xy.ccode, xy.bill_style\n" +
            "     , xy.ccode_date as xyccode_date\n" +
            "     , sw.squantity as base_quantity\n" +
            "     , sw.isum\n" +
            "     , sw.cmemo\n" +
            "     , sw.cwhcode\n" +
            "     , dept.dept_name\n" +
            "     , psn.psn_name\n" +
            "from stock_xy_csource xy\n" +
            "         left join stock_warehousing sw on sw.ccode = xy.ccode\n" +
            "         left join sys_department dept on dept.unique_code = sw.cdepcode\n" +
            "         left join sys_psn psn on psn.unique_code = sw.cpersoncode\n" +
            "where xy.xy_bill_style =:xytype \n" +
            "  and xy.bill_style =:sytype \n" +
            "  and xy.xyccode =:xyccode \n" +
            "order by sw.ccode ")
    Flux<StockXySourceVo> findBySyDataMainSourrceMap(String xytype,String sytype,String xyccode);

    @Query("select COALESCE(sw.isum_daohuo, '0') isum_daohuo, COALESCE(sw.isum_ruku, '0') isum_ruku, COALESCE(sw.isum_fapiao, '0') isum_fapiao from stock_xy_csource xy left join stock_warehousings sw on xy.ccode = sw.ccode where xy.ccode=:ccode ")
    Flux<StockWarehousings2Vo> findByStockWareSMax(String ccode);

    @Query("delete from stock_xy_csource where ccode=:ccode and xy_bill_style in ('HZHCD','LZHCD')")
    Mono<Void> delXyHCD(String ccode);

    @Query("delete from stock_xy_csource where ccode=:ccode and xy_bill_style=:xyBillStyle ")
    Mono<Void> delCcodeAndXyBillStyle(String ccode,String xyBillStyle);
}



