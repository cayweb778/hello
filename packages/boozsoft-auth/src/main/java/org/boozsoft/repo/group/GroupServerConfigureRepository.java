package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupServerConfigure;
import org.boozsoft.domain.entity.group.GroupTask;
import org.boozsoft.domain.vo.group.GroupTaskVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupServerConfigureRepository extends ReactiveCrudRepository<GroupServerConfigure, String> {
}
