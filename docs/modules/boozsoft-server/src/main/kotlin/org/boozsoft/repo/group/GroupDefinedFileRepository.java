package org.boozsoft.repo.group;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.group.GroupDefinedFile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupDefinedFileRepository extends ReactiveCrudRepository<GroupDefinedFile, String> {

    Flux<GroupDefinedFile> findAllByOrderById(Pageable pageable);
    @Query("select * from _app_group_defined_file order by flag desc,defined_code asc")
    Flux<GroupDefinedFile> findAllByOrderByDefinedCode();
    @Query("select * from _app_group_defined_file where scope = :scope order by flag desc,defined_code asc")
    Flux<GroupDefinedFile> findByScopeOrderByDefinedCode(String scope);
    @Query("select * from _app_group_defined_file where scope = :scope and model = :model order by flag desc,defined_code asc")
    Flux<GroupDefinedFile> findByScopeAndModelOrderByDefinedCode(@Param("scope") String scope, @Param("model") String model);
    Flux<GroupDefinedFile> findByFlagOrderById(String flag);
    Mono<Long> countAllBy();
    Flux<GroupDefinedFile> findByDefinedCodeOrderById(String definedCode);
    Flux<GroupDefinedFile> findByDefinedNameOrderById(String definedName);

    /**
     * 获取最大的编码
     */
    @Query("select Max(cast(defined_code as int)) as defined_code from _app_group_defined_file ")
    Mono<GroupDefinedFile> findMaxDefinedCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(defined_code as int)+1) as defined_code from _app_group_defined_file t1 " +
            "where not exists(select * from _app_group_defined_file t2 " +
            "where cast(t2.defined_code as int) = cast(t1.defined_code as int) + 1) ")
    Mono<GroupDefinedFile> findBukongDefinedCode();

    @Query("select * from _app_group_defined_file where scope = '1' or model = :model and flag = '1' order by defined_code asc")
    Flux<GroupDefinedFile> findByModelOrderByDefinedCode(String model);

}
