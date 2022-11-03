package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ProjectClass;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectClassRepository extends ReactiveCrudRepository<ProjectClass, String> {

    Flux<ProjectClass> findByProjectCateCodeAndTenantIdOrderById(String projectCateCode, String accId);

    Flux<ProjectClass> findByProjectCateCodeOrderByProjectClassCode(String projectCateCode);
    Flux<ProjectClass> findAllByOrderByProjectClassCode();
    Flux<ProjectClass> findByParentIdOrderByProjectClassCode(String parentId);

    Flux<ProjectClass> findByProjectClassCodeOrderByProjectClassCode(String projectClassCode);

    Flux<ProjectClass> findByProjectClassCodeAndProjectCateCodeOrderByProjectClassCode(String projectClassCode, String projectCateCode);

    Flux<ProjectClass> findByBendAndProjectCateCodeOrderByProjectClassCode(String bend, String projectCateCode);
    Flux<ProjectClass> findByBendOrderByProjectClassCode(String bend);

    Mono<ProjectClass> deleteByProjectCateCode(String projectCateCode);


    @Modifying
    @Query("delete from project_class where tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Query("delete from project_class where project_cate_code = :itemCode and project_class_code like :classCode ")
    Mono<ProjectClass> deleteByItemCodeAndProjectClassCode(String itemCode,String classCode);
    @Query("delete from project_class where project_class_code like :classCode ")
    Mono<ProjectClass> deleteByProjectClassCode(String classCode);

    @Query("select * from project_class where project_cate_code = :itemCode and jici!='4' order by project_class_code ")
    Flux<ProjectClass> findByProjectCateCodeAndNotJiciOrderByProjectClassCode(String projectCateCode);
    @Query("select * from project_class where jici!='4' order by project_class_code ")
    Flux<ProjectClass> findByNotJiciOrderByProjectClassCode();

    Flux<ProjectClass> findByUniqueCode(String uniqueCode);

}
