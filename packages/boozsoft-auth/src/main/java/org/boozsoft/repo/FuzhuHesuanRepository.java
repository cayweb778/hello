package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.FuzhuHesuan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FuzhuHesuanRepository extends ReactiveCrudRepository<FuzhuHesuan, String> {

    Flux<FuzhuHesuan> findAllByOrderByCcode(Pageable pageable);

    Flux<FuzhuHesuan> findByFlagOrderByCcode(String flag);

    Mono<Long> countAllBy();

    Flux<FuzhuHesuan> findByCcodeOrderByCcode(String ccode);

    Flux<FuzhuHesuan> findByCnameOrderByCcode(String cname);

    Flux<FuzhuHesuan> findByCankaoDuixiangOrderByCcode(String cankaoDuixiang);

    @Query("select * from fuzhu_hesuan where tenant_id=:tenantid ")
    Flux<FuzhuHesuan> findByTenantId(String tenantid);

    /**
     * 获取最大的编码
     */
    @Query("select Max(ccode) as ccode from fuzhu_hesuan ")
    Mono<FuzhuHesuan> findMaxCcode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(ccode as int))+1 as ccode from fuzhu_hesuan " +
            "where cast(ccode as int) <(select MIN(cast(ccode as int)) as minbreak " +
            "from(select ccode,ROW_NUMBER() over(order by cast(ccode as int)) as sort " +
            "from (select distinct cast(ccode as int) from fuzhu_hesuan) temp1) temp2 " +
            "where cast(ccode as int) <> sort) ")
    Mono<FuzhuHesuan> findBukongCcode();

    Mono<FuzhuHesuan> findByDefinedCode(String definedCode);
    Mono<FuzhuHesuan> findByCfield(String cfield);
}
