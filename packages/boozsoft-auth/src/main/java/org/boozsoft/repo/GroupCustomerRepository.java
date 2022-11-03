package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupCustomer;
import org.boozsoft.domain.vo.CustomerVo;
import org.boozsoft.domain.vo.GroupCustomerVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupCustomerRepository extends ReactiveCrudRepository<GroupCustomer, String> {
    Mono<Long> countByCustSregcode(String custSregcode);
    Mono<Long> countByCustAbbname(String jiacheng);
    Mono<GroupCustomer> findByCustAbbnameAndCustName(String custAbbname,String custName);
    Mono<Long> countByCustCode(String num);
    Mono<Long> countByCustName(String name);
    Mono<Long> countByCustAccount(String name);
    Flux<GroupCustomer> findAllBySuccessStateAndFlag(String successstate,String flag);
    Flux<GroupCustomer> findAllByOrderBySuccessStateAscCustCodeAsc();
    Mono<GroupCustomer> findBySuccessStateAndUniqueCode(String successstate,String uniqueCode);
    Mono<GroupCustomer> findByUniqueCode(String uniqueCode);
    Mono<GroupCustomer> findByCustAbbname(String custAbbname);
    Mono<GroupCustomer> findByCustName(String custName);

    @Query(" select * from _app_group_customer where cust_name like :custName and cust_abbname like :custAbbname and success_state='1' ")
    Flux<GroupCustomer> findAllByGroupCusNameAndCustAbbnameLike(String custName,String custAbbname);


    @Query(" select g.cus_cclass_name from _app_group_customer_class g join _app_group_customer c on c.unique_custclass=g.unique_custclass where g.unique_custclass in ( :uniqueCustclass ) order by g.unique_custclass ")
    Flux<String> findAllByUniqueCustclass(List<String> uniqueCustclass);

    @Query("SELECT c.*, cc.cus_cclass_name as unique_custclass_name, org.org_name,acc.acc_name,acc.year_start_date as acc_start_date,(select s.cust_name from _app_group_supplier s where s.unique_code = c.unique_code_supplier) as sup_name,\n" +
            "       (select c2.cust_name from _app_group_customer c2 where c2.unique_code = c.unique_code_ccus)  as parent_name\n" +
            "from _app_group_customer c\n" +
            "         left join _app_group_customer_class cc on cc.unique_custclass = c.unique_custclass\n" +
            "         left join _app_group_sys_org org on org.unique_code = c.origin_unique\n" +
            "         left join _app_group_sys_account acc on acc.acc_id = c.tenant_id\n" +
            "order by success_state, cust_code asc, cust_devdate desc")
    Flux<CustomerVo> findAlls();

    @Query(" select gcus.* from _app_group_customer gcus where gcus.unique_code not in (select lot.data_unique from _app_group_allot_record lot where gcus.unique_code=lot.data_unique and lot.acc_id=:tenantId) ")
    Flux<GroupCustomer> findAllByGroupAndAllot(String tenantId);

    @Query(" SELECT count(*) from _app_group_allot_record where data_unique=:cusUnique ")
    Mono<Long> countAllotByCustUnique(String cusUnique);

    /********************* Mr. Ye *******************/
    @Query("SELECT cust_code,cust_name,cust_abbname,unique_code from _app_group_customer WHERE 1=1 and flag = '1' order by cust_code Asc")
    Flux<GroupCustomer> findAllCustCodeOrCustNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("update _app_group_customer set is_del=:idDel,del_name=:delName,del_date=:delDate where id=:id")
    Mono<Void> editByidIsDel(String id,String idDel,String delName,String delDate);

    @Query("select cust_code from _app_group_customer")
    Flux<String> getCusCode();

    @Query("SELECT c.*,cc.cus_cclass_name as unique_custclass_name from _app_group_customer c left join _app_group_customer_class cc on cc.unique_custclass = c.unique_custclass WHERE origin_unique=:originUnique and success_state!='1' order by success_state,apply_date,cust_code Asc")
    Flux<GroupCustomerVo> findGroupCusByOrg(String originUnique);

    @Query("SELECT c.*,cc.cus_cclass_name as unique_custclass_name\n" +
            "from _app_group_customer c\n" +
            "         left join _app_group_customer_class cc on cc.unique_custclass = c.unique_custclass\n" +
            "WHERE tenant_id = :tenantId\n" +
            "  and success_state != '1'\n" +
            "order by success_state, apply_date, cust_code Asc")
    Flux<GroupCustomerVo> findGroupCusByTenantId(String tenantId);

    @Query("update _app_group_customer set success_state=:successState,approve_user=:approveUser,approve_date=:approveDate where id=:id")
    Mono<Void> editByIdSuccessState(String successState,String id,String approveUser,String approveDate);

    @Query("delete from _app_group_customer where cust_name=:custName and cust_abbname=:custAbbName and success_state=:successState")
    Mono<Void> delByIdCustNameAndCustAbbnameAndSuccessState(String custName,String custAbbName,String successState);
}

