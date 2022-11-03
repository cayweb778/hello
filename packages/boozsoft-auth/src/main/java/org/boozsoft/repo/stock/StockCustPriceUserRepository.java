package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustPrice;
import org.boozsoft.domain.entity.stock.StockCustPriceUser;
import org.boozsoft.domain.vo.stock.StockCustPriceUserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockCustPriceUserRepository extends ReactiveCrudRepository<StockCustPriceUser, String> {


    @Query("select cust.cust_code, cust.cust_abbname, pu.* from stock_cust_price_user pu left join customer cust on pu.cust_id = cust.id ")
    Flux<StockCustPriceUserVo> findCustAll();

    Flux<StockCustPriceUser> findAllByCustId(String cust);
}



