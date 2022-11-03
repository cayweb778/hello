package org.boozsoft.repo.stock;

import org.boozsoft.domain.entity.stock.StockDefineBody;
import org.boozsoft.domain.entity.stock.StockDefineReceiptBody;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface StockDefineReceiptBodyRepository extends ReactiveCrudRepository<StockDefineReceiptBody, String> {

    @Query("select b.id,b.de_name,b.de_type,h.define_name as define_head_id from  stock_define_receipt_body b left join stock_define_receipt_head h on b.define_head_id=h.id where h.define_name=:headName ")
    Flux<StockDefineReceiptBody> findAllByDefineHeadName(String headName);

    @Query("delete from stock_define_receipt_body where id in (:list) ")
    Mono<Void> delById(List<String> list);
    @Query("delete from stock_define_receipt_body where define_head_id =:headId ")
    Mono<Void> delByHeadId(String headId);

    Mono<Long> countByDefineHeadIdAndDeName(String defineHeadId,String deName);
}
