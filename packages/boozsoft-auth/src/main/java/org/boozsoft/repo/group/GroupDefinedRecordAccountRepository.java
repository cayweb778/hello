package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupDefinedFileAccount;
import org.boozsoft.domain.entity.group.GroupDefinedRecordAccount;
import org.boozsoft.domain.vo.GroupDefinedRecordAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupDefinedRecordAccountRepository extends ReactiveCrudRepository<GroupDefinedRecordAccount, String> {

    Flux<GroupDefinedRecordAccount> findByCtypeAndOriginIdAndUniqueCodeAndDefinedCodeOrderById(String ctype,String originId,String uniqueCode,String definedCode);

    Flux<GroupDefinedRecordAccount> findByCtypeAndTenantIdAndUniqueCodeAndDefinedCodeOrderById(String ctype, String tenantId, String uniqueCode,String definedCode);

    Flux<GroupDefinedRecordAccount> findByUniqueCodeAndDefinedCodeOrderById(String uniqueCode,String definedCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,a.defined_code,b.record_code,b.record_name " +
            "from _app_group_defined_record_account a " +
            "left join _app_group_defined_record b on a.unique_code=b.record_code and a.defined_code=b.defined_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.origin_id = :originId and b.defined_code=:definedCode " +
            "order by a.flag, b.record_code ")
    Flux<GroupDefinedRecordAccountVo> findAllByOriginId(String ctype, String originId,String definedCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,a.defined_code,b.record_code,b.record_name " +
            "from _app_group_defined_record_account a " +
            "left join _app_group_defined_record b on a.unique_code=b.record_code and a.defined_code=b.defined_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.tenant_id = :tenantId and b.defined_code=:definedCode " +
            "order by a.flag, b.record_code ")
    Flux<GroupDefinedRecordAccountVo> findAllByTenantId(String ctype, String tenantId,String definedCode);

}
