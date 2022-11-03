package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysDistRecord;
import org.boozsoft.domain.vo.SysDistRecordVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysDistReocrdRepository extends ReactiveCrudRepository<SysDistRecord, String> {
    @Query("SELECT acc_id,record_unique_code,c.id from _app_group_sys_dist_record INNER JOIN _app_group_sys_account on database_unique_code=unique_code INNER JOIN _app_group_customer C ON C.unique_code = record_unique_code WHERE record_unique_code=:recordUniqueCode ")
    Flux<SysDistRecordVo> findAllByRecordUniqueCode(String recordUniqueCode);

    Mono<Long> countByDatabaseUniqueCodeAndRecordUniqueCodeAndRecordType(String database,String unique,String type);
}
