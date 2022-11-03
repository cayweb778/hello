package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupProjectClass;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupProjectClassRepository extends ReactiveCrudRepository<GroupProjectClass, String> {

    Flux<GroupProjectClass> findByProjectCateCodeOrderById(String projectCateCode);

    Flux<GroupProjectClass> findByProjectClassCodeOrderById(String projectClassCode);

    Flux<GroupProjectClass> findByBendOrderByProjectClassCode(String bend);

    Flux<GroupProjectClass> findAllByOrderByProjectClassCode();

    Flux<GroupProjectClass> findByParentIdOrderByProjectClassCode(String parentId);
    @Query("delete from _app_group_project_class where project_cate_code = :itemCode and project_class_code like :classCode order by project_class_code ")
    Mono<GroupProjectClass> deleteByItemCodeAndProjectClassCode(String itemCode, String classCode);
    @Query("delete from _app_group_project_class where project_class_code like :classCode order by project_class_code ")
    Mono<GroupProjectClass> deleteByProjectClassCode(String classCode);
    @Query("select * from _app_group_project_class where jici!='4' order by project_class_code ")
    Flux<GroupProjectClass> findByNotJiciOrderByProjectClassCode();

}
