package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupProjectCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectCategoryRepository extends ReactiveCrudRepository<GroupProjectCategory, String> {

    Flux<GroupProjectCategory> findAllByOrderByProjectCateCode();
    Flux<GroupProjectCategory> findAllByOrderBySuccessState(Pageable pageable);

    /** 根据大类编码查询项目大类信息 */
    Mono<GroupProjectCategory> findByProjectCateCodeOrderById(String projectCateCode);
    /** 根据大类编码删除项目大类信息 */
    Mono<GroupProjectCategory> deleteByProjectCateCode(String projectCateCode);
    /** 根据大类编码和生效状态查询项目大类信息 */
    Mono<GroupProjectCategory> findByProjectCateCodeAndSuccessStateOrderByProjectCateCode(String projectCateCode,String successState);

    /** 根据生效状态和启用状态查询项目大类信息 */
    Flux<GroupProjectCategory> findBySuccessStateAndFlagOrderByProjectCateCode(String successState,String flag);

    /** 根据大类名称查询项目大类信息 */
    Mono<GroupProjectCategory> findByProjectCateNameOrderById(String projectCateName);

    Mono<Long> countByProjectCateCode(String code);

    /**
     * 获取最大的样式编码
     */
    @Query("select Max(project_cate_code) as project_cate_code from _app_group_project_category ")
    Mono<GroupProjectCategory> findMaxProjectCateCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(project_cate_code as int))+1 as project_cate_code from _app_group_project_category " +
            "where cast(project_cate_code as int) <(select MIN(cast(project_cate_code as int)) as minbreak " +
            "from(select project_cate_code,ROW_NUMBER() over(order by cast(project_cate_code as int)) as sort " +
            "from (select distinct cast(project_cate_code as int) from _app_group_project_category) temp1) temp2 " +
            "where cast(project_cate_code as int) <> sort) ")
    Mono<GroupProjectCategory> findBukongProjectCateCode();

}
