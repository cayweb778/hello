package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupFaAccount;
import org.boozsoft.domain.vo.group.GroupFaAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

public interface GroupFaAccountRepository extends ReactiveCrudRepository<GroupFaAccount, String> {
    Mono<Long> countAllBy();

    Flux<GroupFaAccount> findAllByCoCode(String code);
    Flux<GroupFaAccount> findAllByFaAccNameAndUniqueCode(String name,String accId);
    Flux<GroupFaAccount> findAllByIdInOrderByCoCodeAsc(HashSet<String> ids);
    Flux<GroupFaAccount> findAllByCoCodeInOrderByCoCodeAsc(List<String> ids);
    Flux<GroupFaAccount> findAllByUniqueCodeIn(List<String> ids);

    Mono<Long> countAllByUniqueCode(String accId);

    @Query("select fa.*,zm.zj_name as zjname, sc.currency_zh_cn_name as cname from _app_group_fa_account fa " +
            " left join _app_group_fa_zhejiu_method  zm on zm.id = fa.zhejiu_code" +
            " left join _app_group_sys_currency sc on sc.abbreviation = fa.currency_type " +
            " where fa.unique_code = :uniqueCode ")
    Mono<GroupFaAccountVo> findByUniqueCode(String uniqueCode);

    Flux<GroupFaAccount> findByUniqueCodeOrderByFaAccId(String uniqueCode);

    @Query("select distinct fa.id, fa.unique_code,fa.fa_acc_id, fa.fa_acc_name  from _app_group_fa_account fa ")
    Flux<GroupFaAccount> findAllList();

    @Query("select * from _app_group_fa_account where acc_id =:accId limit 1")
    Mono<GroupFaAccount> findFirstByAccIdOrderByAccId(String accId);

}
