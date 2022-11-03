package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.BudgetSource;
import org.boozsoft.domain.entity.group.GroupBudgetSource;
import org.boozsoft.domain.entity.group.GroupSysSettModes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupBudgetSourceRepository extends ReactiveCrudRepository<GroupBudgetSource, String> {

    @Query("select * from _app_group_budget_source where is_del = '0' order by create_date")
    Flux<GroupBudgetSource> findAllByOrderById(Pageable pageable);
    Mono<GroupBudgetSource> findByBsCodeOrderById(String code);
    Mono<GroupBudgetSource> findByBsNameOrderById(String bsName);
    Flux<GroupBudgetSource> findByFlagOrderById(String flag);


    @Query("select * from _app_group_budget_source where is_del = '1' order by create_date")
    Flux<GroupBudgetSource> findAllByIsDel();

    @Query("delete  from _app_group_budget_source  where id in (:ids)")
    Mono<Void> deleteAllByIds(List ids);

    @Query("update _app_group_budget_source set is_del = '0' where id in (:ids)")
    Mono<Void>  updateIsDel(List ids);
}
