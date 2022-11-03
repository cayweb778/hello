package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AuditSysPsn;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuditSysPsnRepository extends ReactiveCrudRepository<AuditSysPsn, String> {

}
