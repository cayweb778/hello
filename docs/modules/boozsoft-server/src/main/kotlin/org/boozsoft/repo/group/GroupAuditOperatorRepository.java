package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupAuditOperator;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupAuditOperatorRepository extends ReactiveCrudRepository<GroupAuditOperator, String> {

}
