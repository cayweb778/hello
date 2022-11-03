package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.GroupAllotRecord;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupAllotRecordRepository extends ReactiveCrudRepository<GroupAllotRecord, String> {
    Flux<GroupAllotRecord> findAll();
    Flux<GroupAllotRecord> findAllByAccIdAndDataType(String accId,String dataType);

    @Query("update _app_group_allot_record set allot_type=:acclotType where data_unique=:dataUnique and acc_id=:accId ")
    Mono<Void> editAcclotType(String accId,String dataUnique,String acclotType);
}
