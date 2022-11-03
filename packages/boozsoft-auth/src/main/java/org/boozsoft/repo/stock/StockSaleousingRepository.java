package org.boozsoft.repo.stock;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.stock.StockSaleousing;
import org.boozsoft.domain.vo.stock.StockSaleousingVo;
import org.boozsoft.domain.vo.stock.StockSaleousingsVo;
import org.boozsoft.domain.vo.stock.StockWarehousingVo;
import org.boozsoft.domain.vo.stock.StockXySourceVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockSaleousingRepository extends ReactiveCrudRepository<StockSaleousing, String> {

    Mono<StockSaleousing> findFirstByBillStyleAndDdateLikeOrderByDdateDescCcodeDesc(String type, String s);

    Flux<StockSaleousing> findAllByBillStyleAndIyearOrderByDdateAscCcodeAsc(String type, String iyear);
    Flux<StockSaleousing> findAllByBillStyleAndIyearAndBdocumStyleAndCcodeLikeOrderByDdateAscCcodeAsc(String type, String iyear,String style,String ccode);

    Mono<StockSaleousing> findFirstByBillStyleAndIyearOrderByDdateDesc(String type, String iyear);
    Flux<StockSaleousing> findAllByBillStyleAndDdateBetweenOrderByDdateAscCcodeAsc(String type, String start,String end);

    Flux<StockSaleousing> findAllByIyearAndDdateLessThanAndBillStyleInOrderByDdateAscCcodeAsc(String year, String start,List<String> types);

    Flux<StockSaleousing> findAllByBillStyleAndBcheckAndDdateBetweenOrderByDdateAscCcodeAsc(String type, String chekc,String start,String end);

    Mono<StockSaleousing> findByIyearAndDdateAndCcode(String iyear,String date,String code);
    Mono<StockSaleousing> findByIyearAndCcode(String iyear,String code);
    Mono<StockSaleousing> findByCcode(String code);
    Flux<StockSaleousing> findByCcodeIn(List<String> codes);

    @Query("delete from stock_saleousing where ccode=:ccode and bill_style=:billStyle ")
    Mono<Void> deleteByCcodeAndBillType(String ccode, String billStyle);

    @Query("select * from stock_saleousing where iyear=:iyear and bcheck = '1' and (bcloser !='1' or bcloser is null) " +
            "and (bill_style='XHD' or bill_style='YSD') order by ccode ")
    Flux<StockSaleousing> findByXhd(String iyear);

    @Query("select * from stock_saleousing where iyear=:iyear and bcheck = '1' " +
            "and (bcloser !='1' or bcloser is null) and (hx_flag !='1' or hx_flag is null) " +
            "and (bill_style='XHD' or bill_style='YSD' or bill_style='XSFP') " +
            "and cvencode_js=:cvencode order by ccode ")
    Flux<StockSaleousing> findBySkWhxXhd(@Param("iyear") String iyear, @Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where iyear=:iyear and bcheck = '1' " +
            "and (bcloser !='1' or bcloser is null) and (hx_flag !='1' or hx_flag is null) " +
            "and (bill_style='XHD' or bill_style='YSD' or bill_style='XSFP') " +
            "and ddate<=:endDate order by ccode ")
    Flux<StockSaleousing> findWhxskdByEndDate(@Param("iyear") String iyear, @Param("endDate") String endDate);

    @Query("select st.*, sp1.psn_name as puname, sp3.psn_name as buname, sd.dept_name as dname, sc.ck_name as cname " +
            " from stock_saleousing st " +
            " left join sys_psn sp1 on st.cmaker = sp1.unique_code " +
            " left join sys_psn sp3 on st.bcheck_user = sp3.unique_code " +
            " left join sys_department sd on st.cdepcode = sd.unique_code " +
            " left join stock_cangku sc on st.cwhcode = sc.id "+
            " where st.ddate BETWEEN :strDate AND :endDate and iyear=:iyear and bill_style=:type ")
    Flux<StockWarehousingVo> findByPkList(String strDate, String endDate, String iyear, String type);

    @Query("select cus.cust_name,dept.dept_name,psn.psn_name as cperson_name,psn2.real_name as cmaker_name,sa.* from stock_saleousing sa\n" +
            "    left join sys_department dept on dept.unique_code=sa.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sa.cpersoncode\n" +
            "    left join _app_group_sys_user psn2 on psn2.id=sa.cmaker " +
            "   left join customer cus on cus.id = sa.cvencode " +
            "where sa.bill_style=:billStyle and sa.bcheck='1' and sa.iyear=:iyear ")
    Flux<StockSaleousingVo> findByDdateAndType(String billStyle,String iyear);

    @Query("select dept.dept_name,psn.psn_name,psn2.real_name as cmaker_name,sa.* from stock_saleousing sa\n" +
            "    left join sys_department dept on dept.unique_code=sa.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sa.cpersoncode\n" +
            "    left join _app_group_sys_user psn2 on psn2.id=sa.cmaker\n" +
            "where sa.ddate between :strDate and :endDate and sa.bill_style=:billStyle and sa.bcheck='1' and sa.cwhcode=:cwhcode ")
    Flux<StockSaleousingVo> findByDdateAndCk(String strDate, String endDate, String billStyle, String cwhcode);
    @Query("select dept.dept_name,psn.psn_name,psn2.real_name as cmaker_name,sa.* from stock_saleousing sa\n" +
            "    left join sys_department dept on dept.unique_code=sa.cdepcode\n" +
            "    left join sys_psn psn on psn.id=sa.cpersoncode\n" +
            "    left join _app_group_sys_user psn2 on psn2.id=sa.cmaker\n" +
            "where sa.ddate between :strDate and :endDate and sa.bill_style=:billStyle and sa.bcheck='1' ")
    Flux<StockSaleousingVo> findByDdateAndCkAll(String strDate, String endDate, String billStyle);

    @Query("update stock_saleousing set delivery_date=:deliveryDate,delivery_id=:deliveryId,delivery_explain=:deliveryExplain where ccode in (:ccode) ")
    Mono<Void> editStockSaleousingDelivery(String deliveryDate,String deliveryId,String deliveryExplain,List<String> ccode);




    @Query("select dept.dept_name as dname,psn.psn_name as buname,psn2.real_name as cmaker_name,sw.*\n" +
            "from stock_saleousing sw\n" +
            " left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            " left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
            " left join stock_saleousings sws on sws.ccode=sw.ccode\n" +
            "where sw.bill_style in (:list)\n" +
            "and sw.bcheck = '1' and sw.cvencode=:cvencode and sw.iyear=:iyear \n" +
            "order by sw.bill_style desc, sw.ccode asc")
    Flux<StockSaleousingVo> findAllXHD_And_QCXHD(@Param("iyear") String iyear,@Param("cvencode") String cvencode,@Param("list") List<String> list);



    @Query("select dept.dept_name as dname,psn.psn_name as buname,psn2.real_name as cmaker_name,sw.*\n" +
            "from stock_saleousing sw\n" +
            " left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            " left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
            "where 1=1 and sw.bcheck = '1' and sw.cvencode=:cvencode and sw.bill_style in (:list)\n" +
            "and  (sw.ddate is not null and sw.ddate >= :start and sw.ddate <= :end ) \n" +
            "order by sw.bill_style desc, sw.ccode asc")
    Flux<StockSaleousingVo> findAllSalesRefer(@Param("start") String start,@Param("end") String end,@Param("cvencode") String cvencode,@Param("list") List<String> list);


    @Query("select distinct dept.dept_name as dname,psn.psn_name as buname,psn2.real_name as cmaker_name,sw.*\n" +
            "from stock_saleousing sw\n" +
            " left join sys_department dept on dept.unique_code=sw.cdepcode\n" +
            " left join sys_psn psn on psn.id=sw.cpersoncode\n" +
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker\n" +
            " left join stock_saleousings sws on sws.ccode=sw.ccode\n" +
            "where sw.bill_style in (:list) and sw.bcheck = '1' and sw.bdocum_style = '0' and (sw.bcloser is null or sw.bcloser <> '1') and (sw.queren_status is null or sw.queren_status <> '1') \n" +
            "and (sw.ddate between :start and :end) \n" +
            "order by sw.bill_style desc, sw.ccode asc")
    Flux<StockSaleousingVo> findAllXSCKD(@Param("start") String start,@Param("end") String end,@Param("list") List<String> list);
    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(ccode,:num) as int)+1) as ccode from stock_saleousing t1 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and not exists(select * from stock_saleousing t2 " +
            "where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle and cast(right(t2.ccode,:num) as int) = cast(right(t1.ccode,:num) as int) + 1) ")
    Mono<StockSaleousing> findBukongCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);
    /**
     * 获取最大的编码
     */
    @Query("select Max(right(ccode,:num)) as ccode from stock_saleousing where length(ccode)=:sum and left(ccode,:qzNum)=:qianzhui and bill_style=:billStyle ")
    Mono<StockSaleousing> findMaxCcode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui, @Param("billStyle") String billStyle);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockSaleousing> findYsdXhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and bcheck='1' and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockSaleousing> findYshYsdXhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode,cvencode_js")
    Flux<StockSaleousing> findWshYsdXhdtjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockSaleousing> findYsdXsfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and bcheck='1' and (ddate between :ddate1 and :ddate2) group by cvencode,cvencode_js")
    Flux<StockSaleousing> findYshYsdXsfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode,cvencode_js, " +
            "sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) " +
            "group by cvencode,cvencode_js")
    Flux<StockSaleousing> findWshYsdXsfptjbList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYsdXhdByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYshYsdXhdByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findWshYsdXhdByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYsdXsfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYshYsdXsfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and (ddate between :ddate1 and :ddate2) and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findWshYsdXsfpByDateList(@Param("iyear") String iyear,@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYsdXhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYshYsdXhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and bus_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findWshYsdXhdByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where iyear=:iyear and ddate <= :endDate and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYsdXsfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and bcheck='1' and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where bcheck='1' and iyear=:iyear and ddate <= :endDate and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findYshYsdXsfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum from " +
            "(select cvencode_js,sum(cast(COALESCE(isum,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from stock_saleousing where (bill_style='YSD' or bill_style='XSFP') and (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate group by cvencode_js " +
            "union all " +
            "select cvencode_js,sum(cast(COALESCE(isum_benbi,'0') as decimal)) as isum,sum(cast(COALESCE(hx_isum,'0') as decimal)) as hx_isum " +
            "from ar_begin_balance where (bcheck !='1' or bcheck is null) and iyear=:iyear and ddate <= :endDate and ar_style='YSD' group by cvencode_js) as tmp group by cvencode_js")
    Flux<StockSaleousing> findWshYsdXsfpByEndDateList(@Param("iyear") String iyear,@Param("endDate") String endDate);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD' or bill_style='XSFP') and (ddate between :ddate1 and :ddate2) order by ddate,ccode ")
    Flux<StockSaleousing> findYsdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD' or bill_style='XSFP') and bcheck='1' and (ddate between :ddate1 and :ddate2) order by ddate,ccode ")
    Flux<StockSaleousing> findYshYsdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD' or bill_style='XSFP') and (bcheck !='1' or bcheck is null) and (ddate between :ddate1 and :ddate2) order by ddate,ccode ")
    Flux<StockSaleousing> findWshYsdmxzList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

    Flux<StockSaleousing> findAllByBillStyleAndDdateLikeOrderByBcheckAscDdateAscCcodeAsc(String cgrkd, String s);

    @Query("update stock_saleousing set cost_status = '1' where ccode in (:list) ")
    Mono<Void> updateCostStatusByCcodes(List list);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findYsdXhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and bcheck='1' " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findYshYsdXhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (bcheck !='1' or bcheck is null) " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findWshYsdXhdmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findYsdXsfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and bcheck='1' " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findYshYsdXsfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select * from stock_saleousing where (bill_style='YSD' or bill_style='XHD') and (bcheck !='1' or bcheck is null) " +
            "and (ddate between :ddate1 and :ddate2) and cvencode_js=:cvencode order by ddate,ccode ")
    Flux<StockSaleousing> findWshYsdXsfpmxzByDateAndCvencodeList(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2,@Param("cvencode") String cvencode);

    @Query("select psn3.real_name as buname,psn2.real_name as cmaker_name,sup.cust_name,sd.dept_name,ck.ck_name,sp.psn_name, sw.* " +
            "from stock_saleousing sw " +
            " left join supplier sup on sup.id=sw.cvencode " +
            " left join stock_cangku ck on ck.id=sw.cwhcode "+
            " left JOIN sys_psn sp on sp.id = sw.cpersoncode " +
            " left join _app_group_sys_user psn2 on psn2.id=sw.cmaker " +
            " left join _app_group_sys_user psn3 on psn3.id=sw.bcheck_user " +
            " left JOIN sys_department sd on sd.unique_code = sw.cdepcode " +
            "where sw.bill_style=:type and sw.ddate between :strDate and :endDate " +
            "order by sw.ccode asc")
    Flux<StockSaleousingVo> findAllByTypeList(String type, String strDate, String endDate);

    Flux<StockSaleousing> findByCcodePlIn(List<String> codes);

    Flux<StockSaleousing> findAllByDdateBetweenAndBillStyleAndBcheckOrderByCcodeAsc(String strDate, String endDate, String billStyle, String s);

    Flux<StockSaleousing> findAllByDdateBetweenAndBillStyleAndCwhcodeAndBcheckOrderByCcodeAsc(String strDate, String endDate, String billStyle, String cwhcode, String s);

    @Query("select * from stock_saleousing where bill_style='XHD' and bcheck='1' and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockSaleousing> findYshXhdByIyear(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);
    @Query("select * from stock_saleousing where bill_style='XSFP' and (ddate between :ddate1 and :ddate2) order by ccode ")
    Flux<StockSaleousing> findXsfpByIyear(@Param("ddate1") String ddate1,@Param("ddate2") String ddate2);

}



