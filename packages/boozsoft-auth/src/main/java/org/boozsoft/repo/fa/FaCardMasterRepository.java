package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaCardMaster;
import org.boozsoft.domain.ro.GdGeneralLedgerRo;
import org.boozsoft.domain.vo.fa.FaCardMasterVo;
import org.boozsoft.domain.vo.fa.FaCardVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface FaCardMasterRepository extends ReactiveCrudRepository<FaCardMaster, String> {

    Flux<FaCardMaster> findAllByOrderBySysId();

    Mono<FaCardMaster> findByCardUniqueOrderBySysId(String cardUnique);

    Mono<FaCardMaster> deleteByCardUnique(String cardUnique);

    /**
     * 获取最大的系统编号
     */
    @Query("select Max(sys_id) as sys_id from fa_card_master")
    Mono<FaCardMaster> findMaxSysId();

    Flux<FaCardMaster> findBySysId(String sysId);

    Flux<FaCardMaster> findByCardCode(String cardCode);

    Mono<FaCardMaster> findFirstByCardUnique(String cardCode);

    //    @Query("select * from fa_card_master where :period between left(first_time,7) and left(jzdqr_time,7) and chaifen='0' and jianshao='0'")
    @Query("select a.*,b.jzdqr_time,b,chaifen,b.jianshao,b.jiechu from fa_card_master a " +
            "left join fa_change b on a.card_unique=b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where :period between left(a.first_time,7) and left(b.jzdqr_time,7) and b.chaifen='0' and b.jianshao='0'")
    Flux<FaCardMasterVo> findByIymonth(String iperiod);


    @Query("select a.* from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where 1=1 and b.chaifen!='1' and b.jianshao!='1' and a.manage_code = :code and a.creat_time like :date Order by sys_id asc")
    Flux<FaCardMaster> findAllByManageCodeAndCreatTimeLike(String code, String date);

    @Query("select a.* from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where 1=1 and b.chaifen!='1' and b.jianshao!='1' and a.manage_code = :code and a.creat_time < :date Order by sys_id asc")
    Flux<FaCardMaster> findAllByManageCodeAndCreatTime(String code, String date);


    //单类别查询
    @Query("SELECT " +
            " ft.ec_code as eccode, " +
            " ft.ec_name as ecname, " +
            " fc.card_unique as cardunique, " +
            " fc.cdate as cdate, " +
            " ( " +
            " SELECT " +
            "  fc.yuanzhi  " +
            " FROM " +
            "  fa_change fc " +
            "  LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            " WHERE " +
            "  fc.cdate <= :years " +
            "  AND ft.ec_code LIKE :ecCode  " +
            "  AND fc.card_unique = cm.card_unique  " +
            " ORDER BY " +
            "  cdate DESC  " +
            "  LIMIT 1  " +
            " ) AS yuanzhi, " +
            " ( " +
            " SELECT " +
            "  fc.ljzhejiu " +
            " FROM " +
            "  fa_change fc " +
            "  LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            " WHERE " +
            "  fc.cdate <= :years  " +
            "  AND ft.ec_code LIKE :ecCode  " +
            "  AND fc.card_unique = cm.card_unique  " +
            " ORDER BY " +
            "  cdate DESC  " +
            "  LIMIT 1  " +
            " ) AS ljzhejiu  " +
            "FROM " +
            " fa_card_master cm " +
            " LEFT JOIN fa_change fc ON cm.card_unique = fc.card_unique " +
            " LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            "WHERE " +
            " cm.manage_code = :manageCode  " +
            " AND cm.chaifen != '1'  " +
            " AND cm.jianshao != '1'  " +
            " AND cm.cdate LIKE :year " +
            " AND ft.ec_code LIKE :ecCode ")
    Flux<GdGeneralLedgerRo> findOneByLb(String manageCode, String year, String years, String ecCode);

    //全类别查询
    @Query("SELECT " +
            " ft.ec_code as eccode, " +
            " ft.ec_name as ecname, " +
            " fc.card_unique as cardunique, " +
            " fc.cdate as cdate, " +
            " ( " +
            " SELECT " +
            "  fc.yuanzhi  " +
            " FROM " +
            "  fa_change fc " +
            "  LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            " WHERE " +
            "  fc.cdate <= :years " +
            "  AND fc.card_unique = cm.card_unique  " +
            " ORDER BY " +
            "  cdate DESC  " +
            "  LIMIT 1  " +
            " ) AS yuanzhi, " +
            " ( " +
            " SELECT " +
            "  fc.ljzhejiu " +
            " FROM " +
            "  fa_change fc " +
            "  LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            " WHERE " +
            "  fc.cdate <= :years  " +
            "  AND fc.card_unique = cm.card_unique  " +
            " ORDER BY " +
            "  cdate DESC  " +
            "  LIMIT 1  " +
            " ) AS ljzhejiu  " +
            "FROM " +
            " fa_card_master cm " +
            " LEFT JOIN fa_change fc ON cm.card_unique = fc.card_unique " +
            " LEFT JOIN fa_asset_type ft ON ft.unique_code = fc.fa_class  " +
            "WHERE " +
            " cm.manage_code = :manageCode  " +
            " AND cm.chaifen != '1'  " +
            " AND cm.jianshao != '1'  " +
            " AND cm.cdate LIKE :year ")
    Flux<GdGeneralLedgerRo> findAllByLb(String manageCode, String year, String years);

    @Query("select a.* from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where 1=1 and b.chaifen!='1' and b.jianshao!='1' and a.manage_code = :code and a.creat_time < :date and sys_id like :strValue  Order by sys_id asc")
    Flux<FaCardMaster> findAllByManageCodeAndCreatTimeLessThanAndSysIdLikeOrderBySysId(String code, String date, String strValue);

    @Query("select a.* from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where 1=1 and b.chaifen!='1' and b.jianshao!='1' and a.manage_code = :code and a.creat_time < :date and card_code like :strValue  Order by sys_id asc")
    Flux<FaCardMaster> findAllByManageCodeAndCreatTimeLessThanAndCardCodeLikeOrderByCardCode(String code, String date, String strValue);

    @Query("select a.* from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "where 1=1 and b.chaifen!='1' and b.jianshao!='1' and a.manage_code = :code and a.creat_time < :date and fa_name like :strValue  Order by sys_id asc")
    Flux<FaCardMaster> findAllByManageCodeAndCreatTimeLessThanAndFaNameLikeOrderBySysId(String code, String date, String strValue);

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.sys_id,a.card_code,a.fa_name,a.cdate,a.creat_time,a.buy_time, " +
            "a.first_time,b.jzdqr_time,a.speci_type,a.zhejiu_type, e.ec_name as use_type,b.life,b.yuanzhi,b.ljzhejiu, " +
            "b.zhejiu_method,b.jingcanzhilv,f.ec_name as fa_class,b.yuezhejiue,b.yuezhejiulv,b.jiechu,b.jianshao,b.chaifen, " +
            "(select string_agg(d.dept_name,',') as dept_name from fa_change_dept c " +
            "left join sys_department d on c.dept_unique = d.unique_code " +
            "where a.card_unique=c.card_unique), " +
            "(select string_agg(d.project_name,',') as project_name from fa_change_project c " +
            "left join project d on c.project_unique = d.unique_code " +
            "where a.card_unique=c.card_unique), " +
            "(select count(*) as jiti_life from fa_depreciation c where a.card_unique=c.card_unique and c.iyear <= :iyear and c.imonth <= :imonth), " +
            "g.zj_by,g.zj_lj,g.zj_bn,b.shuliang,b.iyear,b.imonth,b.jingxiangshui,b.jinxiangshuilv " +
            "from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(c.iyear) from fa_change c where a.card_unique = c.card_unique and c.iyear <= :iyear) " +
            "and b.imonth = (select Max(c.imonth) from fa_change c where a.card_unique = c.card_unique and c.imonth <= :imonth) " +
            "left join fa_usage_status e on b.use_type=e.unique_code " +
            "left join fa_asset_type f on b.fa_class=f.unique_code " +
            "left join fa_depreciation g on a.card_unique=g.card_unique " +
            "and g.iyear = (select Max(c.iyear) from fa_depreciation c where a.card_unique=c.card_unique and c.iyear <= :iyear ) " +
            "and g.imonth = (select Max(c.imonth) from fa_depreciation c where a.card_unique=c.card_unique and c.imonth <= :imonth) " +
            "where a.manage_code=:manageCode and b.jianshao=:jianshao and b.chaifen=:chaifen and b.jiechu!=:jiechu and left(a.cdate,7) <= :cdate " +
            "order by a.sys_id ")
    Flux<FaCardVo> findCardList(String manageCode,String cdate, String jianshao, String chaifen, String jiechu, String iyear, String imonth);

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.sys_id,a.card_code,a.fa_name,a.cdate,a.creat_time,a.buy_time, " +
            "a.first_time,b.jzdqr_time,a.speci_type,a.zhejiu_type, e.ec_name as use_type,b.life,b.yuanzhi,b.ljzhejiu, " +
            "b.zhejiu_method,b.jingcanzhilv,f.ec_name as fa_class,b.yuezhejiue,b.yuezhejiulv,b.jiechu,b.jianshao,b.chaifen, " +
            "(select count(*) as jiti_life from fa_depreciation c where a.card_unique=c.card_unique and c.iyear <= :iyear and c.imonth <= :imonth), " +
            "g.zj_by,g.zj_lj,g.zj_bn,b.shuliang,b.iyear,b.imonth,b.jingxiangshui,b.jinxiangshuilv " +
            "from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(c.iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(c.imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "left join fa_usage_status e on b.use_type=e.unique_code " +
            "left join fa_asset_type f on b.fa_class=f.unique_code " +
            "left join fa_depreciation g on a.card_unique=g.card_unique " +
            "and g.iyear = (select Max(c.iyear) from fa_depreciation c where a.card_unique=c.card_unique and c.iyear <= :iyear ) " +
            "and g.imonth = (select Max(c.imonth) from fa_depreciation c where a.card_unique=c.card_unique and c.imonth <= :imonth) " +
            "where a.manage_code=:manageCode and b.jianshao=:jianshao and b.chaifen=:chaifen and b.jiechu!=:jiechu and left(a.cdate,7) < :cdate " +
            "and left(b.cdate,7) < :cdate " +
            "order by a.sys_id ")
    Flux<FaCardVo> findCardByLessCdateList(String manageCode,String cdate, String jianshao, String chaifen, String jiechu, String iyear, String imonth);

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.sys_id,a.card_code,a.fa_name,a.cdate,a.creat_time,a.buy_time, " +
            "a.first_time,b.jzdqr_time,a.speci_type,a.zhejiu_type, e.ec_name as use_type,b.life,b.yuanzhi,b.ljzhejiu, " +
            "b.zhejiu_method,b.jingcanzhilv,f.ec_name as fa_class,b.yuezhejiue,b.yuezhejiulv,b.jiechu,b.jianshao,b.chaifen, " +
            "(select count(*) as jiti_life from fa_depreciation c where a.card_unique=c.card_unique), " +
            "g.zj_by,g.zj_lj,g.zj_bn,b.shuliang,b.iyear,b.imonth,b.jingxiangshui,b.jinxiangshuilv " +
            "from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(c.iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(c.imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "left join fa_usage_status e on b.use_type=e.unique_code " +
            "left join fa_asset_type f on b.fa_class=f.unique_code " +
            "left join fa_depreciation g on a.card_unique=g.card_unique " +
            "and g.iyear = (select Max(c.iyear) from fa_depreciation c where a.card_unique=c.card_unique) " +
            "and g.imonth = (select Max(c.imonth) from fa_depreciation c where a.card_unique=c.card_unique) " +
            "where a.card_unique=:cardUnique " +
            "order by a.sys_id ")
    Mono<FaCardVo> findCardByCardUnique(String cardUnique);

    @Query("select a.unique_code,a.manage_code,a.card_unique,a.sys_id,a.card_code,a.fa_name,a.cdate,a.creat_time,a.buy_time, " +
            "a.first_time,b.jzdqr_time,a.speci_type,a.zhejiu_type, e.ec_name as use_type,b.life,b.yuanzhi,b.ljzhejiu, " +
            "b.zhejiu_method,b.jingcanzhilv,f.ec_name as fa_class,b.yuezhejiue,b.yuezhejiulv,b.jiechu,b.jianshao,b.chaifen, " +
            "(select count(*) as jiti_life from fa_depreciation c where a.card_unique=c.card_unique), " +
            "g.zj_by,g.zj_lj,g.zj_bn,b.shuliang,b.iyear,b.imonth,b.jingxiangshui,b.jinxiangshuilv " +
            "from fa_card_master a " +
            "left join fa_change b on a.card_unique = b.card_unique " +
            "and b.iyear = (select Max(c.iyear) from fa_change c where a.card_unique = c.card_unique) " +
            "and b.imonth = (select Max(c.imonth) from fa_change c where a.card_unique = c.card_unique) " +
            "left join fa_usage_status e on b.use_type=e.unique_code " +
            "left join fa_asset_type f on b.fa_class=f.unique_code " +
            "left join fa_depreciation g on a.card_unique=g.card_unique " +
            "and g.iyear = (select Max(c.iyear) from fa_depreciation c where a.card_unique=c.card_unique) " +
            "and g.imonth = (select Max(c.imonth) from fa_depreciation c where a.card_unique=c.card_unique) " +
            "where a.source_card_unique=:cardUnique " +
            "order by a.sys_id ")
    Flux<FaCardVo> findCardBySourceCardUnique(String cardUnique);

    Flux<FaCardMaster> findBySourceCardUniqueOrderByCardCode(String cardUnique);

}
