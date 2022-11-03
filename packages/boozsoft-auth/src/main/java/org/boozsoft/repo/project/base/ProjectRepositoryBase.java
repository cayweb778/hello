package org.boozsoft.repo.project.base;

import io.lettuce.core.dynamic.annotation.Param;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.boozsoft.domain.vo.ProjectVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectRepositoryBase extends ReactiveCrudRepository<Project,String> {
    /*//根据项目分类查询项目
    Flux<Project> findByProjectClassCodeAndProjectCateCodeOrderByProjectCode(String projectClassCode,String projectCateCode,Pageable pageable);
    Mono<Long> countByProjectClassCodeAndProjectCateCode(String projectClassCode,String projectCateCode);
    Flux<Project> findByProjectCateCodeOrderById(String projectCateCode,Pageable pageable);
    Mono<Long> countByProjectCateCode(String projectCateCode);
    Flux<Project> findAllByOrderByProjectCode();
    Flux<Project> findAllByItemCodeOrderByProjectCode(String itemCode);
    //根据生效状态查询项目
    Mono<Project> findByProjectCodeAndSuccessStateAndProjectCateCodeOrderByProjectCode(String projectCode, String successState,String projectCateCode);
    //根据项目编码查询项目
    Mono<Project> findByProjectCodeAndProjectCateCodeOrderByProjectCode(String projectCode,String projectCateCode);
    Mono<Project> findByProjectCodeOrderByProjectCode(String projectCode);
    Mono<Project> findByProjectCodeAndItemCodeOrderByProjectCode(String projectCode,String itemCode);
    //根据项目编码查询是否存在
    Mono<Project> countByProjectCodeAndProjectCateCode(String code,String projectCateCode);
    //根据项目名称查询项目
    Mono<Project> findByProjectNameAndProjectCateCodeOrderByProjectCode(String projectName,String projectCateCode);
    Mono<Project> findByProjectNameOrderByProjectCode(String projectName);
    Mono<Project> findByProjectNameAndItemCodeOrderByProjectCode(String projectName,String itemCode);
    //根据项目大类查询项目信息
    Flux<Project> findByProjectCateCodeAndTenantIdOrderByProjectCode(String projectCateCode,String accId);

    Flux<Project> findByProjectCateCodeOrderByProjectCode(String projectCateCode);
    Flux<Project> findByProjectCateCodeAndItemCodeOrderByProjectCode(String projectCateCode,String itemCode);

    @Query("select * from project order by project_code ")
    Flux<Project> findAllOrderByProCode();

    @Query("select pro.unique_code, pro.project_code, pro.project_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join project pro on pro.unique_code = acv.project_id\n" +
            "where pro.success_state = '1'\n" +
            "group by pro.unique_code, pro.project_code, pro.project_name, acv.ccode\n" +
            "order by pro.project_code")
    Flux<ProjectVo> findAllOrderByProCode2();

    @Query("SELECT project_code, project_name, project_cate_code,unique_code from project WHERE success_state='1' order by project_cate_code Asc,project_code Asc")
    Flux<Project> findAllProCodeOrProNameByFlag();

    Mono<Void> deleteAllByUniqueCode(String key);


    @Modifying
    @Query("delete from project where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("SELECT project_code, project_name, unique_code from project WHERE 1=1 order by project_code Asc")
    Flux<Project> findAllProjectCodeOrProjectNameByAll();*/

    //根据项目分类查询项目
    Flux<Project> findByProjectClassCodeAndProjectCateCodeAndIsDelOrderByProjectCode(String projectClassCode,String projectCateCode,String isDel,Pageable pageable);
    Mono<Long> countByProjectClassCodeAndProjectCateCodeAndIsDel(String projectClassCode,String projectCateCode,String isDel);
    Flux<Project> findByProjectCateCodeAndIsDelOrderByProjectCode(String projectCateCode,String isDel,Pageable pageable);
    Mono<Long> countByProjectCateCodeAndIsDel(String projectCateCode,String isDel);
    Flux<Project> findAllByIsDelOrderByProjectCode(String isDel);
    Flux<Project> findAllByItemCodeAndIsDelOrderByProjectCode(String itemCode,String isDel);
    //根据生效状态查询项目
    Mono<Project> findByProjectCodeAndSuccessStateAndProjectCateCodeOrderByProjectCode(String projectCode, String successState,String projectCateCode);
    //根据项目编码查询项目
    Mono<Project> findByProjectCodeAndProjectCateCodeOrderByProjectCode(String projectCode,String projectCateCode);
    Mono<Project> findByProjectCodeOrderByProjectCode(String projectCode);
    Mono<Project> findByProjectCodeAndItemCodeOrderByProjectCode(String projectCode,String itemCode);
    Flux<Project> findByProjectCodeAndProjectClassCodeOrderByProjectCode(String projectCode,String projectClassCode);
    //根据项目编码查询是否存在
    Mono<Project> countByProjectCodeAndProjectCateCode(String code,String projectCateCode);
    //根据项目名称查询项目
    Mono<Project> findByProjectNameAndProjectCateCodeOrderByProjectCode(String projectName,String projectCateCode);
    Mono<Project> findByProjectNameOrderByProjectCode(String projectName);
    Mono<Project> findByProjectNameAndItemCodeOrderByProjectCode(String projectName,String itemCode);
    Flux<Project> findByProjectNameAndProjectClassCodeOrderByProjectCode(String projectName,String projectClassCode);
    //根据项目大类查询项目信息
    Flux<Project> findByProjectCateCodeAndTenantIdAndIsDelOrderByProjectCode(String projectCateCode,String accId,String isDel);

    Flux<Project> findByProjectCateCodeAndIsDelOrderByProjectCode(String projectCateCode,String isDel);
    Flux<Project> findByProjectCateCodeAndItemCodeAndIsDelOrderByProjectCode(String projectCateCode,String itemCode,String isDel);
    Flux<Project> findByItemCodeAndIsDelOrderByProjectCode(String itemCode,String isDel);

    @Query("select * from project where is_del='0' order by project_code ")
    Flux<Project> findAllOrderByProCode();

    @Query("select pro.unique_code, pro.project_code, pro.project_name, acv.ccode\n" +
            "from accvoucher acv\n" +
            "         left join project pro on pro.unique_code = acv.project_id\n" +
            "where pro.success_state = '1' and pro.is_del = '0' \n" +
            "group by pro.unique_code, pro.project_code, pro.project_name, acv.ccode\n" +
            "order by pro.project_code")
    Flux<ProjectVo> findAllOrderByProCode2();

    @Query("SELECT project_code, project_name, project_cate_code,unique_code from project WHERE success_state='1' and is_del='0' order by project_cate_code Asc,project_code Asc")
    Flux<Project> findAllProCodeOrProNameByFlag();

    Mono<Void> deleteAllByUniqueCode(String key);


    @Modifying
    @Query("delete from project where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("SELECT project_code, project_name, unique_code from project WHERE 1=1 and is_del='0' order by project_code Asc")
    Flux<Project> findAllProjectCodeOrProjectNameByAll();

    /**
     * 获取缺少的最小数字
     */
    @Query("select MIN(cast(right(project_code,:num) as int)+1) as project_code from project t1 " +
            "where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui and not exists(select * from project t2 " +
            "where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui and cast(right(t2.project_code,:num) as int) = cast(right(t1.project_code,:num) as int) + 1) ")
    Mono<Project> findBukongProjectCode(@Param("num") Integer num, @Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

    /**
     * 获取最大的编码
     */
    @Query("select Max(right(project_code,:num)) as project_code from project where length(project_code)=:sum and left(project_code,:qzNum)=:qianzhui ")
    Mono<Project> findMaxProjectCode(@Param("num") Integer num,@Param("sum") Integer sum, @Param("qzNum") Integer qzNum, @Param("qianzhui") String qianzhui);

}
