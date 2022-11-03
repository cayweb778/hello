package org.boozsoft.repo;


import org.boozsoft.repo.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User,String> {

   Flux<User> findAllBy();
   Mono<User> findByOpenid(String openid);

}
