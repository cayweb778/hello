package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSupplierClassAccount;
import org.boozsoft.domain.vo.GroupCustomerClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSupplierClassAccountRepository extends ReactiveCrudRepository<GroupSupplierClassAccount, String> {
    Flux<GroupSupplierClassAccount> findAllByOrgUniqueAndCtype(String orgUnique,String ctype);
    Flux<GroupSupplierClassAccount> findAllByOrgUniqueAndCtypeAndTenantId(String orgUnique,String ctype,String tenantId);

    @Query("delete from _app_group_supplier_class_account where ctype=:ctype and org_unique=:orgUnique and data_unique in ( :orgDelList )")
    Mono<Void> delByCtypeAndOrgUnique(String ctype, String orgUnique, List<String> orgDelList);

    @Query("delete from _app_group_supplier_class_account where ctype=:ctype and org_unique=:orgUnique and data_unique in ( :orgDelList ) and tenant_id=:tenantId ")
    Mono<Void> delByCtypeAndOrgUniqueAndTenantId(String ctype, String orgUnique, List<String> orgDelList,String tenantId);

    @Query("delete from _app_group_supplier_class_account where ctype=:ctype and org_unique=:orgUnique and flag=:flag")
    Mono<Void> app_group_supplier_class_account(String ctype, String orgUnique, String flag);
    @Query("delete from _app_group_supplier_class_account where ctype=:ctype and org_unique=:orgUnique and flag=:flag and tenant_id=:tenantId")
    Mono<Void> delByCtypeAndOrgUniqueAndFlagAndTenantId(String ctype, String orgUnique, String flag,String tenantId);
    @Query("delete from _app_group_supplier_class_account where ctype=:ctype and org_unique=:orgUnique and flag=:flag")
    Mono<Void> delByCtypeAndOrgUniqueAndFlag(String ctype, String orgUnique, String flag);
    @Query("select distinct data_id\n" +
            "from _app_group_supplier_class_account\n" +
            "WHERE org_unique =:orgUnique \n" +
            "  and ctype =:ctype \n" +
            "  and flag = '0'\n" +
            "  and data_unique in (select data_id\n" +
            "                  from _app_group_supplier_class_account\n" +
            "                  where org_unique =:orgUnique \n" +
            "                    and ctype =:ctype \n" +
            "                    and flag = '0'\n" +
            "                  group by data_unique\n" +
            "                  having count(data_unique) > 1)")
    Flux<String> findByRepeat(String ctype, String orgUnique);


    // 组织未引入的数据
    @Query("select g.*,(select cc.cus_cclass_name from _app_group_customer_class cc where cc.cus_bend='0' and cc.unique_custclass=g.parent_id ) as super_class_name\n" +
            "from _app_group_supplier_class_account ga\n" +
            "         left join _app_group_supplier_class g on g.unique_custclass = ga.data_unique\n" +
            "where ga.org_unique =:orgUnique \n" +
            "  and ga.flag = '0' and g.cus_bend=:bend \n" +
            "  and ga.ctype =:ctype order by g.cus_grade_code")
    Flux<GroupCustomerClassVo> findAllByNoBringOrg(String orgUnique, String ctype,String bend);

    // 账套未引入的数据
    @Query("select g.*,(select cc.cus_cclass_name from _app_group_customer_class cc where cc.cus_bend='0' and cc.unique_custclass=g.parent_id ) as super_class_name\n" +
            "from _app_group_supplier_class_account ga\n" +
            "         left join _app_group_supplier_class g on g.unique_custclass = ga.data_unique\n" +
            "where ga.tenant_id =:tenantId \n" +
            "  and ga.flag = '0' and g.cus_bend=:bend \n" +
            "  and ga.ctype =:ctype order by g.cus_class_grade")
    Flux<GroupCustomerClassVo> findAllByNoBringTenantId(String tenantId, String ctype, String bend);

    @Query("update _app_group_supplier_class_account set flag=:flag where ctype=:ctype and org_unique=:orgUnique and data_unique in ( :orgDelList )")
    Mono<Void> editFlagByCtypeAndOrgUnique(String flag,String ctype, String orgUnique, List<String> orgDelList);
    @Query("update _app_group_supplier_class_account set flag=:flag where ctype=:ctype and tenant_id=:tenantId and data_unique in ( :orgDelList )")
    Mono<Void> editFlagByCtypeAndTenantId(String flag,String ctype, String tenantId, List<String> orgDelList);
    Flux<GroupSupplierClassAccount> findAllByCtype(String ctype);
}
