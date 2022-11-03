package org.boozsoft.repo;

import org.boozsoft.domain.entity.AccClose;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;

public interface AccCloseRepository extends ReactiveCrudRepository<AccClose, String> {

    Flux<AccClose> findAllByIyearOrderByImonthAsc(String year);


    @Query("SELECT iyear,imonth from acc_close  WHERE 1=1 and  (gl is not null and gl = '1') and iyear in(:years) and imonth in (:months)  ORDER BY period asc")
    Flux<AccClose> findAllByGlCloseAndCondition(Set<String> years, Set<String> months);
}
