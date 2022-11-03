package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaKmDept;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.vo.FaKmGdjzVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaKmDeptRepository extends ReactiveCrudRepository<FaKmDept, String> {


    @Query("SELECT  " +
            "  fat.dept_code ecode,  " +
            "  fat.dept_name ename,  " +
            "  ck.ccode kmcode,  " +
            "  ck.ccode_name kmname   " +
            "FROM  " +
            "  sys_department fat  " +
            "  LEFT JOIN fa_km_dept gd ON gd.dept_code = fat.ID   " +
            "  LEFT JOIN code_kemu ck ON gd.ccode = ck.ccode   " +
            "WHERE  " +
            "  fat.FLAG = '1'")
    Flux<FaKmGdjzVo> findAllDept();

}
