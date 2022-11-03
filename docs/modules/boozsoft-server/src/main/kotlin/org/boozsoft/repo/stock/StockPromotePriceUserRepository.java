package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustPriceUser;
import org.boozsoft.domain.entity.stock.StockPromotePriceUser;
import org.boozsoft.domain.vo.stock.StockCustPriceUserVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface StockPromotePriceUserRepository extends ReactiveCrudRepository<StockPromotePriceUser, String> {


    @Query("select cust.cust_code, cust.cust_abbname, pu.* from stock_promote_price_user pu " +
            " left join customer cust on pu.cust_id = cust.id " +
            "where activity_id=:activityId ")
    Flux<StockCustPriceUserVo> findCustAll(String activityId);


    Flux<StockPromotePriceUser> findAllByActivityIdInAndCustId(List<String> activityIds,String cust);
}



