package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ProjectCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectCategoryRepository extends ReactiveCrudRepository<ProjectCategory, String> {

    Flux<ProjectCategory> findAllByOrderByProjectCateCode();
    Flux<ProjectCategory> findAllByOrderBySuccessState(Pageable pageable);

    /**
     * 获取最大的样式编码
     */
    @Query("select Max(project_cate_code) as project_cate_code from project_category ")
    Mono<ProjectCategory> findMaxProjectCateCode();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MAX(cast(project_cate_code as int))+1 as project_cate_code from project_category " +
            "where cast(project_cate_code as int) <(select MIN(cast(project_cate_code as int)) as minbreak " +
            "from(select project_cate_code,ROW_NUMBER() over(order by cast(project_cate_code as int)) as sort " +
            "from (select distinct cast(project_cate_code as int) from project_category) temp1) temp2 " +
            "where cast(project_cate_code as int) <> sort) ")
    Mono<ProjectCategory> findBukongProjectCateCode();

    /**
     * 根据大类编码查询项目大类信息
     */
    Flux<ProjectCategory> findByProjectCateCodeOrderById(String projectCateCode);

    /**
     * 根据大类编码删除项目大类信息
     */
    Mono<ProjectCategory> deleteByProjectCateCode(String projectCateCode);
    /** 根据大类编码和生效状态查询项目大类信息 */
    Mono<ProjectCategory> findByProjectCateCodeAndSuccessStateOrderById(String projectCateCode,String successState);

    @Query("SELECT * from project_category where apply_database_unique_code=:database and apply_user=:user and success_state=:state" +
            " UNION ALL" +
            " SELECT * from project_category order by project_cate_code ")
    Flux<ProjectCategory> findAllByApplyDatabaseUniqueCodeAndApplyUserAndSuccessState(String database, String user, String state);

    /** 根据生效状态和启用状态查询项目大类信息 */
    Flux<ProjectCategory> findBySuccessStateAndFlagOrderByProjectCateCode(String successState, String flag);

    /**
     * 根据大类名称查询项目大类信息
     */
    Flux<ProjectCategory> findByProjectCateNameOrderById(String projectCateName);

    Mono<Long> countByProjectCateCode(String code);

    /********************* Mr. Ye *******************/
    @Query("SELECT project_cate_code,project_cate_name from project_category WHERE 1=1 and flag = '1' order by project_cate_code Asc")
    Flux<String[]> findAllProjectCateCodeOrProjectCateNameByFlag();

    @Query("SELECT project_cate_code,project_cate_name from project_category WHERE 1=1 and flag = '1' order by project_cate_code Asc")
    Flux<ProjectCategory> findProjectCateCodeOrProjectCateNameByFlag();

    @Query("SELECT project_code,project_name from project WHERE 1=1 and flag = '1' order by project_code Asc")
    Flux<String[]> findAllPrsojectCodeOrProjectCateNameByFlag();
    /********************* Mr. Ye *******************/

    Flux<ProjectCategory> findBySuccessStateAndFlagOrderById(String successState,String flag);



    @Modifying
    @Query("delete from project_category where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);
}
