package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysMessage;
import org.boozsoft.domain.entity.SysMessageUser;
import org.boozsoft.domain.vo.SysMessageUserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.List;

public interface SysMessageUserRepository extends ReactiveCrudRepository<SysMessageUser, String> {

    @Query("select m.*, u.ifrag as ifrag, u.red_time as redtime, su.username as username, ty.type_name typename, ty.id tid" +
            " from _app_group_sys_message_user u" +
            " left join _app_group_sys_message m on u.message_id = m.message_id " +
            " left join _app_group_sys_user su on su.id = m.sender " +
            " left join _app_group_sys_message_type ty on ty.id = m.type_id " +
            " where user_id = :user order by M.weights, M.send_time desc ")
    public  Flux<SysMessageUserVo>  findByUserId(String user);

    @Query("select m.*, u.ifrag as ifrag, u.red_time as redtime, su.username as username, ty.type_name typename, ty.id tid" +
            " from _app_group_sys_message_user u" +
            " left join _app_group_sys_message m on u.message_id = m.message_id " +
            " left join _app_group_sys_user su on su.id = m.sender " +
            " left join _app_group_sys_message_type ty on ty.id = m.type_id " +
            " where user_id = :user order by M.weights, M.send_time desc LIMIT 4 ")
    public  Flux<SysMessageUserVo>  findByUserIdLimtFour(String user);

    @Query("select count(id) from _app_group_sys_message_user  where user_id = :user and ifrag = '0' " )
    public  Mono<Integer>  findNewMseeageCount(String user);

    @Query("update _app_group_sys_message_user set ifrag = '1', red_time=:localDate where message_id in (:ids) and user_id = :userId ")
    public  Mono<Void> updateIfragById(List<String> ids, String userId, LocalDate localDate);

    @Query("update _app_group_sys_message_user set ifrag = '2' where message_id in (:ids)  and user_id = :userId ")
    public Mono<Void> updateById(List<String> ids, String userId);

    @Query("delete from _app_group_sys_message_user where message_id = :messageId ")
    public Mono<Void> deleteByMessageId(String messageId);


    @Query("delete from _app_group_sys_message_user where message_id in (:ids)")
    Mono<Void> deleteByIds(List ids);
}
