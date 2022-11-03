package org.boozsoft.repo;

import org.boozsoft.domain.entity.UserJiGou;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface UserJiGouRepository extends ReactiveCrudRepository<UserJiGou, String> {

  Flux<UserJiGou> findAllByUserId(String userId);
}

