package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ProjectColumn;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectColumnRepository extends ReactiveCrudRepository<ProjectColumn, String> {

    Flux<ProjectColumn> findByProjectCateCodeOrderByNum(String projectCateCode);
    /** 根据大类编码删除项目栏目信息 */
    Mono<ProjectColumn> deleteByProjectCateCode(String projectCateCode);
    /** 根据大类编码和字段排序号删除项目栏目信息 */
    Mono<ProjectColumn> deleteByProjectCateCodeAndNum(String projectCateCode, Integer num);
    /** 根据大类编码和显示状态查询项目栏目 */
    Flux<ProjectColumn> findByProjectCateCodeAndIslistOrderByNum(String projectCateCode, String islist);
    /** 根据大类编码,显示状态和生效状态查询项目栏目 */
    Flux<ProjectColumn> findByProjectCateCodeAndIslistAndSuccessStateOrderByNum(String projectCateCode, String islist, String successState);
    /** 根据大类编码和生效状态查询项目栏目 */
    Flux<ProjectColumn> findByProjectCateCodeAndSuccessStateOrderByNum(String projectCateCode, String successState);
    /** 根据大类编码删除项目栏目不生效信息 */
    Mono<ProjectColumn> deleteByProjectCateCodeAndSuccessState(String projectCateCode,String successState);

    @Modifying
    @Query("delete from project_column where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);
}
