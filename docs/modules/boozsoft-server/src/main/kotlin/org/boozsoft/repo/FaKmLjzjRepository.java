package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaKmLjzj;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.vo.FaKmGdjzVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface FaKmLjzjRepository extends ReactiveCrudRepository<FaKmLjzj, String> {

    @Query("SELECT " +
            " fat.ec_code ecode, " +
            " fat.ec_name ename, " +
            " ck.ccode kmcode, " +
            " ck.ccode_name kmname  " +
            "FROM " +
            " fa_asset_type fat " +
            " LEFT JOIN fa_km_ljzj gd ON gd.asset_type_id = fat.ec_code  " +
            " LEFT JOIN code_kemu ck ON gd.ccode = ck.ccode  " +
            "WHERE " +
            " fat.bend = '1'  " +
            " AND fat.FLAG = '1'")
    Flux<FaKmGdjzVo> findAllLjzj();

}
