package org.boozsoft.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.accstyle.AccStyle;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.KmHuiZongVo;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.service.SubjectInitalBalanceService;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Slf4j
@Service
public class KeMuHuiZongServiceImpl {

    @Autowired
    SubjectInitalBalanceService subjectInitalBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccStyleRepository accStyleRepository;


    public Mono<R> findAll(Pageable pageable,String bend, String strDate, String endDate, String minJc, String maxJc, String minKm, String maxKm, String bz,String styleValue, String ibook,String cbill, String voucherTypes, String minInoId,String maxInoId,Map<String, String> searchMap, Map<String, String> filterMap,List<AccStyle> styleList,String database) {
        String year = strDate.substring(0, 4);
        String str=strDate.length()>6?strDate.substring(0,strDate.length()-2):strDate;
        String end=endDate.length()>6?endDate.substring(0,endDate.length()-2):endDate;

        AtomicReference<Long> totalAR = new AtomicReference(0);
        return Mono.just(styleList)
                .flatMap(accStyles->{
                    List<AccStyle> newaccstylelist=accStyles;
                    if("财务会计".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> StringUtils.isBlank(s.getFlagYusuan())).collect(Collectors.toList());
                    }else if("预算会计".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> StringUtils.isNotBlank(s.getFlagYusuan())&&s.getFlagYusuan().equals("1")).collect(Collectors.toList());
                    }else if(!"全部".equals(styleValue)){
                        newaccstylelist=accStyles.stream().filter(s-> s.getCclass().equals(styleValue)).collect(Collectors.toList());
                    }
                    List<AccStyle> finalNewaccstylelist = newaccstylelist;
                    // 科目
                    return findCode(year,minJc,maxJc,minKm,maxKm).flatMap(codelist->{
                        List<CodeKemu> newcodelist=codelist;
                            // 科目条件
                            if("2".equals(bend)){ // 是末级
                                newcodelist=newcodelist.stream().filter(ck->ck.getBend().equals("1")).collect(Collectors.toList());
                            }
                            List<CodeKemu> finalNewcodelist = newcodelist;
                            // 凭证
                            return accvoucherRepository.AccvoucherKmHuiZong(str,end).collectList()
                                .flatMap(item->{
                                    List<KmHuiZongVo> pzlist=item;
                                    // 凭证条件
                                    if(!"全部".equals(bz)){
                                        pzlist=pzlist.stream().filter(pz->pz.getForeignCurrency().equals(bz)).collect(Collectors.toList());
                                    }
                                    // ibook 1是 否记账
                                    if("1".equals(ibook)){
                                        pzlist=pzlist.stream().filter(pz->pz.getIbook().equals("1")).collect(Collectors.toList());
                                    }
                                    // 制单人
                                    if(StringUtils.isNotBlank(cbill)){
                                        pzlist=pzlist.stream().filter(pz->pz.getCbill().equals(cbill)).collect(Collectors.toList());
                                    }
                                    // 凭证类型
                                    if(StringUtils.isNotBlank(voucherTypes)){
                                        pzlist=pzlist.stream().filter(pz->pz.getCsign().equals(voucherTypes)).collect(Collectors.toList());
                                    }
                                    // 凭证号
                                    if(StringUtils.isNotBlank(voucherTypes)){
                                        pzlist=pzlist.stream().filter(pz->pz.getCsign().equals(voucherTypes)).collect(Collectors.toList());
                                    }
                                    // 凭证编码
                                    if(StringUtils.isNotBlank(minInoId) && StringUtils.isNotBlank(maxInoId)){
                                        pzlist=pzlist.stream().filter(pz->pz.getInoId()<=Integer.valueOf(maxInoId) && pz.getInoId()>=Integer.valueOf(minInoId)).collect(Collectors.toList());
                                    }

                                    return Mono.just(CollectOfUtils.mapof("stylelist", finalNewaccstylelist,"codelist", finalNewcodelist,"pzlist",pzlist));
                                });
                    });
                })
                .flatMap(map -> {
                    List<Map<String, Object>> listmap = new ArrayList<>();

                    List<AccStyle>  stylelist= (List<AccStyle>) map.get("stylelist");
                    List<CodeKemu>  codelist= (List<CodeKemu>) map.get("codelist");
                    List<KmHuiZongVo>  pzlist= (List<KmHuiZongVo>) map.get("pzlist");

                    long zcsum = pzlist.stream().filter(v -> v.getIfrag().equals("0")).count();
                    long zfsum = pzlist.stream().filter(v -> v.getIfrag().equals("1")).count();

                    for (int i = 0; i < stylelist.size(); i++) {
                        BigDecimal styMd= new BigDecimal(0);
                        BigDecimal styMc= new BigDecimal(0);
                        BigDecimal styNds= new BigDecimal(0);
                        BigDecimal styNcs= new BigDecimal(0);
                        BigDecimal styNfratMd= new BigDecimal(0);
                        BigDecimal styNfratMc= new BigDecimal(0);
                        int finalI = i;
                        List<CodeKemu> km = codelist.stream().filter(ck -> ck.getCclass().equals(stylelist.get(finalI).getCclass())).collect(Collectors.toList());
                        if(km.size()>0){
                            for (int j = 0; j < km.size(); j++) {
                                Map<String, Object> newmap=new HashMap<>();
                                int finalJ = j;
                                List<KmHuiZongVo> accpz = pzlist.stream().filter(pz -> pz.getCcode().startsWith(km.get(finalJ).getCcode())).collect(Collectors.toList());
                                BigDecimal md=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal mc=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal nds=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal ncs=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal nfratMd=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);
                                BigDecimal nfratMc=accpz.size()>0?accpz.stream().map(KmHuiZongVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum):new BigDecimal(0);

                                styMd=styMd.add(md);styMc=styMc.add(mc);styNds=styNds.add(nds);styNcs=styNcs.add(ncs);styNfratMd=styNfratMd.add(nfratMd);styNfratMc=styNfratMc.add(nfratMc);
                                newmap.put("cclass",stylelist.get(finalI).getCclass());
                                newmap.put("ccode",km.get(j).getCcode());
                                newmap.put("ccodeName",km.get(j).getCcodeName());
                                newmap.put("md",md);
                                newmap.put("mc",mc);
                                newmap.put("ndS",nds);
                                newmap.put("ncS",ncs);
                                newmap.put("nfrat_md",nfratMd);
                                newmap.put("nfrat_mc",nfratMc);
                                newmap.put("unitMeasurement",km.get(j).getMenterage());
                                newmap.put("foreignCurrency",km.get(j).getCurrencyType());
                                newmap.put("zcsum",zcsum);
                                newmap.put("zfsum",zfsum);
                                if(md.compareTo(BigDecimal.ZERO)!=0 || mc.compareTo(BigDecimal.ZERO)!=0){
                                    listmap.add(newmap);
                                }
                            }
                            if(styMd.compareTo(BigDecimal.ZERO)!=0 || styMc.compareTo(BigDecimal.ZERO)!=0){
                                listmap.add(CollectOfUtils.mapof("cclass",stylelist.get(finalI).getCclass()+"小计","md",styMd,"mc",styMc,"ndS",styNds,"ncS",styNcs,"nfrat_md",styNfratMd,"nfrat_mc",styNfratMc));
                            }
                        }
                    }

                    totalAR.set((long) listmap.size());
                    return Mono.just(listmap.stream().filter(item->{
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                return false;
                            }
                        }
                        // 过滤漏斗-----------------
                        if (StringUtils.isNotBlank(filterMap.get("amountMinQcMd")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQcMd"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinQcMd"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxQcMd"));
                            BigDecimal s_md = new BigDecimal(item.get("md").toString().replaceAll(",",""));
                            if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                                return false;
                            }
                        }
                        // 过滤漏斗----------------
                        if (StringUtils.isNotBlank(filterMap.get("amountMinQcMc")) && org.springbooz.core.tool.utils.StringUtils.isNotBlank(filterMap.get("amountMaxQcMc"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinQcMc"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxQcMc"));
                            BigDecimal s_md = new BigDecimal(item.get("mc").toString().replaceAll(",",""));
                            if (s_md.compareTo(min) < 0 || s_md.compareTo(max) > 0) {
                                return false;
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    public Mono<List<CodeKemu>> findCode(String iyear,String minJc,String maxJc,String minKm,String maxKm){
        Mono<List<CodeKemu>> all=codeKemuRepository.findAllByIyearAndIgradeBetween(iyear,minJc,maxJc).collectList();
        Mono<List<CodeKemu>> all2=codeKemuRepository.findAllByIyearAndIgradeAndCodeBetween(iyear,minJc,maxJc,minKm,maxKm).collectList();
        return StringUtils.isNotBlank(minKm)&&StringUtils.isNotBlank(maxKm)?all2:all;
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
