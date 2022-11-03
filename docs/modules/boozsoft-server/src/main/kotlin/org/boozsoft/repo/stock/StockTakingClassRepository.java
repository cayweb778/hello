package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockTakingClass;
import org.boozsoft.domain.vo.stock.StockTakingClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTakingClassRepository extends ReactiveCrudRepository<StockTakingClass, String> {


   @Query("delete from stock_taking_class where pid=:pid")
   Mono<Void> deleteByPid(String pid);

/*
   @Query("select stc.* from stock_taking_class stc left join stock_taking on st.ccode = stc.pid " +
           " where stc.pid=:pid and st.cwhcode =:cwhcode and st.bcheck != '1' and stc.stock_class in (:classList)")
    Flux<StockTakingClass> findAllByPidAndClass(String pid, String cwhcode, String[] classList);
*/

    @Query("select stc.* from stock_taking_class stc left join stock_taking st on st.ccode = stc.pid " +
            " where st.cwhcode =:cwhcode and st.bcheck != '1' ")
    Flux<StockTakingClassVo> findAllByPidAndClass(String cwhcode);
}



