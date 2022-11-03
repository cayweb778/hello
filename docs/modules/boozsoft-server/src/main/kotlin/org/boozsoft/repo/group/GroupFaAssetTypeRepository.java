package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.FaAssetType;
import org.boozsoft.domain.entity.group.GroupFaAssetType;
import org.boozsoft.domain.vo.FaAssetTypeVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupFaAssetTypeRepository extends ReactiveCrudRepository<GroupFaAssetType, String> {


    @Query("select dc.*, dc.ec_name as pname, fp.peoperty_name as aname, fz.zj_name as mname,um.unit_name as uname " +
            " from _app_group_fa_asset_type dc " +
            " left join _app_group_fa_asset_type zc on zc.id = dc.parent_id " +
            " left join _app_group_fa_property fp on fp.id = dc.asset_att_id " +
            " left join _app_group_fa_zhejiu_method fz on fz.id = dc.dep_method_id " +
            " left join _app_group_sys_unit_of_mea um on um.id = dc.unit_id " +
            " order by dc.ec_code Asc")
    Flux<FaAssetTypeVo> findAllByOrderByEcCode();

    Flux<GroupFaAssetType> findByFlagOrderByEcCode(String flag);

    Flux<GroupFaAssetType> findByFlagAndBendOrderByEcCode(String flag, String isEnd);

    /********************* Mr. Ye *******************/
    @Query("SELECT ec_code,ec_name,unique_code from _app_group_fa_asset_type WHERE 1=1 and flag = '1' order by ec_code Asc")
    Flux<GroupFaAssetType> findAllDeptCodeOrDeptNameByFlag();
    /********************* Mr. Ye *******************/

    @Query("SELECT * from _app_group_fa_asset_type WHERE id = :id or parent_id = :id order by ec_code asc")
    Flux<GroupFaAssetType> findByIdOrderByDeptCode(String id);

    @Query("SELECT max(ec_code) from _app_group_fa_asset_type WHERE parent_id = :pid ")
    Mono<String> findMaxCodeByPid(String pid);
}
