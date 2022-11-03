package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCangkuLevelRecord;
import org.boozsoft.domain.vo.stock.StockCangkuLevelRecordVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockCangkuLevelRecordRepository extends ReactiveCrudRepository<StockCangkuLevelRecord, String> {

    @Query("select id,\n" +
            "       acc_id,\n" +
            "       cangku_id,\n" +
            "       cast(sys_num as integer),\n" +
            "       record_num,\n" +
            "       record_name,\n" +
            "       record_explain,\n" +
            "       record_default,\n" +
            "       record_flag,\n" +
            "       cangku_level_id,\n" +
            "       cangku_level_order,\n" +
            "       parent_id,\n" +
            "       parent_level_num\n" +
            "from stock_cangku_level_record\n" +
            "where cangku_id = :cangkuId and cangku_level_id=:cangkuLevelId and cangku_level_order=:levelOrder \n" +
            "order by sys_num")
    Flux<StockCangkuLevelRecordVo> findAllByCangkuIdAndCangkuLevelIdAndCangkuLevelOrderOrderBySysNum(String cangkuId, String cangkuLevelId, String levelOrder);

    @Query("select id,\n" +
            "       acc_id,\n" +
            "       cangku_id,\n" +
            "       cast(sys_num as integer),\n" +
            "       record_num,\n" +
            "       record_name,\n" +
            "       record_explain,\n" +
            "       record_default,\n" +
            "       record_flag,\n" +
            "       cangku_level_id,\n" +
            "       cangku_level_order,\n" +
            "       parent_id,\n" +
            "       parent_level_num\n" +
            "from stock_cangku_level_record\n" +
            "where cangku_id = :cangkuId and cangku_level_order=:levelOrder and parent_id=:parentId \n" +
            "order by sys_num")
    Flux<StockCangkuLevelRecordVo> findAllByCangkuIdAndCangkuLevelOrderAndParentId(String cangkuId,String levelOrder,String parentId);

    @Query("select id,\n" +
            "       acc_id,\n" +
            "       cangku_id,\n" +
            "       cast(sys_num as integer),\n" +
            "       record_num,\n" +
            "       record_name,\n" +
            "       record_explain,\n" +
            "       record_default,\n" +
            "       record_flag,\n" +
            "       cangku_level_id,\n" +
            "       cangku_level_order,\n" +
            "       parent_id,\n" +
            "       parent_level_num\n" +
            "from stock_cangku_level_record\n" +
            "where parent_id = :parentId \n" +
            "order by sys_num")
    Flux<StockCangkuLevelRecordVo> findAllByParentIdOrderBySysNum(String parentId);

    @Query("select id,\n" +
            "       acc_id,\n" +
            "       cangku_id,\n" +
            "       cast(sys_num as integer),\n" +
            "       record_num,\n" +
            "       record_name,\n" +
            "       record_explain,\n" +
            "       record_default,\n" +
            "       record_flag,\n" +
            "       cangku_level_id,\n" +
            "       cangku_level_order,\n" +
            "       parent_id,\n" +
            "       parent_level_num\n" +
            "from stock_cangku_level_record\n" +
            "where cangku_id = :cangkuId and cangku_level_order=:levelOrder \n" +
            "order by sys_num")
    Flux<StockCangkuLevelRecordVo> findAllByCangkuIdAndCangkuLevelOrder(String cangkuId,String levelOrder);

    @Query("update stock_cangku_level_record set record_flag=:flag where id in (:id) ")
    Mono<Void> editByflag(String flag, List<String> id);

    @Query("update stock_cangku_level_record set record_default=:flag where id=:id ")
    Mono<Void> editBydefaultflag(String flag, String id);

    @Query("update stock_cangku_level_record set record_explain=:recordExplain where id=:id ")
    Mono<Void> editByExpalin(String recordExplain, String id);

    Mono<StockCangkuLevelRecord> findById(String id);

    @Query("delete from stock_cangku_level_record where cangku_id in (:cangkuId) ")
    Mono<Void> deleteByCangkuId(List<String> cangkuId);

    @Query("select sc.ck_num as cangku_num,sc.ck_name as cangku_name,sclr.* from stock_cangku_level_record sclr left join stock_cangku sc on sc.id=sclr.cangku_id")
    Flux<StockCangkuLevelRecordVo> findAllRecord();

    @Query("update stock_cangku_level_record set record_bend=:recordBend where id in (:id) ")
    Mono<Void> editByBend(String recordBend, List<String> id);


    @Query("select id,\n" +
            "       acc_id,\n" +
            "       cangku_id,\n" +
            "       cast(sys_num as integer),\n" +
            "       record_num,\n" +
            "       record_name,\n" +
            "       record_explain,\n" +
            "       record_default,\n" +
            "       record_flag,\n" +
            "       cangku_level_id,\n" +
            "       cangku_level_order,\n" +
            "       parent_id,\n" +
            "       parent_level_num\n" +
            "from stock_cangku_level_record\n" +
            "where cangku_id = :cangkuId and parent_id=:parentId and record_bend=:recordBend \n" +
            "order by sys_num")
    Flux<StockCangkuLevelRecordVo> findAllByRecordBend(String cangkuId,String parentId,String recordBend);
}
