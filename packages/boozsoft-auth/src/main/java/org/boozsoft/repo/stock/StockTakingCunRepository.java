
package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockTakingClass;
import org.boozsoft.domain.entity.stock.StockTakingCun;
import org.boozsoft.domain.vo.stock.StockTakingCunVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockTakingCunRepository extends ReactiveCrudRepository<StockTakingCun, String> {

    @Query("delete from stock_taking_cun where pid=:pid")
    Mono<Void> deleteByPid(String pid);

 /*   @Query("select * from stock_taking_class  stc left join stock_taking on st.ccode = stc.pid" +
            "where pid=:pid and st.cwhcode =:cwhcode and st.bcheck != '1' and stock_num in (:classList)")
    Flux<StockTakingCun> findAllByPidAndStockNum(String pid, String cwhcode, String[] classList);
*/
    @Query("select stc.*,sto.stock_class from stock_taking_cun  stc left join stock_taking st on st.ccode = stc.pid" +
            " left join stock sto on sto.stock_num = stc.stock_num " +
            " where st.cwhcode =:cwhcode and st.bcheck != '1'")
    Flux<StockTakingCunVo> findAllByPidAndStockNum( String cwhcode);

}






