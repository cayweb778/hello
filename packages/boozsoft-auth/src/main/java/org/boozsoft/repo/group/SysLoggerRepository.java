package org.boozsoft.repo.group;


import org.boozsoft.domain.entity.group.SysLogger;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface SysLoggerRepository extends ReactiveCrudRepository<SysLogger, String> {
    Flux<SysLogger> findAllBy(Pageable pageable);
    Flux<SysLogger> findAllByIyearAndImonth(Pageable pageable,String year,String month);
    Flux<SysLogger> findAllByIyearAndImonthAndIday(Pageable pageable,String year,String month,String day);
}

