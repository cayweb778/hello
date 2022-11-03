package org.boozsoft.repo;

import org.boozsoft.domain.entity.AppGroupPwdRule;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SysPwdRuleRepository extends ReactiveCrudRepository<AppGroupPwdRule, String> {
        @Query("select max(pwd_num) from _app_group_pwd_rule")
        Mono<String> findByMaxPwdNum();
        @Query("select count(pwd_name) from _app_group_pwd_rule where pwd_name=:pwdName")
        Mono<Long> findByPwdName(String pwdName);
}
