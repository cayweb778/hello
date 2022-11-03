package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSupplier;
import org.boozsoft.domain.entity.group.GroupSupplierAccount;
import org.boozsoft.domain.vo.GroupCustomerVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSupplierAccountRepository extends ReactiveCrudRepository<GroupSupplierAccount, String> {
    Flux<GroupSupplierAccount> findAllByOrgUniqueAndCtype(String orgUnique, String ctype);
    Flux<GroupSupplierAccount> findAllByCtype(String ctype);

    @Query("select cus.* from _app_group_supplier_account cusa left join _app_group_supplier cus on cusa.data_unique=cus.unique_code where cusa.org_unique=:orgUnique and cusa.ctype=:ctype and cusa.flag=:flag")
    Flux<GroupSupplier> findAllByOrgUniqueAndCtypeAndFlag(String orgUnique, String ctype, String flag);

    @Query("select cus.* from _app_group_supplier_account cusa left join _app_group_supplier cus on cusa.data_unique=cus.unique_code where cusa.org_unique=:orgUnique and cusa.tenant_id=:tenantId and cusa.ctype=:ctype and cusa.flag=:flag")
    Flux<GroupSupplier> findAllByOrgUniqueAndTenantIdAndCtypeAndFlag(String orgUnique,String tenantId, String ctype, String flag);

    @Query("delete from _app_group_supplier_account where org_unique=:orgUnique and ctype=:ctype and data_unique in (:dataUnique)")
    Mono<Void> deleteAllById(String orgUnique, String ctype,List<String> dataUnique);

    @Query("delete from _app_group_supplier_account where org_unique=:orgUnique and ctype=:ctype and tenant_id=:tenantId and data_unique in (:dataUnique)")
    Mono<Void> deleteAllByIdAndTenantId(String orgUnique, String ctype,String tenantId,List<String> dataUnique);

    @Query("update _app_group_supplier_account set flag=:flag where ctype=:ctype and org_unique=:orgUnique and data_unique in ( :orgDelList )")
    Mono<Void> editFlagByCtypeAndOrgUnique(String flag,String ctype, String orgUnique, List<String> orgDelList);

    @Query("update _app_group_supplier_account set flag=:flag where ctype=:ctype and org_unique=:orgUnique and tenant_id=:tenantId and data_unique in ( :orgDelList )")
    Mono<Void> editFlagByCtypeAndOrgUniqueAndTenantId(String flag,String ctype, String orgUnique,String tenantId, List<String> orgDelList);

    @Query("select count(cus.id) from _app_group_supplier_account cca left join _app_group_supplier cus on cus.unique_code=cca.data_unique where cca.ctype=:ctype and cca.flag=:flag and cus.cust_abbname=:abbName and cca.org_unique=:orgUnique")
    Mono<Long> findByOrgCusAbbNameAssignAccount(String ctype,String flag,String abbName,String orgUnique);
    @Query("select count(cus.id) from _app_group_supplier_account cca left join _app_group_supplier cus on cus.unique_code=cca.data_unique where cca.ctype=:ctype and cca.flag=:flag and cus.cust_name=:name and cca.org_unique=:orgUnique")
    Mono<Long> findByOrgCusNameAssignAccount(String ctype,String flag,String name,String orgUnique);

    @Query("select cus.*,cca.flag as assign_flag\n" +
            "from _app_group_supplier_account cca\n" +
            "         left join _app_group_supplier cus on cus.unique_code = cca.data_unique\n" +
            "where cca.ctype =:ctype \n" +
            "  and cca.org_unique =:orgUnique ")
    Flux<GroupCustomerVo> findAllByCusAssignLeftJoinGroupCusData(String orgUnique, String ctype);
    @Query("select cus.*,cca.flag as assign_flag\n" +
            "from _app_group_supplier_account cca\n" +
            "         left join _app_group_supplier cus on cus.unique_code = cca.data_unique\n" +
            "where cca.ctype =:ctype \n" +
            "  and cca.org_unique =:orgUnique and cca.tenant_id=:tenantId ")
    Flux<GroupCustomerVo> findAllByCusAssignLeftJoinGroupCusDataAndTenantId(String orgUnique, String ctype,String tenantId);

    @Query("select count(cus.id) from _app_group_supplier_account cca left join _app_group_supplier cus on cus.unique_code=cca.data_unique where cca.ctype=:ctype and cca.flag=:flag and cus.cust_abbname=:abbName and cus.cust_name=:custName and cca.org_unique=:orgUnique")
    Mono<Long> findByOrgCusAbbNameAndCustNameAssignAccount(String ctype,String flag,String abbName,String custName,String orgUnique);

    @Query("select count(cus.id) from _app_group_supplier_account cca left join _app_group_supplier cus on cus.unique_code=cca.data_unique where cca.ctype=:ctype and cca.flag=:flag and cus.cust_abbname=:abbName and cus.cust_name=:custName and cca.tenant_id=:tenantId")
    Mono<Long> findByTenantIdCusAbbNameAndCustNameAssignAccount(String ctype,String flag,String abbName,String custName,String tenantId);
}
