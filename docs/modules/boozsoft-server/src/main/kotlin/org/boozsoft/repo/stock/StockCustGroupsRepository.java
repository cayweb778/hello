package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockCustGroups;
import org.boozsoft.domain.vo.CustomerVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockCustGroupsRepository extends ReactiveCrudRepository<StockCustGroups, String> {

    @Query("select cust_code from stock_cust_groups where ccode_group_ccode=:ccodeGroupCcode ")
    Flux<String> findAllCustCode(String ccodeGroupCcode);

    @Query("select sc.ccode_group_ccode,cl.cus_cclass_name as unique_custclass_name,scg.cust_group_name,c.* from stock_cust_groups sc " +
            "left join customer c on c.unique_code=sc.cust_code " +
            "left join customer_class cl on cl.unique_custclass=c.unique_custclass " +
            "left join stock_cust_group scg on scg.ccode =sc.ccode_group_ccode ")
    Flux<CustomerVo> findAllStockCustGroups();

    @Query("delete from stock_cust_groups where cust_code in (:custCode) and ccode_group_ccode=:ccodeGroupCcode ")
    Mono<Void> deleteByCustCode(List<String> custCode,String ccodeGroupCcode);

    @Query("delete from stock_cust_groups where ccode_group_ccode=:ccodeGroupCcode ")
    Mono<Void> deleteByCcodeGroupCcode(String ccodeGroupCcode);
}


