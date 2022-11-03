package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupProjectItemAccount;
import org.boozsoft.domain.vo.GroupProjectItemAccountVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupProjectItemAccountRepository extends ReactiveCrudRepository<GroupProjectItemAccount, String> {

    Flux<GroupProjectItemAccount> findByCtypeAndOriginIdAndUniqueCodeOrderById(String ctype,String originId,String uniqueCode);
    Flux<GroupProjectItemAccount> findByCtypeAndTenantIdAndUniqueCodeOrderById(String ctype,String tenantId,String uniqueCode);
    Flux<GroupProjectItemAccount> findByUniqueCodeOrderById(String uniqueCode);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.item_code,b.item_name,b.cate_code " +
            "from _app_group_project_item_account a " +
            "left join _app_group_project_item b on a.unique_code=b.item_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.origin_id = :originId " +
            "order by a.flag, b.item_code ")
    Flux<GroupProjectItemAccountVo> findAllByOriginId(String ctype, String originId);

    @Query("select a.id,a.unique_code,a.ctype,a.origin_id,a.tenant_id,a.assign_user,c.real_name as assign_user_name,a.assign_date, " +
            "a.flag,a.accept_user,d.real_name as accept_user_name,a.accept_date,b.item_code,b.item_name,b.cate_code " +
            "from _app_group_project_item_account a " +
            "left join _app_group_project_item b on a.unique_code=b.item_code " +
            "left join _app_group_sys_user c on a.assign_user = c.id " +
            "left join _app_group_sys_user d on a.accept_user = d.id " +
            "where a.ctype = :ctype and a.tenant_id = :tenantId " +
            "order by a.flag, b.item_code ")
    Flux<GroupProjectItemAccountVo> findAllByTenantId(String ctype, String tenantId);

}
