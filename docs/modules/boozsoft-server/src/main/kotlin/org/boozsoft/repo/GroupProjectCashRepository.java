package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.vo.ProjectCashCodeVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupProjectCashRepository extends ReactiveCrudRepository<GroupProjectCash, String> {

    Flux<GroupProjectCash> findAllByOrderByProjectCode();
    Flux<GroupProjectCash> findByProjectCodeOrderById(String projectCode);
    @Query("SELECT project_code from _app_group_project_cash order by project_code Asc")
    Flux<String> findByProjectAllOrderByCode();

    @Query("SELECT " +
            " pc.*, " +
            " (select array_to_string( ARRAY_AGG ( jie_code ), ',' ) from _app_group_project_cash_code cc where  cc.project_code = pc.project_code AND cc.acc_standard = pc.acc_standard) as jcode, " +
            " (select array_to_string( ARRAY_AGG ( dai_code ), ',' ) from _app_group_project_cash_code cc where  cc.project_code = pc.project_code AND cc.acc_standard = pc.acc_standard) as dcode " +
            "FROM " +
            " _app_group_project_cash pc " +
            "WHERE " +
            " acc_standard = :accStandard  " +
            "ORDER BY project_code ASC")
    Flux<ProjectCashCodeVo> findByAccStandardOrderById(String accStandard);

    Flux<GroupProjectCash> findAllByAccStandard(String uniqueAccStandard);

    @Query("SELECT project_type,project_type_name from _app_group_project_cash where acc_standard = :accStandard group by project_type,project_type_name order by project_type Asc")
    Flux<GroupProjectCash> findAllByTypeGroup(String accStandard);

}
