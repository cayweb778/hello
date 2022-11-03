package org.boozsoft.repo;

import org.boozsoft.domain.entity.SysAccTypeAuth;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SysAccTypeAuthRepository extends ReactiveCrudRepository<SysAccTypeAuth, String> {

    Flux<SysAccTypeAuth> findAllByUserNumAndAccIdAndIyear(String userId,String accId,String year);
    @Query("select * from sys_acc_auth_type where user_num=:userId and iyear=:year")
    Flux<SysAccTypeAuth> findAllByUserNumAndIyear(String userId,String year);
}
