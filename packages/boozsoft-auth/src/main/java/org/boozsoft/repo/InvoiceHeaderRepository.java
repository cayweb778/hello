package org.boozsoft.repo;

import org.boozsoft.domain.entity.invoice.InvoiceHeader;
import org.boozsoft.domain.vo.InvoiceHeaderVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InvoiceHeaderRepository extends ReactiveCrudRepository<InvoiceHeader, String> {

    Mono<Long> countByFapiaoCodeAndFapiaoNumber(String fapiaoCode, String fapiaoNumber);

    @Query("delete from invoice_header where id in (:ids)")
    Mono<Void> deleteByIds(List ids);

    @Query("update invoice_header set dj_status = '1'  where id in (:ids)")
    Mono<Void> updateDjStatusByIds(List ids);

    @Query("update invoice_header set rz_status = '1' where id in (:ids)")
    Mono<Void> updateRzStatusByIds(List ids);

    @Query("select * from invoice_header where id > :id order by id limit 1")
    Mono<InvoiceHeader> findByNextById(String id);

    @Query("select * from invoice_header where id < :id order by id limit 1")
    Mono<InvoiceHeader> findByUpById(String id);

    @Query("update invoice_header set accunique_code = :pzCode  where id in (:ids)")
    Mono<Void> updateAccuniqueCodeByIds(String pzCode, List ids);

    @Query("select distinct ih.*,acc.csign as csign,acc.ino_id as inoid  from invoice_header ih left join accvoucher acc on ih.accunique_code = acc.unique_code")
    Flux<InvoiceHeaderVo> findAllPzList();
}

