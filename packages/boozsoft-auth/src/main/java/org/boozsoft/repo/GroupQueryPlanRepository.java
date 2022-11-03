package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupQueryPlan;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupQueryPlanRepository extends ReactiveCrudRepository<GroupQueryPlan, String> {
    Mono<GroupQueryPlan> findFirstByAccountIdAndOwningMenuNameAndOwningPlanAndPlanPerson(String parm1,String parm2,String parm3,String parm4);
    Mono<GroupQueryPlan> findByAccountIdAndOwningMenuName(String accId,String menuName);
    @Query(" delete from _app_group_query_plan WHERE owning_menu_name=:menuName ")
    Mono<Void> delByMenuName(String menuName);
}
