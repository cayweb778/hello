package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustPriceUser;
import org.boozsoft.domain.entity.stock.StockSupPriceUser;
import org.boozsoft.domain.vo.stock.StockCustPriceUserVo;
import org.boozsoft.domain.vo.stock.StockSupPriceUserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockSupPriceUserRepository extends ReactiveCrudRepository<StockSupPriceUser, String> {


    @Query("select cust.cust_code, cust.cust_abbname, pu.* from stock_sup_price_user pu left join supplier cust on pu.sup_id = cust.id ")
    Flux<StockSupPriceUserVo> findCustAll();

}



