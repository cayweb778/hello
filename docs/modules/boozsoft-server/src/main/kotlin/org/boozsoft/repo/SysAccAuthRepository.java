package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysAccAuth;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysAccAuthRepository extends ReactiveCrudRepository<GroupSysAccAuth, String> {

    Flux<GroupSysAccAuth> findAllByUserNumOrderByIyearDescAccIdAsc(String userId);


    @Query(value = "select code_num from sys_acc_auth_code where 1=1 and acc_id=:accId and iyear=:year and user_num=:userId order by code_num Asc")
    Flux<String>  findAllCodesByCondition(String userId, String accId, String year);


    @Query(value = "select accvocher_type from sys_acc_type_auth where 1=1 and acc_id=:accId and iyear=:year and user_num=:userId")
    Flux<String> findAllVoucherTypesByCondition(String userId,String accId,String year);


    @Query(value = "select * from _app_group_sys_acc_auth where 1=1 and acc_id=:accId and iyear=:year and user_num=:userId")
    Flux<GroupSysAccAuth>  findAllByUserIdAmdAccIdAndYear(String userId, String accId, String year);

    Flux<GroupSysAccAuth> findAllByAccIdAndIyear(String accId, String year);

    Flux<GroupSysAccAuth> findAllByUserNumAndDefaultLogin(String userId,String one);

    Mono<GroupSysAccAuth> findFirstByAccIdAndUserNumOrderByIyearDesc(String accId, String userId);

    Flux<GroupSysAccAuth> findAllByAccId(String accId);

//    @Query(value = "select * from _app_group_sys_acc_auth where acc_id=:accId and iyear=:year and user_num=:userId")
    Flux<GroupSysAccAuth> findByUserNumAndAccIdAndIyear(String userId, String accId, String year);

     Mono<Void> deleteAllByAccId(String accId);
}
