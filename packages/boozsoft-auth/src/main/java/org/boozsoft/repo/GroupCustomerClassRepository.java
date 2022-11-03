package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupCustomerClass;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupCustomerClassRepository extends ReactiveCrudRepository<GroupCustomerClass, String> {
    @Query("select  COALESCE(max(cus_grade_code), '0') from _app_group_customer_class where parent_id=:parentId ")
    Mono<String> findByMaxParentId(String parentId);
    Mono<Long> countByParentId(String parentId);
    Mono<Long> countByCusClass(String cusClass);
    Mono<Long> countByParentIdAndCusCclassName(String parentId,String cusCclassName);
    Mono<Long> countByCusCclassName(String cusCclassName);

    @Query("select * from _app_group_customer_class where cus_bend=:cusBend order by cus_class")
    Flux<GroupCustomerClass> findAllByCusBend(String cusBend);
    Mono<GroupCustomerClass> findByUniqueCustclass(String uniqueCustclass);

    @Query("select * from _app_group_customer_class order by cus_grade_code ")
    Flux<GroupCustomerClass> findAll();

    @Query("select * from _app_group_customer_class where cus_grade_code like :cusGradeCode and cus_grade_code=:cusClassGrade and cus_bend=:cusBend ")
    Flux<GroupCustomerClass> findByLikeCusGradeCode(String cusGradeCode,String cusClassGrade,String cusBend);

    @Query("update _app_group_customer_class set cus_bend=:flg where unique_custclass in (:uniqueCustclass)")
    Mono<Void> updateBendByUniqueCust(String flg, List<String> uniqueCustclass);
}

