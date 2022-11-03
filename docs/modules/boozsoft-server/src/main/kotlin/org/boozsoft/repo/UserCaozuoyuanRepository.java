//package org.boozsoft.repo;
//
//import org.boozsoft.domain.entity.UserCaozuoyuan;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//public interface UserCaozuoyuanRepository extends ReactiveCrudRepository<UserCaozuoyuan, String> {
//
//    Flux<UserCaozuoyuan> findAllByOrderByFlagDesc(Pageable pageable);
//
//    Mono<UserCaozuoyuan> findByUniqueCode(String uniqueCode);
//
//}
