package org.boozsoft.repo;

import org.boozsoft.domain.entity.CustomerClass;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerClassRepository extends ReactiveCrudRepository<CustomerClass, String> {
    @Query("select  COALESCE(max(cus_grade_code), '0') from customer_class where parent_id=:parentId ")
    Mono<String> findByMaxParentId(String parentId);
    Mono<Long> countByParentId(String parentId);
    Mono<Long> countByCusClass(String cusClass);
    Mono<Long> countByParentIdAndCusCclassName(String parentId,String cusCclassName);
    Mono<Long> countByCusCclassName(String cusCclassName);

    @Query("(select * from customer_class where cus_bend=:cusBend and cus_class='9999' ) union all " +
            "(select * from customer_class where cus_bend=:cusBend and cus_class !='9999' order by cus_class)")
    Flux<CustomerClass> findAllByCusBend(String cusBend);
    Mono<CustomerClass> findByUniqueCustclass(String uniqueCustclass);
    @Query("select * from customer_class order by cus_grade_code")
    Flux<CustomerClass> findAll();

    @Modifying
    @Query("delete from customer_class where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("update customer_class set cus_bend=:flg where unique_custclass in (:uniqueCustclass)")
    Mono<Void> updateBendByUniqueCust(String flg,List<String> uniqueCustclass);

    @Query("select cus.*\n" +
            "from _app_group_data_authorization auth\n" +
            "         left join customer_class cus on cus.unique_custclass = auth.data_id and cus.tenant_id = auth.tenantry_id\n" +
            "where auth.archives_id =:tableName \n" +
            "  and auth.tenantry_id =:database \n" +
            "  and auth.operator_id =:operatorId ")
    Flux<CustomerClass> findByCusClassAuthData(String tableName,String database,String operatorId);
}

