package org.boozsoft.repo;

import org.boozsoft.domain.entity.group.SysUserAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysUserAccountRepository extends ReactiveCrudRepository<SysUserAccount, String> {
    Flux<SysUserAccount> findByUserIdOrderById(Long userId);
    Mono<SysUserAccount> deleteByUserId(Long userId);
}
