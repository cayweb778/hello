package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaKmGdjz;
import org.boozsoft.domain.vo.FaKmGdjzVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaKmGdjzRepository extends ReactiveCrudRepository<FaKmGdjz, String> {


    @Query("SELECT " +
            " gd.id id, " +
            " fat.ec_code ecode, " +
            " fat.ec_name ename, " +
            " ck.ccode kmcode, " +
            " ck.ccode_name kmname  " +
            "FROM " +
            " fa_asset_type fat " +
            " LEFT JOIN fa_km_gdzj gd ON gd.asset_type_id = fat.ec_code  " +
            " LEFT JOIN code_kemu ck ON gd.ccode = ck.ccode  " +
            "WHERE " +
            " fat.bend = '1'  " +
            " AND fat.FLAG = '1'")
    Flux<FaKmGdjzVo> findAllGdjz();

}
