package org.boozsoft.repo.origin;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.origin.OriginDefinedFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginDefinedFileRepository extends ReactiveCrudRepository<OriginDefinedFile, String> {

    Flux<OriginDefinedFile> findAllByOriginIdOrderById(Pageable pageable,String originId);
    @Query("select * from _app_origin_defined_file where origin_id=:originId order by flag desc,defined_code asc")
    Flux<OriginDefinedFile> findAllByOriginIdOrderByDefinedCode(String originId);
    @Query("select * from _app_origin_defined_file where scope = :scope and origin_id=:originId order by flag desc,defined_code asc")
    Flux<OriginDefinedFile> findByScopeAndOriginIdOrderByDefinedCode(String scope,String originId);
    @Query("select * from _app_origin_defined_file where scope = :scope and model = :model and origin_id=:originId order by flag desc,defined_code asc")
    Flux<OriginDefinedFile> findByScopeAndModelAndOriginIdOrderByDefinedCode(@Param("scope") String scope, @Param("model") String model, @Param("originId") String originId);
    Flux<OriginDefinedFile> findByFlagAndOriginIdOrderById(String flag,String originId);
    Mono<Long> countAllByOriginId(String originId);
    Flux<OriginDefinedFile> findByDefinedCodeAndOriginIdOrderById(String definedCode,String originId);
    Flux<OriginDefinedFile> findByDefinedNameAndOriginIdOrderById(String definedName,String originId);

    /**
     * 获取最大的编码
     */
    @Query("select Max(cast(defined_code as int)) as defined_code from _app_origin_defined_file where origin_id=:originId ")
    Mono<OriginDefinedFile> findMaxDefinedCode(String originId);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(defined_code as int)+1) as defined_code from _app_origin_defined_file t1 " +
            "where origin_id=:originId and not exists(select * from _app_origin_defined_file t2 " +
            "where origin_id=:originId and cast(t2.defined_code as int) = cast(t1.defined_code as int) + 1) ")
    Mono<OriginDefinedFile> findBukongDefinedCode(String originId);

    @Query("select * from _app_origin_defined_file where scope = '1' or model = :model and flag = '1' and origin_id=:originId order by defined_code asc")
    Flux<OriginDefinedFile> findByModelOrderByDefinedCode(String model,String originId);

}
