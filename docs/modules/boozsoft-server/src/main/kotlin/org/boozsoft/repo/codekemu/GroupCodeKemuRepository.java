package org.boozsoft.repo.codekemu;

import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GroupCodeKemuRepository extends ReactiveCrudRepository<GroupCodeKemu, String> {
    Flux<GroupCodeKemu> findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc(String cclass,String flag,String year,String bend);
    Flux<GroupCodeKemu> findAllByUniqueAccStandardAndTemplateId(String uniqueAccStandard,String templateId);

    Flux<GroupCodeKemu> findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(String uniqueAccStandard,String templateId);
    Flux<GroupCodeKemu> findAllByTemplateIdOrderByCcodeAsc(String templateId);
    Flux<GroupCodeKemu> findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(String uniqueAccStandard,String templateId,String cclass);
    Mono<Void> deleteAllByTemplateId(String id);
    Flux<GroupCodeKemu> findByBbankOrderByCcodeAsc(String br);
    Mono<Long> countByUniqueAccStandardAndTemplateIdAndCcode(String uniqueAccStandard,String templateId,String ccode);
    Flux<GroupCodeKemu> findAllByUniqueAccStandardOrderByCcodeAsc(String uniqueAccStandard);

    Flux<GroupCodeKemu> findAllByFlagAndIyearAndBendOrderByCcodeAscBendAsc(String s, String year, String s1);
    Flux<GroupCodeKemu> findAllByFlagAndIyearOrderByCcodeAscBendAsc(String s, String year);
    Mono<Long> countByUniqueAccStandardAndTemplateIdAndCcodeName(String uniqueAccStandard,String templateId,String ccodename);
    @Query("select count(id) from _app_group_code_kemu where unique_acc_standard=:uniqueAccStandard and template_id=:templateId and ccode_name=:ccodename and superior_ccode like :superCcod ")
    Mono<Long> countByUniqueAccStandardAndTemplateIdAndCcodeNameAndSuperCode(String uniqueAccStandard,String templateId,String ccodename,String superCcod);

    @Query("SELECT COALESCE(max(ccode), '0') from _app_group_code_kemu WHERE unique_acc_standard=:uniqueAccStandard and template_id=:templateId AND superior_ccode=:ccodename and igrade=:igrade ")
    Mono<String> findByLowerMaxCodeNum(String uniqueAccStandard,String templateId,String ccodename,String igrade);
    Mono<GroupCodeKemu> findByUniqueAccStandardAndTemplateIdAndCcode(String uniqueAccStandard,String templateId,String ccode);
    Mono<Long> countByUniqueAccStandardAndTemplateIdAndSuperiorCcode(String uniqueAccStandard,String templateId,String superiorCcode);
    Mono<GroupCodeKemu> findByUniqueAccStandardAndTemplateIdAndSuperiorCcode(String uniqueAccStandard,String templateId,String superiorCcode);
    Flux<GroupCodeKemu> findAllByBendAndIyearOrderByCcode(String bend,String iyear);
    @Query("SELECT * from _app_group_code_kemu WHERE iyear=:iyear and bend='1' and (bperson in ('1') OR bdept in ('1') or bcus in ('1') or bsup in ('1')  or bitem in ('1')) ORDER BY ccode")
    Flux<GroupCodeKemu> findByFuzhuCode(String iyear);
    Mono<GroupCodeKemu> findByCcodeAndTemplateId(String ccode,String templateId);
    @Query("SELECT * from _app_group_code_kemu WHERE 1=1 and iyear=:year order by ccode")
    Flux<GroupCodeKemu> findAllByIyear(String iyear);
    @Query("SELECT * from _app_group_code_kemu WHERE unique_acc_standard=:uniqueAccStandard and template_id=:templateId and bend=:bend order by ccode")
    Flux<GroupCodeKemu> findByIyearAndBendCode(String uniqueAccStandard,String templateId,String bend);
    @Query("SELECT * from _app_group_code_kemu WHERE iyear=:year and ccode like :ccode order by ccode")
    Flux<GroupCodeKemu> findByIyearLikeCode(String iyear,String ccdoe);

    @Query("SELECT * from _app_group_code_kemu WHERE unique_acc_standard=:uniqueAccStandard and template_id=:templateId and ccode like :ccdoe2 order by ccode")
    Flux<GroupCodeKemu> findByUniqueAccStandardAndTemplateIdLikeCode(String uniqueAccStandard,String templateId,String ccdoe2);

    @Query("SELECT * from _app_group_code_kemu WHERE ccode between :strCode and :endCode and igrade between :strJc and :endJc and iyear=:iyear order by ccode")
    Flux<GroupCodeKemu> findAllByCcodeAndBendAndIgradeAndIyear(String strCode,String endCode,String strJc,String endJc,String iyear);
    @Query("select count(id) from _app_group_code_kemu where unique_acc_standard=:uniqueAccStandard and template_id=:templateId AND iyear=:year and ccode_name=:ccode and superior_ccode like :superCcod ")
    Mono<Long> countByNameSuperCode(String uniqueAccStandard,String templateId,String ccode,String superCcod,String year);

    Mono<Long> countBySuperiorCcodeAndTemplateId(String superiorCcode,String templateId);
    /********************* Mr. Ye *******************/
    @Query("SELECT DISTINCT ccode,coalesce(flag,'0') as  flag from _app_group_code_kemu WHERE 1=1 and iyear=:year order by ccode")
    Flux<GroupCodeKemu> findAllByYear(String year);

    @Query("SELECT  ccode,ccode_name,bend,bcash,bbank from _app_group_code_kemu WHERE 1=1 and (bcash='1' or bbank='1') order by ccode")
    Flux<GroupCodeKemu> findAllByXjOrLi();

    Flux<GroupCodeKemu> findAllByFlagAndBendOrderByCcodeAsc(String flag,String bend);

    @Query("SELECT DISTINCT ccode,ccode_name,coalesce(bnum,'0') as bnum,coalesce(currency,'0') as currency,coalesce(bcash,'0') as bcash,coalesce(bbank,'0') as bbank " +
            ",coalesce(bperson,'0') as  bperson,coalesce(bcus,'0') as bcus ,coalesce(bsup,'0') as bsup ,coalesce(bdept,'0') as bdept " +
            ",coalesce(bitem,'0') as bitem  from _app_group_code_kemu WHERE 1=1 and  flag = '1' and bend = '1' and iyear=:year order by ccode")
    Flux<GroupCodeKemu> findAllByYearAndBend(String year);

    @Query("SELECT  ccode,ccode_name,bend,bcash,bbank,bcash_equivalence from _app_group_code_kemu WHERE 1=1 and iyear = :year and (bcash='1' or bbank='1' or bcash_equivalence = '1') order by ccode")
    Flux<GroupCodeKemu> findAllByYearAndCashierConditions(String year);

    /********************* Mr. Ye *******************/

    @Query("SELECT * from _app_group_code_kemu WHERE 1=1 and iyear=:year and bdept = '1' order by ccode")
    Flux<GroupCodeKemu> findAllByIyearAndDept(String year);

    @Query("SELECT * from _app_group_code_kemu WHERE 1=1 and iyear=:year and bitem = '1' order by ccode")
    Flux<GroupCodeKemu> findAllByIyearAndProject(String year);

    @Query("select * from _app_group_code_kemu where(ccode like :strcode or ccode like :endcode ) and igrade between :minjc and :maxjc and iyear=:iyear order by ccode")
    Flux<GroupCodeKemu> findAllByStrCodeAndEndCodeAndMinJcAndMaxJc(String strcode,String endcode,String minjc,String maxjc,String iyear);

    Flux<GroupCodeKemu> findByBdeptAndIyearOrderByCcodeAsc(String bdept,String iyear);

    Flux<GroupCodeKemu> findAllByTemplateIdOrderByCcode(String templateid);

    Flux<GroupCodeKemu> findAllByUniqueAccStandardOrderByCcode(String accStandard);

    @Query("delete from _app_group_code_kemu WHERE unique_acc_standard=:uniqueAccStandard and template_id=:templateId")
    Mono<Void> delByStandardAndTemplate(String uniqueAccStandard,String templateId);

    @Query("select string_agg(a.ccode, ',') from (select ccode from _app_group_code_kemu where superior_ccode=:superiorCcode and template_id=:templateId order by ccode asc) as a ")
    Mono<String> findBySuperiorCcodeStrAgg(String superiorCcode,String templateId);

    @Query("delete from _app_group_code_kemu WHERE template_id=:templateId")
    Mono<Void> delByTemplate(String templateId);
}

