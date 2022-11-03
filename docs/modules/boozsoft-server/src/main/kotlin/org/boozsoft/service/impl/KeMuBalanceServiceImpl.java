package org.boozsoft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.AccvoucherVo;
import org.boozsoft.domain.vo.KeMuBalanceVo;
import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.SysLogRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.KeMuBalanceService;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName : KeMuMingXiServiceImpl
 * @Description : 科目明细账
 * @Author : miao
 * @Date: 2021-06-23 14:57
 */
@Slf4j
@Service
public class KeMuBalanceServiceImpl implements KeMuBalanceService {

    @Autowired
    SubjectInitalBalanceService subjectInitalBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    SysLogRepository logRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccStyleRepository accStyleRepository;

    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Author myh
     * @Description 查询科目明细账
     * @Date 9:28 2021/6/25
     * @Param [bend：2是1否末级, strDate-开始期间, endDate-结束期间, maxJc-最大级次, minJc-最小级次,timflg-按 rq日期还是 qj期间查询; ishaveRjz-1包含未记账\0不包含;bz-币种,parameterAccuracyList-保留几位小数]
     **/
    public Mono<R>
    findAll(Integer page,Integer size,Pageable pageable,String bend, String strDate, String endDate, String maxJc, String minJc, String maxKm, String minKm, String timflg, String ishaveRjz,
                           String bz, String styleValue, Map<String, String> searchMap, Map<String, String> filterMap, List<AccStyle> styleList,
            String database,String querytype,String accvoucherType,List<CodeKemu> kmList) {
        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);
        String newbend = bend.equals("1") ? "false" : "true"; // 是否末级 1否 2是
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return Mono.just(styleList)
                .flatMap(accStyles->{
                    List<AccStyle>newaccstylelist=accStyles;
                    if("财务会计".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> StringUtils.isBlank(s.getFlagYusuan())).collect(Collectors.toList());
                    }else if("预算会计".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> StringUtils.isNotBlank(s.getFlagYusuan())&&s.getFlagYusuan().equals("1")).collect(Collectors.toList());
                    }else if(!"全部".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> s.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }
                  return Mono.just(newaccstylelist);
                })
                .flatMap(accStyles -> {
                    Map map = new HashMap();
                    map.put("paccStylelist", accStyles);  // 科目类型list
                    return subjectInitalBalanceService.findAllSubjectInitialBalance(year, newbend,year+"00",database,"","全部","类型")
                            // 【期初list】
                            .flatMap(qcl -> {
                                Map qcmap = (Map) qcl.getResult();
                                map.put("qclist", qcmap.get("tablesData")); // 期初list
                                return accvoucherRepository.findByIyearVo(year+"01",endDate).collectList()
                                        // 获取本年度所有正常凭证
                                        .flatMap(pzlist -> {
                                            List<AccvoucherVo> newpzlist=pzlist;
                                            if(ishaveRjz.equals("0")){
                                                newpzlist = pzlist.stream().filter(vo -> org.springbooz.core.tool.utils.StringUtils.isNotBlank(vo.getIbook()) && vo.getIbook().equals("1")).collect(Collectors.toList());
                                            }
                                            // 按币种过滤
                                            if(!"全部".equals(bz)){
                                                newpzlist=newpzlist.stream().filter(p->p.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                                            }
                                            // 按照凭证类型查找
                                            if(StringUtils.isNotBlank(accvoucherType)){
                                                List<String> str = Arrays.asList(accvoucherType.split(","));
                                                newpzlist=newpzlist.stream().filter(p->str.indexOf(p.getCsign())!=-1).collect(Collectors.toList());
                                            }
                                            map.put("pzlist", newpzlist); // 凭证list

                                            return StringUtils.isNotBlank(minKm)||StringUtils.isNotBlank(maxKm)?codeKemuRepository.findAllByStrCodeAndEndCodeAndMinJcAndMaxJc(minKm+"%",maxKm+"%",minJc,maxJc,year).collectList()
                                                    .flatMap(cl -> {
                                                        List<CodeKemu> codelist = cl;
                                                        // 按照末级科目条件：不等于1 是 末级科目条件。
                                                        if ("2".equals(bend)) {
                                                            codelist = cl.stream().filter(ck -> ck.getBend().equals("1")&&Integer.valueOf(ck.getIgrade())<=Integer.valueOf(maxJc)&&Integer.valueOf(ck.getIgrade())>=Integer.valueOf(minJc)).collect(Collectors.toList());
                                                        }else{
                                                            codelist = cl.stream().filter(ck -> Integer.valueOf(ck.getIgrade())<=Integer.valueOf(maxJc)&&Integer.valueOf(ck.getIgrade())>=Integer.valueOf(minJc)).collect(Collectors.toList());
                                                        }
                                                        map.put("codelist", codelist);
                                                        return Mono.just(map);
                                                    }):codeKemuRepository.findAllByIyear(year).collectList()
                                                    .flatMap(cl -> {
                                                        List<CodeKemu> codelist = cl;
                                                        // 按照末级科目条件：不等于1 是 末级科目条件。
                                                        if ("2".equals(bend)) {
                                                            codelist = cl.stream().filter(ck -> ck.getBend().equals("1")).collect(Collectors.toList());
                                                        }else{
                                                            codelist = cl.stream().filter(ck -> Integer.valueOf(ck.getIgrade())<=Integer.valueOf(maxJc)&&Integer.valueOf(ck.getIgrade())>=Integer.valueOf(minJc)).collect(Collectors.toList());
                                                        }
                                                        map.put("codelist", codelist);
                                                        return Mono.just(map);
                                                    });
                                        });
                            });
                })
                .flatMap(map -> {
                    List<AccStyle> accStyleList = (List<AccStyle>) map.get("paccStylelist");
                    List<CodeKemu> oldCodelist = (List<CodeKemu>) map.get("codelist");
                    // 取交集
                    List<CodeKemu> codelist=new ArrayList<>();
                    kmList.forEach(a->{
                        List<CodeKemu> collect = oldCodelist.stream().filter(b -> b.getCcode().equals(a.getCcode())).collect(Collectors.toList());
                        if(collect.size()>0){
                            codelist.add(collect.get(0));
                        }
                    });

                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) map.get("qclist");
                    qclist.stream().filter(a->kmList.indexOf(a.getCcode())!=-1).collect(Collectors.toList());

                    List<AccvoucherVo> pzlist = (List<AccvoucherVo>) map.get("pzlist");
                    pzlist.stream().filter(a->kmList.indexOf(a.getCcode())!=-1).collect(Collectors.toList());

                    List<KeMuBalanceVo> keMuBalanceVoList = new ArrayList<>();
                    List<KeMuBalanceVo> finalKeMuBalanceVoList = keMuBalanceVoList;
                    accStyleList.stream().forEach(accStyle -> {
                        // 按照科目类型重新查询科目
                        List<CodeKemu> newcodelist = codelist.stream().filter(ck -> ck.getCclass().equals(accStyle.getCclass())).collect(Collectors.toList());
                        if(newcodelist.size()>0){
                            BigDecimal accStyleQcMdTotal=new BigDecimal(0);
                            BigDecimal accStyleQcMcTotal=new BigDecimal(0);
                            BigDecimal accStyleQcNumTotal=new BigDecimal(0);
                            BigDecimal accStyleQcNfratTotal=new BigDecimal(0);

                            BigDecimal accStylePzMdTotal=new BigDecimal(0);
                            BigDecimal accStylePzMcTotal=new BigDecimal(0);
                            BigDecimal accStylePzNumTotal=new BigDecimal(0);
                            BigDecimal accStylePzNfratTotal=new BigDecimal(0);

                            BigDecimal accStyleLjMdTotal=new BigDecimal(0);
                            BigDecimal accStyleLjMcTotal=new BigDecimal(0);
                            BigDecimal accStyleLjNumTotal=new BigDecimal(0);
                            BigDecimal accStyleLjNfratTotal=new BigDecimal(0);

                            BigDecimal accStyleQmMdTotal=new BigDecimal(0);
                            BigDecimal accStyleQmMcTotal=new BigDecimal(0);
                            BigDecimal accStyleQmNumTotal=new BigDecimal(0);
                            BigDecimal accStyleQmNfratTotal=new BigDecimal(0);
                            newcodelist.stream().forEach(newcode -> {
                                KeMuBalanceVo vo=new KeMuBalanceVo();
                                String ccode = newcode.getCcode();
                                String ccodeName = newcode.getCcodeName();
                                String cclass = newcode.getCclass();
                                String bprogerty = newcode.getBprogerty();  // 方向
                                String findByBend = newcode.getBend();      // 末级

                                // 是下级级科目 并且 不是末级；标识改成 末级；【特殊情况：例如 级次1-2；科目 100201【是下级科目 并且 不是末级科目】 小计 要汇总这条科目 】
//                                if(!newcode.getSuperiorCcode().equals("0")){
                                    // 查询下级是否存在集合中
                                    List<CodeKemu> collect = newcodelist.stream().filter(f -> f.getSuperiorCcode().equals(ccode)).collect(Collectors.toList());
                                    if(collect.size()==0){
                                        findByBend=findByBend.equals("0")?"1":findByBend;
                                    }
//                                }
                                String unitMeasurement = newcode.getMenterage();
                                String foreignCurrency = newcode.getCurrencyType();
                                String igrade = newcode.getIgrade();

                                // 检索科目获取期初
                                List<SubjectInitialBalanceVo> newqclist = qclist.stream().filter(qc -> qc.getCcode().equals(newcode.getCcode())).collect(Collectors.toList());
                                /********************************************************************************************************************************************/
                                String fuzhu = newqclist.size() > 0 ? newqclist.get(0).getFuzhu() : "";
                                BigDecimal qcMd = newqclist.size() > 0 ? newqclist.get(0).getMd() : new BigDecimal(0);
                                BigDecimal qcMc = newqclist.size() > 0 ? newqclist.get(0).getMc() : new BigDecimal(0);

                                // 数量
                                BigDecimal qcNdS = newqclist.size() > 0?newqclist.get(0).getNdS():new BigDecimal(0);
                                BigDecimal qcNcS = newqclist.size() > 0?newqclist.get(0).getNcS():new BigDecimal(0);
                                BigDecimal qcNum=new BigDecimal(0);
                                // 外币金额
                                BigDecimal qcNfratMd = newqclist.size() > 0 ? newqclist.get(0).getNfratMd() : new BigDecimal(0);
                                BigDecimal qcNfratMc = newqclist.size() > 0 ? newqclist.get(0).getNfratMc() : new BigDecimal(0);
                                BigDecimal qcNfrat = new BigDecimal(0);

                                // 期间 不是从一月开始的 计算出上月的期末余额
                                if (Integer.valueOf(strDate.substring(4, 6)) > 1) {
                                    String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;
                                    List<AccvoucherVo> lastpz = null;
                                    // 标准模式 及 个人模式
                                    if(querytype.equals("gs")){
                                        lastpz=pzlist.stream().filter(last ->last.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(last.getImonth()) <= Integer.valueOf(lastmonth)).collect(Collectors.toList());
                                    }else{  // 集团模式
                                        String day=strDate.substring(6, 8);
                                        String a=year+lastmonth+day;
                                        lastpz=pzlist.stream().filter(last ->last.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(last.getDbillDate().replaceAll("-","")) <= Integer.valueOf(a)).collect(Collectors.toList());
                                    }
                                    BigDecimal lastMd =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    BigDecimal lastMc =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    BigDecimal lastNdS =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    BigDecimal lastNcS =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    BigDecimal lastNfratMd =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    BigDecimal lastNfratMc =lastpz.size()>0?lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                    // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                    if(bprogerty.equals("1")){  // 借
                                        qcNum=qcNdS.add(lastNdS).subtract(qcNcS.add(lastNcS));
                                        qcNfrat=qcNfratMd.add(lastNfratMd).subtract(qcNfratMc.add(lastNfratMc));
                                        BigDecimal yue=qcMd.add(lastMd).subtract(qcMc.add(lastMc));
                                        if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                            qcMc=new BigDecimal(0);
                                            qcMd=yue;
                                        }else{
                                            qcMd=new BigDecimal(0);
                                            qcMc=yue.multiply(new BigDecimal(-1));
                                        }
                                    }
                                    else{                       // 贷
                                        qcNum=qcNcS.add(lastNcS).subtract(qcNdS.add(lastNdS));
                                        qcNfrat=qcNfratMc.add(lastNfratMc).subtract(qcNfratMd.add(lastNfratMd));
                                        BigDecimal yue=qcMc.add(lastMc).subtract(qcMd.add(lastMd));
                                        if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                            qcMd=new BigDecimal(0);
                                            qcMc=yue;
                                        }else{
                                            qcMc=new BigDecimal(0);
                                            qcMd=yue.multiply(new BigDecimal(-1));
                                        }
                                    }
                                }

                                // 检索科目获取凭证
                                List<AccvoucherVo> newpzlist = null;
                                // 标准模式 及 个人模式
                                if(querytype.equals("gs")){
                                    newpzlist=pzlist.stream().filter(pz -> pz.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(pz.getIyperiod()) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getIyperiod()) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                                }else{  // 集团模式
                                    newpzlist=pzlist.stream().filter(pz -> pz.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(pz.getDbillDate().replaceAll("-","")) >= Integer.valueOf(strDate) && Integer.valueOf(pz.getDbillDate().replaceAll("-","")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                                }
                                BigDecimal pzMd =newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzMc =newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzNfratMd =newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzNfratMc =newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzNdS=newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzNcS=newpzlist.size()>0?newpzlist.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzNum=new BigDecimal(0);
                                BigDecimal pzNfrat=new BigDecimal(0);

                                // 获取累计发生 【月份：<= endDate】
                                List<AccvoucherVo> pzljlist = null;
                                // 标准模式 及 个人模式
                                if(querytype.equals("gs")){
                                    pzljlist=pzlist.stream().filter(pz -> pz.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(pz.getImonth()) <= Integer.valueOf(endDate.substring(4,6))).collect(Collectors.toList());
                                }else {  // 集团模式
                                    pzljlist=pzlist.stream().filter(pz -> pz.getCcode().startsWith(newcode.getCcode()) && Integer.valueOf(pz.getDbillDate().replaceAll("-","")) <= Integer.valueOf(endDate)).collect(Collectors.toList());
                                }
                                BigDecimal pzLjMd =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjMc =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjNfratMd =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjNfratMc =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjNdS =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjNcS =pzljlist.size()>0?pzljlist.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal pzLjNum=new BigDecimal(0);
                                BigDecimal pzLjNfrat=new BigDecimal(0);

                                BigDecimal qmMd=new BigDecimal(0);
                                BigDecimal qmMc=new BigDecimal(0);
                                BigDecimal qmNum=new BigDecimal(0);
                                BigDecimal qmNfrat=new BigDecimal(0);

                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(bprogerty.equals("1")){  // 借
                                    pzNum=pzNdS.subtract(pzNcS);
                                    pzLjNum=pzLjNdS.subtract(pzLjNcS);
                                    pzNfrat=pzNfratMd.subtract(pzNfratMc);
                                    pzLjNfrat=pzLjNfratMd.subtract(pzLjNfratMc);
                                    qmNum=qcNum.add(pzNdS.subtract(pzNcS));
                                    qmNfrat=qcNfrat.add(pzNfratMd.subtract(pzNfratMc));
                                    BigDecimal yue=qcMd.add(pzMd).subtract(qcMc.add(pzMc));
                                    if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                        qmMc=new BigDecimal(0);
                                        qmMd=yue;
                                    }else{
                                        qmMd=new BigDecimal(0);
                                        qmMc=yue.multiply(new BigDecimal(-1));
                                    }
                                }
                                else{
                                    pzNum=pzNcS.subtract(qcNdS);
                                    qmNum=qcNum.add(pzNcS.subtract(pzNdS));
                                    pzLjNum=pzLjNcS.subtract(pzLjNdS);
                                    pzNfrat=pzNfratMc.subtract(pzNfratMd);
                                    pzLjNfrat=pzLjNfratMc.subtract(pzLjNfratMd);
                                    qmNfrat=qcNfrat.add(pzNfratMc.subtract(pzNfratMd));
                                    BigDecimal yue=qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                                    if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                        qmMc=yue;
                                        qmMd=new BigDecimal(0);
                                    }else{
                                        qmMc=new BigDecimal(0);
                                        qmMd=yue.multiply(new BigDecimal(-1));
                                    }
                                }

                                if(qcMd.compareTo(BigDecimal.ZERO) == 0&&qcMc.compareTo(BigDecimal.ZERO) == 0 &&pzMd.compareTo(BigDecimal.ZERO) == 0&&pzMc.compareTo(BigDecimal.ZERO) == 0
                                        &&pzLjMd.compareTo(BigDecimal.ZERO) == 0&&pzLjMc.compareTo(BigDecimal.ZERO) == 0
                                ){

                                }else{
                                    if(qcMd.compareTo(BigDecimal.ZERO) < 0){
                                        qcMc=qcMd.multiply(new BigDecimal(-1));
                                        qcMd=new BigDecimal(0);
                                    }
                                    if(qcMc.compareTo(BigDecimal.ZERO) < 0){
                                        qcMd=qcMc.multiply(new BigDecimal(-1));
                                        qcMc=new BigDecimal(0);
                                    }
                                    vo.setCcode(ccode)
                                            .setCcodeName(ccodeName)
                                            .setCclass(cclass)
                                            .setFindByBend(findByBend)
                                            .setUnitMeasurement(unitMeasurement)
                                            .setForeignCurrency(foreignCurrency)
                                            .setFuzhu(fuzhu)
                                            .setQcMd(qcMd).setQcMc(qcMc).setQcNum(qcNum).setQcNfrat(qcNfrat)
                                            .setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat)
                                            .setLjMd(pzLjMd).setLjMc(pzLjMc).setLjNum(pzLjNum).setLjNfrat(pzLjNfrat)
                                            .setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat)
                                            .setIgrade(igrade);
                                    finalKeMuBalanceVoList.add(vo);
                                }
                            });
                            //
                            List<KeMuBalanceVo> findAllByLastCode = finalKeMuBalanceVoList.stream().filter(f ->f.getCclass()!=null && f.getCclass().equals(accStyle.getCclass())).collect(Collectors.toList());
                            // 获取所有 不是 末级的科目凭证
                            List<KeMuBalanceVo> LastCode = finalKeMuBalanceVoList.stream().filter(f ->f.getCclass()!=null&& f.getFindByBend().equals("0")&& f.getCclass().equals(accStyle.getCclass())).collect(Collectors.toList());

                            // 需要过滤 正好处于 级次查询条件下 并且 不是末级科目 末级标识改成1 ；小计、合计 需要此科目的借贷金额汇总
                            LastCode.stream().forEach(l->{
                                List<KeMuBalanceVo> collect1 = findAllByLastCode.stream().filter(f -> f.getCcode().startsWith(l.getCcode())).collect(Collectors.toList());
                                if(collect1.size()==0){
                                    KeMuBalanceVo keMuBalanceVo = finalKeMuBalanceVoList.stream().filter(f -> f.getCcode().equals(l.getCcode())).collect(Collectors.toList()).get(0);
                                    keMuBalanceVo.setFindByBend("1");
                                    findAllByLastCode.add(keMuBalanceVo);
                                }
                            });
                            if(findAllByLastCode.size()>0){
                                List<KeMuBalanceVo> findAllByLastCode2=findAllByLastCode;
                                // 不是末级  1否 2是
//                                if(bend.equals("1")){
//                                    findAllByLastCode2=findAllByLastCode.stream().filter(v->v.getIgrade()!=null && v.getIgrade().equals(minJc)).collect(Collectors.toList());
//                                }else{
                                    findAllByLastCode2=findAllByLastCode.stream().filter(v->v.getFindByBend().equals("1")).collect(Collectors.toList());
//                                }

                                accStyleQcMdTotal=findAllByLastCode2.stream().map(KeMuBalanceVo::getQcMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQcMcTotal=findAllByLastCode2.stream().map(KeMuBalanceVo::getQcMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQcNumTotal=findAllByLastCode2.stream().map(KeMuBalanceVo::getQcNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQcNfratTotal=findAllByLastCode2.stream().map(KeMuBalanceVo::getQcNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                                accStylePzMdTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getPzMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStylePzMcTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getPzMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStylePzNumTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getPzNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStylePzNfratTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getPzNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                                accStyleLjMdTotal=findAllByLastCode2.stream().map(KeMuBalanceVo::getLjMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleLjMcTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getLjMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleLjNumTotal= accStylePzNumTotal;
                                accStyleLjNfratTotal= accStylePzNfratTotal;

                                accStyleQmMdTotal=  findAllByLastCode2.stream().map(KeMuBalanceVo::getQmMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQmMcTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getQmMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQmNumTotal=  findAllByLastCode2.stream().map(KeMuBalanceVo::getQmNum).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                                accStyleQmNfratTotal= findAllByLastCode2.stream().map(KeMuBalanceVo::getQmNfrat).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                                KeMuBalanceVo vo=new KeMuBalanceVo();
                                vo.setCcode(accStyle.getCclass()+"小计").setFindByBend("0")
                                        .setQcMd(accStyleQcMdTotal).setQcMc(accStyleQcMcTotal).setQcNum(accStyleQcNumTotal).setQcNfrat(accStyleQcNfratTotal)
                                        .setPzMd(accStylePzMdTotal).setPzMc(accStylePzMcTotal).setPzNum(accStylePzNumTotal).setPzNfrat(accStylePzNfratTotal)
                                        .setLjMd(accStyleLjMdTotal).setLjMc(accStyleLjMcTotal).setLjNum(accStyleLjNumTotal).setLjNfrat(accStyleLjNfratTotal)
                                        .setQmMd(accStyleQmMdTotal).setQmMc(accStyleQmMcTotal).setQmNum(accStyleQmNumTotal).setQmNfrat(accStyleQmNfratTotal);
                                finalKeMuBalanceVoList.add(vo);
                            }
                        }
                    });
                    long page_total=keMuBalanceVoList.size();
//                    keMuBalanceVoList=keMuBalanceVoList.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    totalAR.set(page_total);
                    return Mono.just(keMuBalanceVoList.stream().filter(item->{
                        if(searchMap!=null){
                            // 按条件过滤
                            if (StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                                String value = searchMap.get("value");
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
