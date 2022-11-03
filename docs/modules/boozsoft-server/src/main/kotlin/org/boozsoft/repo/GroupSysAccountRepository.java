package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.domain.vo.AccCount2Vo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysAccountRepository extends ReactiveCrudRepository<GroupSysAccount,String> {


    Flux<GroupSysAccount> findAllByOrderById(Pageable pageable);
    Flux<GroupSysAccount> findAllByOrderByIdAsc();

    @Query("select a.id,a.acc_id,a.acc_name,b.user_id as beiyong1 from _app_group_sys_account a left join sys_user_account b on a.id=b.account_id and user_id=:userId ")
    Flux<GroupSysAccount> findByUserAccount(Long userId);

    Mono<GroupSysAccount> findFirstByAccIdOrderByAccStartDateDesc(String accId);

    @Query("select * from _app_group_sys_account where acc_id =:accId limit 1")
    Mono<GroupSysAccount> findFirstByAccIdOrderByAccId(String accId);
    Mono<GroupSysAccount> findByAccId(String accontId);

    Mono<Long> countAllBy();

    Flux<GroupSysAccount> findAllByAccGroup(String code);
    Flux<GroupSysAccount> findAllByAccGroupIn(List<String> code);

    @Query("select * from _app_group_sys_account where 1=1 and (acc_group is null or acc_group = '')")
    Flux<GroupSysAccount> findAllByAccGroupIsNull();

    Flux<GroupSysAccount> findAllByAccNameCn(String orgCode);
    Flux<GroupSysAccount> findAllByAccNameFull(String orgCode);
    Flux<GroupSysAccount> findAllByCoCode(String orgCode);
    Flux<GroupSysAccount> findAllByUniqueCodeAndCoCodeLike(String code,String orgCode);

    Flux<GroupSysAccount> findAllByAccIdLikeOrderByAccIdDesc(String code);
    Flux<GroupSysAccount> findAllByFlagOrderByCoCodeAsc(String flag);

    Mono<Long> countAllByAccGroup(String code);
    Mono<Long> countAllByCorpCodeAndFlagNotOrUniqueCodeAndFlagNot(String code,String flag,String code2,String flag2);
    Mono<Long> countAllByUniqueCode(String code);
    Mono<Long> countAllByAccStandard(String accStandard);

    Flux<GroupSysAccount> findAllByFlagAndCoCodeInOrderByCoCodeAsc(String s, List<String> ids);



    @Query("select t.unique_acc_standard, t.id as template_id, a.ccode_level, a.independent,s.acc_style_unique,a.ibudget_accounting,a.acc_group\n" +
            "from _app_group_sys_account a\n" +
            "         left join _app_group_acc_template t on a.acc_standard = t.id\n" +
            "left join _app_group_acc_standard s on s.unique_acc_standard=t.unique_acc_standard\n" +
            "where a.acc_id =:accId")
    Mono<AccCount2Vo> findAllByAccCountPartColumn(String accId);

    Flux<GroupSysAccount> findAllByaccGroup(String groupId);

}
