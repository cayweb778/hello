package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginProjectClass;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectClassRepository extends ReactiveCrudRepository<OriginProjectClass, String> {

    Flux<OriginProjectClass> findByProjectCateCodeAndOriginIdOrderById(String projectCateCode,String originId);

    Flux<OriginProjectClass> findByProjectClassCodeAndOriginIdOrderById(String projectClassCode,String originId);

    Flux<OriginProjectClass> findByBendAndOriginIdOrderById(String bend,String originId);

    Flux<OriginProjectClass> findAllByOriginIdOrderByProjectClassCode(String originId);

    Flux<OriginProjectClass> findByParentIdAndOriginIdOrderByProjectClassCode(String parentId,String originId);
    @Query("delete from _app_origin_project_class where project_cate_code = :itemCode and project_class_code like :classCode and origin_id=:originId ")
    Mono<OriginProjectClass> deleteByItemCodeAndProjectClassCode(String itemCode, String classCode,String originId);
    @Query("delete from _app_origin_project_class where project_class_code like :classCode and origin_id=:originId ")
    Mono<OriginProjectClass> deleteByProjectClassCode(String classCode,String originId);
    @Query("select * from _app_origin_project_class where jici!='4' and origin_id=:originId order by project_class_code ")
    Flux<OriginProjectClass> findByNotJiciOrderByProjectClassCode(String originId);

}
