package org.boozsoft.repo;


import org.boozsoft.domain.entity.CashFlow;
import org.boozsoft.domain.vo.CashFlowVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CashFlowRepository extends ReactiveCrudRepository<CashFlow, String> {

    @Query("SELECT distinct" +
            " cf.*, " +
            "\tpo.project_code as projectnum,\n" +
            "\tpo.project_name as projectname,\n" +
            "\tpo.fangxiang as fx,\n" +
            "\tacc.cdigest as cdigest, \n" +
            "\t( SELECT ck.ccode_name FROM code_kemu ck WHERE ck.ccode = cf.ccode and ck.tenant_id = cf.tenant_id) AS codename,\n" +
            "\t( SELECT ck.ccode_name FROM code_kemu ck WHERE ck.ccode = cf.dccode and  ck.tenant_id = cf.tenant_id) AS dcodename \n" +
            "FROM " +
            " cash_flow cf  " +
            "\tLEFT JOIN accvoucher acc on acc.unique_code = cf.unique_code and acc.tenant_id = cf.tenant_id and cf.ccode = acc.ccode " +
            "\tLEFT JOIN project_cash po on po.project_code = cf.project_code and po.tenant_id = cf.tenant_id\n" +
            "WHERE " +
            " cf.unique_code = :uniqueCode and cf.tenant_id = :accId ")
    Flux<CashFlowVo> findByUniqueCode(@Param("uniqueCode") String uniqueCode, @Param("accId")String accId);

    Mono<Void> deleteByUniqueCode(String uniqueCode);

}

