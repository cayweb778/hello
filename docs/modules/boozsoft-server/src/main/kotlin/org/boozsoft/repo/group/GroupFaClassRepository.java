package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupFaClass;
import org.boozsoft.domain.entity.group.GroupFileEncodingRules;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface GroupFaClassRepository extends ReactiveCrudRepository<GroupFaClass, String> {
}
