package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OrgCustomerClass;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface OriginCustomerClassRepository extends ReactiveCrudRepository<OrgCustomerClass, String> {

    @Query("select * from _app_origin_customer_class where org_unique=:orgUnique order by cus_grade_code ")
    Flux<OrgCustomerClass> findAllByOrgUnique(String orgUnique);

    @Query("update _app_origin_customer_class set cus_bend=:flg where unique_custclass in (:ids)")
    Mono<Void> updateBendByIds(String flg,List<String> ids);
}
