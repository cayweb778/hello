package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysMessage;
import org.boozsoft.domain.entity.SysTask;
import org.boozsoft.domain.vo.SysMessageUserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface SysTaskRepository extends ReactiveCrudRepository<SysTask, String> {


    @Query("select m.*,su.username as username  from _app_group_sys_task m" +
            " left join _app_group_sys_user su on su.id = m.sender " +
            " where m.sender = :user order by m.weights, m.send_time desc ")
    Flux<SysMessageUserVo> findByIfrag(String user);

    @Query("update _app_group_sys_task set ifrag = '1' where message_id = :id ")
    public Mono<Void> updateIfragById(String id);

    @Query("update _app_group_sys_task set ifrag = '2' where message_id = :id ")
    public Mono<Void> updateById(String id);

    @Query("delete from _app_group_sys_task where message_id in (:ids)")
    Mono<Void> deleteByIds(List ids);
}
