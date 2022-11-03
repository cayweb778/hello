package org.boozsoft.repo;


import org.boozsoft.repo.entity.Jigou;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface JigouRepository extends ReactiveCrudRepository<Jigou,String> {

   Flux<Jigou> findAllBy();
}
