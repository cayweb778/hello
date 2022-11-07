package org.boozsoft.repo;

import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.ro.WriteOffRo;
import org.boozsoft.domain.vo.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.boozsoft.rest.initialBalance.subject.SubjectInitialBalanceContorller.VIEW_CODE_KEMU;

public interface AccvoucherRepository extends ReactiveCrudRepository<Accvoucher, String> {

    @Query("select max(ino_id) ino_id from accvoucher")
    Mono<Accvoucher> queryMaxInoid();

    @Query("select dbill_date from accvoucher order by dbill_date desc limit 1 offset 0")
    Mono<Accvoucher> queryMaxPingZhengDate();

    @Query("select ino_id from accvoucher where  iyperiod=:iyperiod order by cast(ino_id as integer ) desc limit 1 offset 0")
    Mono<Accvoucher> queryMaxPingZhengInoId(String iyperiod);

    @Query("select iyear,imonth,max(ino_id) ino_id from accvoucher where 1=1 and imonth between '01' and '13' group by iyear, imonth order by iyear,imonth")
    Flux<Accvoucher> queryAllYaerAndMonthMaxInoid();

    @Query("SELECT DISTINCT ac.dbill_date,\n" + "                ac.unique_code,\n" + "                ac.csign,\n" + "                cast(ac.ino_id as integer)\n" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And ac.dbill_date like :yearMonth \n" + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherTreeByDbillDateLike(@Param("yearMonth") String yearMonth);

    /*
    *             "                (SELECT cdigest FROM accvoucher acc WHERE 1=1 and acc.iyperiod between :startDate And :endDate and acc.unique_code  = ac.unique_code LIMIT 1)  as cdigest,\n" +
                "                (SELECT SUM(CAST(md AS FLOAT)) FROM accvoucher acc WHERE 1=1 and acc.iyperiod between :startDate And :endDate and acc.unique_code = ac.unique_code) AS md,\n" +
    * */
    @Query("SELECT DISTINCT ac.dbill_date,\n" + "                ac.unique_code,\n" + "                ac.csign,\n" + "                cast(ac.ino_id as integer),\n" + "                ac.idoc,\n" + "                ac.cbill,\n" + "                ac.ccashier,\n" + "                ac.ccheck,\n" + "                ac.cdirector,\n" + "                ac.ifrag,\n" + "                ac.cbook\n" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And iyperiod between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherPoolByIyperiod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT distinct SUM(CAST(md AS FLOAT)) as md,unique_code FROM accvoucher  WHERE 1=1 and imonth between '01' and '13' And iyperiod between :startDate And :endDate  GROUP BY unique_code")
    Flux<Accvoucher> findAllVoucherPoolMdByIyperiod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT distinct cdigest,unique_code FROM accvoucher  WHERE 1=1 and imonth between '01' and '13' And iyperiod between :startDate And :endDate  GROUP BY cdigest,unique_code")
    Flux<Accvoucher> findAllVoucherPoolCdigestByIyperiod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT DISTINCT ac.dbill_date,\n" + "                ac.unique_code,\n" + "                ac.csign,\n" + "                cast(ac.ino_id as integer),\n" + "                ac.idoc,\n" + "                ac.cbill,\n" + "                ac.ccashier,\n" + "                ac.ccheck,\n" + "                ac.cdirector,\n" + "                ac.ifrag,\n" + "                ac.cbook\n" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And dbill_date between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherPoolFastByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT distinct SUM(CAST(md AS FLOAT)) as md,unique_code FROM accvoucher  WHERE 1=1 and imonth between '01' and '13' And dbill_date between :startDate And :endDate  GROUP BY unique_code")
    Flux<Accvoucher> findAllVoucherPoolMdByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT distinct cdigest,unique_code FROM accvoucher  WHERE 1=1 and imonth between '01' and '13' And dbill_date between :startDate And :endDate  GROUP BY cdigest,unique_code")
    Flux<Accvoucher> findAllVoucherPoolCdigestByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT DISTINCT ac.dbill_date,\n" + "                ac.unique_code,\n" + "                ac.csign,\n" + "                cast(ac.ino_id as integer),\n" + "                ac.idoc,\n" + "                (SELECT cdigest FROM accvoucher acc WHERE 1=1 and acc.iyperiod between :startDate And :endDate and acc.unique_code = ac.unique_code LIMIT 1)  as cdigest,\n" + "                (SELECT SUM(CAST(md AS FLOAT)) FROM accvoucher acc WHERE 1=1 and acc.iyperiod between :startDate And :endDate and acc.unique_code = ac.unique_code) AS md,\n" + "                ac.cbill,\n" + "                ac.ccashier,\n" + "                ac.ccheck,\n" + "                ac.cdirector,\n" + "                ac.ifrag,\n" + "                ac.cbook\n" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And dbill_date between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC, cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherPoolByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT  ac.*" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And iyperiod between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherDetailByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT ac.*" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13'" + "ORDER BY ac.dbill_date ASC,ac.ino_id ASC")
    Flux<Accvoucher> findAllVouchers();

