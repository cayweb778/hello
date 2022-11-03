package org.boozsoft.rest.group;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupCodeKemuOrg;
import org.boozsoft.domain.vo.OrgPeriodCode;
import org.boozsoft.repo.GroupSysAccountRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.group.GroupCodeKemuOrgRepository;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/sys/org_code")
@Api(value = "集团客户信息", tags = "API系统：集团客户信息")
public class GroupOrgKeMuController {

    @Autowired
    GroupCodeKemuOrgRepository groupCodeKemuOrgRepository;
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcRouterLoader r2dbcRouterLoader;
    @Autowired
    GroupSysAccountRepository groupSysAccountRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;


    @PostMapping("/findAll")
    public Mono<R> findAll(String cclass,String orgIyear,String orgUnique) {
        return groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(orgIyear, orgUnique)
                .collectList().flatMap(obj->{
                    List<GroupCodeKemuOrg> list=obj.stream().filter(v-> StrUtil.isNotBlank(v.getIyear())&&v.getIyear().equals(orgIyear)).collect(Collectors.toList());
                    if(!cclass.equals("全部")){
                        list=obj.stream().filter(tx->tx.getCclass().equals(cclass)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(o -> R.ok().setResult(o));
    }


    // 两个方法 在主数据 客户分类中测试通过

    /**
     * 组织新建下年期间 执行 创建[复制上年科目]下年科目
     * @param newyear       新年度
     * @param orgUnique     组织唯一码
     * @return
     */
    @PostMapping("/orgNewYearCodeKeMu")
    public Mono<R> orgNewYearCodeKeMu(String newyear,String orgUnique){
        // 上年
        int upyear=Integer.valueOf(newyear)-1;
        return groupCodeKemuOrgRepository.findAllByIyearAndorgUniqueOrderByCcodeAsc(String.valueOf(upyear),orgUnique).collectList()
            .flatMap(copyUpYearCodeList->{
                // 没有返回空
                return copyUpYearCodeList.size()==0?Mono.just(""): Mono.just(copyUpYearCodeList)
                    .flatMap(copyUpCode->{
                        // 所属本组织的账套
                        return groupSysAccountRepository.findAllByAccGroup(orgUnique).collectList()
                            .flatMap(orgDataBase->{
                                List<GroupCodeKemuOrg> orgList=new ArrayList<>();
                                copyUpYearCodeList.forEach(cp->{
                                    GroupCodeKemuOrg org=new GroupCodeKemuOrg();
                                    org.setIyear(newyear)
                                            .setUniqueAccStandard(cp.getUniqueAccStandard())
                                            .setTemplateId(cp.getTemplateId())
                                            .setOrgUnique(orgUnique)
                                            .setCclass(cp.getCclass())
                                            .setCcode(cp.getCcode())
                                            .setCcodeName(cp.getCcodeName())
                                            .setIgrade(cp.getIgrade())
                                            .setBprogerty(cp.getBprogerty())
                                            .setCbookType(cp.getCbookType())
                                            .setUniqueCode(cp.getUniqueCode())
                                            .setBend(cp.getBend())
                                            .setFlag(cp.getFlag())
                                            .setLowerControl(cp.getLowerControl())
                                            .setFuzhuControl(cp.getFuzhuControl())
                                            .setBperson(cp.getBperson())
                                            .setBcus(cp.getBcus())
                                            .setBsup(cp.getBsup())
                                            .setBdept(cp.getBdept())
                                            .setBitem(cp.getBitem())
                                            .setCassItem(cp.getCassItem())
                                            .setBnum(cp.getBnum())
                                            .setBstock(cp.getBstock())
                                            .setBcash(cp.getBcash())
                                            .setBbank(cp.getBbank())
                                            .setBdaybook(cp.getBdaybook())
                                            .setCurrency(cp.getCurrency())
                                            .setBcashEquivalence(cp.getBcashEquivalence())
                                            .setControlled(cp.getControlled())
                                            .setPxjz(cp.getPxjz())
                                            .setXjll(cp.getXjll())
                                            .setCyfx(cp.getCyfx())
                                            .setYusuan(cp.getYusuan())
                                            .setYsBmzcjjfl(cp.getYsBmzcjjfl())
                                            .setYsYsly(cp.getYsYsly())
                                            .setYsZcgnfl(cp.getYsZcgnfl())
                                            .setYsZfzcjjfl(cp.getYsZfzcjjfl())
                                            .setCdfine1(cp.getCdfine1())
                                            .setCdfine2(cp.getCdfine2())
                                            .setCdfine3(cp.getCdfine3())
                                            .setCdfine4(cp.getCdfine4())
                                            .setCdfine5(cp.getCdfine5())
                                            .setCdfine6(cp.getCdfine6())
                                            .setCdfine7(cp.getCdfine7())
                                            .setCdfine8(cp.getCdfine8())
                                            .setCdfine9(cp.getCdfine9())
                                            .setCdfine10(cp.getCdfine10())
                                            .setCdfine11(cp.getCdfine11())
                                            .setCdfine12(cp.getCdfine12())
                                            .setCdfine13(cp.getCdfine13())
                                            .setCdfine14(cp.getCdfine14())
                                            .setCdfine15(cp.getCdfine15())
                                            .setCdfine16(cp.getCdfine16())
                                            .setCdfine17(cp.getCdfine17())
                                            .setCdfine18(cp.getCdfine18())
                                            .setCdfine19(cp.getCdfine19())
                                            .setCdfine20(cp.getCdfine20())
                                            .setCdfine21(cp.getCdfine21())
                                            .setCdfine22(cp.getCdfine22())
                                            .setCdfine23(cp.getCdfine23())
                                            .setCdfine24(cp.getCdfine24())
                                            .setCdfine25(cp.getCdfine25())
                                            .setCdfine26(cp.getCdfine26())
                                            .setCdfine27(cp.getCdfine27())
                                            .setCdfine28(cp.getCdfine28())
                                            .setCdfine29(cp.getCdfine29())
                                            .setCdfine30(cp.getCdfine30());
                                    orgList.add(org);
                                });

                                // 生成下年组织科目
                                return orgDataBase.size()==0?Mono.just(""):groupCodeKemuOrgRepository.saveAll(orgList)
                                    .collectList()
                                    .flatMap(cpSuccess->{
                                        // 账套所有科目
                                       return r2dbcRouterLoader.bind(()->client.sql("select * from code_kemu").fetch().all().collectList(),R2dbcRouterBuilder.ofDefault())
                                            .flatMap(databaseCode->{
                                                List<OrgPeriodCode> orgCodeVoList=new ArrayList<>();
                                                List<CodeKemu> ckList=new ArrayList<>();
                                                orgDataBase.forEach(accId->{

                                                    // 查询所属租户的科目
                                                    List<Map<String, Object>> collect = databaseCode.stream().filter(zt -> zt.get("tenant_id").toString().equals(accId.getAccId() + "-" + upyear)).collect(Collectors.toList());
                                                    if(collect.size()>0){
                                                        collect.forEach(v->{
                                                            CodeKemu ck=new CodeKemu();
                                                            ck.setIyear(newyear)
                                                                .setTenantId(v.get("tenant_id").toString())
                                                                .setUniqueAccStandard(v.get("unique_acc_standard").toString())
                                                                .setTemplateId(v.get("template_id").toString())
                                                                .setCclass(v.get("cclass").toString())
                                                                .setCcode(v.get("ccode").toString())
                                                                .setBend(v.get("bend").toString())
                                                                .setCcodeName(v.get("ccode_name").toString())
                                                                .setIgrade(v.get("igrade").toString())
                                                                .setBprogerty(v.get("bprogerty").toString())
                                                                .setCbookType(v.get("cbook_type").toString())
                                                                .setUniqueCode(v.get("unique_code").toString())
                                                                .setFlag(v.get("flag").toString())
                                                                .setLowerControl(v.get("lower_control").toString())
                                                                .setFuzhuControl(v.get("fuzhu_control").toString())
                                                                .setBperson(v.get("bperson").toString())
                                                                .setBcus(v.get("bcus").toString())
                                                                .setBsup(v.get("bsup").toString())
                                                                .setBdept(v.get("bdept").toString())
                                                                .setBitem(v.get("bitem").toString())
                                                                .setCassItem(v.get("cass_item")==null?"":v.get("cass_item").toString())
                                                                .setBnum(v.get("bnum").toString())
                                                                .setBstock(v.get("bstock")==null?"0":v.get("bstock").toString())
                                                                .setBcash(v.get("bcash").toString())
                                                                .setBbank(v.get("bbank").toString())
                                                                .setBdaybook(v.get("bdaybook").toString())
                                                                .setCurrency(v.get("currency").toString())
                                                                .setBcashEquivalence(v.get("bcash_equivalence").toString())
                                                                .setControlled(v.get("controlled").toString())
                                                                .setPxjz(v.get("pxjz").toString())
                                                                .setXjll(v.get("xjll").toString())
                                                                .setCyfx(v.get("cyfx").toString())
                                                                .setYusuan(v.get("yusuan").toString())
                                                                .setYsBmzcjjfl(v.get("ys_bmzcjjfl")==null?"0":v.get("ys_bmzcjjfl").toString())
                                                                .setYsYsly(v.get("ys_ysly")==null?"0":v.get("ys_ysly").toString())
                                                                .setYsZcgnfl(v.get("ys_zcgnfl")==null?"0":v.get("ys_zcgnfl").toString())
                                                                .setYsZfzcjjfl(v.get("ys_zfzcjjfl")==null?"0":v.get("ys_zfzcjjfl").toString())
                                                                .setIsDel(v.get("is_del").toString())
                                                                .setDelName(v.get("del_name")==null?"0":v.get("del_name").toString())
                                                                .setDelDate(v.get("del_date")==null?"0":v.get("del_date").toString())
                                                                .setCdfine1(v.get("cdfine1").toString())
                                                                .setCdfine2(v.get("cdfine2").toString())
                                                                .setCdfine3(v.get("cdfine3").toString())
                                                                .setCdfine4(v.get("cdfine4").toString())
                                                                .setCdfine5(v.get("cdfine5").toString())
                                                                .setCdfine6(v.get("cdfine6").toString())
                                                                .setCdfine7(v.get("cdfine7").toString())
                                                                .setCdfine8(v.get("cdfine8").toString())
                                                                .setCdfine9(v.get("cdfine9").toString())
                                                                .setCdfine10(v.get("cdfine10").toString())
                                                                .setCdfine11(v.get("cdfine11").toString())
                                                                .setCdfine12(v.get("cdfine12").toString())
                                                                .setCdfine13(v.get("cdfine13").toString())
                                                                .setCdfine14(v.get("cdfine14").toString())
                                                                .setCdfine15(v.get("cdfine15").toString())
                                                                .setCdfine16(v.get("cdfine16").toString())
                                                                .setCdfine17(v.get("cdfine17").toString())
                                                                .setCdfine18(v.get("cdfine18").toString())
                                                                .setCdfine19(v.get("cdfine19").toString())
                                                                .setCdfine20(v.get("cdfine20").toString())
                                                                .setCdfine21(v.get("cdfine21").toString())
                                                                .setCdfine22(v.get("cdfine22").toString())
                                                                .setCdfine23(v.get("cdfine23").toString())
                                                                .setCdfine24(v.get("cdfine24").toString())
                                                                .setCdfine25(v.get("cdfine25").toString())
                                                                .setCdfine26(v.get("cdfine26").toString())
                                                                .setCdfine27(v.get("cdfine27").toString())
                                                                .setCdfine28(v.get("cdfine28").toString())
                                                                .setCdfine29(v.get("cdfine29").toString())
                                                                .setCdfine30(v.get("cdfine30").toString());
                                                            ckList.add(ck);

                                                        });
                                                    }
                                                });
                                                List<String> collect = ckList.stream().map(CodeKemu::getTenantId).distinct().collect(Collectors.toList());
                                                collect.forEach(str->{
                                                    OrgPeriodCode op=new OrgPeriodCode();
                                                    op.setAccId(str).setCodeKemuList(ckList);
                                                    orgCodeVoList.add(op);
                                                });

                                                // 所属本组织的账套-生成下年科目
                                                return Mono.just(orgCodeVoList);
                                            });
                                    });
                            });
                    });
            }).map(a->R.ok().setResult(a));
    }

    /**
     * 组织下所属账套增加最新年度科目
     * @param list
     * @return
     */
    @PostMapping("/company_NewYearCodeSave")
    public Mono<R> company_NewYearCodeSave(@RequestBody List<CodeKemu> list){
        return codeKemuRepository.saveAll(list).collectList().map(a->R.ok().setResult(a));
    }
}
