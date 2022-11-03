package org.boozsoft.repo.origin;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.origin.OriginDefinedRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginDefinedRecordRepository extends ReactiveCrudRepository<OriginDefinedRecord, String> {

    Flux<OriginDefinedRecord> findByDefinedCodeAndOriginIdOrderById(Pageable pageable,String definedCode,String originId);
    Mono<Long> countByDefinedCodeAndOriginId(String definedCode,String originId);
    Flux<OriginDefinedRecord> findByDefinedCodeAndOriginIdOrderById(String definedCode,String originId);
    Flux<OriginDefinedRecord> findByDefinedCodeAndFlagAndOriginIdOrderById(String definedCode,String flag,String originId);
    Flux<OriginDefinedRecord> findByDefinedCodeAndRecordCodeAndOriginIdOrderById(String definedCode,String recordCode,String originId);
    Flux<OriginDefinedRecord> findByDefinedCodeAndRecordNameAndOriginIdOrderById(String definedCode,String recordName,String originId);

    /**
     * 获取最大的编码
     */
    @Query("select Max(record_code) as record_code from _app_origin_defined_record where defined_code=:definedCode ")
    Mono<OriginDefinedRecord> findMaxRecordCode(@Param("definedCode") String definedCode, @Param("originId") String originId);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(record_code as int)+1) as record_code from _app_origin_defined_record t1 " +
            "where defined_code=:definedCode and not exists(select * from _app_origin_defined_record t2 " +
            "where defined_code=:definedCode and cast(t2.record_code as int) = cast(t1.record_code as int) + 1) ")
    Mono<OriginDefinedRecord> findBukongRecordCode(@Param("definedCode") String definedCode, @Param("originId") String originId);

}
