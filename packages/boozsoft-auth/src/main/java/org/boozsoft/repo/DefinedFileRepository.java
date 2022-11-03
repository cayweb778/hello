package org.boozsoft.repo;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.account.DefinedFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DefinedFileRepository extends ReactiveCrudRepository<DefinedFile, String> {

    Flux<DefinedFile> findAllByOrderById(Pageable pageable);
    @Query("select * from defined_file order by flag desc,defined_code asc")
    Flux<DefinedFile> findAllByOrderByDefinedCode();
    @Query("select * from defined_file where scope = :scope order by flag desc,defined_code asc")
    Flux<DefinedFile> findByScopeOrderByDefinedCode(String scope);
    @Query("select * from defined_file where scope = :scope and model = :model order by flag desc,defined_code asc")
    Flux<DefinedFile> findByScopeAndModelOrderByDefinedCode(@Param("scope") String scope, @Param("model") String model);
    Flux<DefinedFile> findByFlagOrderById(String flag);
    Mono<Long> countAllBy();
    Flux<DefinedFile> findByDefinedCodeOrderById(String definedCode);
    Flux<DefinedFile> findByDefinedNameOrderById(String definedName);

    /**
     * 获取最大的编码
     */
    @Query("select Max(defined_code) as defined_code from defined_file ")
    Mono<DefinedFile> findMaxDefinedCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(defined_code as int))+1 as defined_code from defined_file " +
            "where cast(defined_code as int) <(select MIN(cast(defined_code as int)) as minbreak " +
            "from(select defined_code,ROW_NUMBER() over(order by cast(defined_code as int)) as sort " +
            "from (select distinct cast(defined_code as int) from defined_file) temp1) temp2 " +
            "where cast(defined_code as int) <> sort) ")
    Mono<DefinedFile> findBukongDefinedCode();

    @Query("select * from defined_file where scope = '1' or model = :model and flag = '1' order by defined_code asc")
    Flux<DefinedFile> findByModelOrderByDefinedCode(String model);

}
