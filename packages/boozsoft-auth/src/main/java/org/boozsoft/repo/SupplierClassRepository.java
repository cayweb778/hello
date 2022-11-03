package org.boozsoft.repo;

import org.boozsoft.domain.entity.SupplierClass;
import org.boozsoft.domain.vo.SupplierClass2Vo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SupplierClassRepository extends ReactiveCrudRepository<SupplierClass, String> {
    @Query("select  COALESCE(max(cus_grade_code), '0') from supplier_class where parent_id=:parentId ")
    Mono<String> findByMaxParentId(String parentId);
    Mono<Long> countByParentId(String parentId);
    Mono<Long> countByCusClass(String cusClass);
    Mono<Long> countByParentIdAndCusCclassName(String parentId,String cusCclassName);
    Mono<Long> countByCusCclassName(String cusCclassName);

    @Query("(select * from supplier_class where cus_bend=:cusBend and cus_class='9999' ) union all " +
            "(select * from supplier_class where cus_bend=:cusBend and cus_class !='9999' order by cus_class)")
    Flux<SupplierClass> findAllByCusBend(String cusBend);
    Mono<SupplierClass> findByUniqueCustclass(String uniqueCustclass);
    @Query("select * from supplier_class order by cus_grade_code")
    Flux<SupplierClass> findAll();

    @Modifying
    @Query("delete from supplier_class where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("update supplier_class set cus_bend=:flg where unique_custclass in (:uniqueCustclass)")
    Mono<Void> updateBendByUniqueCust(String flg, List<String> uniqueCustclass);

    @Query("select cus.*\n" +
            "from _app_group_data_authorization auth\n" +
            "         left join supplier_class cus on cus.unique_custclass = auth.data_id and cus.tenant_id = auth.tenantry_id\n" +
            "where auth.archives_id =:tableName \n" +
            "  and auth.tenantry_id =:database \n" +
            "  and auth.operator_id =:operatorId ")
    Flux<SupplierClass> findByCusClassAuthData(String tableName,String database,String operatorId);

    @Query("SELECT (select sc2.cus_cclass_name from supplier_class sc2 where sc2.unique_custclass=sc.parent_id) as super_class_name,sc.* from supplier_class sc ")
    Flux<SupplierClass2Vo> findAllSupClass();
}

