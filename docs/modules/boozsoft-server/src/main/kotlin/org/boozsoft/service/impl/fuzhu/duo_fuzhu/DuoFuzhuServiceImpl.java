package org.boozsoft.service.impl.fuzhu.duo_fuzhu;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.vo.*;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.service.impl.SubjectInitialFuZhuBalanceServiceImpl;
import org.boozsoft.service.impl.fuzhu.cus.yue.KmYueCusServiceImpl;
import org.boozsoft.service.impl.fuzhu.dept.yue.KmYueDeptServiceImpl;
import org.boozsoft.service.impl.fuzhu.person.yue.KmPsnYueServiceImpl;
import org.boozsoft.service.impl.fuzhu.pro.yue1.KmYueProServiceImpl;
import org.boozsoft.service.impl.fuzhu.sup.yue.KmYueSupServiceImpl;
import org.boozsoft.util.BigDecimalUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DuoFuzhuServiceImpl {

    @Autowired
    SubjectInitialFuZhuBalanceServiceImpl fuZhuBalanceService;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    SysDepartmentRepository sysDepartmentRepository;
    @Autowired
    SysPsnRepository psnRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;


    @Autowired
    KmYueDeptServiceImpl kmYueDeptService;
    @Autowired
    KmPsnYueServiceImpl kmPsnYueService;
    @Autowired
    KmYueCusServiceImpl kmYueCusService;
    @Autowired
    KmYueSupServiceImpl kmYueSupService;
    @Autowired
    KmYueProServiceImpl kmYueProService;


    public Mono<R> findAll(Pageable pageable,String strDate, String endDate, String styleValue , String pzType , String ccode, String ishaveRjz, int page, int size, String jsonStr, List<String> fuzhu) {

        String year = strDate.substring(0, 4);
        String month = strDate.substring(4, 6);

        //先将这条数据解析为JSONObject
        JSONObject outJson = JSONObject.parseObject(jsonStr);
        //--------------------------- 部门 JSON --------------------------
        String dept=outJson.getString("dept");
        JSONObject deptJson = JSONObject.parseObject(dept);
        Boolean deptflag=deptJson.getBoolean("flag");
        List<String>deptList= (List<String>) deptJson.get("data");
        //--------------------------- 个人 JSON --------------------------
        String psn=outJson.getString("psn");
        JSONObject psnJson = JSONObject.parseObject(psn);
        Boolean psnflag=psnJson.getBoolean("flag");
        List<String>psnList= (List<String>) psnJson.get("data");
        //--------------------------- 客户 JSON --------------------------
        String cus=outJson.getString("cus");
        JSONObject cusJson = JSONObject.parseObject(cus);
        Boolean cusflag=cusJson.getBoolean("flag");
        List<String>cusList= (List<String>) cusJson.get("data");
        //--------------------------- 供应商 JSON --------------------------
        String sup=outJson.getString("sup");
        JSONObject supJson = JSONObject.parseObject(sup);
        Boolean supflag=supJson.getBoolean("flag");
        List<String>supList= (List<String>) supJson.get("data");
        //--------------------------- 项目 JSON --------------------------
        String pro=outJson.getString("pro");
        JSONObject proJson = JSONObject.parseObject(pro);
        Boolean proflag=proJson.getBoolean("flag");
        List<String>proList= (List<String>) proJson.get("data");

        // 上月期间
        String lastmonth = Integer.valueOf(month) - 1 < 10 ? "0" + (Integer.valueOf(month) - 1) : month;

        Map searchMap=new HashMap();
        searchMap.put("requirement","");
        searchMap.put("value","");

        Map filterMap=new HashMap();
        filterMap.put("amountMinQcMd","");
        filterMap.put("amountMaxQcMd","");
        filterMap.put("amountMinQcMc","");
        filterMap.put("amountMaxQcMc","");
        filterMap.put("amountMinPzMd","");
        filterMap.put("amountMaxPzMd","");
        filterMap.put("amountMinPzMc","");
        filterMap.put("amountMaxPzMc","");
        filterMap.put("amountMinQmMd","");
        filterMap.put("amountMaxQmMd","");
        filterMap.put("amountMinQmMc","");
        filterMap.put("amountMaxQmMc","");

        // 勾选多个辅助项
        Mono<R> duo=codeKemuRepository.findByCcode(ccode,year)
                .flatMap(codelist->{
                    // 这个接口待测
                    return accvoucherRepository.findByCodeAccvoucher(ccode,strDate,endDate,pzType).collectList()
                            .flatMap(pzlist->{
                                return accvoucherRepository.findByThisMonthCodeMdMc(year+lastmonth).collectList()
                                        .flatMap(upMonthPzList->{
                                            return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false", year + "21", "")
                                                    .flatMap(fuzhuqclist -> {
                                                        Map fuzhuqcmap = (Map) fuzhuqclist.getResult();
                                                        return fuZhuBalanceService.findAllSubjectInitialFuZhuBalance(year, "false", year + "00", "")
                                                                .flatMap(qclist -> {
                                                                    Map qcmap = (Map) qclist.getResult();
                                                                    return Mono.just(CollectOfUtils.mapof("codelist",codelist,"pzlist",pzlist,
                                                                            "fuzhuqclist",fuzhuqcmap.get("tablesData"),"upMonthPzList",upMonthPzList,"qclist",qcmap.get("tablesData")));
                                                                });
                                                    });
                                        });
                            });
                })
                .flatMap(maparr->{
                    CodeKemu codeinfo= (CodeKemu) maparr.get("codelist");
                    List<SubjectInitialBalanceVo> fuzhuqclist = (List<SubjectInitialBalanceVo>) maparr.get("fuzhuqclist");
                    List<SubjectInitialBalanceVo> qclist = (List<SubjectInitialBalanceVo>) maparr.get("qclist");
                    List<AccvoucherVo> upMonthPzList= (List<AccvoucherVo>) maparr.get("upMonthPzList");
                    List<AccvoucherVo2> pzlist= (List<AccvoucherVo2>) maparr.get("pzlist");

                    List<DuoFuZhuVo> newlist=new ArrayList<>();
                    pzlist.stream().forEach(item->{
                        BigDecimal qcMd=new BigDecimal(0);
                        BigDecimal qcMc=new BigDecimal(0);
                        BigDecimal qcNdS=new BigDecimal(0);
                        BigDecimal qcNcS=new BigDecimal(0);
                        BigDecimal qcNfratMd=new BigDecimal(0);
                        BigDecimal qcNfratMc=new BigDecimal(0);
                        BigDecimal qcNum = new BigDecimal(0);
                        BigDecimal qcNfrat = new BigDecimal(0);

                        if(deptflag){
                            List<SubjectInitialBalanceVo> findByCodeQcList=null;
                            List<AccvoucherVo> lastpz=null;
                            if(deptList.size()>0){
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCdeptId()) && last.getCcode().equals(ccode)&&deptList.indexOf(last.getCdeptId())>-1).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCdeptId()) && qc.getCcode().equals(ccode) && deptList.indexOf(qc.getCdeptId())>-1).collect(Collectors.toList());
                            }else{
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCdeptId()) && last.getCcode().equals(ccode)&&last.getCdeptId().equals(item.getCdeptId())).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCdeptId()) && qc.getCcode().equals(ccode) && qc.getCdeptId().equals(item.getCdeptId())).collect(Collectors.toList());
                            }
                            qcMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0);
                            qcMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0);
                            // 数量
                            qcNdS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0);
                            qcNcS = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0);
                            // 外币金额
                            qcNfratMd = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0);
                            qcNfratMc = findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0);

                            // 期间 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(month) > 1) {
                                BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(codeinfo.getBprogerty().equals("1")){  // 借
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
                        }
                        if(psnflag){
                            List<SubjectInitialBalanceVo> findByCodeQcList=null;
                            List<AccvoucherVo> lastpz=null;
                            if(psnList.size()>0){
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCpersonId()) && last.getCcode().equals(ccode)&&psnList.indexOf(last.getCpersonId())>-1).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCpersonId()) && qc.getCcode().equals(ccode) && psnList.indexOf(qc.getCpersonId())>-1).collect(Collectors.toList());
                            }else{
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCpersonId()) && last.getCcode().equals(ccode)&&last.getCpersonId().equals(item.getCpersonId())).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCpersonId()) && qc.getCcode().equals(ccode) && qc.getCpersonId().equals(item.getCpersonId())).collect(Collectors.toList());
                            }
                            qcMd = qcMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0));
                            qcMc = qcMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0));
                            // 数量
                            qcNdS = qcNdS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0));
                            qcNcS = qcNcS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0));
                            // 外币金额
                            qcNfratMd = qcNfratMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0));
                            qcNfratMc = qcNfratMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0));

                            // 期间 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(month) > 1) {
                                BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(codeinfo.getBprogerty().equals("1")){  // 借
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
                        }
                        if(cusflag){
                            List<SubjectInitialBalanceVo> findByCodeQcList=null;
                            List<AccvoucherVo> lastpz=null;
                            if(cusList.size()>0){
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCcusId()) && last.getCcode().equals(ccode)&&cusList.indexOf(last.getCcusId())>-1).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCcusId()) && qc.getCcode().equals(ccode) && cusList.indexOf(qc.getCcusId())>-1).collect(Collectors.toList());
                            }else{
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCcusId()) && last.getCcode().equals(ccode)&&last.getCcusId().equals(item.getCcusId())).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCcusId()) && qc.getCcode().equals(ccode) && qc.getCcusId().equals(item.getCcusId())).collect(Collectors.toList());
                            }
                            qcMd = qcMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0));
                            qcMc = qcMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0));
                            // 数量
                            qcNdS = qcNdS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0));
                            qcNcS = qcNcS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0));
                            // 外币金额
                            qcNfratMd = qcNfratMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0));
                            qcNfratMc = qcNfratMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0));

                            // 期间 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(month) > 1) {
                                BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(codeinfo.getBprogerty().equals("1")){  // 借
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
                        }
                        if(supflag){
                            List<SubjectInitialBalanceVo> findByCodeQcList=null;
                            List<AccvoucherVo> lastpz=null;
                            if(supList.size()>0){
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCsupId()) && last.getCcode().equals(ccode)&&supList.indexOf(last.getCsupId())>-1).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCsupId()) && qc.getCcode().equals(ccode) && supList.indexOf(qc.getCsupId())>-1).collect(Collectors.toList());
                            }else{
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getCsupId()) && last.getCcode().equals(ccode)&&last.getCsupId().equals(item.getCsupId())).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getCsupId()) && qc.getCcode().equals(ccode) && qc.getCsupId().equals(item.getCsupId())).collect(Collectors.toList());
                            }
                            qcMd = qcMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0));
                            qcMc = qcMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0));
                            // 数量
                            qcNdS = qcNdS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0));
                            qcNcS = qcNcS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0));
                            // 外币金额
                            qcNfratMd = qcNfratMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0));
                            qcNfratMc = qcNfratMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0));

                            // 期间 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(month) > 1) {
                                BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(codeinfo.getBprogerty().equals("1")){  // 借
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
                        }
                        if(proflag){
                            List<SubjectInitialBalanceVo> findByCodeQcList=null;
                            List<AccvoucherVo> lastpz=null;
                            if(proList.size()>0){
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getProjectId()) && last.getCcode().equals(ccode)&&proList.indexOf(last.getProjectId())>-1).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getProjectId()) && qc.getCcode().equals(ccode) && proList.indexOf(qc.getProjectId())>-1).collect(Collectors.toList());
                            }else{
                                // ************* 获取上月凭证 *************
                                lastpz = upMonthPzList.stream().filter(last ->StrUtil.isNotBlank(last.getProjectId()) && last.getCcode().equals(ccode)&&last.getProjectId().equals(item.getProjectId())).collect(Collectors.toList());
                                // ************* 获取此科目下期初 *************
                                findByCodeQcList = fuzhuqclist.stream().filter(qc ->StrUtil.isNotBlank(qc.getProjectId()) && qc.getCcode().equals(ccode) && qc.getProjectId().equals(item.getProjectId())).collect(Collectors.toList());
                            }
                            qcMd = qcMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMd() : new BigDecimal(0));
                            qcMc = qcMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getMc() : new BigDecimal(0));
                            // 数量
                            qcNdS = qcNdS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNdS() : new BigDecimal(0));
                            qcNcS = qcNcS.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNcS() : new BigDecimal(0));
                            // 外币金额
                            qcNfratMd = qcNfratMd.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMd() : new BigDecimal(0));
                            qcNfratMc = qcNfratMc.add(findByCodeQcList.size() > 0 ? findByCodeQcList.get(0).getNfratMc() : new BigDecimal(0));

                            // 期间 不是从一月开始的 计算出上月的期末余额
                            if (Integer.valueOf(month) > 1) {
                                BigDecimal lastMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNdS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNdS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNcS = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNcS).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMd = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMd).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                BigDecimal lastNfratMc = lastpz.size() > 0 ? lastpz.stream().map(AccvoucherVo::getNfratMc).reduce(BigDecimal.ZERO, BigDecimalUtils::sum) : new BigDecimal(0);
                                // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                                if(codeinfo.getBprogerty().equals("1")){  // 借
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
                        }

                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setDeptUnique(item.getCdeptId())
                                .setPsnUnique(item.getCpersonId())
                                .setCusUnique(item.getCcusId())
                                .setSupUnique(item.getCsupId())
                                .setProUnique(item.getProjectId())

                                .setDeptName(item.getDeptName())
                                .setPsnName(item.getPsnName())
                                .setCusName(item.getCustName())
                                .setSupName(item.getSupName())
                                .setProName(item.getProName())

                                .setDeptCode(item.getDeptCode())
                                .setPsnCode(item.getPsnCode())
                                .setCusCode(item.getCustCode())
                                .setSupCode(item.getSupCode())
                                .setProCode(item.getProCode())

                                .setQcMd(qcMd)
                                .setQcMc(qcMc)
                                .setQcNum(qcNum)
                                .setQcNfrat(qcNfrat);

                        BigDecimal pzMd =item.getMd();
                        BigDecimal pzMc =item.getMc();
                        BigDecimal pzNfratMd =item.getNfratMd();
                        BigDecimal pzNfratMc =item.getNfratMc();
                        BigDecimal pzNdS=item.getNdS();
                        BigDecimal pzNcS=item.getNcS();
                        BigDecimal pzNum=new BigDecimal(0);
                        BigDecimal pzNfrat=new BigDecimal(0);

                        BigDecimal qmMd=new BigDecimal(0);
                        BigDecimal qmMc=new BigDecimal(0);
                        BigDecimal qmNum=new BigDecimal(0);
                        BigDecimal qmNfrat=new BigDecimal(0);

                        // 根据方向进行计算 借方【期初借+凭证借-期初贷+凭证贷】 贷方【期初贷+凭证贷-期初借+凭证借】
                        if(codeinfo.getBprogerty().equals("1")){  // 借
                            pzNum=pzNdS.subtract(pzNcS);
                            pzNfrat=pzNfratMd.subtract(pzNfratMc);
                            qmNum= qcNum.add(pzNdS.subtract(pzNcS));
                            qmNfrat= qcNfrat.add(pzNfratMd.subtract(pzNfratMc));

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
                            pzNum=pzNcS.subtract(pzNdS);
                            qmNum= qcNum.add(pzNcS.subtract(pzNdS));
                            pzNfrat=pzNfratMc.subtract(pzNfratMd);
                            qmNfrat= qcNfrat.add(pzNfratMc.subtract(pzNfratMd));
                            BigDecimal yue=qcMc.add(pzMc).subtract(qcMd.add(pzMd));
                            if(yue.compareTo(BigDecimal.ZERO) >= 0){
                                qmMc=yue;
                                qmMd=new BigDecimal(0);
                            }else{
                                qmMc=new BigDecimal(0);
                                qmMd=yue.multiply(new BigDecimal(-1));
                            }
                        }

                        v.setPzMd(pzMd).setPzMc(pzMc).setPzNum(pzNum).setPzNfrat(pzNfrat);
                        v.setQmMd(qmMd).setQmMc(qmMc).setQmNum(qmNum).setQmNfrat(qmNfrat);
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a));

        // 勾选多个辅助项 走上边方法
        List<DuoFuZhuVo> newlist=new ArrayList<>();
        return fuzhu.size()>1?duo
        :deptflag?kmYueDeptService.findAll(pageable,deptList.stream().collect(Collectors.joining(",")),ccode,strDate,endDate,"全部",ishaveRjz,styleValue,searchMap,filterMap,"")
                .flatMap(test->{
                    R t= (R) test;

                    Map<String, Object> aaa = ((HashMap<String,  Object>) t.getResult());
                    ArrayList<KeMuBalanceVo> mapList = (ArrayList<KeMuBalanceVo>) aaa.get("items");

                    mapList.stream().forEach(f->{
                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setDeptCode(f.getCcode())
                            .setDeptName(f.getCcodeName())
                            .setQcMd(f.getQcMd())
                            .setQcMc(f.getQcMc())
                            .setQcNum(f.getQcNum())
                            .setQcNfrat(f.getQcNfrat())
                            .setPzMd(f.getPzMd()).setPzMc(f.getPzMc()).setPzNum(f.getPzNum()).setPzNfrat(f.getPzNfrat())
                            .setQmMd(f.getQmMd()).setQmMc(f.getQmMc()).setQmNum(f.getQmNum()).setQmNfrat(f.getQmNfrat());
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a))
        :psnflag?kmPsnYueService.findAll(pageable,psnList.stream().collect(Collectors.joining(",")),ccode,strDate,endDate,"全部",ishaveRjz,styleValue,searchMap,filterMap,"")
                .flatMap(test->{
                    R t= (R) test;

                    Map<String, Object> aaa = ((HashMap<String,  Object>) t.getResult());
                    ArrayList<KeMuBalanceVo> mapList = (ArrayList<KeMuBalanceVo>) aaa.get("items");

                    mapList.stream().forEach(f->{
                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setPsnCode(f.getCcode())
                                .setPsnName(f.getCcodeName())
                                .setQcMd(f.getQcMd())
                                .setQcMc(f.getQcMc())
                                .setQcNum(f.getQcNum())
                                .setQcNfrat(f.getQcNfrat())
                                .setPzMd(f.getPzMd()).setPzMc(f.getPzMc()).setPzNum(f.getPzNum()).setPzNfrat(f.getPzNfrat())
                                .setQmMd(f.getQmMd()).setQmMc(f.getQmMc()).setQmNum(f.getQmNum()).setQmNfrat(f.getQmNfrat());
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a))
        :cusflag?kmYueCusService.findAll(pageable,cusList.stream().collect(Collectors.joining(",")),ccode,strDate,endDate,"全部",ishaveRjz,styleValue,searchMap,filterMap,"")
                .flatMap(test->{
                    R t= (R) test;

                    Map<String, Object> aaa = ((HashMap<String,  Object>) t.getResult());
                    ArrayList<KeMuBalanceVo> mapList = (ArrayList<KeMuBalanceVo>) aaa.get("items");

                    mapList.stream().forEach(f->{
                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setCusCode(f.getCcode())
                                .setCusName(f.getCcodeName())
                                .setQcMd(f.getQcMd())
                                .setQcMc(f.getQcMc())
                                .setQcNum(f.getQcNum())
                                .setQcNfrat(f.getQcNfrat())
                                .setPzMd(f.getPzMd()).setPzMc(f.getPzMc()).setPzNum(f.getPzNum()).setPzNfrat(f.getPzNfrat())
                                .setQmMd(f.getQmMd()).setQmMc(f.getQmMc()).setQmNum(f.getQmNum()).setQmNfrat(f.getQmNfrat());
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a))
        :supflag?kmYueSupService.findAll(pageable,supList.stream().collect(Collectors.joining(",")),ccode,strDate,endDate,"全部",ishaveRjz,styleValue,searchMap,filterMap,"")
                .flatMap(test->{
                    R t= (R) test;

                    Map<String, Object> aaa = ((HashMap<String,  Object>) t.getResult());
                    ArrayList<KeMuBalanceVo> mapList = (ArrayList<KeMuBalanceVo>) aaa.get("items");

                    mapList.stream().forEach(f->{
                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setSupCode(f.getCcode())
                                .setSupName(f.getCcodeName())
                                .setQcMd(f.getQcMd())
                                .setQcMc(f.getQcMc())
                                .setQcNum(f.getQcNum())
                                .setQcNfrat(f.getQcNfrat())
                                .setPzMd(f.getPzMd()).setPzMc(f.getPzMc()).setPzNum(f.getPzNum()).setPzNfrat(f.getPzNfrat())
                                .setQmMd(f.getQmMd()).setQmMc(f.getQmMc()).setQmNum(f.getQmNum()).setQmNfrat(f.getQmNfrat());
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a))
        :proflag?kmYueProService.findAll(pageable,proList.stream().collect(Collectors.joining(",")),ccode,strDate,endDate,"全部",ishaveRjz,styleValue,searchMap,filterMap,"")
                .flatMap(test->{
                    R t= (R) test;

                    Map<String, Object> aaa = ((HashMap<String,  Object>) t.getResult());
                    ArrayList<KeMuBalanceVo> mapList = (ArrayList<KeMuBalanceVo>) aaa.get("items");

                    mapList.stream().forEach(f->{
                        DuoFuZhuVo v=new DuoFuZhuVo();
                        v.setProCode(f.getCcode())
                                .setProName(f.getCcodeName())
                                .setQcMd(f.getQcMd())
                                .setQcMc(f.getQcMc())
                                .setQcNum(f.getQcNum())
                                .setQcNfrat(f.getQcNfrat())
                                .setPzMd(f.getPzMd()).setPzMc(f.getPzMc()).setPzNum(f.getPzNum()).setPzNfrat(f.getPzNfrat())
                                .setQmMd(f.getQmMd()).setQmMc(f.getQmMc()).setQmNum(f.getQmNum()).setQmNfrat(f.getQmNfrat());
                        newlist.add(v);
                    });
                    return Mono.just(newlist);
                })
                .map(a->R.ok().setResult(a))
        :Mono.just(R.ok());
    }
}