    @Query("SELECT  ac.*" + "FROM accvoucher ac\n" + "WHERE 1=1 and ac.imonth between '01' and '13' And iyperiod between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC,cast(ac.inid as integer) ASC")
    Flux<Accvoucher> findAllVoucherDetailByIyperiod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("SELECT ac.*" + "FROM accvoucher ac left join code_kemu ck on ck.ccode = ac.ccode " + "WHERE 1=1 " + "and ck.bcash = '1' " + "and ac.imonth between '01' and '13' " + "And iyperiod between :startDate And :endDate " + "and ac.tenant_id = :accId " + "and ac.ifrag = '0' " + "ORDER BY ac.dbill_date ASC,cast(ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllVoucherDetailByIyperiodAndTenantId(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("accId") String accId);

    @Query("SELECT DISTINCT ac.*" + "FROM accvoucher ac " + "WHERE 1=1 and ac.unique_code is not null and ac.iyear=:year and ac.imonth between '01' and '13' And dbill_date between :startDate And :endDate \n" + "ORDER BY ac.ino_id ASC")
    Flux<Accvoucher> findAllPingZhengMingXing(@Param("year") String year, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取科目期初
     */
    @Query("SELECT " + " km.bdept,km.cclass, " + " km.bperson, " + " km.bcus, " + " km.bsup, " + " km.bitem, " + " km.bnum, " + " km.ccode, " + " km.ccode_name, " + " km.iyear, " + " km.bprogerty, " + " km.bend, " + " km.superior_ccode, " + " km.cdfine1, " + " km.cdfine1, " + " km.cdfine2, " + " km.cdfine3, " + " km.cdfine4, " + " km.cdfine5, " + " km.cdfine6, " + " km.cdfine7, " + " km.cdfine8, " + " km.cdfine9, " + " km.cdfine10, " + " km.cdfine11, " + " km.cdfine12, " + " km.cdfine13, " + " km.cdfine14, " + " km.cdfine15, " + " km.cdfine16, " + " km.cdfine17, " + " km.cdfine18, " + " km.cdfine19, " + " km.cdfine20, " + " km.cdfine21, " + " km.cdfine22, " + " km.cdfine23, " + " km.cdfine24, " + " km.cdfine25, " + " km.cdfine26, " + " km.cdfine27, " + " km.cdfine28, " + " km.cdfine29, " + " km.cdfine30, " + " var.iyperiod, " + " var.iperiod, " + " km.igrade, " + " km.flag, " + " COALESCE(var.md,'0') as md, " + " COALESCE(var.mc,'0') as mc, " + " var.\"id\" AS accvouid , " + " km.menterage, " + " km.currency_type, " + " case when var.nd_s !='' then var.nd_s else '0' end as nd_s, " + " case when var.nc_s !='' then var.nc_s else '0' end as nc_s, " + " case when var.nfrat_md !='' then var.nfrat_md else '0' end as nfrat_md, " + " case when var.nfrat_mc !='' then var.nfrat_mc else '0' end as nfrat_mc, " + " case when var.lj_md !='' then var.lj_md else '0' end as lj_md, " + " case when var.lj_mc !='' then var.lj_mc else '0' end as lj_mc, " + " case when var.lj_sl_md !='' then var.lj_sl_md else '0' end as lj_sl_md, " + " case when var.lj_sl_mc !='' then var.lj_sl_mc else '0' end as lj_sl_mc, " + " case when var.lj_wb_md !='' then var.lj_wb_md else '0' end as lj_wb_md, " + " case when var.lj_wb_mc !='' then var.lj_wb_mc else '0' end as lj_wb_mc " + " FROM " + " code_kemu km " + " LEFT JOIN accvoucher var ON var.ccode =km.ccode " + " AND km.iyear = var.iyear and km.tenant_id = var.tenant_id and var.iyperiod=:iyperiod " + " WHERE " + " km.iyear =:iyear " + " ORDER BY " + " km.ccode ")
    Flux<SubjectInitialBalanceVo> findAllSubjectInitialBalance(@Param("iyear") String iyear, @Param("iyperiod") String iyperiod);

    /**
     * 获取末级科目期初
     */
    @Query("SELECT " + " km.bdept,km.cclass, " + " km.bperson, " + " km.bcus, " + " km.bsup, " + " km.bitem, " + " km.bnum, " + " km.ccode, " + " km.ccode_name, " + " km.iyear, " + " km.bprogerty, " + " km.bend, " + " km.superior_ccode, " + " km.cdfine1, " + " km.cdfine2, " + " km.cdfine3, " + " km.cdfine4, " + " km.cdfine5, " + " km.cdfine6, " + " km.cdfine7, " + " km.cdfine8, " + " km.cdfine9, " + " km.cdfine10, " + " km.cdfine11, " + " km.cdfine12, " + " km.cdfine13, " + " km.cdfine14, " + " km.cdfine15, " + " km.cdfine16, " + " km.cdfine17, " + " km.cdfine18, " + " km.cdfine19, " + " km.cdfine20, " + " km.cdfine21, " + " km.cdfine22, " + " km.cdfine23, " + " km.cdfine24, " + " km.cdfine25, " + " km.cdfine26, " + " km.cdfine27, " + " km.cdfine28, " + " km.cdfine29, " + " km.cdfine30, " + " var.iyperiod, " + " var.iperiod, " + " km.igrade, " + " km.flag, " + " COALESCE(var.md,'0') as md, " + " COALESCE(var.mc,'0') as mc, " + " var.\"id\" AS accvouid , " + " km.menterage, " + " km.currency_type, " + " case when var.nd_s !='' then var.nd_s else '0' end as nd_s, " + " case when var.nc_s !='' then var.nc_s else '0' end as nc_s, " + " case when var.nfrat_md !='' then var.nfrat_md else '0' end as nfrat_md, " + " case when var.nfrat_mc !='' then var.nfrat_mc else '0' end as nfrat_mc, " + " case when var.lj_md !='' then var.lj_md else '0' end as lj_md, " + " case when var.lj_mc !='' then var.lj_mc else '0' end as lj_mc, " + " case when var.lj_sl_md !='' then var.lj_sl_md else '0' end as lj_sl_md, " + " case when var.lj_sl_mc !='' then var.lj_sl_mc else '0' end as lj_sl_mc, " + " case when var.lj_wb_md !='' then var.lj_wb_md else '0' end as lj_wb_md, " + " case when var.lj_wb_mc !='' then var.lj_wb_mc else '0' end as lj_wb_mc " + " FROM " + " code_kemu km " + " LEFT JOIN accvoucher var ON var.ccode =km.ccode " + " AND km.iyear = var.iyear and km.tenant_id = var.tenant_id and var.iyperiod=:iyperiod " + " WHERE " + " km.iyear =:iyear and km.bend=:lastCode " + " ORDER BY " + " km.ccode ")
    Flux<SubjectInitialBalanceVo> findAllSubjectInitialBalanceLastCode(@Param("iyear") String iyear, @Param("iyperiod") String iyperiod, @Param("lastCode") String lastCode);

    /**
     * 获取辅助科目期初
     */
    @Query("SELECT " + "km.bdept, " + "km.bperson, " + "km.bcus, " + "km.bsup, " + "km.bitem, " + "km.ccode, " + "km.ccode_name, " + "km.iyear, " + "km.bprogerty, " + "km.bend, " + "km.superior_ccode, " + "var.iyperiod, " + "var.iperiod, " + "km.igrade, " + "km.flag, " + "km.menterage, " + "km.currency_type, " + "var.cdept_id, " + "var.cperson_id, " + "var.ccus_id, " + "var.csup_id, " + "var.project_id, " + "sum(cast(COALESCE(var.md,'0') as decimal)) as md, " + "sum(cast(COALESCE(var.mc,'0') as decimal)) as mc, " + "sum(cast(case when var.nd_s !='' then var.nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when var.nc_s !='' then var.nc_s else '0' end as decimal)) as nc_s, " + "sum(cast(case when var.nfrat_md !='' then var.nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when var.nfrat_mc !='' then var.nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when var.lj_md !='' then var.lj_md else '0' end as decimal)) as lj_md, " + "sum(cast(case when var.lj_mc !='' then var.lj_mc else '0' end as decimal)) as lj_mc " + "FROM " + "code_kemu km " + "LEFT JOIN accvoucher var ON var.ccode =km.ccode " + "AND km.iyear = var.iyear " + "WHERE " + "km.iyear =:iyear and var.iyperiod=:iyperiod " + "group by " + "var.cdept_id, " + "var.cperson_id, " + "var.ccus_id, " + "var.csup_id, " + "var.project_id, " + "km.bdept, " + "km.bperson, " + "km.bcus, " + "km.bsup, " + "km.bitem, " + "km.ccode, " + "km.ccode_name, " + "km.iyear, " + "km.bprogerty, " + "km.bend, " + "km.superior_ccode, " + "var.iyperiod, " + "var.iperiod, " + "km.igrade, " + "km.flag, " + "km.menterage, " + "km.currency_type " + "ORDER BY " + "km.ccode ")
    Flux<SubjectInitialBalanceVo> findAllSubjectInitialFuZhuBalance(String iyear, String iyperiod);

    /**
     * 获取辅助科目期初
     */
    @Query("SELECT " + "km.bdept,km.cclass, " + "km.bperson, " + "km.bcus, " + "km.bsup, " + "km.bitem, " + "km.ccode, " + "km.ccode_name, " + "km.iyear, " + "km.bprogerty, " + "km.bend, " + "km.superior_ccode, " + "var.iyperiod, " + "var.iperiod, " + "km.igrade, " + "km.flag, " + "km.menterage, " + "km.currency_type, " +
            "       (select dept.dept_name from sys_department dept where dept.unique_code=var.cdept_id) as cdept_name,var.cdept_id,\n" +
            "       (select psn.psn_name from sys_psn psn where psn.unique_code=var.cperson_id) as cperson_name,var.cperson_id,\n" +
            "       (select cus.cust_name from customer cus where cus.unique_code=var.ccus_id) as ccus_name,var.ccus_id,\n" +
            "       (select sup.cust_name from customer sup where sup.unique_code=var.csup_id) as csup_name,var.csup_id,\n" +
            "       (select pro.project_name from project pro where pro.unique_code=var.project_id) as project_name,var.project_id, " + "cast(COALESCE(var.md,'0') as decimal) as md, " + "cast(COALESCE(var.mc,'0') as decimal) as mc, " + "cast(case when var.nd_s !='' then var.nd_s else '0' end as decimal) as nd_s, " + "cast(case when var.nc_s !='' then var.nc_s else '0' end as decimal) as nc_s, " + "cast(case when var.nfrat_md !='' then var.nfrat_md else '0' end as decimal) as nfrat_md, " + "cast(case when var.nfrat_mc !='' then var.nfrat_mc else '0' end as decimal) as nfrat_mc, " + "cast(case when var.lj_md !='' then var.lj_md else '0' end as decimal) as lj_md, " + "cast(case when var.lj_mc !='' then var.lj_mc else '0' end as decimal) as lj_mc," +
            "       var.cdfine1,var.cdfine2,var.cdfine3,var.cdfine4,var.cdfine5,var.cdfine6,var.cdfine7,var.cdfine8,var.cdfine9,var.cdfine10,\n" +
            "       var.cdfine11,var.cdfine12,var.cdfine13,var.cdfine14,var.cdfine15,var.cdfine16,var.cdfine17,var.cdfine18,var.cdfine19,var.cdfine20,\n" +
            "       var.cdfine21,var.cdfine22,var.cdfine23,var.cdfine24,var.cdfine25,var.cdfine26,var.cdfine27,var.cdfine28,var.cdfine29,var.cdfine30," +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine1) as cdfine1_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine2) as cdfine2_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine3) as cdfine3_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine4) as cdfine4_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine5) as cdfine5_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine6) as cdfine6_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine7) as cdfine7_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine8) as cdfine8_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine9) as cdfine9_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine10) as cdfine10_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine11) as cdfine11_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine12) as cdfine12_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine13) as cdfine13_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine14) as cdfine14_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine15) as cdfine15_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine16) as cdfine16_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine17) as cdfine17_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine18) as cdfine18_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine19) as cdfine19_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine20) as cdfine20_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine21) as cdfine21_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine22) as cdfine22_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine23) as cdfine23_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine24) as cdfine24_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine25) as cdfine25_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine26) as cdfine26_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine27) as cdfine27_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine28) as cdfine28_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine29) as cdfine29_name,\n" +
            "       (select fs.cname from fuzhu_hesuan fs where fs.id = var.cdfine30) as cdfine30_name  " +
            " " + "FROM " + "code_kemu km " + "LEFT JOIN accvoucher var ON var.ccode =km.ccode " + "AND km.iyear = var.iyear " + "WHERE " + "km.iyear =:iyear and var.iyperiod=:iyperiod " +
            " " + "group by " + "var.cdept_id, " + "var.cperson_id, " + "var.ccus_id, " + "var.csup_id, " + "var.project_id, " + "km.bdept, " + "km.bperson, " + "km.bcus, " + "km.bsup, " + "km.bitem, " + "km.ccode, " + "km.ccode_name, " + "km.iyear, " + "km.bprogerty, " + "km.bend, " + "km.superior_ccode, " + "var.iyperiod, " + "var.iperiod, " + "km.igrade, " + "km.flag, " + "km.menterage, " + "km.currency_type,km.cclass,var.md,var.mc,var.nd_s,var.nc_s,var.nfrat_md,var.nfrat_mc,var.lj_md,var.lj_mc," +
            "       var.cdfine1,var.cdfine2,var.cdfine3,var.cdfine4,var.cdfine5,var.cdfine6,var.cdfine7,var.cdfine8,var.cdfine9,var.cdfine10,\n" +
            "       var.cdfine11,var.cdfine12,var.cdfine13,var.cdfine14,var.cdfine15,var.cdfine16,var.cdfine17,var.cdfine18,var.cdfine19,var.cdfine20,\n" +
            "       var.cdfine21,var.cdfine22,var.cdfine23,var.cdfine24,var.cdfine25,var.cdfine26,var.cdfine27,var.cdfine28,var.cdfine29,var.cdfine30 " + "ORDER BY " + "km.ccode ")
    Flux<SubjectInitialBalanceVo> findAllSubjectInitialFuZhuBalance2(String iyear, String iyperiod);

    Flux<Accvoucher> findAllByIyperiodAndInoIdIn(String yearMonth, List<String> inods);

    @Query("select count(id) from accvoucher where iyear=:iyear and ibook=:ibook and tenant_id=:databasenum ")
    Mono<Long> countByIyearAndIbook(String iyear, String ibook, String databasenum);

    @Query("select count(id) from accvoucher where iyperiod=:iyear and ibook=:ibook and tenant_id=:databasenum ")
    Mono<Long> countByIperiodAndIbook(String iyear, String ibook, String databasenum);

    @Query("SELECT * from accvoucher where iyperiod =:iyperiod and tenant_id=:databasenum ORDER BY iyperiod")
    Flux<Accvoucher> findAllByIyperiodOrderByCcode(String yearMonth, String databasenum);

    @Query("SELECT km.ccode , acc.md, acc.mc, km.cclass,case when acc.lj_md !='' then acc.lj_md else '0' end as lj_md,case when acc.lj_mc !='' then acc.lj_mc else '0' end as lj_mc FROM code_kemu km JOIN accvoucher acc on acc.ccode=km.ccode and acc.iyperiod=:iyperiod WHERE km.iyear =:iyear ORDER BY km.ccode")
    Flux<SubjectInitialBalanceVo> findByAccStyleCcodeMDMC(String iyperiod, String iyear);

    @Query("SELECT km.ccode,\n" +
            "       km.ccode_name,\n" +
            "       km.cclass,case when km.bprogerty='1' then '借方' else '贷方' end as bprogerty,\n" +
            "       km.bnum,\n" +
            "       km.menterage,\n" +
            "       km.currency,\n" +
            "       km.currency_type,\n" +
            "        (select coalesce(sum(cast(acv.md as decimal)),'0') from accvoucher acv where acv.ccode=km.ccode and acv.iyperiod=:iyperiod) md,\n" +
            "        (select coalesce(sum(cast(acv.mc as decimal)),'0') from accvoucher acv where acv.ccode=km.ccode and acv.iyperiod=:iyperiod) mc," +
            "       (select case when km.bprogerty='1' then coalesce(sum(cast(acv.nd_s as decimal)),'0') else coalesce(sum(cast(acv.nc_s as decimal)),'0') end  from accvoucher acv where acv.ccode=km.ccode and acv.iyperiod=:iyperiod ) cnum,\n" +
            "       (select case when km.bprogerty='1' then coalesce(sum(cast(acv.nfrat_md as decimal)),'0') else coalesce(sum(cast(acv.nfrat_mc as decimal)),'0') end  from accvoucher acv where acv.ccode=km.ccode and acv.iyperiod=:iyperiod ) nfrat " +
            "FROM code_kemu km\n" +
            "WHERE km.iyear =:iyear order by km.ccode")
    Flux<SsphVo> findBySsph(@Param("iyperiod") String iyperiod,@Param("iyear") String iyear);

    Flux<Accvoucher> findAllByIyperiod(String iyperiod);

    @Query("SELECT * from accvoucher where 1 = 1 and iyear =:iyper and imonth <> '00' and (ino_id is not null or ino_id <> '') ORDER BY cast(iyperiod as Integer)  Asc,cast(ino_id as Integer) Asc,cast(inid as Integer) Asc")
    Flux<Accvoucher> findAllByIyearOrderByIyperiodAscInoIdAsc(String iyper);

    @Query("SELECT * from accvoucher where 1 = 1 and csign =:csign and iyperiod like :iyperiod and imonth <> '00' and (ino_id is not null or ino_id <> '') ORDER BY cast(iyperiod as Integer)  Asc,cast(ino_id as Integer) Asc,cast(inid as Integer) Asc")
    Flux<Accvoucher> findAllByCsignAndIyperiodLike(String csign,String iyperiod);

    Flux<Accvoucher> findAllByIyperiodAndCcodeOrderById(String iyperiod, String ccode);
    Flux<Accvoucher> findAllByIyperiodAndCcodeInOrderByIdAscCcodeAsc(String iyperiod, List<String> ccode);

    // 只查询凭证
    @Query("SELECT * from accvoucher where iyperiod !=:iyperiod and iyperiod !=:iyperiod2 and iyperiod !=:iyperiod3 and iyear=:iyear ORDER BY iyperiod")
    Flux<Accvoucher> findAllAccVoucher(String iyperiod, String iyperiod2, String iyperiod3, String iyear);

    //总账
    @Query("SELECT " + " acc.*, km.bend bend,  km.igrade igrade, km.bprogerty bprogerty " + "FROM" + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + "WHERE" + " acc.ccode LIKE :kmCode  " + " AND acc.iyperiod BETWEEN :strDate AND :endDate " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllByCcodeAndIyperiod(@Param("kmCode") String kmCode, @Param("strDate") String strDate, @Param("endDate") String endDate);

    //总账 科目范围 1001 - 1003
    @Query("SELECT " + " acc.*, km.bend bend,  km.igrade igrade, km.bprogerty bprogerty " + "FROM" + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + "WHERE" + " acc.ccode >= :minKm and acc.ccode <= :maxKm " + " AND acc.iyperiod BETWEEN :strDate AND :endDate " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllByCcodeRangeAndIyperiod(@Param("minKm") String minKm, @Param("maxKm") String maxKm, @Param("strDate") String strDate, @Param("endDate") String endDate);

    //科目多栏账 
    @Query("select acc.*, km.bend bend,  km.igrade igrade, km.bprogerty bprogerty " + " FROM " + "  accvoucher acc " + "  LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " WHERE " + "  acc.ccode in (:kmList)  " + "  AND acc.iyperiod BETWEEN :strDate AND :endDate " + "  ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllMulti(@Param("kmList") List kmList, @Param("strDate") String strDate, @Param("endDate") String endDate);

    /**
     * 获取末级科目期初
     */
    @Query("SELECT km.bdept,km.bperson,km.bcus,km.bsup,km.bitem,km.ccode,km.ccode_name,km.iyear," + "km.bprogerty,km.bend,km.superior_ccode,var.iyperiod,var.iperiod,km.igrade,km.FLAG," + "COALESCE ( var.md, '0' ) AS md,COALESCE ( var.mc, '0' ) AS mc,var.ID AS accvouid," + "km.menterage,km.currency_type,'0' AS nd_s,'0' AS nc_s,'0' AS nfrat_md,'0' AS nfrat_mc," + "'0' AS lj_md,'0' AS lj_mc,km.cdfine1,km.cdfine2,km.cdfine3,km.cdfine4,km.cdfine5,km.cdfine6,km.cdfine7" + ",km.cdfine8,km.cdfine9,km.cdfine10,km.cdfine11,km.cdfine12,km.cdfine13,km.cdfine14,km.cdfine15,km.cdfine16" + ",km.cdfine17,km.cdfine18,km.cdfine19,km.cdfine20,km.cdfine21,km.cdfine22,km.cdfine23,km.cdfine24,km.cdfine25" + ",km.cdfine26,km.cdfine27,km.cdfine28,km.cdfine29,km.cdfine30,km.fzCount " + " FROM (" + VIEW_CODE_KEMU + ") km LEFT JOIN accvoucher var ON var.ccode = km.ccode AND " + " var.tenant_id=:databasenum1 AND km.iyear = var.iyear and var.iyperiod =:iyperiod " + " WHERE km.bend = '1' " + " AND km.fzcount!='0' and km.iyear=:year AND km.tenant_id=:databasenum2 ORDER BY km.ccode")
    Flux<SubjectInitialBalanceVo> findAllSubjectInitialBalanceLastCodeFuZhu(@Param("year") String year, @Param("iyperiod") String iyperiod, @Param("databasenum1") String databasenum1, @Param("databasenum2") String databasenum2);

    @Query("select k.tenant_id,k.iyear,sum(cast(k.bperson as int)) as bperson,sum(cast(k.bdept as int)) as bdept,sum(cast(k.bcus as int)) as bcus," + "sum(cast(k.bsup as int)) as bsup,sum(cast(k.bitem as int)) as bitem,sum(cast(k.cdfine1 as int)) as cdfine1,sum(cast(k.cdfine2 as int)) as cdfine2," + "sum(cast(k.cdfine3 as int)) as cdfine3,sum(cast(k.cdfine4 as int)) as cdfine4,sum(cast(k.cdfine5 as int)) as cdfine5," + "sum(cast(k.cdfine6 as int)) as cdfine6,sum(cast(k.cdfine7 as int)) as cdfine7,sum(cast(k.cdfine8 as int)) as cdfine8," + "sum(cast(k.cdfine9 as int)) as cdfine9,sum(cast(k.cdfine10 as int)) as cdfine10,sum(cast(k.cdfine11 as int)) as cdfine11," + "sum(cast(k.cdfine12 as int)) as cdfine12,sum(cast(k.cdfine13 as int)) as cdfine13,sum(cast(k.cdfine14 as int)) as cdfine14," + "sum(cast(k.cdfine15 as int)) as cdfine15,sum(cast(k.cdfine16 as int)) as cdfine16,sum(cast(k.cdfine17 as int)) as cdfine17," + "sum(cast(k.cdfine18 as int)) as cdfine18,sum(cast(k.cdfine19 as int)) as cdfine19,sum(cast(k.cdfine20 as int)) as cdfine20," + "sum(cast(k.cdfine21 as int)) as cdfine21,sum(cast(k.cdfine22 as int)) as cdfine22,sum(cast(k.cdfine23 as int)) as cdfine23," + "sum(cast(k.cdfine24 as int)) as cdfine24,sum(cast(k.cdfine25 as int)) as cdfine25,sum(cast(k.cdfine26 as int)) as cdfine26," + "sum(cast(k.cdfine27 as int)) as cdfine27,sum(cast(k.cdfine28 as int)) as cdfine28,sum(cast(k.cdfine29 as int)) as cdfine29," + "sum(cast(k.cdfine30 as int)) as cdfine30 " + " from (" + VIEW_CODE_KEMU + ") k" + " where k.tenant_id=:databasenum and k.iyear=:iyear GROUP BY k.tenant_id,k.iyear")
    Mono<SubjectInitialBalanceVo> findAllSubjectInitialBalanceLastCodeFuZhu2(@Param("year") String iyear, @Param("databasenum") String databasenum);

    //科目部门总账
    @Query("SELECT " + " acc.*, km.bend bend, km.igrade igrade, km.bprogerty bprogerty, km.menterage menterage, km.currency_type currencyType, dt.dept_name dname, dt.dept_code dcode " + "FROM" + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " LEFT JOIN sys_department dt ON acc.cdept_id = dt.unique_code " + "WHERE" + " acc.ccode LIKE :kmCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod = '21') " + " and km.bdept = '1' " + " AND acc.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllByKmAndDept(@Param("kmCode") String kmCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);


    //部门总账
    @Query("SELECT " + " acc.*, km.bend bend,  km.igrade igrade, km.bprogerty bprogerty, km.menterage menterage, km.currency_type currencytype " + "FROM" + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + "WHERE" + " acc.ccode BETWEEN :minCode AND :maxCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod = '21') " + " AND acc.cdept_id = :deptCode " + " AND acc.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllByCcodeAndIyperiodAndDeptCode(@Param("deptCode") String deptCode, @Param("minCode") String minCode, @Param("maxCode") String maxCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);

    //部门总账-sheet
    @Query("SELECT " + " acc.*, km.bend bend,  km.igrade igrade, km.bprogerty bprogerty, km.menterage menterage, km.currency_type currencytype," + " dep.dept_code dcode , dep.dept_name dname " + "FROM" + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " LEFT JOIN sys_department dep ON dep.unique_code = acc.cdept_id " + "WHERE" + " acc.ccode BETWEEN :minCode AND :maxCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod = '21') " + " AND dep.dept_code BETWEEN :minDept AND :maxDept " + " AND acc.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllSheetData(@Param("minDept") String minDept, @Param("maxDept") String maxDept, @Param("minCode") String minCode, @Param("maxCode") String maxCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);

    //项目总账 
    @Query("SELECT " + " acc.*, km.bend bend, km.igrade igrade, km.bprogerty bprogerty, pro.project_code pcode, km.currency_type currencyType, pro.project_name pname " + "FROM " + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode and acc.iyear = km.iyear AND acc.tenant_id = km.tenant_id" + " LEFT JOIN project pro ON acc.project_id = pro.unique_code " + "WHERE" + " acc.project_class_id = :pcCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod =:iyear+'21') " + " AND acc.ccode BETWEEN :minCode AND :maxCode " + " AND acc.tenant_id = :accId " + " AND km.bitem = '1' " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllByKmAndProject(@Param("pcCode") String pcCode, @Param("minCode") String minCode, @Param("maxCode") String maxCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);

    //科目项目总账
    @Query("SELECT " + " acc.*, km.bend bend, km.igrade igrade, km.bprogerty bprogerty, pro.project_code pcode, km.currency_type currencyType, pro.project_name pname " + "FROM " + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " LEFT JOIN project pro ON acc.project_id = pro.unique_code " + "WHERE" + " acc.project_class_id = :pcCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod = '21') " + " and acc.ccode LIKE :kmCode  " + " AND km.bitem = '1' " + " AND acc.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllKmProject(@Param("pcCode") String pcCode, @Param("kmCode") String kmCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);

    //项目部门总账
    @Query("SELECT " + " acc.*, km.bend bend, km.igrade igrade, km.bprogerty bprogerty, pro.project_code pcode, km.currency_type currencyType, pro.project_name pname, dt.dept_name dname, dt.dept_code dcode " + "FROM " + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " LEFT JOIN sys_department dt ON dt.unique_code = acc.cdept_id " + " LEFT JOIN project pro ON acc.project_id = pro.unique_code " + "WHERE" + " acc.project_class_id = :pcCode  " + " AND (acc.iyperiod BETWEEN :strDate AND :endDate or acc.iyperiod = '21') " + " and acc.ccode LIKE :kmCode  " + " AND km.bitem = '1' " + " AND acc.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findAllKmProjectDept(@Param("pcCode") String pcCode, @Param("kmCode") String kmCode, @Param("strDate") String strDate, @Param("endDate") String endDate, @Param("accId") String accId);

    /**
     * @return reactor.core.publisher.Mono<java.lang.Void>
     * @Author myh
     * @Description 期初记账
     * @Date 13:41 2021/6/24
     * @Param [iyperiod]
     **/
    @Query("update accvoucher set ibook=:ibook where iyperiod =:iyperiod")
    Mono<Void> qcjz(@Param("iyperiod") String iyperiod, @Param("ibook") String ibook);

    @Query("select count(ibook) from accvoucher where iyperiod =:iyperiod and tenant_id=:databasenum and ibook='1' ")
    Mono<Long> qcjzsum(String iyperiod, String databasenum);

    @Query("select count(id) from accvoucher where iyear=:iyear and imonth not in ('00','20','21') and ibook='1' and tenant_id=:databasenum ")
    Mono<Long> pzjzibook(String iyear, String databasenum);

    // 清空一条数据
    @Query("delete from accvoucher where id =:id ")
    Mono<Void> emptyAll(String id);

    /* *************** 科目明细账：查询凭证 ******************* */
    // 获取正常的凭证
    @Query(" select acc.*,ck.igrade from accvoucher acc left join code_kemu ck on acc.ccode = ck.ccode and acc.iyear=ck.iyear where acc.ccode like :ccode and acc.iyear=:iyear and acc.ifrag='0' and acc.imonth not in ('00','20','21') order by  acc.dbill_date,cast(acc.ino_id as Integer) ")
    Flux<AccvoucherVo> findByiyperiodpz(String ccode, String iyear);

    @Query(" select acc.*,ck.igrade from accvoucher acc left join code_kemu ck on acc.ccode = ck.ccode and acc.iyear=ck.iyear where acc.iyear=:iyear and acc.ifrag='0' and acc.imonth not in ('00','20','21') order by  acc.dbill_date,cast(acc.ino_id as Integer) ")
    Flux<AccvoucherVo> findByiyperiodpz2(String iyear);

    // 获取科目凭证的所有区间
    @Query("select acv.iyperiod from accvoucher acv left join code_kemu ck on acv.ccode = ck.ccode and ck.iyear=acv.iyear where acv.iyear=:iyear and ck.ccode like :ccode and acv.imonth not in ('00','20','21') and acv.iyperiod between :strDate and :endDate group by acv.iyperiod order by acv.iyperiod")
    Flux<IyperiodVo> findByCcodeGroupByIyperiod(String iyear, String ccode, String strDate, String endDate);

    @Query("select acv.iyperiod,acv.ccode from accvoucher acv left join code_kemu ck on acv.ccode = ck.ccode and ck.iyear=acv.iyear where acv.iyear=:iyear and acv.imonth not in ('00','20','21') and acv.iyperiod between :strDate and :endDate group by acv.iyperiod,acv.ccode order by acv.iyperiod")
    Flux<IyperiodVo> findByCcodeGroupByIyperiod2(String iyear, String strDate, String endDate);

    // 获取科目凭证的所有日期
    @Query("select acv.dbill_date from accvoucher acv where acv.ccode like :ccode and acv.iyperiod between :strDate and :endDate group by acv.dbill_date order by acv.dbill_date")
    Flux<DnillDateVo> findByCcodeGroupByDbillDate(String ccode, String strDate, String endDate);
    /* *************** 科目明细账：END ******************* */

    @Query("select iyperiod from accvoucher where  1=1 and imonth between '01' and '13' order by iyperiod desc limit 1")
    Mono<String> findFirstByMaxIyperiodValue();

    @Query("select iyperiod from accvoucher where  1=1 and imonth between '01' and '13' and iyear = :iyear order by iyperiod desc limit 1")
    Mono<String> findFirstByMaxIyperiodMonthValue(String iyear);

    Mono<Accvoucher> findFirstByIyearAndImonthBetweenOrderByIyperiodDesc(String year, String monthOne, String monthTwo);

    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 未记账、未主管签字和未审核的凭证集合  —— Mr.Ye
     */
    @Query("select * from accvoucher where  1=1 and ifrag = '0'   and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck = '' or ccheck is null) and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionOld(List<String> keys);

    @Query("select * from accvoucher where  1=1 and (ibook <> '1' or ibook is null) and unique_code in (:keys)  order by dbill_date ASC,ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndCondition(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck = '' or ccheck is null) and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndCondition(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck = '' or ccheck is null) and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndCondition(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 凭证审核 条件 未记账 未主管签字 未审核   —— Mr.Ye
     */
    @Query("update accvoucher  set ccheck = :reviewMan,ccheck_date = :reviewDate where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck = '' or ccheck is null) and unique_code in (:keys) ")
    Flux<Accvoucher> reviewVoucherByYaerAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan, @Param("reviewDate") String reviewDate);

    /**
     * 凭证审核总数   —— Mr.Ye
     */
    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ccheck is not null and ccheck <> '') and dbill_date between :startDate and :endDate) t")
    Mono<Integer> countByCcheckByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ccheck is not null and ccheck <> '') and iyperiod between :startDate And :endDate) t")
    Mono<Integer> countByCcheckByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 未记账、未主管签字和已审核的凭证集合  —— Mr.Ye
     */
    @Query("select * from accvoucher where  1=1 and  (ifrag = '0' or ifrag = '1')   and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck <> '' and ccheck is not null) and unique_code in (:keys)  order by dbill_date ASC,ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionClose(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck <> '' and ccheck is not null) and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionClose(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + " and (ccheck <> '' and ccheck is not null) and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionClose(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 凭证弃审 条件 未记账 未主管签字 已审核   —— Mr.Ye
     */
    @Query("update accvoucher  set ccheck = NULL,ccheck_date = NULL where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and (ibook <> '1' or ibook is null)" + " and (ccheck <> '' and ccheck is not null) and unique_code in (:keys) ")
    Flux<Accvoucher> closeReviewVoucherByYaerAndUniqueCodes(String iyear, Set<String> keys);


    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 未记账、未主管签字的凭证集合  —— Mr.Ye
     * and (ccheck <> '' or ccheck is not null)
     */
    @Query("select * from accvoucher where  1=1 and  (ifrag = '0' or ifrag = '1')   and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null) " + "and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionSignOld(List<String> keys);

    @Query("select * from accvoucher where  1=1 and  (ifrag = '0' or ifrag = '1')   and (ibook <> '1' or ibook is null)  " +/*and (ccheck <> '' or ccheck is not null)*/
            "and unique_code in (:keys)  order by dbill_date ASC,ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionSign(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null) " + "and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionSign(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null) " + "and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionSign(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 主管签字 条件 未记账  已审核  已主管签字  —— Mr.Ye
     */
    @Query("update accvoucher  set cdirector = :reviewMan where  1=1 and  (ifrag = '0' or ifrag = '1')   and iyear=:iyear and (ibook <> '1' or ibook is null) and (cdirector = '' or cdirector is null)" + "and unique_code in (:keys) ")
    Flux<Accvoucher> symbolVoucherByYaerAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan);

    /**
     * 凭证审核总数   —— Mr.Ye
     */
    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (cdirector is not null and cdirector <> '') and dbill_date between :startDate and :endDate) t")
    Mono<Integer> countByCdirectorByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (cdirector is not null and cdirector <> '') and iyperiod between :startDate And :endDate) t")
    Mono<Integer> countByCdirectorByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 未记账、已主管签字和已审核的凭证集合  —— Mr.Ye
     */
    @Query("select * from accvoucher where  1=1 and  (ifrag = '0' or ifrag = '1')   and (ibook <> '1' or ibook is null) and (cdirector <> '' and cdirector is not null)" + " and (ccheck <> '' and ccheck is not null) and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionSignClose(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector <> '' and  cdirector is not null)" + " and (ccheck <> '' and ccheck is not null) and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionSignClose(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) and (cdirector <> '' and  cdirector is not null)" + " and (ccheck <> '' and ccheck is not null) and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionSignClose(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 主管弃签 条件 未记账 已审核 已主管签字   —— Mr.Ye
     */
    @Query("update accvoucher  set cdirector = NULL where  1=1 and  (ifrag = '0' or ifrag = '1')   and iyear=:iyear and (ibook <> '1' or ibook is null) and (cdirector <> '' and cdirector is not null)" + "and unique_code in (:keys) ")
    Flux<Accvoucher> closeSymbolVoucherByYaerAndUniqueCodes(String iyear, Set<String> keys);


    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 未记账 凭证集合  —— Mr.Ye
     * and (ccheck <> '' or ccheck is not null)
     */
    @Query("select * from accvoucher where  1=1 and (ifrag = '0' or ifrag = '1')  and (ibook <> '1' or ibook is null) " + "and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionBookOld(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'" + "and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionBookOld2(List<String> keys);

    @Query("select * from accvoucher where  1=1 " + "and unique_code in (:keys)  order by dbill_date ASC,ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionBook(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) " + "and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionBook(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook <> '1' or ibook is null) " + "and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionBook(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ibook = '1') and dbill_date between :startDate and :endDate) t")
    Mono<Integer> countByIbookByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ibook = '1') and iyperiod between :startDate And :endDate) t")
    Mono<Integer> countByIbookByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 记账凭证 条件 未记账  已审核  已主管签字  —— Mr.Ye
     */
    @Query("update accvoucher  set ibook = '1',cbook = :reviewMan,ibook_date=:reviewDate where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and (ibook <> '1' or ibook is null)" + "and unique_code in (:keys) ")
    Flux<Accvoucher> bookVoucherByYaerAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan, @Param("reviewDate") String reviewDate);

    /**
     * 根据 凭证唯一码、凭证期间和制单日期区间 获取 已记账 凭证集合  —— Mr.Ye
     * and (ccheck <> '' or ccheck is not null)
     */
    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook = '1') " + "and unique_code in (:keys)  order by ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodeAndConditionBookClose(List<String> keys);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook = '1') " + "and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionBookClose(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and (ibook  = '1') " + "and dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionBookClose(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 记账凭证 条件 未记账  已审核  已主管签字  —— Mr.Ye
     */
    @Query("update accvoucher  set ibook = NULL,cbook = NULL,ibook_date=NULL where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and ibook = '1'" + " and unique_code in (:keys) ")
    Flux<Accvoucher> closeBookVoucherByYaerAndUniqueCodes(String iyear, Set<String> keys);


    @Query("select * from accvoucher where  1=1 and unique_code in (:keys)  ORDER BY dbill_date ASC,cast(ino_id as integer) ASC,cast(inid as integer) ASC")
    Flux<Accvoucher> findAllByUniqueCodes(List<String> keys);



    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ccashier is not null and ccashier <> '') and dbill_date between :startDate and :endDate) t")
    Mono<Integer> countByCcashierByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select count(*) from(select DISTINCT unique_code from accvoucher where  1=1 and (ccashier is not null and ccashier <> '') and iyperiod between :startDate And :endDate) t")
    Mono<Integer> countByCcashierByPeriod(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionCashier(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0'  and  dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionCashier(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select * from accvoucher where  1=1 and ifrag = '0' and (ccashier <> '' and ccashier is not null) and iyperiod = :period  order by ino_id ASC")
    Flux<Accvoucher> findAllByPeriodAndConditionCashierClose(String period);

    @Query("select * from accvoucher where  1=1 and ifrag = '0' and (ccashier <> '' and ccashier is not null)  and  dbill_date between :startDate and :endDate order by ino_id ASC")
    Flux<Accvoucher> findAllByIntervalAndConditionCashierClose(@Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select * from accvoucher where  1=1 and ifrag=:status" + " and unique_code in (:keys)  order by dbill_date ASC,ino_id ASC")
    Flux<Accvoucher> findAllByUniqueCodesClose(@Param("keys") List<String> keys, @Param("status") String status);

    @Query("update accvoucher  set ifrag = :status,icancel_name = :reviewMan,icancel_time=:reviewDate where  1=1 and ifrag = '0'  and iyear=:iyear  " + "and unique_code in (:keys) ")
    Flux<Accvoucher> changeVoucherStatusByYaerAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan, @Param("reviewDate") String reviewDate, @Param("status") String status);

    @Query("update accvoucher  set ifrag = :status,iwrong_name = :reviewMan,iwrong_time=:reviewDate where  1=1 and ifrag = '0'  and iyear=:iyear  " + "and unique_code in (:keys) ")
    Flux<Accvoucher> changeVoucherStatusByYaerAndUniqueCodes2(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan, @Param("reviewDate") String reviewDate, @Param("status") String status);

    @Query("update accvoucher  set ifrag = '0',iwrong_name = NULL ,iwrong_time=NULL,icancel_name = NULL,icancel_time=NULL where  1=1 and ifrag =:status  and iyear=:iyear  " + "and unique_code in (:keys) ")
    Flux<Accvoucher> resetVoucherStatusByYearAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("status") String status);

    @Query("update accvoucher  set ccashier = :reviewMan,ccashier_date=:reviewDate where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and (ibook <> '1'  or ibook is null ) and (cdirector = '' or cdirector is null)" + " and (ccashier = '' or ccashier is null) and unique_code in (:keys) ")
    Flux<Object> cashierVoucherByYaerAndUniqueCodes(@Param("iyear") String iyear, @Param("keys") Set<String> keys, @Param("reviewMan") String reviewMan, @Param("reviewDate") String reviewDate);

    @Query("update accvoucher  set ccashier = NULL,ccashier_date = NULL where  1=1 and (ifrag = '0' or ifrag = '1')  and iyear=:iyear and (ibook <> '1' or ibook is null) " + " and (ccashier <> '' and ccashier is not null) and unique_code in (:keys) ")
    Flux<Object> closeCashierVoucherByYaerAndUniqueCodes(String iyear, Set<String> keys);

    @Query("select * from accvoucher where iyear=:iyear and iyperiod not in (:iyperiod) order by iyperiod ")
    Flux<Accvoucher> findByIyear(String iyear, Set<String> iyperiod);

    @Query("select * from accvoucher where 1=1 and (ibook is null or ibook <> '1')  and (ccheck is null or ccheck = '') and iyear=:iyear and unique_code =:uniqueCode and vouch_un_code =:vouchUnCode")
    Flux<Accvoucher> findAllByVoucherByVouchUnCode(String year, String uniqueCode, String vouchUnCode);

    @Query("select * from accvoucher where 1=1 and (ibook is null or ibook <> '1')   and iyear=:iyear and unique_code =:uniqueCode and vouch_un_code =:vouchUnCode")
    Flux<Accvoucher> findAllByVoucherByVouchUnCodeXj(String year, String uniqueCode, String vouchUnCode);

    @Query("select * from accvoucher where iyear=:iyear and imonth not in ('00','20','21') order by iyperiod ")
    Flux<Accvoucher> findByIyear(String iyear);

    @Query("select * from accvoucher where iyear=:iyear order by iyperiod ")
    Flux<Accvoucher> findByIyear2(String iyear);

    @Query("select * from accvoucher where iyperiod between :strDsate and :endDate and ifrag='0' order by iyperiod ")
        // and ccheck_date!=''
    Flux<AccvoucherVo> findByIyearVo(String strDsate, String endDate);

    @Query("select count(id) from accvoucher where ccode =:ccode and iyear=:iyear and imonth not in ('00','20','21')")
    Mono<Long> countByCcode(String ccode, String iyear);

    @Query("select ccode from accvoucher where iyear=:iyear and imonth not in ('00','20','21')")
    Flux<String> findByCcodeStr(String iyear);

    @Query("select ccode from accvoucher where iyperiod=:iyperiod and (cast(md as decimal)!=0 or cast(mc as decimal)!=0) ")
    Flux<String> findByQcCcodeStr(String iyperiod);

    @Query("select count(id) from accvoucher where ccode =:ccode and iyperiod=:iyperiod ")
    Mono<Long> countByQCCcode(String ccode, String iyperiod);

    Flux<Accvoucher> findByCcodeAndIyearAndIperiod(String ccode, String iyear, String iperiod);


    @Query("SELECT " + " acc.*, km.bend bend, km.igrade igrade, km.bprogerty bprogerty, pro.project_code pcode, km.currency_type currencyType, pro.project_name pname " + "FROM " + " accvoucher acc " + " LEFT JOIN code_kemu km ON acc.ccode = km.ccode " + " LEFT JOIN project pro ON acc.project_id = pro.unique_code " + "WHERE" + " acc.unique_code = :uniqueCode  " + " AND acc.tenant_id = :accId " + " AND km.tenant_id = :accId " + " ORDER BY acc.iyperiod asc")
    Flux<GeneralLedgerRo> findByUniqueCodeAndTenantId(String uniqueCode, String accId);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f\n" + "        from accvoucher accv  where accv.iyear=:iyear and accv.project_id = :minDept  and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name,accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md,accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearAndItemId(@Param("iyear") String iyear, @Param("minDept") String minDept, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f\n" + "        from accvoucher accv  where accv.iyear=:iyear and accv.cdept_id = :minDept  and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name,accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md,accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearAndCdeptId(@Param("iyear") String iyear, @Param("minDept") String minDept, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f\n" + "        from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' where accv.iyear=:iyear and accv.cperson_id like :minPsn  and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name,accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md,accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency,accv.ibook\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearAndPsnId(@Param("iyear") String iyear, @Param("minPsn") String minPsn, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f\n" + "        from accvoucher accv  where accv.iyear=:iyear and accv.ccus_id = :minDept  and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name,accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md,accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearAndCcusId(@Param("iyear") String iyear, @Param("minDept") String minDept, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f,accv.cdept_id,\n" + "       accv.cperson_id,\n" + "       accv.ccus_id,\n" + "       accv.csup_id,\n" + "       accv.project_id\n" + "        from accvoucher accv  where accv.iyear=:iyear and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name," + "accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md," + "accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency,accv.cdept_id,\n" + "       accv.cperson_id,\n" + "       accv.ccus_id,\n" + "       accv.csup_id,\n" + "       accv.project_id\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearFuzhuAccvoucher(@Param("iyear") String iyear, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal) md,\n" + "       cast(accv.mc as decimal) mc,\n" + "       cast(case when accv.nd_s !='' then accv.nd_s else '0' end as decimal )as nd_s,\n" + "       cast(case when accv.nc_s !='' then accv.nc_s else '0' end as decimal )as nc_s,\n" + "       cast(case when accv.nfrat_md !='' then accv.nfrat_md else '0' end as decimal )as nfrat_md,\n" + "       cast(case when accv.nfrat_mc !='' then accv.nfrat_mc else '0' end as decimal )as nfrat_mc,\n" + "       cast(case when accv.cunit_price !='' then accv.cunit_price else '0' end as decimal )as cunit_price,\n" + "       cast(case when accv.md_f !='' then accv.md_f else '0' end as decimal )as md_f\n" + "        from accvoucher accv  where accv.iyear=:iyear and accv.csup_id = :minDept  and iyperiod between :startDate and :endDate \n" + "        group by accv.iyperiod,accv.csign,accv.ino_id,accv.inid,accv.ccode,accv.ccode_name,accv.imonth,accv.dbill_date,accv.cdigest,accv.md,accv.mc,accv.nd_s,accv.nc_s,accv.nfrat_md,accv.nfrat_mc,accv.cunit_price,accv.md_f,accv.foreign_currency\n" + "order by  accv.ccode,accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByIyearAndCsupId(@Param("iyear") String iyear, @Param("minDept") String minDept, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,\n" + "       accv.ccode,accv.inid,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.cperson_id != '' then accv.cperson_id else '0' end as cperson_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccode like :ccode\n" + "  and accv.cperson_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id,accv.inid, accv.ccode, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.cperson_id,accv.foreign_currency,accv.ibook\n" + "order by accv.ccode, accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherPsn(@Param("iyear") String iyear, @Param("ccode") String ccode, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.cdept_id != '' then accv.cdept_id else '0' end as cdept_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccode like :ccode\n" + "  and accv.cdept_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id, accv.ccode,accv.inid, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.cdept_id,accv.foreign_currency,accv.ibook\n" + "order by accv.ccode, accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherDept(@Param("iyear") String iyear, @Param("ccode") String ccode, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.ccus_id != '' then accv.ccus_id else '0' end as ccus_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccode like :ccode\n" + "  and accv.ccus_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id,accv.inid, accv.ccode, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.ccus_id,accv.foreign_currency,accv.ibook\n" + "order by accv.ccode, accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherCus(@Param("iyear") String iyear, @Param("ccode") String ccode, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.ccus_id != '' then accv.ccus_id else '0' end as ccus_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccus_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id,accv.inid, accv.ccode, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.ccus_id,accv.foreign_currency,accv.ibook\n" + "order by accv.ccode, accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherCus2(@Param("iyear") String iyear, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.csup_id != '' then accv.csup_id else '0' end as csup_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccode like :ccode\n" + "  and accv.csup_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id,accv.inid, accv.ccode, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.csup_id,accv.foreign_currency,accv.ibook\n" + "order by accv.dbill_date,accv.ccode,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherSup(@Param("iyear") String iyear, @Param("ccode") String ccode, @Param("startDate") String startDate, @Param("endDate") String endDate);

    @Query("select accv.iyperiod,\n" + "       accv.csign,\n" + "       accv.ino_id,accv.inid,\n" + "       accv.ccode,\n" + "       accv.ccode_name,\n" + "       accv.dbill_date,\n" + "       accv.cdigest,\n" + "       accv.imonth,\n" + "       case when accv.ibook != '' then accv.ibook else '0' end as ibook,\n" + "       case when accv.project_id != '' then accv.project_id else '0' end as project_id,\n" + "       case when accv.foreign_currency != '' then accv.foreign_currency else '0' end as foreign_currency,\n" + "       cast(accv.md as decimal)                                                                md,\n" + "       cast(accv.mc as decimal)                                                                mc,\n" + "       cast(case when accv.nd_s != '' then accv.nd_s else '0' end as decimal)               as nd_s,\n" + "       cast(case when accv.nc_s != '' then accv.nc_s else '0' end as decimal)               as nc_s,\n" + "       cast(case when accv.nfrat_md != '' then accv.nfrat_md else '0' end as decimal)       as nfrat_md,\n" + "       cast(case when accv.nfrat_mc != '' then accv.nfrat_mc else '0' end as decimal)       as nfrat_mc,\n" + "       cast(case when accv.cunit_price != '' then accv.cunit_price else '0' end as decimal) as cunit_price,\n" + "       cast(case when accv.md_f != '' then accv.md_f else '0' end as decimal)               as md_f\n" + "from accvoucher accv left join code_kemu ck on ck.ccode=accv.ccode and ck.bend='1' \n" + "where accv.iyear = :iyear\n" + "  and accv.ccode like :ccode\n" + "  and accv.project_id >'0' \n" + "  and iyperiod between :startDate and :endDate\n" + "group by accv.iyperiod, accv.csign, accv.ino_id,accv.inid, accv.ccode, accv.ccode_name, accv.imonth, accv.dbill_date,\n" + "         accv.cdigest, accv.md, accv.mc, accv.nd_s, accv.nc_s, accv.nfrat_md, accv.nfrat_mc, accv.cunit_price, accv.md_f,accv.project_id,accv.foreign_currency,accv.ibook\n" + "order by accv.ccode, accv.dbill_date,cast(accv.ino_id as INTEGER),cast(accv.inid as INTEGER)")
    Flux<DeptCodeAccvoucherVo> findAllByAccVoucherPro(@Param("iyear") String iyear, @Param("ccode") String ccode, @Param("startDate") String startDate, @Param("endDate") String endDate);


    // 清空一条数据
    Mono<Void> deleteByUniqueCode(String uniqueCode);

    @Query("select * from accvoucher where iyperiod=:yearMonth order by cast(ino_id as integer )  asc limit 1 offset 0")
    Mono<Accvoucher> abc(String yearMonth);

    @Query("select ino_id from (select distinct ino_id  from accvoucher where tenant_id='gsy-001-2021' and iyperiod=:yearMonth) a  order by cast(ino_id as integer )")
    Mono<Accvoucher> aaa(String yearMonth);

    @Query("select * from accvoucher where iyperiod=:yearMonth and ino_id='1'")
    Flux<Accvoucher> lastPage(String yearMonth);

    @Query(" select acc.* from accvoucher acc where acc.iyear=:iyear and acc.ifrag='0' and acc.imonth not in ('00','20','21') order by  acc.dbill_date,ino_id ")
    Flux<AccvoucherVo> findAllByiyperiodpz(String iyear);

    @Query("select distinct ino_id from accvoucher where iyperiod=:yearMonth")
    Flux<AccvoucherVo> oneMonth(String yearMonth);


    @Query("update accvoucher set cash_project='1' where unique_code = :uniqueCode ")
    Mono<Void> updateCashProjectByUniqueCode(String uniqueCode);

    @Query("select cbill,iyperiod from accvoucher where iyperiod between :strDate and :endDate group by cbill,iyperiod order by iyperiod ")
    Flux<Accvoucher> groupByCbill(String strDate, String endDate);

    @Query("select\n" + "        var.ccode,ck.ccode_name,\n" + "        REPLACE (var.dbill_date,'-','') dbill_date,\n" + "        var.foreign_currency,\n" + "        var.cbill,\n" + "        var.ibook,\n" + "        var.csign,\n" + "        var.ifrag,\n" + "        var.ino_id,\n" + "        cast(var.md as decimal) md,\n" + "        cast(var.mc as decimal) mc,\n" + "        case when var.nd_s !='' then var.nd_s else '0' end as nd_s,\n" + "        case when var.nc_s !='' then var.nc_s else '0' end as nc_s,\n" + "        case when var.nfrat_md !='' then var.nfrat_md else '0' end as nfrat_md,\n" + "        case when var.nfrat_mc !='' then var.nfrat_mc else '0' end as nfrat_mc,\n" + "        case when var.lj_md !='' then var.lj_md else '0' end as lj_md,\n" + "        case when var.lj_mc !='' then var.lj_mc else '0' end as lj_mc\n" + "\n" + "from accvoucher var left join code_kemu ck on ck.ccode=var.ccode  where var.iyperiod between :strDate and :endDate order by var.ccode,var.dbill_date")
    Flux<KmHuiZongVo> AccvoucherKmHuiZong(String strDate, String endDate);

    @Query("select b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash, " + "sum(cast(COALESCE(md,'0') as decimal)) as md,sum(cast(COALESCE(mc,'0') as decimal)) as mc, " + "sum(cast(case when nfrat_md !='' then nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when nfrat_mc !='' then nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when nd_s !='' then nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when nc_s !='' then nc_s else '0' end as decimal)) as nc_s " + "from code_kemu as b " + "left join accvoucher as a on a.ccode=b.ccode and iyperiod between :strDate and :endDate and (ifrag='0' or ifrag is null)" + "where (b.bbank='1' or b.bcash='1' or b.bcash_equivalence = '1') and b.bend='1' " + "group by b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash ")
    Flux<AccvoucherYueVo> findAccountByBankYue(String strDate, String endDate);

    @Query("select b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash, " + "sum(cast(COALESCE(md,'0') as decimal)) as md,sum(cast(COALESCE(mc,'0') as decimal)) as mc, " + "sum(cast(case when nfrat_md !='' then nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when nfrat_mc !='' then nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when nd_s !='' then nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when nc_s !='' then nc_s else '0' end as decimal)) as nc_s " + "from code_kemu as b " + "left join accvoucher as a on a.ccode=b.ccode and iyperiod between :strDate and :endDate and (ifrag='0' or ifrag is null) and a.ibook='1' " + "where (b.bbank='1' or b.bcash='1' or b.bcash_equivalence = '1') and b.bend='1' " + "group by b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash ")
    Flux<AccvoucherYueVo> findAccountByBankYueByIbook(String strDate, String endDate);

    @Query("select b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash, " + "sum(cast(COALESCE(md,'0') as decimal)) as md,sum(cast(COALESCE(mc,'0') as decimal)) as mc, " + "sum(cast(case when nfrat_md !='' then nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when nfrat_mc !='' then nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when nd_s !='' then nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when nc_s !='' then nc_s else '0' end as decimal)) as nc_s " + "from code_kemu as b " + "left join accvoucher as a on a.ccode=b.ccode and iyperiod between :strDate and :endDate and (ifrag='0' or ifrag is null) " + "where b.bcash='1' and bend='1' " + "group by b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash ")
    Flux<AccvoucherYueVo> findAccountByCashYue(String strDate, String endDate);

    @Query("select b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash, " + "sum(cast(COALESCE(md,'0') as decimal)) as md,sum(cast(COALESCE(mc,'0') as decimal)) as mc, " + "sum(cast(case when nfrat_md !='' then nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when nfrat_mc !='' then nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when nd_s !='' then nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when nc_s !='' then nc_s else '0' end as decimal)) as nc_s " + "from code_kemu as b " + "left join accvoucher as a on a.ccode=b.ccode and iyperiod between :strDate and :endDate and (ifrag='0' or ifrag is null) and a.ibook='1' " + "where b.bcash='1' and bend='1' " + "group by b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash ")
    Flux<AccvoucherYueVo> findAccountByCashYueByIbook(String strDate, String endDate);

    @Query("select b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash, " + "sum(cast(COALESCE(md,'0') as decimal)) as md,sum(cast(COALESCE(mc,'0') as decimal)) as mc, " + "sum(cast(case when nfrat_md !='' then nfrat_md else '0' end as decimal)) as nfrat_md, " + "sum(cast(case when nfrat_mc !='' then nfrat_mc else '0' end as decimal)) as nfrat_mc, " + "sum(cast(case when nd_s !='' then nd_s else '0' end as decimal)) as nd_s, " + "sum(cast(case when nc_s !='' then nc_s else '0' end as decimal)) as nc_s " + "from code_kemu as b " + "left join accvoucher as a on a.ccode=b.ccode and a.dbill_date between :strDate and :endDate  and iyperiod!= :iyperiod and (ifrag='0' or ifrag is null) " + "where b.bbank='1' and b.bend='1' " + "group by b.ccode,b.ccode_name,a.unit_measurement,a.foreign_currency,b.bprogerty,b.bbank,b.bcash ")
    Flux<AccvoucherYueVo> findAccountByYuetiaojie(String strDate, String endDate, String iyperiod);

    @Query("select " + "\tca.project_code as xjcode,\n" + "\tca.project_name as xjname,\n" + "\tca.project_type_name as xjtypename,\n" + "\tca.fangxiang as fx,\n" + "\tacc.dbill_date,\n" + "\tacc.ino_id,\n" + "\tacc.cdigest,\n" + "\tacc.md,\n" + "\tacc.mc  \n" + "FROM\n" + "\tcash_flow cf\n" + "\tLEFT JOIN accvoucher acc ON acc.unique_code = cf.unique_code\n" + "\tLEFT JOIN project_cash ca ON cf.project_code = ca.project_code " + "\tWHERE\n" + "\t ino_id NOTNULL and iyperiod between :strDate and :endDate " + " and ( CASE WHEN ca.fangxiang = '1' THEN acc.md != '0.00' ELSE acc.mc != '0.00' END ) " + " order by ca.project_code asc,acc.dbill_date asc,acc.ino_id asc ")
    Flux<AccvoucherXjlxMxVo> findXjlxMx(String strDate, String endDate);

    @Query("SELECT DISTINCT ac.*" + "FROM accvoucher ac\n" + "WHERE 1=1 and ifrag = '1' and ac.imonth between '01' and '13' And iyperiod between :startDate And :endDate \n" + "ORDER BY ac.dbill_date ASC,ac.ino_id ASC")
    Flux<Accvoucher> findAllVoucherInvalidByIyperiod(String trim, String trim1);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bcash='1' and a.iyperiod = :iyperiod  and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByDayAccountAndQc(String iyperiod);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bcash='1' and a.iyperiod between :iyperiod1 and :iyperiod2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer) ")
    Flux<Accvoucher> findByDayAccount(String iyperiod1, String iyperiod2);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bcash='1' and a.dbillDate between :dbillDate1 and :dbillDate2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer)")
    Flux<Accvoucher> findByDayAccountAndDate(String dbillDate1, String dbillDate2);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bbank='1' and a.iyperiod = :iyperiod  and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByBankAccountAndQc(String iyperiod);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bbank='1' and a.iyperiod between :iyperiod1 and :iyperiod2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer) ")
    Flux<Accvoucher> findByBankAccount(String iyperiod1, String iyperiod2);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where a.ccode = :ccode and a.iyperiod = :iyperiod  and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByDayBookAccountAndQc(String iyperiod, String ccode);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where a.ccode = :ccode and a.iyperiod between :iyperiod1 and :iyperiod2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer) ")
    Flux<Accvoucher> findByDayBookAccount(String iyperiod1, String iyperiod2, String ccode);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where a.ccode = :ccode and a.dbillDate between :dbillDate1 and :dbillDate2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer)")
    Flux<Accvoucher> findByDayBookAccountAndDate(String dbillDate1, String dbillDate2, String ccode);

    @Query("select * from accvoucher a left join code_kemu b on a.ccode = b.ccode " + "where b.bbank='1' and a.dbillDate between :dbillDate1 and :dbillDate2  and (ifrag='0' or ifrag is null) " + "order by a.dbill_date,cast(a.ino_id as Integer)")
    Flux<Accvoucher> findByBankAccountAndDate(String dbillDate1, String dbillDate2);

    @Query("select * from accvoucher where ccode = :ccode and iyear = :iyear order by dbill_date,cast(ino_id as Integer) ")
    Flux<Accvoucher> findByCcodeAndIyearOrderByDbillDate(String ccode, String iyear);

    Flux<Accvoucher> findByUniqueCodeOrderByInid(String uniqueCode);

    Mono<Accvoucher> findFirstByImonthBetweenOrderByDbillDateDesc(String start, String end);

    @Query("select * from accvoucher where iyperiod <= :iyperiod and imonth not in ('00','20','21') order by iyperiod ")
    Flux<Accvoucher> findByIyperiod(String iyperiod);

    //个人核销
    @Query("SELECT " + " acc.*, " + "  (SELECT SUM(cast(wo.hx_money as decimal)) FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code ) AS money, " + "  (SELECT wo.hx_code FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code limit 1) AS hxcode, " + "   cu.psn_name as custname " + " FROM " + "   accvoucher acc " + "  LEFT JOIN sys_psn cu ON acc.cperson_id = cu.unique_code " + " WHERE " + "  acc.ccode = :kmCode " + "  AND acc.ibook = '1' AND acc.cperson_id IS NOT NULL" + "  AND (acc.hx_statue is null or acc.hx_statue = '0' or acc.hx_statue = '2') " + "  AND ( ('00' < acc.imonth AND acc.imonth <= '16') or acc.imonth = '21') " + " ORDER BY acc.iyperiod asc")
    Flux<WriteOffRo> findAllByCodeGeren(@Param("kmCode") String kmCode);

    //客户核销
    @Query("SELECT " + " acc.*, " + "  (SELECT SUM(cast(wo.hx_money as decimal)) FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code ) AS money, " + "  (SELECT wo.hx_code FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code limit 1) AS hxcode, " + "   cu.cust_abbname as custname " + " FROM " + "   accvoucher acc " + "  LEFT JOIN customer cu ON acc.ccus_id = cu.unique_code " + " WHERE " + "  acc.ccode = :kmCode " + "  AND acc.ibook = '1' AND acc.ccus_id IS NOT NULL" + "  AND (acc.hx_statue is null or acc.hx_statue = '0' or acc.hx_statue = '2') " + "  AND ( ('00' < acc.imonth AND acc.imonth <= '16') or acc.imonth = '21') " + " ORDER BY acc.iyperiod asc")
    Flux<WriteOffRo> findAllByCode(@Param("kmCode") String kmCode);

    //供应商核销
    @Query("SELECT " + " acc.*, " + "  (SELECT SUM(cast(wo.hx_money as decimal)) FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code ) AS money, " + "  (SELECT wo.hx_code FROM write_off wo WHERE wo.unique_code = wo.unique_code AND acc.vouch_un_code = wo.vouch_un_code limit 1) AS hxcode, " + "   cu.sup_abbname as custname " + " FROM " + "   accvoucher acc " + "  LEFT JOIN supplier cu ON acc.csup_id = cu.unique_code " + " WHERE " + "  acc.ccode = :kmCode " + "  AND acc.ibook = '1' AND acc.csup_id IS NOT NULL" + "  AND (acc.hx_statue is null or acc.hx_statue = '0' or acc.hx_statue = '2') " + "  AND ( ('00' < acc.imonth AND acc.imonth <= '16') or acc.imonth = '21') " + " ORDER BY acc.iyperiod asc")
    Flux<WriteOffRo> findAllByCodeGys(@Param("kmCode") String kmCode);

    //核销历史
    @Query("SELECT\n" + "\tacc.*,\n" + "\two.hx_date hxdate, \n" + "\two.hx_code hxcode, \n" + "\two.hx_money money, \n" + "\tcu.cust_abbname as custname, \n" + "\tsu.username username \n" + " FROM \n" + "\twrite_off wo\n" + "\tLEFT JOIN accvoucher acc ON acc.vouch_un_code = wo.vouch_un_code\n" + "\tLEFT JOIN customer cu ON acc.ccus_id = cu.unique_code \n" + "\tLEFT JOIN _app_group_sys_user su ON su.id = wo.create_code \n" + " WHERE \n" + "\tacc.ccode = :kmCode \n" + "\tAND acc.ibook = '1' \n" + "\tAND acc.ccus_id IS NOT NULL \n" + " ORDER BY\n" + "\tacc.iyperiod ASC")
    Flux<WriteOffRo> findHistoryByCode(@Param("kmCode") String kmCode);

    @Query("select count(id) from accvoucher where iyear = :year and tenant_id = :ten")
    Mono<Long> countAllByIyearAndTenantId(String year, String ten);

    @Query("update accvoucher set hx_statue=:hxStatue where vouch_un_code in (:list) ")
    Mono<Void> updateHxStatueByVouchUnCode(@Param("list") List<String> vlist, @Param("hxStatue") String hxStatue);

    @Query("select * from accvoucher where iyperiod=:iyperiod ")
    Flux<AccvoucherVo> findByThisMonthCodeMdMc(String iyperiod);

    @Query("select * from accvoucher where iyperiod between :strDate and :endDate ")
    Flux<AccvoucherVo> findByBeforeMonthCodeMdMc(String strDate, String endDate);

    @Query("select * from accvoucher where ccode = :ccode and iyear = :iyear and iperiod = :iperiod and imonth = :iperiod and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByCodeAndYearAndPeriod(String ccode, String iyear, String iperiod);

    @Query("select * from accvoucher where ccode = :kemuCode and iyear = :year and dbill_date <= :endDate and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByKemuAndDate(String kemuCode, String year, String endDate);

    @Query("select * from accvoucher where ccode = :ccode and iyear = :iyear and iperiod between :iperiod1 and :iperiod2 and imonth between :iperiod1 and :iperiod2 and (ifrag='0' or ifrag is null) ")
    Flux<Accvoucher> findByCodeAndYearAndPeriodList(String ccode, String iyear, String iperiod1, String iperiod2);

    @Query("select  " + "sum(cast(md as decimal))       md,\n" + "       sum(cast(COALESCE(md,'0') as decimal)) as md,\n" + "       sum(cast(COALESCE(mc,'0') as decimal)) as mc,\n" + "       sum(cast(COALESCE(md_f,'0') as decimal)) as md_f,\n" + "       sum(cast(COALESCE(mc_f,'0') as decimal)) as mc_f,\n" + "       sum(cast(COALESCE(nd_s,'0') as decimal)) as nd_s,\n" + "       sum(cast(COALESCE(nc_s,'0') as decimal)) as nc_s,\n" + "       sum(cast(COALESCE(nfrat_md,'0') as decimal)) as nfrat_md,\n" + "       sum(cast(COALESCE(nfrat_mc,'0') as decimal)) as nfrat_mc," + " unit_measurement,foreign_currency,\n" + "                    cdept_id,cperson_id,ccus_id,csup_id,project_id,\n" + "dept.dept_code   as            dept_code,\n" + "       dept.dept_name   as            dept_name,\n" + "       psn.psn_code     as            psn_code,\n" + "       psn.psn_name     as            psn_name,\n" + "       cus.cust_code    as            cust_code,\n" + "       cus.cust_name    as            cust_name,\n" + "       sup.sup_code     as            sup_code,\n" + "       sup.sup_name     as            sup_name,\n" + "       pro.project_code as            pro_code,\n" + "       pro.project_name as            pro_name " + "            from accvoucher acc \n" + "                     left join sys_department dept on dept.unique_code = cdept_id\n" + "                     left join sys_psn psn on psn.unique_code = cperson_id\n" + "                     left join customer cus on cus.unique_code = ccus_id\n" + "                     left join supplier sup on sup.unique_code = csup_id\n" + "                     left join project pro on pro.unique_code = project_id\n" + "            where ccode =:ccode \n" + "              and ifrag = '0'\n" + "              and csign=:csign \n" + "and iyperiod between :str and :end \n" + "group by dept.dept_name,\n" + "         psn.psn_name,\n" + "         cus.cust_name,\n" + "         sup.sup_name,dept.dept_code,psn.psn_code,cus.cust_code,sup.sup_code,pro.project_code,\n" + "         pro.project_name, cdept_id, cperson_id, ccus_id, csup_id, project_id, unit_measurement, foreign_currency\n" + "order by dept.dept_code,psn.psn_code,cus.cust_code,sup.sup_code,pro.project_code")
    Flux<AccvoucherVo2> findByCodeAccvoucher(String ccode, String str, String end, String csign);

    @Query("select * from accvoucher where 1=1 and imonth between '01' and '13'  and (ifrag = '2' or ifrag = '3') and dbill_date >= :strDate and dbill_date <= :endDate ")
    Flux<Accvoucher> findAllByErrorPingZhengList(String strDate, String endDate);

    @Query("select * from accvoucher where 1=1 and imonth between '01' and '13' and dbill_date >= :strDate and dbill_date <= :endDate and (ccheck is null or ccheck = '') ")
    Flux<Accvoucher> findAllByUnReviewPingZhengList(String strDate, String endDate);

    @Query("select * from accvoucher where 1=1 and imonth between '01' and '13' and dbill_date >= :strDate and dbill_date <= :endDate and (ibook is null or ibook != '1') ")
    Flux<Accvoucher> findAllByUnIbookPingZhengList(String strDate, String endDate);

    @Query("SELECT DISTINCT ac.dbill_date,\n" + "                ac.unique_code,\n" + "                cast(ac.ino_id as integer)\n" + "FROM accvoucher ac WHERE 1=1 and ac.imonth between '01' and '13' And dbill_date >= :strDate and dbill_date <= :endDate \n" + "ORDER BY ac.dbill_date ASC, cast (ac.ino_id as integer) ASC")
    Flux<Accvoucher> findAllByAllDateAndNumber(String strDate, String endDate);

    // 凭证表指定月 最大 凭证号加1
    @Query("select max(cast(ino_id as INTEGER))+1 from accvoucher where imonth=:imonth")
    Mono<Long> finByMonthMaxInoId(String imonth);

    @Query("SELECT DISTINCT ac.unique_code,ac.csign, ac.ino_id,ac.dbill_date FROM accvoucher ac WHERE 1=1 and ac.imonth between '01' and '13' And ifrag = '0' ORDER BY ac.dbill_date ASC,ac.ino_id  ASC")
    Flux<Accvoucher> findAllByIfrag();

    /** 根据凭证类别查询凭证条数 **/
    Mono<Long> countByCsign(String csign);

    @Query("select count(id) from accvoucher where ccode like :ccode")
    Mono<Long> countByLikeCcode(String ccode);


    /** 结转上年 **/
    /**
     * 获取期初
     * @param iyear
     * @return
     */
    @Query("select * from accvoucher where imonth ='00' and iyear=:iyear")
    Flux<AccvoucherVo> findAllByCodeQc(String iyear);

    /**
     * 获取辅助科目期初
     * @param iyear
     * @return
     */
    @Query("select * from accvoucher where imonth ='21' and iyear=:iyear")
    Flux<AccvoucherVo> findAllByFuzhuCodeQc(String iyear);

    @Query("select * from accvoucher where imonth in ('21','00') and iyear=:iyear ")
    Flux<Accvoucher> findAllByCode0021(String iyear);

    Flux<Accvoucher> findAllByCpersonId(String cpersonId);

    Flux<Accvoucher> findAllByUniqueCode(String uniqueCode);

    @Query("select distinct cdigest,dbill_date  from accvoucher where  1=1 and iyear = :iyear and cdigest is not null order by dbill_date desc")
    Flux<Accvoucher> findAllByCdigest(String iyear);
}

