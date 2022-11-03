package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.GroupTableAudit;
import org.boozsoft.domain.entity.group.SysLogger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface GroupTableAuditRepository extends ReactiveCrudRepository<GroupTableAudit, String> {

}

