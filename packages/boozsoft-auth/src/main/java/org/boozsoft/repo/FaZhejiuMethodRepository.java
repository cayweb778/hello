package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaZhejiuMethod;
import org.boozsoft.domain.entity.group.GroupFaZhejiuMethod;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaZhejiuMethodRepository extends ReactiveCrudRepository<FaZhejiuMethod, String> {


    Flux<FaZhejiuMethod> findAllByOrderByCreateDate();

    Flux<FaZhejiuMethod> findByFlagOrderByCreateDate(String flag);

    @Query("SELECT * from fa_zhejiu_method WHERE 1=1 and flag = '1' order by create_date Asc")
    Flux<FaZhejiuMethod> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from fa_zhejiu_method WHERE id = :id or parent_id = :id order by create_date asc")
    Flux<FaZhejiuMethod> findByIdOrderByDeptCode(String id);


    @Query("SELECT max(ec_code) from fa_zhejiu_method WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);

}
