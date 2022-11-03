package org.boozsoft.repo.fa;

import org.boozsoft.domain.entity.fa.FaChangeProject;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaChangeProjectRepository extends ReactiveCrudRepository<FaChangeProject, String> {

    Flux<FaChangeProject> findByCardUniqueOrderByCdate(String cardUnique);
    @Query("select * from fa_change_project a " +
            "where a.iyear = (select Max(iyear) from fa_change_project c where a.card_unique = c.card_unique) " +
            "and a.imonth = (select Max(imonth) from fa_change_project c where a.card_unique = c.card_unique) " +
            "and a.card_unique=:cardUnique ")
    Flux<FaChangeProject> findByCardUniqueAndNewCdate(String cardUnique);
    Flux<FaChangeProject> findByCardUniqueOrderByCdateDesc(String cardUnique);
    Mono<FaChangeProject> deleteByCardUnique(String cardUnique);

    Flux<FaChangeProject> findAllBySuperiorIdOrderByCdateDesc(String id);

    Flux<FaChangeProject> findAllBySuperiorIdNotAndCdateLessThanOrCdateLessThanAndSuperiorIdIsNullOrderByCdateDesc(String s,String s1,String s2);
}
