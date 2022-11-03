package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginProject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginProjectRepository extends ReactiveCrudRepository<OriginProject,String> {
    //根据项目分类查询项目
    Flux<OriginProject> findByProjectClassCodeAndProjectCateCodeAndOriginIdOrderByProjectCode(Pageable pageable,String projectClassCode,String projectCateCode,String originId);
    Mono<Long> countByProjectClassCodeAndProjectCateCodeAndOriginId(String projectClassCode,String projectCateCode,String originId);
    Flux<OriginProject> findByProjectCateCodeAndOriginIdOrderByProjectCode(Pageable pageable,String projectCateCode,String originId);
    Mono<Long> countByProjectCateCodeAndOriginId(String projectCateCode,String originId);
    //根据项目编码查询项目
    Flux<OriginProject> findByProjectCodeAndProjectClassCodeAndOriginIdOrderById(String projectCode,String projectClassCode,String originId);
    //根据项目编码查询是否存在
    Mono<OriginProject> countByProjectCodeAndProjectClassCodeAndOriginId(String code,String projectClassCode,String originId);
    //根据项目名称查询项目
    Flux<OriginProject> findByProjectNameAndProjectClassCodeAndOriginIdOrderById(String projectName,String projectClassCode,String originId);

    Flux<OriginProject> findByProjectCateCodeAndOriginIdOrderByProjectCode(String projectCateCode,String originId);

    Flux<OriginProject> findAllByOriginIdOrderByProjectCode(String originId);

    Mono<OriginProject> findByUniqueCodeAndOriginId(String uniqueCode,String originId);

}
