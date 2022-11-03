package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AccAgingRange;

import org.boozsoft.domain.entity.account.StockPrintSetting;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface StockPrintSettingRepository extends ReactiveCrudRepository<StockPrintSetting, String> {

}
