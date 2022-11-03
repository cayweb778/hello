package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysExpense;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysExpenseRepository extends ReactiveCrudRepository<SysExpense, String> {

    Flux<SysExpense> findByFlagOrderById(String flag);
    Flux<SysExpense> findByCnameOrderById(String cname);
    Flux<SysExpense> findByCcodeOrderById(String ccode);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(ccode as int)+1) as ccode from sys_expense t1 " +
            "where not exists(select * from sys_expense t2 where cast(t2.ccode as int) = cast(t1.ccode as int) + 1) ")
    Mono<SysExpense> findBukongCcode();
    /**
     * 获取最大的编码
     */
    @Query("select Max(cast(ccode as int)) as ccode from sys_expense ")
    Mono<SysExpense> findMaxCcode();

}
