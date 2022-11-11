package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.StockWarehousing;
import org.boozsoft.domain.vo.stock.StockWarehousingVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockWarehousingRepository extends ReactiveCrudRepository<StockWarehousing, String> {

    Flux<StockWarehousing> findAllByCcodeAndBillStyle(String ccode,String billStyle);
    @Query("select sup.cust_name,sup.cust_code,sw.*\n" +
            "from stock_warehousing sw\n" +
            "left join supplier sup on sup.id=sw.cvencode\n" +
            "where sw.bill_style =:type \n" +
            "  and sw.iyear =:Iyaer \n" +
            "order by sw.bcheck, sw.ddate, sw.ccode asc")
    Flux<StockWarehousingVo> findAllByBillStyleAndIyearOrderByBcheckAscDdateAscCcodeAsc(String type, String Iyaer);

    @Query("select * from stock_warehousing where bill_style=:type and iyear=:iyear order by ccode,ddate asc")
    Flux<StockWarehousing> findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(String type, String iyear);
    Flux<StockWarehousing> findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(String type, String like);

    @Query("delete from stock_xy_csource where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> delXyCsourceByCcodeAndBillType(String ccode,String billStyle);

    @Query("delete from stock_xy_csource where xyccode=:xyccode and xy_bill_style=:xybillStyle and bill_style=:billStyle and ccode=:ccode ")
    Mono<Void> delXyCsourceByxyCcodeAndxyBillTypeAndBillTypeAndCcode(String xyccode,String xybillStyle,String billStyle,String ccode);

    @Query("delete from stock_xy_csource where xyccode=:xyccode  ")
    Mono<Void> delXyCsourceByxyCcode(String xyccode);

    @Query("delete from stock_warehousing where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndBillType(String ccode, String billStyle);

    @Query("delete from stock_xy_csource where xy_bill_style=:xybillStyle and bill_style=:billStyle and ccode=:ccode ")
    Mono<Void> deleteByXyStyleAndBillStyleAndccode(String xybillStyle,String billStyle,String ccode);

    @Query("select icost from stock_warehousing where bill_style='CGDHD' and cvencode=:supUnique  order by ddate desc limit 1 ")
    Mono<Long> findByStockWareRecentlySupMoney(String supUnique);

    @Query("select * from stock_warehousing where bill_style=:type and iyear=:iyear  order by ddate desc ")
    Flux<StockWarehousing> findAllTypeAndIyear(String type, String iyear);

    Mono<StockWarehousing> findByCcode(String ccode);

    @Query("select sw.*,\n" +
            "       (select sum(cast(coalesce(sws.isum_ruku, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_ruku,\n" +
            "       (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_fapiao,\n" +
            "       (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_tuihuo," +
            "(select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
            "                from stock_warehousings sws\n" +
            "                where sws.ccode = sw.ccode) sws_isum_daohuo," +
            "(select sum(cast(coalesce(sws.isum_jiesuan, '0') as float))\n" +
            "                from stock_warehousings sws\n" +
            "                where sws.ccode = sw.ccode ) sws_isum_jiesuan\n" +
            "from stock_warehousing sw where sw.ccode=:ccode and sw.bill_style not in ('HZHCD', 'LZHCD')")
    Mono<StockWarehousingVo> findByCcodeData(String ccode);

    @Query("select sw.*,\n" +
            "       (select sum(cast(coalesce(sws.isum_ruku, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_ruku,\n" +
            "       (select sum(cast(coalesce(sws.isum_fapiao, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_fapiao,\n" +
            "       (select sum(cast(coalesce(sws.isum_tui_huo, '0') as float))\n" +
            "        from stock_warehousings sws\n" +
            "        where sws.ccode = sw.ccode) as sws_isum_tuihuo," +
            "(select sum(cast(coalesce(sws.isum_daohuo, '0') as float))\n" +
            "                from stock_warehousings sws\n" +
            "                where sws.ccode = sw.ccode) sws_isum_daohuo," +
            "(select sum(cast(coalesce(sws.isum_jiesuan, '0') as float))\n" +
            "                from stock_warehousings sws\n" +
            "                where sws.ccode = sw.ccode) sws_isum_jiesuan\n" +
            "from stock_warehousing sw where sw.ccode=:ccode and sw.bill_style=:billStyle")
    Mono<StockWarehousingVo> findByCcodeAdnBillStyleData(String ccode,String billStyle);

    @Query("select st.*, sp1.psn_name as puname, sp3.psn_name as buname, sd.dept_name as dname, sc.ck_name as cname " +
            " from stock_warehousing st " +
            " left join sys_psn sp1 on st.cmaker = sp1.unique_code " +
            " left join sys_psn sp3 on st.bcheck_user = sp3.unique_code " +
            " left join sys_department sd on st.cdepcode = sd.unique_code " +
            " left join stock_cangku sc on st.cwhcode = sc.id "+
            " where st.ddate BETWEEN :strDate AND :endDate and iyear=:iyear and bill_style=:type ")
    Flux<StockWarehousingVo> findByPyList(String strDate, String endDate, String iyear, String type);

    @Query("select count(sw.id) from stock_xy_csource xy left join stock_warehousing sw on xy.xyccode=sw.ccode and sw.bdocum_style='1' where xy.ccode=:syCode and xy.bill_style=:billStyle and xy.xy_bill_style=:billStyle ")
    Mono<Long> findByCodeSetXyCodeBdocumStyle(String syCode,String billStyle);


    @Query("select * from stock_warehousing where bill_style=:billStyle and iyear=:iyear order by ccode desc ")
    Flux<StockWarehousing> findByCodeMax(String billStyle,String iyear);

    @Query("update stock_warehousing set bcheck=:bcheck,bcheck_time=:bcheckTime,bcheck_user=:bcheckUser where ccode in (:list) ")
    Mono<Void> editBcheck(String bcheck, String bcheckTime, String bcheckUser, List<String> list);

    @Query("delete from stock_warehousing where ccode in (:ccode) ")
    Mono<Void> delAllList(List<String> list);

    @Query("select (case\n" +
            "            when sw.unit_type = 'etc' then sw.unit_value\n" +
            "            when sw.unit_type = 'cust' then (select cus.cust_name from customer cus where cus.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'supplier' then (select sup.cust_name from supplier sup where sup.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'user' then (select psn.psn_name from sys_psn psn where psn.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'project' then (select pro.project_name from project pro where pro.id = sw.unit_value)\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_value_name,\n" +
            "       (case\n" +
            "            when sw.unit_type = 'etc' then '其他'\n" +
            "            when sw.unit_type = 'cust' then '客户'\n" +
            "            when sw.unit_type = 'supplier' then '供应商'\n" +
            "            when sw.unit_type = 'user' then '员工'\n" +
            "            when sw.unit_type = 'project' then '项目'\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_trans_name,\n" +
            "\n" +
            "       (select psn_name from sys_psn where id = sw.cwhcode_user)                                       as cwhcode_user_name,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.bcheck_user)                           as buname,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.cmaker)                                as cmaker_name,\n" +
            "       (select psn_name from sys_psn where id = sw.cpersoncode)                                        as person_name,\n" +
            "       sup.cust_name,\n" +
            "       sup.cust_code,\n" +
            "       (select dept_name from sys_department where unique_code = sw.cdepcode)                          as dept_name,\n" +
            "       sup2.cust_name                                                                                  as jscust_name,\n" +
            "       sup2.cust_code                                                                                  as jscust_code,\n" +
            "       sw.*,\n" +
            "       (select sum(cast(sws.cnumber as float)) from stock_warehousings sws where sws.ccode = sw.ccode) as cnumber,\n" +
            "       (select psn_name from sys_psn where id = sw.bworkable_user)                                     as bworkable_user_name\n" +
            "from stock_warehousing sw\n" +
            "         left join supplier sup on sup.id = sw.cvencode\n" +
            "         left join supplier sup2 on sup2.id = sw.cvencode_js\n" +
            "\n" +
            "where sw.bill_style =:type \n" +
            "order by sw.ccode asc")
    Flux<StockWarehousingVo> findMainList(String type);
    @Query("select (case\n" +
            "            when sw.unit_type = 'etc' then sw.unit_value\n" +
            "            when sw.unit_type = 'cust' then (select cus.cust_name from customer cus where cus.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'supplier' then (select sup.cust_name from supplier sup where sup.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'user' then (select psn.psn_name from sys_psn psn where psn.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'project' then (select pro.project_name from project pro where pro.id = sw.unit_value)\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_value_name,\n" +
            "       (case\n" +
            "            when sw.unit_type = 'etc' then '其他'\n" +
            "            when sw.unit_type = 'cust' then '客户'\n" +
            "            when sw.unit_type = 'supplier' then '供应商'\n" +
            "            when sw.unit_type = 'user' then '员工'\n" +
            "            when sw.unit_type = 'project' then '项目'\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_trans_name,\n" +
            "\n" +
            "       (select psn_name from sys_psn where id = sw.cwhcode_user)                                       as cwhcode_user_name,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.bcheck_user)                           as buname,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.cmaker)                                as cmaker_name,\n" +
            "       (select psn_name from sys_psn where id = sw.cpersoncode)                                        as person_name,\n" +
            "       sup.cust_name,\n" +
            "       sup.cust_code,\n" +
            "       (select dept_name from sys_department where unique_code = sw.cdepcode)                          as dept_name,\n" +
            "       sup2.cust_name                                                                                  as jscust_name,\n" +
            "       sup2.cust_code                                                                                  as jscust_code,\n" +
            "       sw.*,\n" +
            "       (select sum(cast(sws.cnumber as float)) from stock_warehousings sws where sws.ccode = sw.ccode) as cnumber,\n" +
            "       (select sum(cast(sws.itaxprice as float)) from stock_warehousings sws where sws.ccode = sw.ccode) as itaxprice,\n" +
            "       (select psn_name from sys_psn where id = sw.bworkable_user)                                     as bworkable_user_name," +
            "sw.rate,sw.invoice_style,sw.method_pay,sw.pay_date,coalesce(sw.the_deposit, '0') as the_deposit \n" +
            "from stock_warehousing sw\n" +
            "         left join supplier sup on sup.id = sw.cvencode\n" +
            "         left join supplier sup2 on sup2.id = sw.cvencode_js\n" +
            "\n" +
            "where sw.bill_style =:type and sw.ddate between :startDate and :endDate \n" +
            "order by sw.ccode asc")
    Flux<StockWarehousingVo> findMainList2(String type,String startDate,String endDate);

    @Query("select (case\n" +
            "            when sw.unit_type = 'etc' then sw.unit_value\n" +
            "            when sw.unit_type = 'cust' then (select cus.cust_name from customer cus where cus.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'supplier' then (select sup.cust_name from supplier sup where sup.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'user' then (select psn.psn_name from sys_psn psn where psn.id = sw.unit_value)\n" +
            "            when sw.unit_type = 'project' then (select pro.project_name from project pro where pro.id = sw.unit_value)\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_value_name,\n" +
            "       (case\n" +
            "            when sw.unit_type = 'etc' then '其他'\n" +
            "            when sw.unit_type = 'cust' then '客户'\n" +
            "            when sw.unit_type = 'supplier' then '供应商'\n" +
            "            when sw.unit_type = 'user' then '员工'\n" +
            "            when sw.unit_type = 'project' then '项目'\n" +
            "            else '' end\n" +
            "           )                                                                                           as unit_trans_name,\n" +
            "\n" +
            "       (select psn_name from sys_psn where id = sw.cwhcode_user)                                       as cwhcode_user_name,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.bcheck_user)                           as buname,\n" +
            "       (select real_name from _app_group_sys_user where id = sw.cmaker)                                as cmaker_name,\n" +
            "       (select psn_name from sys_psn where id = sw.cpersoncode)                                        as person_name,\n" +
            "       sup.cust_name,\n" +
            "       sup.cust_code,\n" +
            "       (select dept_name from sys_department where unique_code = sw.cdepcode)                          as dept_name,\n" +
            "       sup2.cust_name                                                                                  as jscust_name,\n" +
            "       sup2.cust_code                                                                                  as jscust_code,\n" +
            "       sw.*,\n" +
            "       (select sum(cast(sws.cnumber as float)) from stock_warehousings sws where sws.ccode = sw.ccode) as cnumber,\n" +
            "       (select psn_name from sys_psn where id = sw.bworkable_user)                                     as bworkable_user_name\n" +
            "from stock_warehousing sw\n" +
            "         left join supplier sup on sup.id = sw.cvencode\n" +
            "         left join supplier sup2 on sup2.id = sw.cvencode_js\n" +
            "\n" +
            "where sw.bill_style =:type \n" +
            "  and sw.cmaker_time between :strDate and :endDate\n" +
            "order by sw.ccode asc")
    Flux<StockWarehousingVo> findAllQCZGRKD(String type,String strDate,String endDate);
    @Query("select count(id) from stock_period where stock_year=:iyear and stock_month=:month ")
    Mono<Long> findByStockPeriodIsClose(String iyear,String month);

    @Query("update stock_warehousing set cost_status = '1' where ccode in (:list) ")
    Mono<Void> updateCostStatusByCcodes(List list);

    @Query("select * from stock_warehousing where iyear=:iyear and bcheck = '1' and (bcloser !='1' or bcloser is null) " +
            "and (bill_style='CGDHD' or bill_style='YFD') order by ccode ")
    Flux<StockWarehousing> findByCgdhd(String iyear);

    @Query("select * from stock_warehousing where iyear=:iyear and bcheck = '1' " +
            "and (bcloser !='1' or bcloser is null) and (hx_flag !='1' or hx_flag is null) " +
            "and (bill_style='CGDHD' or bill_style='YFD') " +
            "and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findByFkWhxCgdhd(@Param("iyear") String iyear, @Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where iyear=:iyear and bcheck = '1' " +
            "and (bcloser !='1' or bcloser is null) and (hx_flag !='1' or hx_flag is null) " +
            "and (bill_style='CGDHD' or bill_style='YFD' or bill_style='CGFP') " +
            "and ddate<=:endDate order by ccode ")
    Flux<StockWarehousing> findFkWhxCgdhdByEndDate(@Param("iyear") String iyear, @Param("endDate") String endDate);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from stock_warehousing t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and not exists(select * from stock_warehousing t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<StockWarehousing> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from stock_warehousing where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle ")
    Mono<StockWarehousing> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockWarehousing> findYfdCgdhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and bcheck='1' and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockWarehousing> findYshYfdCgdhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode,cvencode_js")
    Flux<StockWarehousing> findWshYfdCgdhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockWarehousing> findYfdCgfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and bcheck='1' and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockWarehousing> findYshYfdCgfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode,cvencode_js")
    Flux<StockWarehousing> findWshYfdCgfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD' or bill_style='CGFP') and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockWarehousing> findYfdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD' or bill_style='CGFP') and bcheck='1' and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockWarehousing> findYshYfdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD' or bill_style='CGFP') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockWarehousing> findWshYfdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findYfdCgdhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') " +
            "and bcheck='1' and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findYshYfdCgdhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and (bcheck !='1' or bcheck is null) " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findWshYfdCgdhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findYfdCgfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') " +
            "and bcheck='1' and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findYshYfdCgfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and (bcheck !='1' or bcheck is null) " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ccode ")
    Flux<StockWarehousing> findWshYfdCgfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select psn3.real_name as buname,psn2.real_name as cmaker_name,sup.cust_name,sw.* " +
            " from stock_warehousing sw " +
            " left join supplier sup on sup.id=sw.cvencode " +
            " left join stock_cangku ck on ck.id=sw.cwhcode "+
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker " +
            " left join _app_group_sys_user psn3 on psn3.id=sw.bcheck_user " +
            "where sw.bill_style=:type and sw.ddate between :strDate and :endDate " +
            " order by sw.ccode asc")
    Flux<StockWarehousingVo> findAllByTypeList(String type, String strDate, String endDate);

    @Query("select * from stock_warehousing where bill_style='CGDHD' and bcheck='1' and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockWarehousing> findYshCgdhdByIyear(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);
    @Query("select * from stock_warehousing where bill_style='CGFP' and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockWarehousing> findCgfpByIyear(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYfdCgdhdByDateList(@Param("iyear") String iyear, @Param("ddate1") String ddate1, @Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYshYfdCgdhdByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findWshYfdCgdhdByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYfdCgfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYshYfdCgfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findWshYfdCgfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYfdCgdhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYshYfdCgdhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGDHD') and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and bus_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findWshYfdCgdhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYfdCgfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findYshYfdCgfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_warehousing where (bill_style='YFD' or bill_style='CGFP') and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and ar_style='YFD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockWarehousing> findWshYfdCgfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);


    @Query("select * from stock_warehousing where bill_style=:billStyle and split_part(ccode,'-',3)  like :ccode")
    Mono<StockWarehousing> findBySearch(String billStyle,String ccode);

    Flux<StockWarehousing> findAllByBillStyleAndIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc(String type, String year, String style, String s);

    Mono<Void> deleteBySourcetype(String sourcetype);
}



