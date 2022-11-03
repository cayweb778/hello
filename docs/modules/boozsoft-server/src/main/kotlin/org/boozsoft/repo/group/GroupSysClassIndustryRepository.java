package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysClassIndustry;
import org.boozsoft.domain.entity.group.GroupSysOrg;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupSysClassIndustryRepository extends ReactiveCrudRepository<GroupSysClassIndustry, String> {

}
