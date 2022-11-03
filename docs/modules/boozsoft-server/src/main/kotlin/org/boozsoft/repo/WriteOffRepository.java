package org.boozsoft.repo;

import org.boozsoft.domain.entity.WriteOff;
import org.boozsoft.domain.entity.group.GroupDist;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface WriteOffRepository extends ReactiveCrudRepository<WriteOff, String> {

    @Query("select max(hx_code)  from write_off")
    Mono<String> findMaxHxCode();

    @Query("delete from write_off where vouch_un_code in (:list) ")
    Mono<Void> deleteByVouchUnCodes(List list);

    Mono<Long> countAllByVouchUnCodeIn(List<String> list);
}

