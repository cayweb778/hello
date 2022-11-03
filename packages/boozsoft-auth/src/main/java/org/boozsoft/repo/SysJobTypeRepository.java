package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysJobType;
import org.boozsoft.domain.entity.SysZfClass;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysJobTypeRepository extends ReactiveCrudRepository<SysJobType, String> {


    Flux<SysJobType> findAllByOrderByEcCode();

    Flux<SysJobType> findByFlagOrderByEcCode(String flag);

    @Query("SELECT max(ec_code) from sys_job_type WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}
