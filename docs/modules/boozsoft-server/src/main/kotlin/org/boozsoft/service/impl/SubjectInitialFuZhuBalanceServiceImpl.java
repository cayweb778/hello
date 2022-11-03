package org.boozsoft.service.impl;

import org.boozsoft.domain.vo.SubjectInitialBalanceVo;
import org.boozsoft.repo.AccvoucherRepository;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class SubjectInitialFuZhuBalanceServiceImpl {
    @Autowired
    AccvoucherRepository accvoucherRepository;

    public  Map<String,Object> test(List<SubjectInitialBalanceVo> list) {
        // 1 汇总末级科目得上级科目
        List<SubjectInitialBalanceVo> superCodeList = new ArrayList<>();
        list.stream().forEach(item->{
            if (!item.getSuperiorCcode().equals("0")) {
                SubjectInitialBalanceVo vo=new SubjectInitialBalanceVo();
                superCodeList.add(vo.setSuperiorCcode(item.getSuperiorCcode()) );
            }
            String fuzhu="";
            if(StringUtils.isNotBlank(item.getBdept())&& item.getBdept().equals("1")){
                fuzhu+="部门,";
            }
            if(StringUtils.isNotBlank(item.getBperson())&&item.getBperson().equals("1")){
                fuzhu+="个人,";
            }
            if(StringUtils.isNotBlank(item.getBcus())&&item.getBcus().equals("1")){
                fuzhu+="客户,";
            }
            if(StringUtils.isNotBlank(item.getBsup())&&item.getBsup().equals("1")){
                fuzhu+="供应商,";
            }
            if(StringUtils.isNotBlank(item.getBitem())&&item.getBitem().equals("1")){
                fuzhu+="项目,";
            }
            item.setFuzhu(StringUtils.isNotBlank(fuzhu)?fuzhu.substring(0,fuzhu.length()-1):"");
        });

        // 2 上级科目去重
        List<SubjectInitialBalanceVo> collect = superCodeList.stream().distinct().collect(Collectors.toList());
        collect.sort(Comparator.comparing(SubjectInitialBalanceVo::getSuperiorCcode).reversed());

        // 数据
        Map map = test2(collect, list);

        // 设置表头
        List<Map> columns=new ArrayList<>();
        String flag="";
        if(Boolean.valueOf(map.get("menterageflg").toString())&&Boolean.valueOf(map.get("currencyTypeflg").toString())){
            flag="all";
        }else{
            if(Boolean.valueOf(map.get("menterageflg").toString())){
                flag="menterage";
            }
            if(Boolean.valueOf(map.get("currencyTypeflg").toString())){
                flag="currencyType";
            }
        }
        return CollectOfUtils.mapof("tableColumns",flag
                ,"tablesData",map.get("data"));
    }

    public Map test2(List<SubjectInitialBalanceVo> collectlist ,List<SubjectInitialBalanceVo> list) {
        // 判断是否显示 单位/外币  列；
        boolean menterageflg=false;
        boolean currencyTypeflg=false;
        DecimalFormat decimalFormat = new DecimalFormat("###,###.00");
        // 3 计算下级科目 借贷方余额
        for (int i = 0; i < collectlist.size(); i++) {
            int finalI = i;
            List<SubjectInitialBalanceVo> list2=list.stream().filter(vo -> vo.getSuperiorCcode().equals(collectlist.get(finalI).getSuperiorCcode())).collect(Collectors.toList());
            BigDecimal mc = list2.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
            BigDecimal md = list2.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

            for (int j = 0; j < list.size(); j++) {
                if(StringUtils.isNotBlank(list.get(j).getMenterage())){
                    menterageflg=true;
                }
                if(StringUtils.isNotBlank(list.get(j).getCurrencyType())){
                    currencyTypeflg=true;
                }
                // 判断是否父节点
                if(!list.get(j).getSuperiorCcode().equals("0")){
                    if(collectlist.get(i).getSuperiorCcode().equals(list.get(j).getCcode())){
                        list.get(j).setMc(mc).setMd(md);
                    }
                }else{
                    // 4 计算下级科目 借贷方余额。等于0 就是没有下级的一级科目 自己的借贷方余额
                    int finalJ = j;
                    List<SubjectInitialBalanceVo> list3=list.stream().filter(vo -> vo.getSuperiorCcode().equals(list.get(finalJ).getCcode())).collect(Collectors.toList());
                    BigDecimal mc2 = list3.size()==0?list.get(finalJ).getMc():list3.stream().map(SubjectInitialBalanceVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal md2 = list3.size()==0?list.get(finalJ).getMd():list3.stream().map(SubjectInitialBalanceVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal nds2 = list3.size()==0?list.get(finalJ).getNdS():list3.stream().map(SubjectInitialBalanceVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);
                    BigDecimal ncs2 = list3.size()==0?list.get(finalJ).getNcS():list3.stream().map(SubjectInitialBalanceVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum);

                    int ndsflg = nds2.compareTo(new BigDecimal(0));
                    // 前台显示：汇总数量
                    list.get(j).setMc(mc2).setMd(md2).setNdS(nds2).setNcS(ncs2).setNcnum(ndsflg>0?nds2:ncs2);
                    BigDecimal yue=new BigDecimal(0);
                    // 借方
                    if(list.get(j).getBprogerty().equals("1")){
                        yue=md2.subtract(mc2);
                        if(yue.compareTo(BigDecimal.ZERO)>0){   // 整数
                            list.get(j).setYuemd(yue).setYuemc(new BigDecimal(0));
                        }else{
                            list.get(j).setYuemc(yue.multiply(new BigDecimal(-1))).setYuemd(new BigDecimal(0));
                        }
                    }else{
                        yue=mc2.subtract(md2);
                        if(yue.compareTo(BigDecimal.ZERO)>0){   // 整数
                            list.get(j).setYuemc(yue).setYuemd(new BigDecimal(0));
                        }else{
                            list.get(j).setYuemd(yue.multiply(new BigDecimal(-1))).setYuemc(new BigDecimal(0));
                        }
                    }
                }
            }
        }

        list.sort(Comparator.comparing(SubjectInitialBalanceVo::getCcode));
        return CollectOfUtils.mapof("menterageflg",menterageflg,"currencyTypeflg",currencyTypeflg,"data",list);
    }

    /**
     *
     * @param iyear      年度
     * @param lastCode   是否查看末级科目
     * @return
     */
    public Mono<R> findAllSubjectInitialFuZhuBalance(String iyear,String lastCode,String iyperiod,String databasenum) {
        Mono<List<SubjectInitialBalanceVo>> nolast = accvoucherRepository.findAllSubjectInitialFuZhuBalance(iyear,iyperiod).collectList();
        return nolast.map(list -> test(list)).map(o->R.ok().setResult(o));
    }
}
