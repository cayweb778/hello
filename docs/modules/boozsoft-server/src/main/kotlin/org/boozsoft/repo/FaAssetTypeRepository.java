package org.boozsoft.repo;

import org.boozsoft.domain.entity.FaAssetType;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.vo.FaAssetTypeVo;
import org.boozsoft.domain.vo.SysDeptClassVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FaAssetTypeRepository extends ReactiveCrudRepository<FaAssetType, String> {


    @Query("select dc.*, dc.ec_name as pname, fp.peoperty_name as aname, fz.zj_name as mname,um.unit_name as uname " +
            " from fa_asset_type dc " +
            " left join fa_asset_type zc on zc.id = dc.parent_id " +
            " left join fa_property fp on fp.id = dc.asset_att_id " +
            " left join fa_zhejiu_method fz on fz.id = dc.dep_method_id " +
            " left join sys_unit_of_mea um on um.id = dc.unit_id " +
            " order by dc.ec_code Asc")
    Flux<FaAssetTypeVo> findAllByOrderByEcCode();

    Flux<FaAssetType> findByFlagOrderByEcCode(String flag);

    Flux<FaAssetType> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    @Query("SELECT ec_code,ec_name,unique_code from fa_asset_type WHERE 1=1 and flag = '1' order by ec_code Asc")

    Flux<FaAssetType> findAllDeptCodeOrDeptNameByFlag();

    @Query("SELECT * from fa_asset_type WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<FaAssetType> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from fa_asset_type WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);

    @Query("SELECT cast(max(leave) as varchar ) from fa_asset_type ")
    Mono<String> findMaxLeave();

    Flux<FaAssetType> findByFlagAndBendAndZjTypeOrderByEcCode(String flag, String isEnd, String zjType);



}
