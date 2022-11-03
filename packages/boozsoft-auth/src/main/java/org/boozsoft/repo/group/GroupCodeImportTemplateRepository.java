package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupCodeImportTemplate;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupCodeImportTemplateRepository extends ReactiveCrudRepository<GroupCodeImportTemplate, String> {

    Mono<Long> countByTname(String name);

    @Query("select * from _app_group_code_import_template order by ttype,flag desc ")
    Flux<GroupCodeImportTemplate> findAllorderflag();

    @Query("delete from _app_group_code_import_template where id in (:list) ")
    Mono<Void> findAllorderflag(List<String> list);
}
