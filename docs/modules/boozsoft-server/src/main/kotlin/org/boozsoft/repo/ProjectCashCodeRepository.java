package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupProjectCash;
import org.boozsoft.domain.entity.share.ProjectCashCode;
import org.boozsoft.domain.vo.ProjectCashCodeVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectCashCodeRepository extends ReactiveCrudRepository<ProjectCashCode, String> {

    Flux<ProjectCashCode> findByProjectCodeAndIyearOrderById(String projectCode,String iyear);
    Flux<ProjectCashCode> findByIyearOrderById(String iyear);

    @Query("select count(id) from project_cash_code where iyear=:iyear and ( jie_code=:ccode or dai_code=:ccode ) ")
    Mono<Long> countByJieCodeAndDaiCode(String iyear, String ccode);

    @Query("SELECT " +
            " cc.*, pc.project_name pname, pc.fangxiang " +
            "FROM " +
            " project_cash_code cc " +
            " LEFT JOIN project_cash pc ON pc.project_code = cc.project_code  " +
            "WHERE " +
            " 1 = 1 and pc.flag = '1' " +
            " AND pc.tenant_id = :accId")
    Flux<ProjectCashCodeVo> findByTenantIdOrderById(String accId);

    @Query("select jie_code from project_cash_code where iyear=:iyear ")
    Flux<String> findByJieCodeStr(String iyear);
    @Query("select dai_code from project_cash_code where iyear=:iyear ")
    Flux<String> findByDaiCodeStr(String iyear);
}
