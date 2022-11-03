package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.AcceptBill;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AcceptBillRepository extends ReactiveCrudRepository<AcceptBill, String> {

    Flux<AcceptBill> findAllByOrderByDaoqiDate();
    Flux<AcceptBill> findByBillCodeOrderByDaoqiDate(String billCode);
    @Query("select * from accept_bill where daoqi_date > :date and flag = '0' order by daoqi_date")
    Flux<AcceptBill> findByDaoqiDate(String date);
    Flux<AcceptBill> findAllByQichuOrderByDaoqiDate(String qichu);

}
