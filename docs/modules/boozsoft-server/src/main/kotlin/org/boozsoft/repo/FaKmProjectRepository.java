package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaKmProject;
import org.boozsoft.domain.vo.FaKmGdjzVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaKmProjectRepository extends ReactiveCrudRepository<FaKmProject, String> {

    @Query("SELECT  " +
            "  fat.project_code ecode,  " +
            "  fat.project_name ename,  " +
            "  ck.ccode kmcode,  " +
            "  ck.ccode_name kmname   " +
            "FROM  " +
            "  project fat  " +
            "  LEFT JOIN fa_km_project gd ON gd.project_code = fat.project_code   " +
            "  LEFT JOIN code_kemu ck ON gd.ccode = ck.ccode ")
    Flux<FaKmGdjzVo> findAllProject();

}
