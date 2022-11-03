package org.boozsoft.repo.group;

import org.boozsoft.domain.entity.group.GroupCodeKemuOrg;
import org.boozsoft.domain.vo.AOrgKemutemp;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface GroupCodeKemuOrgRepository extends ReactiveCrudRepository<GroupCodeKemuOrg, String> {
    Mono<GroupCodeKemuOrg> findAllByTemplateIdAndCcode(String templateId, String ccode);
    Mono<GroupCodeKemuOrg> findAllByCcodeAndOrgUniqueAndIyear(String ccode, String orgUnique,String iyear);
    Mono<GroupCodeKemuOrg> findAllByCcodeNameAndOrgUniqueAndIyear(String ccodeName, String orgUnique,String iyear);
    Flux<GroupCodeKemuOrg> findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(String uniqueAccStandard, String templateId);

    @Query("select * from _app_group_code_kemu_org where iyear=:iyear and org_unique=:orgUnique order by ccode ")
    Flux<GroupCodeKemuOrg> findAllByIyearAndorgUniqueOrderByCcodeAsc(String iyear,String orgUnique);
    Mono<Void> deleteAllByOrgUniqueAndIyear(String orgId, String iyear);

    Flux<GroupCodeKemuOrg> findAllByIyearAndOrgUniqueOrderByCcode(String year, String originId);

    @Query("update _app_group_code_kemu_org set flag=:flag where ccode in (:ccode) and iyear=:iyear and org_unique=:orgUnique")
    Mono<Void> editOrgCodeKemuFlag(String flag, List<String> ccode, String iyear, String orgUnique);

    @Query("SELECT acc.acc_name\n" +
            "FROM code_kemu c\n" +
            "left join  _app_group_sys_account acc on acc.acc_id=LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5)\n" +
            "WHERE LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5) IN\n" +
            "      (SELECT acc.acc_id FROM _app_group_sys_account acc WHERE acc.acc_group =:orgUnique)\n" +
            "  AND c.iyear =:iyear \n" +
            "  AND c.ccode =:ccode \n" +
            "ORDER BY ccode")
    Flux<String> findByOrgAccountCodeKemu(String orgUnique, String iyear, String ccode);

    @Query("SELECT acc.acc_name,\n" +
            "       (select count(acv.ccode) from accvoucher acv where acv.tenant_id = c.tenant_id and acv.ccode = c.ccode) accvoucher_count,\n" +
            "        (select count(ck.ccode) from code_kemu ck where ck.tenant_id = c.tenant_id and ck.superior_ccode = c.ccode) superior_ccode\n" +
            "FROM code_kemu c\n" +
            "         left join _app_group_sys_account acc on acc.acc_id = LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5)\n" +
            "WHERE LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5) IN\n" +
            "      (SELECT acc.acc_id FROM _app_group_sys_account acc WHERE acc.acc_group =:orgUnique )\n" +
            "  AND c.iyear =:iyear \n" +
            "  AND c.ccode =:ccode \n" +
            "ORDER BY ccode")
    Flux<AOrgKemutemp> findByOrgAccountCodeKemuSuperCodeAccvoucher(String orgUnique, String iyear, String ccode);

    Mono<Long> countBySuperiorCcodeAndOrgUniqueAndIyear(String superiorCcode,String orgUnique,String iyear);

    @Query("select string_agg(a.ccode, ',') from (select ccode from _app_group_code_kemu_org where superior_ccode=:superiorCcode and org_unique=:orgUnique and iyear=:iyear order by ccode asc) as a ")
    Mono<String> findBySuperiorCcodeStrAgg(String superiorCcode,String orgUnique,String iyear);

    @Query("SELECT distinct acc.acc_name\n" +
            "FROM task c\n" +
            "         left join _app_group_sys_account acc on acc.acc_id = LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5)\n" +
            "WHERE LEFT(c.tenant_id, LENGTH(c.tenant_id) - 5) IN\n" +
            "      (SELECT acc.acc_id FROM _app_group_sys_account acc WHERE acc.acc_group =:orgUnique )\n" +
            "and c.function_module=:functionModule ")
    Flux<String> findByAccountCodeKemuTask(String orgUnique,String functionModule);

    @Query("select * from _app_group_code_kemu_org where iyear=:iyear and org_unique=:orgUnique and ccode like :parentCode order by ccode desc ")
    Flux<GroupCodeKemuOrg> findByOrgKemuCodeLike(String orgUnique,String iyear,String parentCode);
}

