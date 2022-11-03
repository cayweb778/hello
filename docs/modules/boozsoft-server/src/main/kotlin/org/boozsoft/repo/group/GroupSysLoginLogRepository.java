package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupSysLoginLog;
import org.boozsoft.domain.vo.GroupSysLoginLogVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GroupSysLoginLogRepository extends ReactiveCrudRepository<GroupSysLoginLog, String> {
    Mono<GroupSysLoginLog> findFirstByUserIdOrderByLoginTimeDesc(String unique);

    @Query("select acc.stock_acc_name,log.* from _app_group_sys_login_log log left join _app_group_stock_account acc on log.acc_id=acc.stock_acc_id where login_time like :date order by login_time desc limit :size offset :page")
    Flux<GroupSysLoginLogVo> findAllByLoginTimeLikeOrderByLoginTimeDesc(String date, int page, int size);


    @Query("select acc.stock_acc_name,log.* from _app_group_sys_login_log log left join _app_group_stock_account acc on log.acc_id=acc.stock_acc_id where (login_time >= :date and login_time <= :date2) and acc_id =:accId order by login_time desc limit :size offset :page")
    Flux<GroupSysLoginLogVo> findAllByLoginTimeBetweenOrderByLoginTimeDesc(String date,String date2,String accId, int page, int size);
    @Query("select acc.stock_acc_name,log.* from _app_group_sys_login_log log left join _app_group_stock_account acc on log.acc_id=acc.stock_acc_id where (login_time >= :date and login_time <= :date2) and acc_id =:accId and user_id =:userId order by login_time desc limit :size offset :page")
    Flux<GroupSysLoginLogVo> findAllByUserIdAndLoginTimeBetweenOrderByLoginTimeDesc(String date,String date2,String accId,String userId ,int page, int size);


    Mono<Long> countAllByLoginTimeLike(String date);
    Mono<Long> countAllByAccIdAndLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(String accId,String date,String date2);
    Mono<Long> countAllByAccIdAndUserIdAndLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(String accId,String userId,String date,String date2);

    @Query("select distinct substr(login_time, 0, 11)  as login_time from _app_group_sys_login_log where login_time like :date order by login_time")
    Flux<String> findDistinctByLoginTimeLikeOrderByLoginTime(String date);

    Mono<Long> countAllByUserId(String id);
}
