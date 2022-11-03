package org.boozsoft.repo;

import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.CustomerClass;
import org.boozsoft.domain.entity.Supplier;
import org.boozsoft.domain.vo.CustomerVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerRepository extends ReactiveCrudRepository<Customer, String> {
    Mono<Long> countByCustSregcode(String custSregcode);
    Mono<Long> countByCustAbbname(String jiacheng);
    Mono<Long> countByCustCode(String num);
    Mono<Long> countById(String id);
    Mono<Long> countByCustName(String name);
    Mono<Long> countByCustAccount(String custAccount);

    @Query("update customer set unique_custclass=:uniqueCustclass where unique_custclass=:oldUniqueCustclass ")
    Mono<Void> editUniqueCustclass(String uniqueCustclass,String oldUniqueCustclass);

    @Query("select count(id) from customer where is_del!='1'")
    Mono<Long> countAll();

    @Query("select * from customer  where flag=:flag and is_del !='1' order by cust_code")
    Flux<Customer> findAllByFlag(String flag);

    @Query("select c.*,cc.cus_cclass_name as unique_custclass_name,(select s.cust_name from supplier s where s.unique_code = c.unique_code_supplier) as sup_name,\n" +
            "       (select c2.cust_name from customer c2 where c2.unique_code = c.unique_code_ccus) as parent_name from customer c inner join customer_class cc on  cc.unique_custclass=c.unique_custclass where c.is_del!='1' and c.unique_custclass=:uniqueCustclass order by c.cust_devdate desc,c.flag,c.cust_name asc ")
    Flux<CustomerVo> findAllByUniqueCustclass(String uniqueCustclass);
    Mono<Customer> findByUniqueCode(String uniqueCode);

    Flux<Customer> findAllByOrderByCustCodeAsc();

    @Query("select * from customer order by cust_code ")
    Flux<Customer> findAllOrderByCustCode();
    @Query("select cus.unique_code,cus.cust_code,cus.cust_name,acv.ccode from accvoucher acv left join customer cus on cus.unique_code=acv.ccus_id group by cus.unique_code,cus.cust_code,cus.cust_name,acv.ccode order by cus.cust_code ")
    Flux<CustomerVo> findAllOrderByCustCode2();

    @Query("SELECT c.*,cc.cus_cclass_name as unique_custclass_name,(select s.cust_name from supplier s where s.unique_code = c.unique_code_supplier) as sup_name,\n" +
            "       (select c2.cust_name from customer c2 where c2.unique_code = c.unique_code_ccus) as parent_name," +
            "       (select dept.dept_name from sys_department dept where dept.unique_code = c.cust_dept)  as dept_name,\n" +
            "       (select psn.psn_name from sys_psn psn where psn.unique_code = c.cust_psn)  as psn_name," +
            "       (select c2.cust_name from customer c2 where c2.unique_code = c.unique_code_supplier)  as parent_name " +
            " from customer c left join customer_class cc on  cc.unique_custclass=c.unique_custclass where c.is_del!='1' order by c.cust_devdate desc,c.flag,c.cust_code asc ")
    Flux<CustomerVo> findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState();

    /********************* Mr. Ye *******************/
    @Query("SELECT cust_code,cust_name,cust_abbname,unique_code from customer WHERE 1=1 and flag = '1' order by cust_code Asc")
    Flux<Customer> findAllCustCodeOrCustNameByFlag();
    /********************* Mr. Ye *******************/
    Mono<Void> deleteAllByUniqueCode(String key);

    @Query("update customer set flag=:flag where id=:id ")
    Mono<Void> editCusFlag(String id,String flag);

    @Query("select count(id) from accvoucher where ccus_id=:cusUnique and imonth not in ('20','21') ")
    Mono<Long> countAccVoucherByCusUnique(String cusUnique);
    @Query("select count(id) from accvoucher where ccus_id=:cusUnique and imonth in ('20') ")
    Mono<Long> countAccVoucherByCusUnique20(String cusUnique);
    @Query("select count(id) from accvoucher where ccus_id=:cusUnique and (cast(md as decimal)>0 or cast(mc as decimal)>0) and imonth in ('21') ")
    Mono<Long> countAccVoucherByCusUnique21(String cusUnique);

    @Query("select gcus.* from _app_group_customer gcus where gcus.unique_code in (select lot.data_unique from _app_group_allot_record lot where gcus.unique_code=lot.data_unique and lot.acc_id=:accId)")
    Flux<Customer> findAllByAllotRecordCus(String accId);


    @Modifying
    @Query("delete from customer where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("update customer set is_del=:isDel,del_name=:delName,del_date=:delDate where id in (:id) ")
    Mono<Void> editisDel(String isDel,String delName,String delDate, List<String> id);

    @Query("select * from customer where is_del='1' order by cust_code ")
    Flux<Customer> findAllIsDel();

    @Query("select distinct cc.cus_cclass_name as unique_custclass_name,(select s.cust_name from supplier s where s.unique_code = cus.unique_code_supplier) as sup_name,\n" +
            "                (select c2.cust_name from supplier c2 where c2.unique_code = cus.unique_code_ccus)  as parent_name,\n" +
            "                (select dept.dept_name from sys_department dept where dept.unique_code = cus.cust_dept)  as dept_name,\n" +
            "                (select psn.psn_name from sys_psn psn where psn.unique_code = cus.cust_psn)  as psn_name,cus.*\n" +
            "from _app_group_data_authorization auth\n" +
            "         left join customer cus on cus.unique_code = auth.data_id and cus.tenant_id = auth.tenantry_id" +
        "             left join customer_class cc on cc.unique_custclass = cus.unique_custclass \n" +
            "where auth.archives_id =:tableName \n" +
            "  and auth.tenantry_id =:database \n" +
            "  and auth.operator_id =:operatorId " +
            "   and cus.is_del!='1' " +
            "   order by cus.cust_code")
    Flux<CustomerVo> findByCusAuthData(String tableName, String database, String operatorId);

    @Query("select cust_code from customer")
    Flux<String> getCustCode();

    @Query("select count(id) + (select count(id) from stock_warehousings where cinvode =:ccode ) as len\n" +
            "from stock_begin_balance\n" +
            "where stock_id =:ccode ")
    Mono<Long> delFindByStock(String ccode);
}

