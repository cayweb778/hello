package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.boozsoft.domain.entity.group.GroupSysOrgDataaccess;
import org.boozsoft.domain.vo.AccCount2Vo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysOrgDataaccessRepository extends ReactiveCrudRepository<GroupSysOrgDataaccess, String> {
    Flux<GroupSysOrgDataaccess> findAllByOrgUniqueCodeOrderById(String org);
    Flux<GroupSysOrgDataaccess> findAllByUniqueCodeOrderById(String org);

}
