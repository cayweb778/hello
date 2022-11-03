package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.share.ProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.boozsoft.domain.vo.ProjectCashCodeVo;
import org.boozsoft.domain.vo.ProjectCashVo;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;

public interface ProjectCashRepository extends ReactiveCrudRepository<ProjectCash, String> {

    Flux<ProjectCash> findAllByProjectTypeOrderByProjectCode(String projectType);

    @Query("SELECT " +
            " pc.*, " +
            " (select array_to_string( ARRAY_AGG ( jie_code ), ',' ) from project_cash_code cc where  cc.project_code = pc.project_code AND cc.iyear = :year) as jcode, " +
            " (select array_to_string( ARRAY_AGG ( dai_code ), ',' ) from project_cash_code cc where  cc.project_code = pc.project_code AND cc.iyear = :year) as dcode " +
            "FROM " +
            " project_cash pc " +
            "ORDER BY project_code ASC")
    Flux<ProjectCashCodeVo> findAllByOrderByProjectCode(String year);

    Flux<ProjectCash> findAllByOrderByProjectCode();

    Flux<ProjectCash> findByProjectCodeOrderById(String projectCode);

    @Query("SELECT project_code from project_cash order by project_code Asc")
    Flux<String> findByProjectAllOrderByCode();

    @Query("SELECT project_code as value, project_name as label, fangxiang as fx from project_cash  where flag = :flag and tenant_id=:accId order by project_code Asc")
    Flux<ProjectCashVo> findByFlagAndTenantIdOrderByProjectCode(String flag, String accId);

    @Query("SELECT " +
            " pc.*, cc.jie_code jcode, cc.dai_code dcode " +
            "FROM " +
            "  project_cash pc" +
            " LEFT JOIN project_cash_code cc ON pc.project_code = cc.project_code  " +
            "WHERE " +
            " 1 = 1 and pc.flag = '1' " +
            " AND pc.tenant_id = :accId")
    Flux<ProjectCashCodeVo> findByTenantId(String accId);

    @Modifying
    @Query("delete from project_cash where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Modifying
    @Query("delete from project_cash where tenant_id =:id")
    Mono<Void> deleteAllByTenantIdAndYear(String id,String year);

    @Query("insert  inot project_cash from project_cash where tenant_id =:id ")
    Mono<Void> saveList(List<GroupProjectCash> iterator);

    @Query("SELECT project_type,project_type_name from project_cash group by project_type,project_type_name order by project_type Asc")
    Flux<ProjectCash> findAllByTypeGroup();

    @Query("update project_cash set beiyong1 = '0'")
    Mono<Void> updateMoney();

    @Query("SELECT * from project_cash where project_code in (:collect) ")
    Flux<ProjectCash> findAllByProjectCodes(List<String> collect);

}
