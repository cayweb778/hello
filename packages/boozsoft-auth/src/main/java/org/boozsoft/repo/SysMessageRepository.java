package org.boozsoft.repo;

import org.boozsoft.domain.entity.AccClose;
import org.boozsoft.domain.entity.SysMessage;
import org.boozsoft.domain.entity.SysMessageType;
import org.boozsoft.domain.vo.SysMessageUserVo;
import org.springbooz.core.tool.result.R;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface SysMessageRepository extends ReactiveCrudRepository<SysMessage, String> {


    @Query("select m.*,su.username as username, ty.type_name typename, ty.id tid  from _app_group_sys_message m" +
            " left join _app_group_sys_user su on su.id = m.sender " +
            " left join _app_group_sys_message_type ty on ty.id = m.type_id " +
            " where m.sender = :user order by m.weights, m.send_time desc ")
    Flux<SysMessageUserVo> findByIfrag(String user);

    @Query("delete from _app_group_sys_message where message_id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("select * from _app_group_sys_message_type ")
    Flux<SysMessageType> findAllByType();

}
