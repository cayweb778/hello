package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginProjectCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectCategoryRepository extends ReactiveCrudRepository<OriginProjectCategory, String> {

    Flux<OriginProjectCategory> findAllByOriginIdOrderByProjectCateCode(String originId);
    Flux<OriginProjectCategory> findAllByOriginIdOrderByProjectCateCode(Pageable pageable,String originId);

    /** 根据大类编码查询项目大类信息 */
    Mono<OriginProjectCategory> findByProjectCateCodeAndOriginIdOrderById(String projectCateCode,String originId);
    /** 根据大类编码删除项目大类信息 */
    Mono<OriginProjectCategory> deleteByProjectCateCodeAndOriginId(String projectCateCode,String originId);
    /** 根据大类编码和生效状态查询项目大类信息 */
    Mono<OriginProjectCategory> findByProjectCateCodeAndOriginIdOrderByProjectCateCode(String projectCateCode,String originId);

    /** 根据生效状态和启用状态查询项目大类信息 */
    Flux<OriginProjectCategory> findByFlagAndOriginIdOrderByProjectCateCode(String flag,String originId);

    /** 根据大类名称查询项目大类信息 */
    Mono<OriginProjectCategory> findByProjectCateNameAndOriginIdOrderById(String projectCateName,String originId);

    Mono<Long> countByProjectCateCodeAndOriginId(String code,String originId);

    /**
     * 获取最大的样式编码
     */
    @Query("select Max(project_cate_code) as project_cate_code from _app_origin_project_category where origin_id=:originId ")
    Mono<OriginProjectCategory> findMaxProjectCateCode(String originId);

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(project_cate_code as int))+1 as project_cate_code from _app_origin_project_category " +
            "where origin_id=:originId and cast(project_cate_code as int) <(select MIN(cast(project_cate_code as int)) as minbreak " +
            "from(select project_cate_code,ROW_NUMBER() over(order by cast(project_cate_code as int)) as sort " +
            "from (select distinct cast(project_cate_code as int) from _app_origin_project_category where origin_id=:originId) temp1) temp2 " +
            "where cast(project_cate_code as int) <> sort) ")
    Mono<OriginProjectCategory> findBukongProjectCateCode(String originId);

}
