package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCurrentstock;
import org.boozsoft.domain.vo.stock.StockCurrentLackVo;
import org.boozsoft.domain.vo.stock.StockCurrentstockVo;
import org.boozsoft.domain.vo.stock.StockKcXCLVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface StockCurrentstockRepository extends ReactiveCrudRepository<StockCurrentstock, String> {

    @Query("select sc.*,u.username as lock_created_user_name from stock_currentstock sc left join _app_group_sys_user u on u.id=sc.lock_created_user")
    Flux<StockCurrentstockVo> findAlls();

    @Query("update stock_currentstock set revision=:revision,lock_created_user=:lockCreatedUser,lock_created_time=:lockCreatedTime,lock_reason=:lockReason where id=:id")
    Mono<Void> updateStockCurrentRevision(String id,Long revision,String lockCreatedUser,String lockCreatedTime,String lockReason);

    @Query("select acc.acc_name,acc.co_code,s.stock_num,s.stock_name,sc.* from stock_currentstock sc left join stock s on s.id = sc.inv_code left join _app_group_sys_account acc on acc.acc_id=left(sc.tenant_id,length(sc.tenant_id)-5) where sc.revision=:revision ")
    Flux<StockCurrentstockVo> findByRevison(Long revision);

    Flux<StockCurrentstock> findAllByIyearAndInvCodeIn(String year, List<String> sets);
    Flux<StockCurrentstock> findAllByIyearAndInvCodeInOrderByDvdateDesc(String year, List<String> sets);

    Flux<StockCurrentstock> findAllByIyearAndBatchIdIsNotNullOrderByInvCode(String year);
    Flux<StockCurrentstock> findAllByCwhcodeAndInvCodeAndIyear(String cwhcode, String code,String year);

    @Query("select * from stock_currentstock where id in (:ids)")
    Flux<StockCurrentstock> findAllByIds(List<String> ids);

    Flux<StockCurrentstock> findAllByCwhcodeAndIyear(String cwhcode, String year);

    @Query("select sc.* from stock_currentstock sc left join stock s on s.stock_num=sc.inv_code " +
            " where sc.cwhcode = :cwhcode and sc.iyear=:year and s.stock_class in (:arr) ")
    Flux<StockCurrentstock> findAllByCwhcodeAndIyearAndStockClass(String cwhcode, String year, String[] arr);

    @Query("select sc.* from stock_currentstock sc left join stock s on s.stock_num=sc.inv_code " +
            " where sc.cwhcode = :cwhcode and sc.iyear=:year and s.stock_num in (:arr) ")
    Flux<StockCurrentstock> findAllByCwhcodeAndIyearAndStockNum(String cwhcode, String year, String[] arr);


    @Query("update stock_currentstock set ztrk_quantity_cgdh=ztrk_quantity_cgdh-(:ztrk) where id=:id")
    Mono<Void> editByIdZTRK(String id, BigDecimal ztrk);

    @Query("update stock_currentstock set ztrk_quantity_cgrk=ztrk_quantity_cgrk-(:ztrk) where id=:id")
    Mono<Void> editByIdCgrk(String id, BigDecimal ztrk);
    @Query("update stock_currentstock set ztrk_quantity_qtrk=ztrk_quantity_qtrk-(:ztrk) where id=:id")
    Mono<Void> editByIdQtrk(String id, BigDecimal ztrk);

    @Query("update stock_currentstock set ztrk_quantity_cgdh=ztrk_quantity_cgdh+(:ztrk) where id=:id")
    Mono<Void> editByIdZTRKAdd(String id, BigDecimal ztrk);
    @Query("update stock_currentstock set ztrk_quantity_cgrk=ztrk_quantity_cgrk+(:ztrk) where id=:id")
    Mono<Void> editByIdCgrkAdd(String id, BigDecimal ztrk);
    @Query("update stock_currentstock set ztrk_quantity_qtrk=ztrk_quantity_qtrk+(:ztrk) where id=:id")
    Mono<Void> editByIdQtrkAdd(String id, BigDecimal ztrk);

    @Query("update stock_currentstock set base_quantity=base_quantity-(:xcl),sub_quantity1=sub_quantity1-(:xcl1),sub_quantity2=sub_quantity2-(:xcl2) where id=:id")
    Mono<Void> editByIdXCL(String id, BigDecimal xcl,BigDecimal xcl1,BigDecimal xcl2);

    @Query("select s.id                                                                      as stock_id,\n" +
            "       s.stock_num,\n" +
            "       s.stock_name,\n" +
            "       s.stock_ggxh,\n" +
            "       sc.iyear,\n" +
            "       sum(sc.base_quantity)                                                     as base_quantity,\n" +
            "       sum(cast(coalesce(sc.ztrk_quantity_cgdh, '0') as float))  as ztrk_quantity_cgdh,\n" +
            "       sum(cast(coalesce(sc.ztrk_quantity_cgrk, '0') as float))  as ztrk_quantity_cgrk,\n" +
            "       sum(cast(coalesce(sc.ztrk_quantity_qtrk, '0') as float))  as ztrk_quantity_qtrk,\n" +
            "       sum(cast(coalesce(sc.ztrk_quantity_ccprk, '0') as float)) as ztrk_quantity_ccprk,\n" +
            "       sum(cast(coalesce(sc.ztck_quantity_xhd, '0') as float))   as ztck_quantity_xhd,\n" +
            "       sum(cast(coalesce(sc.ztck_quantity_xsck, '0') as float))  as ztck_quantity_xsck,\n" +
            "       sum(cast(coalesce(sc.ztck_quantity_clly, '0') as float))  as ztck_quantity_clly,\n" +
            "       sum(cast(coalesce(sc.ztck_quantity_qtck, '0') as float))  as ztck_quantity_qtck,\n" +
            "       sum(cast(coalesce(sc.ztck_quantity_out, '0') as float))   as ztck_quantity_out\n" +
            "from stock_currentstock sc\n" +
            "         left join stock s on s.stock_num = sc.inv_code\n" +
            "where (base_quantity != 0 or ztrk_quantity_cgdh != 0 or\n" +
            "       ztrk_quantity_qtrk != 0 or\n" +
            "       ztrk_quantity_cgrk != 0 or ztrk_quantity_ccprk != 0 or ztck_quantity_xhd != 0 or\n" +
            "       ztck_quantity_xsck != 0 or ztck_quantity_clly != 0 or ztck_quantity_qtck != 0 or\n" +
            "       ztck_quantity_out != 0)\n" +
            "  and sc.batch_id is not null and sc.iyear = :year\n" +
            "group by sc.iyear, s.stock_name, s.stock_num, s.stock_ggxh, s.id\n" +
            "order by s.stock_num")
    Flux<StockKcXCLVo> findByStockXCLThanZero(String year);

    @Query("select base_quantity,\n" +
            "       (cast(coalesce(base_quantity, '0') as decimal) + cast(coalesce(ztrk_quantity, '0') as decimal)) -\n" +
            "       cast(coalesce(ztck_quantity, '0') as decimal)  as keyong\n" +
            "from stock_currentstock\n" +
            "where inv_code =:stockNum ")
    Mono<StockCurrentstockVo> findByXclAndKeyong(String stockNum);


    @Query("select sk.stock_num,\n" +
            "       sk.stock_name,\n" +
            "       sk.stock_ggxh,\n" +
            "       sk.stock_unit_name as unit_name,\n" +
            "       sc.batch_id,\n" +
            "       sc.dpdate,\n" +
            "       sc.dvdate,sc.iyear,sc.cwhcode,CAST(sc.base_quantity as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztrk_quantity_cgdh as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztrk_quantity_cgrk as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztrk_quantity_qtrk as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztrk_quantity_ccprk as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztrk_quantity_int as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztck_quantity_xhd as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztck_quantity_xsck as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztck_quantity_clly as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztck_quantity_qtck as DECIMAL(18, 2)),\n" +
            "       CAST(sc.ztck_quantity_out as DECIMAL(18, 2))\n" +
            "   from stock_currentstock sc\n" +
            "         left join stock sk on sk.stock_num = sc.inv_code")
    Flux<StockCurrentLackVo> findByStockNumVerifyLack();

    @Query("select * from stock_currentstock where inv_code in (:ids)")
    Flux<StockCurrentstock> findAllByinvCodes(List<String> ids);
}