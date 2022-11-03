package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysNotice;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface SysNoticeRepository extends ReactiveCrudRepository<SysNotice, String> {

}
