package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDefinedRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupDefinedRecordRepository extends ReactiveCrudRepository<GroupDefinedRecord, String> {

    Flux<GroupDefinedRecord> findByDefinedCodeOrderById(Pageable pageable,String definedCode);
    Mono<Long> countByDefinedCode(String definedCode);
    Flux<GroupDefinedRecord> findByDefinedCodeOrderById(String definedCode);
    Flux<GroupDefinedRecord> findByDefinedCodeAndFlagOrderById(String definedCode,String flag);
    Flux<GroupDefinedRecord> findByDefinedCodeAndRecordCodeOrderById(String definedCode,String recordCode);
    Flux<GroupDefinedRecord> findByDefinedCodeAndRecordNameOrderById(String definedCode,String recordName);

    /**
     * 获取最大的编码
     */
    @Query("select Max(record_code) as record_code from _app_group_defined_record where defined_code=:definedCode ")
    Mono<GroupDefinedRecord> findMaxRecordCode(String definedCode);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(record_code as int)+1) as record_code from _app_group_defined_record t1 " +
            "where defined_code=:definedCode and not exists(select * from _app_group_defined_record t2 " +
            "where defined_code=:definedCode and cast(t2.record_code as int) = cast(t1.record_code as int) + 1) ")
    Mono<GroupDefinedRecord> findBukongRecordCode(String definedCode);

}
