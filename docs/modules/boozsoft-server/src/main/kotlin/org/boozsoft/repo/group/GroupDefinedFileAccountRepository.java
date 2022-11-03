package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDefinedFileAccount;
import org.boozsoft.domain.vo.GroupDefinedFileAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupDefinedFileAccountRepository extends ReactiveCrudRepository<GroupDefinedFileAccount, String> {

    Flux<GroupDefinedFileAccount> findByCtypeAndOriginIdAndUniqueCodeOrderById(String ctype,String originId,String uniqueCode);

    Flux<GroupDefinedFileAccount> findByCtypeAndTenantIdAndUniqueCodeOrderById(String ctype, String tenantId, String uniqueCode);

    Flux<GroupDefinedFileAccount> findByUniqueCodeOrderById(String uniqueCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.defined_code,b.defined_name, " +
            "b.shuxing,b.ctype as defined_ctype,b.num_weishu,b.scope,b.model " +
            "from _app_group_defined_file_account a " +
            "left join _app_group_defined_file b on a.unique_code=b.defined_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.origin_id = :originId " +
            "order by a.flag, b.defined_code ")
    Flux<GroupDefinedFileAccountVo> findAllByOriginId(String ctype, String originId);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.defined_code,b.defined_name, " +
            "b.shuxing,b.ctype as defined_ctype,b.num_weishu,b.scope,b.model " +
            "from _app_group_defined_file_account a " +
            "left join _app_group_defined_file b on a.unique_code=b.defined_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.tenant_id = :tenantId " +
            "order by a.flag, b.defined_code ")
    Flux<GroupDefinedFileAccountVo> findAllByTenantId(String ctype, String tenantId);

}
