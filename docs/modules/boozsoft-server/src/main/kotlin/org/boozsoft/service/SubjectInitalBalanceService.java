package org.boozsoft.service;

import org.springbooz.core.tool.result.R;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SubjectInitalBalanceService {
    Mono<R> findAllSubjectInitialBalance(String iyear,String lastCode,String iyperiod,String databasenum,String ccode,String cclass,String bend);
    Mono<R> findAllSubjectInitialBalanceNewFuZHu(String iyear,String lastCode,String iyperiod,String databasenum,String cclass);
    Mono<R> findAllSubjectInitialBalanceFuZhu(String iyear,String iyperiod, String databasenum1,String databasenum2);
    void testViewCodeKemu();
    Mono<R> findAllSubjectInitialBalanceFuZhuList(String iyear, String ccode, String databasenum);
    Mono<R> findAllSubjectInitialBalanceFuZhuListMingXi(String iyear, String ccode, String databasenum);
}
