package org.boozsoft.repo;

import org.boozsoft.domain.entity.account.BankStatement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BankStatementRepository extends ReactiveCrudRepository<BankStatement, String> {

    Flux<BankStatement> findAllByOrderByStatementDate(Pageable pageable);
    Flux<BankStatement> findByKemuCodeOrderByStatementDate(String kemuCode);
//    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and statement_date between :date1 and :date2 ")
//    Flux<BankStatement> findByKemuCodeAndStatementDateOrderById(String kemuCode,String date1,String date2);
    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and iyear = :year order by statement_date  ")
    Flux<BankStatement> findByKemuCodeAndStatementDateOrderByStatementDate(String kemuCode,String year);
    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and iyear = :year and flag = :flag order by statement_date ")
    Flux<BankStatement> findByKemuCodeAndStatementDateAndFlagOrderByStatementDate(String kemuCode,String year,String flag);

//    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and statement_date < :qiyongDate order by statement_date ")
    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and qichu = :qichu order by statement_date ")
    Flux<BankStatement> findByKemuCodeAndDateOrderByStatementDate(String kemuCode,String qichu);

    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and statement_date between :date1 and :date2 and qichu!= :qichu ")
    Flux<BankStatement> findByKemuCodeAndDateOrderById(String kemuCode,String date1,String date2,String qichu);

    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and iyear =  :year and flag = :flag and statement_date <= :endDate order by statement_date ")
    Flux<BankStatement> findByKemuAndDate(String kemuCode,String year,String flag,String endDate);

    @Query(value = "select * from bank_statement where kemu_code = :kemuCode and iyear =  :year and flag = :flag and statement_date < :endDate order by statement_date ")
    Flux<BankStatement> findQichuByKemuAndDate(String kemuCode,String year,String flag,String endDate);

}
