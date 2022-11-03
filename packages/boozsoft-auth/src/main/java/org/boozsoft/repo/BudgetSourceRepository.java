package org.boozsoft.repo;

import org.boozsoft.domain.entity.BudgetSource;
import org.boozsoft.domain.entity.group.GroupBudgetSource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BudgetSourceRepository extends ReactiveCrudRepository<BudgetSource, String> {

    @Query("select * from budget_source where is_del = '0' order by create_date")
    Flux<BudgetSource> findAllByOrderById(Pageable pageable);
    Mono<BudgetSource> findByBsCodeOrderById(String code);
    Mono<BudgetSource> findByBsNameOrderById(String bsName);
    Flux<BudgetSource> findByFlagOrderById(String flag);

    @Query("select * from budget_source where is_del = '1' order by create_date")
    Flux<BudgetSource> findAllByIsDel();

    @Query("delete  from budget_source  where id in (:ids)")
    Mono<Void> deleteAllByIds(List ids);

    @Query("update budget_source set is_del = '0' where id in (:ids)")
    Mono<Void>  updateIsDel(List ids);

    @Query("select * from budget_source where is_del = '0' order by create_date")
    Flux<BudgetSource> findAllByIsDelAndOrderById();
}
