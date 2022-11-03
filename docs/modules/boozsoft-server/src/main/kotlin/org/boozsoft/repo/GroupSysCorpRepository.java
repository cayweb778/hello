package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupSysCorp;
import org.boozsoft.domain.entity.group.GroupSysCorp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysCorpRepository extends ReactiveCrudRepository<GroupSysCorp,String> {


    Flux<GroupSysCorp> findAllByOrderById(Pageable pageable);
    Flux<GroupSysCorp> findAllByOrderByIdAsc();

    @Query("select a.id,a.acc_id,a.acc_name,b.user_id as beiyong1 from _app_group_sys_account a left join sys_user_account b on a.id=b.account_id and user_id=:userId ")
    Flux<GroupSysCorp> findByUserAccount(Long userId);



    Mono<Long> countAllBy();

    Flux<GroupSysCorp> findAllByAccGroup(String code);
    Flux<GroupSysCorp> findAllByAccGroupIn(List<String> codes);

    @Query("select * from _app_group_sys_account where 1=1 and (acc_group is null or acc_group = '')")
    Flux<GroupSysCorp> findAllByAccGroupIsNull();

    Flux<GroupSysCorp> findAllByAccNameCn(String orgCode);
    Flux<GroupSysCorp> findAllByCoCode(String orgCode);


    Mono<Long> countAllByAccGroup(String code);
    Mono<Long> countAllByCorpCode(String code);
}
