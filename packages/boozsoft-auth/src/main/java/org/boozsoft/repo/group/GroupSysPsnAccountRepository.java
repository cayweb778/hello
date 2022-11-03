package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysPsnAccount;
import org.boozsoft.domain.vo.GroupSysPsnAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupSysPsnAccountRepository extends ReactiveCrudRepository<GroupSysPsnAccount, String> {

    Flux<GroupSysPsnAccount> findByCtypeAndOriginIdAndUniqueCodeOrderById(String ctype,String originId,String uniqueCode);
    Flux<GroupSysPsnAccount> findByCtypeAndTenantIdAndUniqueCodeOrderById(String ctype,String tenantId,String uniqueCode);
    Flux<GroupSysPsnAccount> findByUniqueCodeOrderById(String uniqueCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.psn_code,b.psn_name,b.psn_sex, " +
            "b.unique_psn_type,b.psn_type,b.cell_phone_num,b.flag as psn_flag,b.success_state " +
            "from _app_group_sys_psn_account a " +
            "left join _app_group_sys_psn b on a.unique_code=b.unique_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.origin_id = :originId " +
            "order by a.flag, b.psn_code ")
    Flux<GroupSysPsnAccountVo> findAllByOriginId(String ctype, String originId);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.psn_code,b.psn_name,b.psn_sex,b.unique_psn_type, " +
            "b.unique_psn_type,b.psn_type,b.cell_phone_num,b.flag as psn_flag,b.success_state " +
            "from _app_group_sys_psn_account a " +
            "left join _app_group_sys_psn b on a.unique_code=b.unique_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.tenant_id = :tenantId " +
            "order by a.flag, b.psn_code ")
    Flux<GroupSysPsnAccountVo> findAllByTenantId(String ctype, String tenantId);

}
