package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupProjectColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectColumnRepository extends ReactiveCrudRepository<GroupProjectColumn, String> {

    Flux<GroupProjectColumn> findByProjectCateCodeOrderByNum(String projectCateCode);
    /** 根据大类编码删除项目栏目信息 */
    Mono<GroupProjectColumn> deleteByProjectCateCode(String projectCateCode);
    /** 根据大类编码和字段排序号删除项目栏目信息 */
    Mono<GroupProjectColumn> deleteByProjectCateCodeAndNum(String projectCateCode, Integer num);
    /** 根据大类编码和显示状态查询项目栏目 */
    Flux<GroupProjectColumn> findByProjectCateCodeAndIslistOrderByNum(String projectCateCode, String islist);
    /** 根据大类编码,显示状态和生效状态查询项目栏目 */
    Flux<GroupProjectColumn> findByProjectCateCodeAndIslistAndSuccessStateOrderByNum(String projectCateCode, String islist, String successState);
    /** 根据大类编码和生效状态查询项目栏目 */
    Flux<GroupProjectColumn> findByProjectCateCodeAndSuccessStateOrderByNum(String projectCateCode, String successState);
    /** 根据大类编码删除项目栏目不生效信息 */
    Mono<GroupProjectColumn> deleteByProjectCateCodeAndSuccessState(String projectCateCode,String successState);
}
