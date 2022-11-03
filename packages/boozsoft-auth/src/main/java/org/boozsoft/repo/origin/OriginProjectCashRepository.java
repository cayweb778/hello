package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.origin.OriginProjectCash;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectCashRepository extends ReactiveCrudRepository<OriginProjectCash, String> {

    @Query("SELECT " +
            " pc.*, " +
            " (select array_to_string( ARRAY_AGG ( jie_code ), ',' ) from _app_origin_project_cash_code cc where  cc.project_code = pc.project_code AND cc.iyear = :year) as jcode, " +
            " (select array_to_string( ARRAY_AGG ( dai_code ), ',' ) from _app_origin_project_cash_code cc where  cc.project_code = pc.project_code AND cc.iyear = :year) as dcode " +
            "FROM " +
            " _app_origin_project_cash pc where origin_id = :groupId " +
            "ORDER BY project_code ASC")
    Flux<OriginProjectCash> findAllByOrderByProjectCode(String groupId, String year);
    Flux<OriginProjectCash> findByProjectCodeOrderById(String projectCode);
    @Query("SELECT project_code from _app_origin_project_cash order by project_code Asc")
    Flux<String> findByProjectAllOrderByCode();
    Flux<OriginProjectCash> findByAccStandardOrderById(String accStandard);

    Flux<OriginProjectCash> findAllByAccStandard(String uniqueAccStandard);

    Mono<Void> deleteAllByOriginIdAndIyear(String groupId, String year);
    Flux<OriginProjectCash> findAllByOriginIdAndIyear(String groupId, String year);

    Flux<OriginProjectCash> findAllByOriginIdAndIyearAndAccStandard(String accGroup, String year,String uniqueAccStandard);

    @Query("SELECT project_type,project_type_name from _app_origin_project_cash where acc_standard = :accStandard and origin_id=:originId and iyear=:iyear group by project_type,project_type_name order by project_type Asc")
    Flux<OriginProjectCash> findAllByTypeGroup(String accStandard,String originId,String iyear);

}
