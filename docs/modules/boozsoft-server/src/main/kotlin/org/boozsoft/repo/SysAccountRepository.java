package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.SysAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysAccountRepository extends ReactiveCrudRepository<SysAccount,String> {


    Flux<SysAccount> findAllByOrderById(Pageable pageable);
    Flux<SysAccount> findAllByOrderByIdAsc();

    @Query("select a.id,a.acc_id,a.acc_name,b.user_id as beiyong1 from sys_account a left join sys_user_account b on a.id=b.account_id and user_id=:userId ")
    Flux<SysAccount> findByUserAccount(Long userId);

    Mono<SysAccount> findFirstByAccIdOrderByAccStartDateDesc(String accId);

    @Query("select * from sys_account where acc_id =:accId limit 1")
    Mono<SysAccount> findFirstByAccIdOrderByAccId(String accId);
    Mono<SysAccount> findByAccId(String accontId);

    Mono<Void> deleteByAccId(String accontId);

    Mono<Long> countAllBy();

    Flux<SysAccount> findAllByAccGroup(String code);

    @Query("select * from _app_group_sys_account where 1=1 and (acc_group is null or acc_group = '')")
    Flux<SysAccount> findAllByAccGroupIsNull();

    Flux<SysAccount> findAllByAccNameCn(String orgCode);
    Flux<SysAccount> findAllByCoCode(String orgCode);

    Flux<SysAccount> findAllByAccIdLikeOrderByAccIdDesc(String code);
    Flux<SysAccount> findAllByFlagOrderByIdAscCoCodeAsc(String flag);

    Mono<Long> countAllByAccGroup(String code);
    Mono<Long> countAllByCorpCodeAndFlagNot(String code,String flag);

}
