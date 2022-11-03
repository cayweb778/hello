package org.boozsoft.repo.stock.biandong;

import org.boozsoft.domain.entity.stock.biandong.StockSaleousingsBiandong;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockSaleousingsBiandongRepository extends ReactiveCrudRepository<StockSaleousingsBiandong, String> {

    Flux<StockSaleousingsBiandong> findAllByCcodeOrderByDdateAscCcodeAscLineIdAsc(String ccode);
    Flux<StockSaleousingsBiandong> findAllByCcodeLikeOrderByDdateAscCcodeAscLineIdAsc(String ccode);

}



