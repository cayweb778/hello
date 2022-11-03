package org.boozsoft.repo.group;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.group.GroupProject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectRepository extends ReactiveCrudRepository<GroupProject,String> {
    /*//根据项目分类查询项目
    Flux<GroupProject> findByProjectClassCodeAndProjectCateCodeOrderById(String projectClassCode,String projectCateCode,Pageable pageable);
    Mono<Long> countByProjectClassCodeAndProjectCateCode(String projectClassCode,String projectCateCode);
    Flux<GroupProject> findByProjectCateCodeOrderById(String projectCateCode,Pageable pageable);
    Mono<Long> countByProjectCateCode(String projectCateCode);
    //根据生效状态查询项目
    Mono<GroupProject> findByProjectCodeAndSuccessStateAndProjectCateCodeOrderById(String projectCode, String successState,String projectCateCode);
    //根据项目编码查询项目
    Mono<GroupProject> findByProjectCodeAndProjectCateCodeOrderById(String projectCode,String projectCateCode);
    //根据项目编码查询是否存在
    Mono<GroupProject> countByProjectCodeAndProjectCateCode(String code,String projectCateCode);
    //根据项目名称查询项目
    Mono<GroupProject> findByProjectNameAndProjectCateCodeOrderById(String projectName,String projectCateCode);

    Flux<GroupProject> findByProjectCateCodeOrderByProjectCode(String projectCateCode);*/

    //根据项目分类查询项目
    Flux<GroupProject> findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(Pageable pageable,String projectClassCode,String projectCateCode,String isDel);
    Mono<Long> countByProjectClassCodeAndProjectCateCodeAndIsDel(String projectClassCode,String projectCateCode,String isDel);
    Flux<GroupProject> findByProjectCateCodeAndIsDelOrderByProjectCode(Pageable pageable,String projectCateCode,String isDel);
    Mono<Long> countByProjectCateCodeAndIsDel(String projectCateCode,String isDel);
    //根据项目编码查询项目
    Flux<GroupProject> findByProjectCodeAndProjectClassCodeOrderById(String projectCode,String projectClassCode);
    //根据项目编码查询是否存在
    Mono<GroupProject> countByProjectCodeAndProjectClassCode(String code,String projectClassCode);
    //根据项目名称查询项目
    Flux<GroupProject> findByProjectNameAndProjectClassCodeOrderById(String projectName,String projectClassCode);

    @Query("select * from _app_group_project where project_cate_code=:projectCateCode and is_del=:isDel order by success_state,project_code")
    Flux<GroupProject> findByProjectCateCodeAndIsDelOrderByProjectCode(String projectCateCode,String isDel);
    Flux<GroupProject> findAllByIsDelOrderByProjectCode(String isDel);

    Mono<GroupProject> findByUniqueCode(String uniqueCode);

    //根据组织查询所有未生效的人员
    @Query("select * from _app_group_project where ctype=:ctype and origin_id=:originId and success_state!='1' and is_del='0' order by project_code ")
    Flux<GroupProject> findByCtypeAndOriginId(String ctype,String originId);
    //根据租户查询所有未生效的人员
    @Query("select * from _app_group_project where ctype=:ctype and tenant_id=:tenantId and success_state!='1' and is_del='0' order by project_code ")
    Flux<GroupProject> findByCtypeAndTenantId(String ctype,String tenantId);
    //查询所有未生效的人员
    @Query("select * from _app_group_project where success_state!='1' and is_del='0'  order by project_code ")
    Flux<GroupProject> findByNotSuccessState();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(project_code,:num) as int)+1) as project_code from _app_group_project t1 " +
            "where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui and not exists(select * from _app_group_project t2 " +
            "where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui and cast(right(t2.project_code,:num) as int) = cast(right(t1.project_code,:num) as int) + 1) ")
    Mono<GroupProject> findBukongProjectCode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

    /**
     * 获取最大的编码
     */
    @Query("select Max(right(project_code,:num)) as project_code from _app_group_project where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui ")
    Mono<GroupProject> findMaxProjectCode(@Param("num") Integer num,@Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}
