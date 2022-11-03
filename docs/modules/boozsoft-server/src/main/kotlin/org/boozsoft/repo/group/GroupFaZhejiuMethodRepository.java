package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupFaAddCutMode;
import org.boozsoft.domain.entity.group.GroupFaProperty;
import org.boozsoft.domain.entity.group.GroupFaZhejiuMethod;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaZhejiuMethodRepository extends ReactiveCrudRepository<GroupFaZhejiuMethod, String> {


    Flux<GroupFaZhejiuMethod> findAllByOrderByCreateDate();

    Flux<GroupFaZhejiuMethod> findByFlagOrderByCreateDate(String flag);

    @Query("SELECT * from _app_group_fa_zhejiu_method WHERE 1=1 and flag = '1' order by create_date Asc")
    Flux<GroupFaZhejiuMethod> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from _app_group_fa_zhejiu_method WHERE id = :id or parent_id = :id order by create_date asc")
    Flux<GroupFaZhejiuMethod> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from _app_group_fa_zhejiu_method WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}
