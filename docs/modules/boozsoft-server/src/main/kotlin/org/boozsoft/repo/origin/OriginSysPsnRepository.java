package org.boozsoft.repo.origin;

import org.boozsoft.domain.entity.origin.OriginSysPsn;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OriginSysPsnRepository extends ReactiveCrudRepository<OriginSysPsn, String> {

    Flux<OriginSysPsn> findAllByOriginIdAndIsDelOrderByPsnCode(Pageable pageable,String originId,String isDel);
    Flux<OriginSysPsn> findAllByOriginIdAndIsDelOrderByPsnCode(String originId,String isDel);
    Mono<Long> countAllByOriginIdAndIsDel(String originId,String isDel);
    Mono<Long> countByPsnCodeAndOriginId(String code,String originId);
    Flux<OriginSysPsn> findByFlagAndOriginIdAndIsDelOrderByPsnCode(String flag,String originId,String isDel);
    Flux<OriginSysPsn> findByFlagAndOriginIdAndIsDelOrderByPsnCode(Pageable pageable,String flag,String originId,String isDel);
    Mono<Long> countByFlagAndOriginIdAndIsDel(String flag,String originId,String isDel);
    @Query("select * from _app_origin_sys_psn where flag=:flag and origin_id=:originId and is_del=:isDel order by psn_code")
    Flux<OriginSysPsn> findAllByFlag(String flag,String originId,String isDel);
    Mono<OriginSysPsn> deleteAllByUniqueCodeAndOriginId(String key,String originId);

    Flux<OriginSysPsn> findAllByUniquePsnTypeAndOriginIdAndIsDelOrderByPsnCode(Pageable pageable,String uniquePsnType,String originId,String isDel);
    Flux<OriginSysPsn> findAllByUniquePsnTypeAndOriginIdAndIsDelOrderByPsnCode(String uniquePsnType,String originId,String isDel);
    Mono<Long> countAllByUniquePsnTypeAndOriginIdAndIsDel(String uniquePsnType,String originId,String isDel);

    Flux<OriginSysPsn> findAllByUniquePsnTypeAndFlagAndOriginIdAndIsDelOrderByPsnCode(Pageable pageable,String uniquePsnType,String flag,String originId,String isDel);
    Flux<OriginSysPsn> findAllByUniquePsnTypeAndFlagAndOriginIdAndIsDelOrderByPsnCode(String uniquePsnType,String flag,String originId,String isDel);
    Mono<Long> countAllByUniquePsnTypeAndFlagAndOriginIdAndIsDel(String uniquePsnType,String flag,String originId,String isDel);

    Mono<OriginSysPsn> findByOriginIdAndUniqueCode(String originId,String uniqueCode);

}
