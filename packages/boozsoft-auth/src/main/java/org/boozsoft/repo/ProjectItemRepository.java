package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.ProjectItem;
import org.boozsoft.domain.entity.share.project.base.Project;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectItemRepository extends ReactiveCrudRepository<ProjectItem, String> {

    Flux<ProjectItem> findAllByOrderByItemCode(Pageable pageable);
    Flux<ProjectItem> findAllByOrderByItemCode();
    Flux<ProjectItem> findByFlagOrderByItemCode(String flag);
    Mono<Long> countAllBy();
    Flux<ProjectItem> findByItemCodeOrderByItemCode(String itemCode);
    Flux<ProjectItem> findByItemNameOrderByItemCode(String itemName);

    @Query("SELECT item_code, item_name, id from project_item WHERE 1=1 order by item_code Asc")
    Flux<ProjectItem> findAllItemCodeOrItemNameByAll();

    Mono<Void> deleteByItemCode(String code);
}

