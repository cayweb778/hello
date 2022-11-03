package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.SysJobType;
import org.boozsoft.domain.entity.group.GroupSysJobType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysJobTypeRepository extends ReactiveCrudRepository<GroupSysJobType, String> {


    Flux<GroupSysJobType> findAllByOrderByEcCode();

    Flux<GroupSysJobType> findByFlagOrderByEcCode(String flag);

    @Query("SELECT max(ec_code) from _app_group_sys_job_type WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}
