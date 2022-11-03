package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.Customer;
import org.boozsoft.domain.entity.stock.StockArCust;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockArCustRepository extends ReactiveCrudRepository<StockArCust, String> {

    @Query("select c.* from stock_ar_cust sac left join customer c on c.id=sac.cust_id order by cust_code ")
    Flux<Customer> findArCustLeftJoinCustomer();

    @Query("delete from stock_ar_cust where cust_id in (:custId)")
    Mono<Void> delSrCustByCustId(List<String> custId);

    @Query("select cust_id from stock_ar_cust ")
    Flux<String> getArCustCustId();
}



