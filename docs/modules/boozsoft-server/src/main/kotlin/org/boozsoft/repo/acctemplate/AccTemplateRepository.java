package org.boozsoft.repo.acctemplate;

import org.boozsoft.domain.entity.acctemplate.AccTemplate;
import org.boozsoft.domain.vo.AccTemplateVo;
import org.boozsoft.domain.vo.CustomTemplateVo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccTemplateRepository extends ReactiveCrudRepository<AccTemplate, String> {
    @Query(value = "SELECT *,\n" +
            "       (SELECT string_agg(s.cclass, '-') FROM _app_group_acc_style as s WHERE s.unique =:accStyleUnique ) AS style_name,\n" +
            "       (SELECT string_agg(s.order, '-') FROM _app_group_acc_style as s WHERE s.unique = '6')  AS style_id\n" +
            "from _app_group_acc_template\n" +
            "WHERE unique_acc_standard =:uniqueAccStandard \n" +
            "  and t_type = :type \n" +
            "ORDER BY t_num")
    Flux<AccTemplateVo> findAllAssTemplate(String uniqueAccStandard,String accStyleUnique,String type);

    @Query(value = "SELECT *,( SELECT string_agg ( s.cclass, \'-\' ) FROM acc_style as s WHERE s.unique =:accStyleUnique ) AS style_name,( SELECT string_agg ( s.order, '-' ) FROM acc_style as s WHERE s.unique ='6' ) AS style_id from acc_template WHERE unique_acc_standard=:uniqueAccStandard ORDER BY t_num")
    Flux<AccTemplateVo> company_findAllAssTemplate(String uniqueAccStandard,String accStyleUnique);

    @Query("select count(1) from _app_group_acc_template where unique_acc_standard=:uniqueAccStandard and t_name=:tName and t_type=:ttype ")
    Mono<Long> countByUniqueAccStandardAndTName(String uniqueAccStandard,String tName,String ttype);

    @Query("select * from _app_group_acc_template where id=:id ")
    Mono<AccTemplate> findById(String id);

    @Query("SELECT ast.id,\n" +
            "       ast.t_name,\n" +
            "       ast.t_type,\n" +
            "       ast.unique_acc_standard,\n" +
            "       asst.acc_standard_name,\n" +
            "       ast.t_jici,\n" +
            "       ass.acc_style_unique,\n" +
            "       ass.code_first,\n" +
            "       ast.t_organization,\n" +
            "       ast.t_pid,\n" +
            "       ast.t_flg,\n" +
            "       (SELECT string_agg(s.cclass, '-') FROM _app_group_acc_style as s WHERE s.unique = ass.acc_style_unique) as style_name,\n" +
            "       (select t_name from _app_group_acc_template where id=ast.t_pid ) parent_name,\n" +
            "       (select org_name from _app_group_sys_org where unique_code=ast.t_organization ) org_name\n" +
            "from _app_group_acc_template ast\n" +
            "             left join _app_group_acc_standard ass\n" +
            "on ass.unique_acc_standard = ast.unique_acc_standard\n" +
            "            left join _app_group_acc_standard asst on asst.unique_acc_standard = ass.unique_acc_standard\n" +
            "WHERE ast.t_type=:ttype\n" +
            "ORDER BY ast.t_num")
    Flux<CustomTemplateVo> findByCustomTemplate(String ttype);

    Flux<AccTemplate> findByUniqueAccStandard(String uniqueAccStandard);

    @Query("select count(1) from _app_group_acc_template where t_type=:ttype and t_name=:tName ")
    Mono<Long> countByName(String ttype,String tName);
    @Query("select * from _app_group_acc_template where t_organization=:tOrganization  ")
    Flux<AccTemplate> findByTOrganization(String tOrganization);

    @Query("update _app_group_acc_template set t_name=:name,t_jici=:tjici where id=:id ")
    Mono<Void> editTemplateName(String id,String name,String tjici);

    @Query("select t.* from _app_group_sys_account left join _app_group_acc_template t on t.id = acc_standard where acc_id=:accId")
    Mono<AccTemplate> findTemplateByAccId(String accId);

    @Query("delete from _app_group_acc_template where t_type=:t_type and unique_acc_standard=:uniqueAccStandard ")
    Mono<Void> delByTypeAndUniqueStandarr(String t_type,String uniqueAccStandard);

    @Query("select t.unique_acc_standard,t.id from _app_group_sys_account a right join _app_group_acc_template t on t.id=a.acc_standard where a.acc_id=:databaseNum ")
    Mono<AccTemplate> findByAccId(String databaseNum);

    @Query("update _app_group_acc_template set t_name=:name where id=:id ")
    Mono<Void> editTemplateNameById(String id,String name);
}

