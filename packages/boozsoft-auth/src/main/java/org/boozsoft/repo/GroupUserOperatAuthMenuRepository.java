package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.GroupUserOperatAuthColumn;
import org.boozsoft.domain.entity.group.GroupUserOperateAuthMenu;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface GroupUserOperatAuthMenuRepository extends ReactiveCrudRepository<GroupUserOperateAuthMenu, String> {

    Flux<GroupUserOperateAuthMenu> findAllByOrderByParentIdAscSortNoAsc();
}
