package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.DefinedRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DefinedRecordRepository extends ReactiveCrudRepository<DefinedRecord, String> {

    Flux<DefinedRecord> findByDefinedCodeOrderById(Pageable pageable,String definedCode);
    Mono<Long> countByDefinedCode(String definedCode);
    Flux<DefinedRecord> findByDefinedCodeOrderById(String definedCode);
    Flux<DefinedRecord> findByDefinedCodeAndFlagOrderById(String definedCode,String flag);
    Flux<DefinedRecord> findByDefinedCodeAndRecordCodeOrderById(String definedCode,String recordCode);
    Flux<DefinedRecord> findByDefinedCodeAndRecordNameOrderById(String definedCode,String recordName);

    /**
     * 获取最大的编码
     */
    @Query("select Max(record_code) as record_code from defined_record where defined_code=:definedCode ")
    Mono<DefinedRecord> findMaxRecordCode(String definedCode);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(record_code as int))+1 as record_code from defined_record " +
            "where defined_code=:definedCode and cast(record_code as int) <(select MIN(cast(record_code as int)) as minbreak " +
            "from(select record_code,ROW_NUMBER() over(order by cast(record_code as int)) as sort " +
            "from (select distinct cast(record_code as int) from defined_record where defined_code=:definedCode ) temp1) temp2 " +
            "where cast(record_code as int) <> sort) ")
    Mono<DefinedRecord> findBukongRecordCode(String definedCode);

}
