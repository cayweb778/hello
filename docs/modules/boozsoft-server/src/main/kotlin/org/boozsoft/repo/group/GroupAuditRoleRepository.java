package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupAuditOperator;
import org.boozsoft.domain.entity.group.GroupAuditRole;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GroupAuditRoleRepository extends ReactiveCrudRepository<GroupAuditRole, String> {

}
