package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupAuditUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GroupAuditSysUserRepository extends ReactiveCrudRepository<GroupAuditUser, String> {


}
