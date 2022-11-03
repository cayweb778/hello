package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaChangeDept;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaChangeDeptRepository extends ReactiveCrudRepository<FaChangeDept, String> {

    Flux<FaChangeDept> findByCardUniqueOrderByCdate(String cardUnique);
    @Query("select * from fa_change_dept a " +
            "where a.iyear = (select Max(iyear) from fa_change_dept c where a.card_unique = c.card_unique) " +
            "and a.imonth = (select Max(imonth) from fa_change_dept c where a.card_unique = c.card_unique) " +
            "and a.card_unique=:cardUnique ")
    Flux<FaChangeDept> findByCardUniqueAndNewCdate(String cardUnique);
    Mono<FaChangeDept> deleteByCardUnique(String cardUnique);
    Flux<FaChangeDept> findByCardUniqueOrderByCdateDesc(String cardUnique);
    Flux<FaChangeDept> findAllBySuperiorIdOrderByCdateDesc(String s);

    Flux<FaChangeDept> findAllBySuperiorIdNotAndCdateLessThanOrCdateLessThanAndSuperiorIdIsNullOrderByCdateDesc(String s,String s1,String s2);


}
