package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginProjectColumn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectColumnRepository extends ReactiveCrudRepository<OriginProjectColumn, String> {

    Flux<OriginProjectColumn> findByProjectCateCodeAndOriginIdOrderByNum(String projectCateCode,String originId);
    /** 根据大类编码删除项目栏目信息 */
    Mono<OriginProjectColumn> deleteByProjectCateCodeAndOriginId(String projectCateCode,String originId);
    /** 根据大类编码和字段排序号删除项目栏目信息 */
    Mono<OriginProjectColumn> deleteByProjectCateCodeAndNumAndOriginId(String projectCateCode, Integer num,String originId);
    /** 根据大类编码和显示状态查询项目栏目 */
    Flux<OriginProjectColumn> findByProjectCateCodeAndIslistAndOriginIdOrderByNum(String projectCateCode, String islist,String originId);
}
