package org.boozsoft.repo.codekemu;

import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.CodeKemuVo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public interface CodeKemuRepository extends ReactiveCrudRepository<CodeKemu, String> {
    @Query("select * from code_kemu order by ccode")
    Flux<CodeKemu> findAll();
    Flux<CodeKemu> findAllByCclassAndFlagAndIyearAndBendOrderByCcodeAscBendAsc(String cclass,String flag,String year,String bend);
    Flux<CodeKemu> findAllByUniqueAccStandardAndTemplateId(String uniqueAccStandard,String templateId);

    Flux<CodeKemu> findAllByUniqueAccStandardAndTemplateIdOrderByCcodeAsc(String uniqueAccStandard,String templateId);
    Flux<CodeKemu> findAllByUniqueAccStandardAndTemplateIdAndCclassOrderByCcodeAsc(String uniqueAccStandard,String templateId,String cclass);
    Mono<Void> deleteAllByTemplateId(String id);
    @Modifying
    @Query("delete from code_kemu where 1=1 and tenant_id =:id ")
    Mono<Void> deleteAllByTenantId(String id);

    @Modifying
    @Query("delete from code_kemu where 1=1 and tenant_id =:id and  iyear=:year")
    Mono<Void> deleteAllByTenantId(String id,String year);

    @Query("SELECT * from code_kemu WHERE bbank=:br and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBbankOrderByCcodeAsc(String br);

    @Query("select count(id) from code_kemu where ccode=:ccode and iyear=:iyear and is_del!='1'")
    Mono<Long> countByCcode(String ccode,String iyear);
    Flux<CodeKemu> findAllByUniqueAccStandardOrderByCcodeAsc(String uniqueAccStandard);

    @Query("select * from code_kemu where flag=:s and iyear=:year and bend=:s1 and is_del!='1' order by ccode,bend ")
    Flux<CodeKemu> findAllByFlagAndIyearAndBendOrderByCcodeAscBendAsc(String s, String year, String s1);

    @Query("select * from code_kemu where flag=:s and iyear=:year and is_del!='1' order by ccode,bend ")
    Flux<CodeKemu> findAllByFlagAndIyearOrderByCcodeAscBendAsc(String s, String year);

    @Query("select count(id) from code_kemu where ccode_name=:ccodename and iyear=:iyear and is_del!='1' ")
    Mono<Long> countByCcodeName(String ccodename,String iyear);

    @Query("select count(id) from code_kemu where iyear=:year and ccode_name=:ccode and superior_ccode like :superCcod and is_del!='1'")
    Mono<Long> countByNameSuperCode(String year,String ccode,String superCcod);

    @Query("SELECT COALESCE(max(ccode), '0') from code_kemu WHERE unique_acc_standard=:uniqueAccStandard and is_del!='1' and template_id=:templateId AND superior_ccode=:ccodename and igrade=:igrade ")
    Mono<String> findByLowerMaxCodeNum(String uniqueAccStandard,String templateId,String ccodename,String igrade);

    @Query("select * from code_kemu where ccode=:ccode and is_del!='1' and iyear=:iyear ")
    Mono<CodeKemu> findByCcode(String ccode,String iyear);

    @Query("select count(id) from code_kemu where superior_ccode=:superiorCcode and is_del!='1' ")
    Mono<Long> countBySuperiorCcode(String superiorCcode);

    @Query("select string_agg(a.ccode, ',') from (select ccode from code_kemu where superior_ccode=:superiorCcode and is_del!='1' order by ccode asc) as a ")
    Mono<String> findBySuperiorCcodeStrAgg(String superiorCcode);

    @Query("select * from code_kemu where bend=:bend and iyear=:iyear and is_del!='1' order by ccode asc ")
    Flux<CodeKemu> findAllByBendAndIyearOrderByCcode(String bend,String iyear);

    @Query("SELECT * from code_kemu WHERE iyear=:iyear and bend='1' and is_del!='1' and (bperson in ('1') OR bdept in ('1') or bcus in ('1') or bsup in ('1')  or bitem in ('1')) ORDER BY ccode")
    Flux<CodeKemu> findByFuzhuCode(String iyear);

    @Query("select * from code_kemu where ccode=:ccode and iyear=:iyear and is_del!='1' ")
    Mono<CodeKemu> findByCcodeAndIyear(String ccode,String iyear);

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByIyear(String iyear);

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and tenant_id=:databasenum and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByIyearAndTenandId(String iyear,String databasenum);

   // @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and tenant_id=:databasenum and bend='1' and flag='1' order by cclass,ccode")
    @Query("    SELECT" +
            "    ck.*" +
            "    FROM" +
            "    code_kemu ck" +
            "    LEFT JOIN _app_group_acc_style st ON st.cclass = ck.cclass" +
            "   WHERE 1 = 1" +
            "    AND iyear = :year and is_del!='1' " +
            "    AND bend = '1'" +
            "    AND flag = '1'" +
            "    AND st.UNIQUE =" +
            "    (SELECT sac.acc_style_unique FROM _app_group_sys_account sa" +
            "    LEFT JOIN _app_group_acc_template acct ON sa.acc_standard= acct.id" +
            "    LEFT JOIN _app_group_acc_standard sac ON sac.unique_acc_standard = acct.unique_acc_standard" +
            "    where  acc_id = :databasenum)" +
            "  ORDER BY st.ORDER, ck.ccode ")
    Flux<CodeKemu> findAllByIyearAndBendAndFlag(String iyear,String databasenum);

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and cclass =:cclass  and bend='1' and flag='1' and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByIyearAndBendAndFlagAndClass(String iyear,String cclass);

    @Query("SELECT * from code_kemu WHERE iyear=:year and bend=:bend and tenant_id=:databasenum and is_del!='1' order by ccode")
    Flux<CodeKemu> findByIyearAndBendCode(String iyear,String bend,String databasenum);

    @Query("SELECT * from code_kemu WHERE iyear=:year and is_del!='1' and ccode like :ccode order by ccode")
    Flux<CodeKemu> findByIyearLikeCode(String iyear,String ccdoe);

    @Query("select * from code_kemu where iyear=:iyear and bend=:bend and is_del!='1' order by ccode")
    Flux<CodeKemu> findByIyearAndBendOrderByCcode(String iyear,String bend);

    @Query("SELECT * from code_kemu WHERE ccode between :strCode and :endCode and igrade between :strJc and :endJc and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByCcodeAndBendAndIgradeAndIyear(String strCode,String endCode,String strJc,String endJc,String iyear);

    /********************* Mr. Ye *******************/
    @Query("SELECT DISTINCT ccode,coalesce(flag,'0') as  flag from code_kemu WHERE 1=1 and is_del!='1' and iyear=:year order by ccode")
    Flux<CodeKemu> findAllByYear(String year);

    @Query("SELECT  ccode,ccode_name,bend,bcash,bbank from code_kemu WHERE 1=1 and is_del!='1' and (bcash='1' or bbank='1' or bcash_equivalence='1') order by ccode")
    Flux<CodeKemu> findAllByXjOrLi();

    @Query("select * from code_kemu where flag=:flag and bend=:bend and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByFlagAndBendOrderByCcodeAsc(String flag,String bend);

    @Query("SELECT DISTINCT ccode,ccode_name,coalesce(bnum,'0') as bnum,coalesce(currency,'0') as currency,coalesce(bcash,'0') as bcash,coalesce(bbank,'0') as bbank " +
            ",coalesce(bperson,'0') as  bperson,coalesce(bcus,'0') as bcus ,coalesce(bsup,'0') as bsup ,coalesce(bdept,'0') as bdept " +
            ",coalesce(bitem,'0') as bitem,project_class_id  from code_kemu WHERE 1=1 and  flag = '1' and bend = '1' and iyear=:year and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByYearAndBend(String year);

    @Query("SELECT * WHERE 1=1 and bend = '1' and iyear=:year order by ccode")
    Flux<CodeKemu> findAllByYearAndBendQiChu(String year);

    @Query("SELECT  ccode,ccode_name,bend,bcash,bbank,bcash_equivalence from code_kemu WHERE 1=1 and iyear = :year and is_del!='1' and (bcash='1' or bbank='1' or bcash_equivalence = '1') order by ccode")
    Flux<CodeKemu> findAllByYearAndCashierConditions(String year);

    /********************* Mr. Ye *******************/

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and is_del!='1' and bdept = '1' and tenant_id=:accId order by ccode")
    Flux<CodeKemu> findAllByIyearAndDept(String year, String accId);

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and is_del!='1' and bitem = '1' and tenant_id=:accId order by ccode ")
    Flux<CodeKemu> findAllByIyearAndProject(String year, String accId);

    @Query("select * from code_kemu where(ccode like :strcode or ccode like :endcode ) and igrade between :minjc and :maxjc and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByStrCodeAndEndCodeAndMinJcAndMaxJc(String strcode,String endcode,String minjc,String maxjc,String iyear);

    /************************* 获取辅助项的科目 ********************************/
    @Query("select * from code_kemu where bdept=:bdept and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBdeptAndIyearOrderByCcodeAsc(String bdept,String iyear);

    @Query("select * from code_kemu where bperson=:psn and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBpersonAndIyearOrderByCcodeAsc(String psn,String iyear);

    @Query("select * from code_kemu where bcus=:cus and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBcusAndIyearOrderByCcodeAsc(String cus,String iyear);

    @Query("select * from code_kemu where bsup=:sup and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBsupAndIyearOrderByCcodeAsc(String sup,String iyear);

    @Query("select * from code_kemu where bitem=:pro and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByBitemAndIyearOrderByCcodeAsc(String pro,String iyear);

    /******************************* 最新的框架结构查询 **********************************************/
    @Query("select unique_acc_standard,template_id from code_kemu where tenant_id=:databaseNum and iyear=:iyear and is_del!='1' group by unique_acc_standard,template_id ")
    Flux<CodeKemu> groupStandardAndTemplate(String databaseNum,String iyear);

    @Query("select * from code_kemu where iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> companyfindAll(String iyear, Pageable pageable);

    @Query("select * from code_kemu where iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> company_treeCode(String iyear);

    @Query("select * from code_kemu where iyear=:iyear and is_del!='1' and flag='1' order by ccode")
    Flux<CodeKemu> company_treeCodeAndFlag1(String iyear);

    @Query("select count(id) from code_kemu where tenant_id=:databaseNum and iyear=:iyear and is_del!='1' ")
    Mono<Long> company_countfindAll(String databaseNum,String iyear);

    Flux<CodeKemu> findByIyearAndTenantIdOrderByCcode(String iyear, String accId);

    @Query("select * from code_kemu where template_id=:template and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByTemplateIdOrderByCcode(String template);

    @Query("select * from code_kemu where iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> findByIyearOrderByCcode(String iyear);

    @Query("select * from code_kemu where iyear in (:iyear) and is_del!='1' order by iyear,ccode")
    Flux<CodeKemu> findByIyearInOrderByCcode(List<String> iyear);

    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and is_del!='1' and igrade between :minjc and :maxjc order by ccode")
    Flux<CodeKemu> findAllByIyearAndIgradeBetween(String iyear,String minjc,String maxjc);
    @Query("SELECT * from code_kemu WHERE 1=1 and iyear=:year and is_del!='1' and igrade between :minjc and :maxjc and ccode between :strCode and :endCode order by ccode")
    Flux<CodeKemu> findAllByIyearAndIgradeAndCodeBetween(String iyear,String minjc,String maxjc,String strCode,String endCode);

    @Query("delete from code_kemu where ccode in ( :list ) ")
    Mono<Void> delInCcode(List<String> list);
    @Query("update accvoucher set ccode=:newccode,ccode_name=:newccodename where ccode=:ccode")
    Mono<Void> updataAccvoucherCode(String newccode,String newccodename,String ccode);

    @Query("select * from code_kemu where is_del!='1' order by ccode ")
    Flux<CodeKemu> findAllCodeOrderByAsc();

    @Query("update code_kemu set pxjz=:flag where ccode in (:id) ")
    Mono<Void> editCodePxjz(String flag,List<String> id);
    @Query("update code_kemu set cyfx=:flag where ccode in (:id) ")
    Mono<Void> editCodeCyfx(String flag,List<String> id);
    @Query("update code_kemu set project_class_id=:flag where ccode in (:id) ")
    Mono<Void> editCodeProItem(String flag,List<String> id);

    //现金、银行、日记账、现金等价物科目查询
    @Query("select * from code_kemu where is_del!='1' or bcash='1' or bbank='1' or bdaybook='1' or bcash_equivalence = '1' order by ccode")
    Flux<CodeKemu> findByDayBook();

    @Query("update code_kemu set flag=:flag where ccode in (:id) and iyear=:iyear")
    Mono<Void> company_editflag(String flag,List<String> id,String iyear);

    /**
     * 处理删除下级科目，把上级科目改成非末级状态【还有下级情况下】
     * @return
     */
    @Query("UPDATE code_kemu set bend='0' where is_del!='1' and ccode in (select superior_ccode from code_kemu where superior_ccode in (select ccode from code_kemu where igrade='1' and bend='1' order by ccode) group by superior_ccode)")
    Mono<Void> company_editCodeFlag();

    /**
     * 计算一级科目编码长度
     * @return
     */
    @Query("select distinct length(ccode) from code_kemu where igrade='1' and is_del!='1'")
    Mono<Long> findByFirstCcodeLength();

    @Query("update code_kemu set project_class_id=:procalss where ccode in (:ccode) and is_del!='1'")
    Mono<Void> company_editproclass(String procalss,List<String> ccode);
    @Query("update code_kemu set project_class_id='' where project_class_id in (:project_class_id) and is_del!='1'")
    Mono<Void> company_editproclassNull(List<String> procalss);

    @Query("select ck.*, proclass.item_code as item_code, proclass.item_name as item_name from code_kemu ck left join project_item proclass on ck.project_class_id = proclass.id where ck.project_class_id is not null and ck.project_class_id !='' and is_del!='1' order by proclass.item_code,ck.ccode")
    Flux<CodeKemuVo> findCodeProClassAll();

    @Query("select * from code_kemu where superior_ccode=:superior_ccode and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllBySuperiorCcodeOrderByCcode(String superiorCcode);

    @Query("select * from code_kemu where flag=:flg and iyear=:year and bend=:bend and bdept=:bdept and is_del!='1' order by ccode")
    Flux<CodeKemu> findAllByFlagAndIyearAndBendAndBdeptOrderByCcodeAsc(String flg, String year, String bend, String bdept);

    @Query("select * from code_kemu where cclass='权益' and is_del!='1' order by ccode")
    Flux<CodeKemu> company_findByBenNianCode();

    @Query("select count(id) from code_kemu where superior_ccode=:superCode and is_del!='1' ")
    Mono<Long> company_findBySuperCode(String superCode);

    @Query("select * from code_kemu where bend=:bend and iyear=:iyear and is_del!='1' order by ccode")
    Flux<CodeKemu> company_findByLastCode(String bend,String iyear);

    @Query("select distinct left(ccode,1) from code_kemu where cclass='损益' and is_del!='1' ")
    Mono<String> company_findBySunYiCodeFirst();

    @Query("select * from code_kemu where iyear=:iyear and ccode=:ccode ")
    Mono<CodeKemu> company_findByIyearAndCcode(String iyear,String ccode);

    /** 获取辅助项末级科目已经设置期初的 **/
    @Query("select distinct ck.ccode, ck.ccode_name from  accvoucher a left join code_kemu ck on a.ccode=ck.ccode and imonth='21' where ck.bend = '1' and ck.iyear =:iyear order by ccode")
    Flux<CodeKemu> findAllByBendFuZhuCode(String iyear);

    @Query("update code_kemu set bcash=:flg where id in (:ids) ")
    Mono<Void> updateBcashByIds(String flg, ArrayList<String> ids);

    @Query("update code_kemu set bend=:flg where iyear=:iyear and ccode in (:ids)")
    Mono<Void> updateBendByIds(String flg,String iyear, List<String> ids);

    @Query("update code_kemu set is_del=:flg,del_name=:delName,del_date=:delDate where iyear=:iyear and ccode in (:ids)")
    Mono<Void> updateIsDelByCodeAndIyear(String flg,String delName,String delDate,String iyear, List<String> ids);

    @Query("select ck.*\n" +
            "from _app_group_data_authorization author\n" +
            "left join code_kemu ck on ck.tenant_id=author.tenantry_id and ck.unique_code=author.data_id\n" +
            "where author.tenantry_id =:tenantryId and author.operator_id=:userId \n" +
            "  and author.archives_id = 'code_kemu'\n" +
            "order by ck.ccode")
    Flux<CodeKemu> findByAuthorizationKeMu(String tenantryId,String userId);

    @Query("select ck.*\n" +
            "from _app_group_data_authorization author\n" +
            "left join code_kemu ck on ck.tenant_id=author.tenantry_id and ck.unique_code=author.data_id and author.iyear=:iyear\n" +
            "where author.tenantry_id =:tenantryId and author.operator_id=:userId \n" +
            "  and author.archives_id = 'code_kemu'\n" +
            "order by ck.ccode")
    Flux<CodeKemu> findByAuthorizationAndIyearKeMu(String tenantryId,String iyear,String userId);
}




