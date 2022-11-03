package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysPsnTypeAccount;
import org.boozsoft.domain.vo.GroupSysPsnTypeAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysPsnTypeAccountRepository extends ReactiveCrudRepository<GroupSysPsnTypeAccount, String> {

    Flux<GroupSysPsnTypeAccount> findByCtypeAndOriginIdAndUniqueCodeOrderById(String ctype,String originId,String uniqueCode);

    Flux<GroupSysPsnTypeAccount> findByCtypeAndTenantIdAndUniqueCodeOrderById(String ctype, String tenantId, String uniqueCode);

    Flux<GroupSysPsnTypeAccount> findByUniqueCodeOrderById(String uniqueCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.psn_type_code,b.psn_type_name " +
            "from _app_group_sys_psn_type_account a " +
            "left join _app_group_sys_psn_type b on a.unique_code=b.unique_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.origin_id = :originId " +
            "order by a.flag, b.psn_type_code ")
    Flux<GroupSysPsnTypeAccountVo> findAllByOriginId(String ctype, String originId);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.psn_type_code,b.psn_type_name " +
            "from _app_group_sys_psn_type_account a " +
            "left join _app_group_sys_psn_type b on a.unique_code=b.unique_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.tenant_id = :tenantId " +
            "order by a.flag, b.psn_type_code ")
    Flux<GroupSysPsnTypeAccountVo> findAllByTenantId(String ctype, String tenantId);

}
