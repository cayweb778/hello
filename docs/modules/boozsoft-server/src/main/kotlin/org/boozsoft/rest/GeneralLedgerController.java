package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.config.r2dbc.R2dbcRouterLoaderImpl;
import org.boozsoft.domain.entity.FaAssetType;
import org.boozsoft.domain.entity.SysAccCodeAuth;
import org.boozsoft.domain.entity.SysAccTypeAuth;
import org.boozsoft.domain.entity.SysDeptClass;
import org.boozsoft.domain.entity.account.SysDepartment;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.ro.GdGeneralLedgerRo;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.vo.*;
import org.boozsoft.repo.*;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;

import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.repository.query.Param;
import org.springframework.r2dbc.core.DatabaseClient;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


/**
 * 总账
 *
 * @author lz
 */
@Slf4j
@RestController
@RequestMapping("/generalLedger")
@Api(value = "总账", tags = "API系统：总账")
public class GeneralLedgerController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcRouterLoaderImpl routerLoader;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    DataLogRepository dataLogRepository;
    @Autowired
    GroupSysAccountRepository sysAccountRepository;
    @Autowired
    SysPeriodRepository sysPeriodRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;
    @Autowired
    ParameterAccuracyRepository parameterAccuracyRepository;
    @Autowired
    SysAccTypeAuthRepository sysAccTypeAuthRepository;
    @Autowired
    SysAccCodeAuthRepository sysAccCodeAuthRepository;
    @Autowired
    SysAccAuthRepository sysAccAuthRepository;

    //查询筛选条件\
    @PostMapping(value = "/findAll")
    @ApiOperation(value = "查询总账", notes = "查询总账")
    public Mono<R> findAll(@RequestBody Map maps) {

        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String kmCode = maps.get("km").toString();
        String dw = Objects.isNull(maps.get("dw")) ? "" : maps.get("dw").toString();
        String wb = Objects.isNull(maps.get("wb")) ? "" : maps.get("wb").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());

        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //权限类型集合
        String accType=maps.get("pzType").toString();                     // 根据凭证类别

        ArrayList<GeneralLedgerVo> list = new ArrayList<>();
        String finalKmCode = kmCode + "%";
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        //添加日志
        String str = strDate.split("-")[0] + "00";
        // 查询所有参数
        return accvoucherRepository.findAllByCcodeAndIyperiod(finalKmCode, str, finalEndDate)
                .collectList()
                .map(v -> {
                    // 期初00
                    List<GeneralLedgerRo> qcListStr = v.stream().filter(obj -> "00".equals(obj.getImonth())).collect(toList());

                    BigDecimal jwbqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal dwbqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal jmdqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal dmcqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal jnumqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                    BigDecimal dnumqc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                    BigDecimal jwbq = jwbqc;
                    BigDecimal dwbq = dwbqc;
                    BigDecimal jmdq = jmdqc;
                    BigDecimal dmcq = dmcqc;
                    BigDecimal jnumq = jnumqc;
                    BigDecimal dnumq = dnumqc;

                    // 累计
                    BigDecimal ljmd = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjMd()) || x.getLjMd().length() <= 0 ? "0.00" : x.getLjMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal ljmc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjMc()) || x.getLjMc().length() <= 0 ? "0.00" : x.getLjMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal ljwbmd = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjWbMd()) || x.getLjWbMd().length() <= 0 ? "0.00" : x.getLjWbMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal ljwbmc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjWbMc()) || x.getLjWbMc().length() <= 0 ? "0.00" : x.getLjWbMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal ljslmd = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjSlMd()) || x.getLjSlMd().length() <= 0 ? "0.00" : x.getLjSlMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                    BigDecimal ljslmc = qcListStr.stream().map(x -> {
                        return Objects.isNull(x.getLjSlMc()) || x.getLjSlMc().length() <= 0 ? "0.00" : x.getLjSlMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                    //期初 01 -  xx-1
                    if (!strDate.contains("-01")) {
                        LocalDate parse = LocalDate.parse(strDate + "-01");
                        LocalDate localDate = parse.minusMonths(1);
                        String strDateQc = strDate.split("-")[0] + "01";
                        String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                        //符合条件的期初
                        //  是否墨迹   级次
                        List<GeneralLedgerRo> qcListEnd = v.stream().filter(obj -> {
                            if (moji.equals("2")) {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                        && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            } else {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                        && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                        && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            }
                        }).collect(toList());

                        jwbqc =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        dwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                        dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                        jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                        dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                    }

                    //期初
                    GeneralLedgerVo vo = new GeneralLedgerVo();
                    vo.setNumber(1);
                    vo.setNum("");
                    vo.setName("期初余额");
                    vo.setDw(dw);
                    vo.setWb(wb);
                    vo.setSjnum(jnumqc);
                    vo.setSjwb(jwbqc);
                    vo.setSjmoney(jmdqc);
                    vo.setSdnum(dnumqc);
                    vo.setSdwb(dwbqc);
                    vo.setSdmoney(dmcqc);

                    vo.setSjnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                    vo.setSjwb(new BigDecimal("0.00"));
                    vo.setSjmoney(new BigDecimal("0.00"));
                    vo.setSdnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                    vo.setSdwb(new BigDecimal("0.00"));
                    vo.setSdmoney(new BigDecimal("0.00"));

                    //判断方向
                    if (jmdqc.compareTo(dmcqc) == 1) {
                        vo.setFx("借");
                        vo.setSymoney(jmdqc.subtract(dmcqc).setScale(2, RoundingMode.HALF_UP));
                        vo.setSywb(jwbqc.subtract(dwbqc).setScale(2, RoundingMode.HALF_UP));
                        vo.setSynum(jnumqc.subtract(dnumqc).setScale(jd, RoundingMode.HALF_UP));
                    } else if (jmdqc.compareTo(dmcqc) == -1) {
                        vo.setFx("贷");
                        vo.setSymoney(dmcqc.subtract(jmdqc).setScale(2, RoundingMode.HALF_UP));
                        vo.setSywb(dwbqc.subtract(jwbqc).setScale(2, RoundingMode.HALF_UP));
                        vo.setSynum(dnumqc.subtract(jnumqc).setScale(jd, RoundingMode.HALF_UP));
                    } else {
                        vo.setFx("平");
                        vo.setSywb(new BigDecimal(0.00));
                        vo.setSymoney(new BigDecimal(0.00));
                        vo.setSynum(new BigDecimal(0.00).setScale(jd, RoundingMode.HALF_UP));
                    }

                    //累计
                    vo.setLjmd(ljmd.add(jmdqc).subtract(jmdq).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjmc(ljmc.add(dmcqc).subtract(dmcq).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjwbmd(ljwbmd.add(jwbqc).subtract(jwbq).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjwbmc(ljwbmc.add(dwbqc).subtract(dwbq).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjslmd(ljslmd.add(jnumqc).subtract(jnumq).setScale(jd, RoundingMode.HALF_UP));
                    vo.setLjslmc(ljslmc.add(dnumqc).subtract(dnumq).setScale(jd, RoundingMode.HALF_UP));
                    list.add(vo);

                    //开始-结束符合的数据
                    List<GeneralLedgerRo> dataList = v.stream().filter(obj -> {
                        if (moji.equals("2")) {
                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                    && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                    && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                        } else {
                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                    && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                    && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                        }
                    }).collect(toList());

                    // dataList  对月份分组
                    Map<String, List<GeneralLedgerRo>> map = dataList.stream()
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getIyperiod));
                    //  对月份排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    AtomicReference<Integer> i = new AtomicReference<>(2);
                    AtomicReference<Integer> index = new AtomicReference<>(0);
                    map2.forEach((k, value) -> {
                        //本月累计
                        GeneralLedgerVo obj = new GeneralLedgerVo();
                        obj.setNumber(i.getAndSet(i.get() + 1));
                        obj.setNum(k);
                        obj.setName("本月累计");
                        obj.setDw(dw);
                        obj.setWb(wb);

                        BigDecimal jwb =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwb = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmd = value.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmc = value.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnum = value.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnum = value.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        obj.setSjnum(jnum);
                        obj.setSjwb(jwb);
                        obj.setSjmoney(jmd);
                        obj.setSdnum(dnum);
                        obj.setSdwb(dwb);
                        obj.setSdmoney(dmc);

                        GeneralLedgerVo listVo = list.get(2 * index.getAndSet(index.get()));//上个本年累计

                        index.getAndSet(index.get() + 1);

                        if ("借".equals(listVo.getFx())) {
                            jmd = jmd.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                            jwb = jwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                        } else {
                            dmc = dmc.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                            dwb = dwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                        }

                        if (jmd.compareTo(dmc) == 1) {
                            obj.setFx("借");
                            obj.setSymoney(jmd.subtract(dmc).setScale(2, RoundingMode.HALF_UP));
                            obj.setSywb(jwb.subtract(dwb).setScale(2, RoundingMode.HALF_UP));
                            obj.setSynum((obj.getSjnum().subtract(obj.getSdnum())).setScale(jd, RoundingMode.HALF_UP));
                        } else if (jmd.compareTo(dmc) == -1) {
                            obj.setFx("贷");
                            obj.setSymoney(dmc.subtract(jmd).setScale(2, RoundingMode.HALF_UP));
                            obj.setSywb(dwb.subtract(jwb).setScale(2, RoundingMode.HALF_UP));
                            obj.setSynum(obj.getSdnum().subtract(obj.getSjnum()).setScale(jd, RoundingMode.HALF_UP));
                        } else {
                            obj.setFx("平");
                            obj.setSymoney(new BigDecimal("0.00"));
                            obj.setSywb(new BigDecimal("0.00"));
                            obj.setSynum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                        }
                        list.add(obj);

                        // 本年累计 + 期初 md + mc
                        GeneralLedgerVo yearVo = new GeneralLedgerVo();
                        yearVo.setNumber(i.getAndSet(i.get() + 1));
                        yearVo.setNum(obj.getNum());
                        yearVo.setName("本年累计");
                        yearVo.setDw(dw);
                        yearVo.setWb(wb);
                        yearVo.setFx(obj.getFx());

                        //期初
                        GeneralLedgerVo qc = list.get(0);

                        if (index.getAndSet(index.get()) == 1) {
                            yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).add(qc.getLjmd()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmd()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).add(qc.getLjslmd()).setScale(jd, RoundingMode.HALF_UP));

                            yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).add(qc.getLjmc()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmc()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).add(qc.getLjslmc()).setScale(jd, RoundingMode.HALF_UP));
                        } else {
                            yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).setScale(jd, RoundingMode.HALF_UP));

                            yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                            yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).setScale(jd, RoundingMode.HALF_UP));
                        }

                        yearVo.setSymoney(obj.getSymoney());
                        yearVo.setSywb(obj.getSywb());
                        yearVo.setSynum(obj.getSynum());
                        list.add(yearVo);
                    });
                    return list;
                })
                .map(item -> {
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerVo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = list.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(flist -> {
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<GeneralLedgerVo> filterList = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("fx")) {
                                if (!item.getFx().contains(value) && !item.getFx().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getSjmoney().compareTo(min) < 0 || item.getSjmoney().compareTo(max) > 0) {
                                return false;
                            }
                            if (item.getSjmoney().compareTo(min) < 0 || item.getSjmoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinDf")) && StrUtil.isNotBlank(filterMap.get("amountMaxDf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinDf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxDf"));
                            if (item.getSdmoney().compareTo(min) < 0 || item.getSdmoney().compareTo(max) > 0) {
                                return false;
                            }
                            if (item.getSdmoney().compareTo(min) < 0 || item.getSdmoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinYe")) && StrUtil.isNotBlank(filterMap.get("amountMaxYe"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinYe"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxYe"));
                            //余额范围
                            if (item.getSymoney().compareTo(min) < 0 || item.getSymoney().compareTo(max) > 0) {
                                return false;
                            }
                            if (item.getSymoney().compareTo(min) < 0 || item.getSymoney().compareTo(max) > 0) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(toList());
                    return filterList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/sheetDate")
    @ApiOperation(value = "查询总账导出多sheet", notes = "查询总账导出多sheet")
    public Mono<R> findAllsheetDate(@RequestBody Map maps) {

        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minKm = maps.get("minKm").toString();
        String maxKm = maps.get("maxKm").toString();

        String dw = Objects.isNull(maps.get("dw")) ? "" : maps.get("dw").toString();
        String wb = Objects.isNull(maps.get("wb")) ? "" : maps.get("wb").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        //km list

        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //权限类型集合
        String accType=maps.get("pzType").toString();

        String finalMinKm = minKm;
        String finalMaxKm = maxKm;

        Map<String,List<GeneralLedgerVo>> rmap = new HashMap();

        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        //添加日志
        String str = strDate.split("-")[0] + "00";
        // 查询所有参数
        return accvoucherRepository.findAllByCcodeRangeAndIyperiod(finalMinKm,finalMaxKm, str, finalEndDate)
                .collectList()
                .map(v -> {
                    //根据code分组排序后 添加计算
                    Map<String, List<GeneralLedgerRo>> cmap = v.stream()
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                    //  对编码排序
                    Map<String, List<GeneralLedgerRo>> cmap2 = cmap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    //循环code
                    cmap2.forEach((ck,l)->{
                        List<GeneralLedgerVo> list = new ArrayList<>();

                        // 期初00
                        List<GeneralLedgerRo> qcListStr = l.stream().filter(obj -> "00".equals(obj.getImonth())).collect(toList());

                        BigDecimal jwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jmdqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmcqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        BigDecimal jwbq = jwbqc;
                        BigDecimal dwbq = dwbqc;
                        BigDecimal jmdq = jmdqc;
                        BigDecimal dmcq = dmcqc;
                        BigDecimal jnumq = jnumqc;
                        BigDecimal dnumq = dnumqc;

                        // 累计
                        BigDecimal ljmd = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjMd()) || x.getLjMd().length() <= 0 ? "0.00" : x.getLjMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal ljmc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjMc()) || x.getLjMc().length() <= 0 ? "0.00" : x.getLjMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal ljwbmd = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjWbMd()) || x.getLjWbMd().length() <= 0 ? "0.00" : x.getLjWbMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal ljwbmc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjWbMc()) || x.getLjWbMc().length() <= 0 ? "0.00" : x.getLjWbMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal ljslmd = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjSlMd()) || x.getLjSlMd().length() <= 0 ? "0.00" : x.getLjSlMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal ljslmc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getLjSlMc()) || x.getLjSlMc().length() <= 0 ? "0.00" : x.getLjSlMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        //期初 01 -  xx-1
                        if (!strDate.contains("-01")) {
                            LocalDate parse = LocalDate.parse(strDate + "-01");
                            LocalDate localDate = parse.minusMonths(1);
                            String strDateQc = strDate.split("-")[0] + "01";
                            String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                            //符合条件的期初
                            //  是否墨迹   级次
                            List<GeneralLedgerRo> qcListEnd = v.stream().filter(obj -> {
                                if (moji.equals("2")) {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                            && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                } else {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                            && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                }
                            }).collect(toList());

                            jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                            dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                            jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                            dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                        }

                        //期初余额
                        GeneralLedgerVo vo = new GeneralLedgerVo();
                        vo.setNumber(1);
                        vo.setNum("");
                        vo.setName("期初余额");
                        vo.setDw(dw);
                        vo.setWb(wb);
                        vo.setSjnum(jnumqc);
                        vo.setSjwb(jwbqc);
                        vo.setSjmoney(jmdqc);
                        vo.setSdnum(dnumqc);
                        vo.setSdwb(dwbqc);
                        vo.setSdmoney(dmcqc);

                        vo.setSjnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                        vo.setSjwb(new BigDecimal("0.00"));
                        vo.setSjmoney(new BigDecimal("0.00"));
                        vo.setSdnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                        vo.setSdwb(new BigDecimal("0.00"));
                        vo.setSdmoney(new BigDecimal("0.00"));

                        //判断方向
                        if (jmdqc.compareTo(dmcqc) == 1) {
                            vo.setFx("借");
                            vo.setSymoney(jmdqc.subtract(dmcqc).setScale(2, RoundingMode.HALF_UP));
                            vo.setSywb(jwbqc.subtract(dwbqc).setScale(2, RoundingMode.HALF_UP));
                            vo.setSynum(jnumqc.subtract(dnumqc).setScale(jd, RoundingMode.HALF_UP));
                        } else if (jmdqc.compareTo(dmcqc) == -1) {
                            vo.setFx("贷");
                            vo.setSymoney(dmcqc.subtract(jmdqc).setScale(2, RoundingMode.HALF_UP));
                            vo.setSywb(dwbqc.subtract(jwbqc).setScale(2, RoundingMode.HALF_UP));
                            vo.setSynum(dnumqc.subtract(jnumqc).setScale(jd, RoundingMode.HALF_UP));
                        } else {
                            vo.setFx("平");
                            vo.setSywb(new BigDecimal(0.00));
                            vo.setSymoney(new BigDecimal(0.00));
                            vo.setSynum(new BigDecimal(0.00).setScale(jd, RoundingMode.HALF_UP));
                        }

                        //累计
                        vo.setLjmd(ljmd.add(jmdqc).subtract(jmdq).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjmc(ljmc.add(dmcqc).subtract(dmcq).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjwbmd(ljwbmd.add(jwbqc).subtract(jwbq).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjwbmc(ljwbmc.add(dwbqc).subtract(dwbq).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjslmd(ljslmd.add(jnumqc).subtract(jnumq).setScale(jd, RoundingMode.HALF_UP));
                        vo.setLjslmc(ljslmc.add(dnumqc).subtract(dnumq).setScale(jd, RoundingMode.HALF_UP));
                        list.add(vo);

                        //开始-结束符合的数据
                        List<GeneralLedgerRo> dataList = v.stream().filter(obj -> {
                            if (moji.equals("2")) {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                        && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            } else {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                        && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            }
                        }).collect(toList());

                        // dataList  对月份分组
                        Map<String, List<GeneralLedgerRo>> map = dataList.stream()
                                .collect(Collectors.groupingBy(GeneralLedgerRo::getIyperiod));
                        //  对月份排序
                        Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                        AtomicReference<Integer> i = new AtomicReference<>(2);
                        AtomicReference<Integer> index = new AtomicReference<>(0);
                        map2.forEach((k, value) -> {
                            //本月累计
                            GeneralLedgerVo obj = new GeneralLedgerVo();
                            obj.setNumber(i.getAndSet(i.get() + 1));
                            obj.setNum(k);
                            obj.setName("本月累计");
                            obj.setDw(dw);
                            obj.setWb(wb);

                            BigDecimal jwb =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwb = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmd = value.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmc = value.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnum = value.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnum = value.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            obj.setSjnum(jnum);
                            obj.setSjwb(jwb);
                            obj.setSjmoney(jmd);
                            obj.setSdnum(dnum);
                            obj.setSdwb(dwb);
                            obj.setSdmoney(dmc);

                            GeneralLedgerVo listVo = list.get(2 * index.getAndSet(index.get()));//上个本年累计

                            index.getAndSet(index.get() + 1);

                            if ("借".equals(listVo.getFx())) {
                                jmd = jmd.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                                jwb = jwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                            } else {
                                dmc = dmc.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                                dwb = dwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                            }

                            if (jmd.compareTo(dmc) == 1) {
                                obj.setFx("借");
                                obj.setSymoney(jmd.subtract(dmc).setScale(2, RoundingMode.HALF_UP));
                                obj.setSywb(jwb.subtract(dwb).setScale(2, RoundingMode.HALF_UP));
                                obj.setSynum((obj.getSjnum().subtract(obj.getSdnum())).setScale(jd, RoundingMode.HALF_UP));
                            } else if (jmd.compareTo(dmc) == -1) {
                                obj.setFx("贷");
                                obj.setSymoney(dmc.subtract(jmd).setScale(2, RoundingMode.HALF_UP));
                                obj.setSywb(dwb.subtract(jwb).setScale(2, RoundingMode.HALF_UP));
                                obj.setSynum(obj.getSdnum().subtract(obj.getSjnum()).setScale(jd, RoundingMode.HALF_UP));
                            } else {
                                obj.setFx("平");
                                obj.setSymoney(new BigDecimal("0.00"));
                                obj.setSywb(new BigDecimal("0.00"));
                                obj.setSynum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            }
                            list.add(obj);

                            // 本年累计 + 期初 md + mc
                            GeneralLedgerVo yearVo = new GeneralLedgerVo();
                            yearVo.setNumber(i.getAndSet(i.get() + 1));
                            yearVo.setNum(obj.getNum());
                            yearVo.setName("本年累计");
                            yearVo.setDw(dw);
                            yearVo.setWb(wb);
                            yearVo.setFx(obj.getFx());

                            //期初
                            GeneralLedgerVo qc = list.get(0);

                            if (index.getAndSet(index.get()) == 1) {
                                yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).add(qc.getLjmd()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmd()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).add(qc.getLjslmd()).setScale(jd, RoundingMode.HALF_UP));

                                yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).add(qc.getLjmc()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmc()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).add(qc.getLjslmc()).setScale(jd, RoundingMode.HALF_UP));
                            } else {
                                yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).setScale(jd, RoundingMode.HALF_UP));

                                yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                                yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).setScale(jd, RoundingMode.HALF_UP));
                            }

                            yearVo.setSymoney(obj.getSymoney());
                            yearVo.setSywb(obj.getSywb());
                            yearVo.setSynum(obj.getSynum());
                            list.add(yearVo);
                        });

                        rmap.put(ck, list);
                    });
                    return rmap;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/sheetDateOne")
    @ApiOperation(value = "查询总账导出单sheet", notes = "查询总账导出单sheet")
    public Mono<R> sheetDateOne(@RequestBody Map maps) {

        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minKm = maps.get("minKm").toString();
        String maxKm = maps.get("maxKm").toString();

        String dw = Objects.isNull(maps.get("dw")) ? "" : maps.get("dw").toString();
        String wb = Objects.isNull(maps.get("wb")) ? "" : maps.get("wb").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        //km list

        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //权限类型集合
        String accType=maps.get("pzType").toString();

        String finalMinKm = minKm;
        String finalMaxKm = maxKm;

        List<GeneralLedgerVo> list = new ArrayList<>();

        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        //添加日志
        String str = strDate.split("-")[0] + "00";
        // 查询所有参数
        return accvoucherRepository.findAllByCcodeRangeAndIyperiod(finalMinKm,finalMaxKm, str, finalEndDate)
                .collectList()
                .map(v -> {
                    //根据code分组排序后 添加计算
                    Map<String, List<GeneralLedgerRo>> cmap = v.stream()
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                    //  对编码排序
                    Map<String, List<GeneralLedgerRo>> cmap2 = cmap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    //循环code
                    cmap2.forEach((ck,l)->{
                        if(l.size() > 1){
                            String ckName = Objects.isNull(l.get(0).getCcodeName())?l.get(1).getCcodeName():l.get(0).getCcodeName();
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = l.stream().filter(obj -> "00".equals(obj.getImonth())).collect(toList());

                            BigDecimal jwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            BigDecimal jwbq = jwbqc;
                            BigDecimal dwbq = dwbqc;
                            BigDecimal jmdq = jmdqc;
                            BigDecimal dmcq = dmcqc;
                            BigDecimal jnumq = jnumqc;
                            BigDecimal dnumq = dnumqc;

                            // 累计
                            BigDecimal ljmd = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjMd()) || x.getLjMd().length() <= 0 ? "0.00" : x.getLjMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal ljmc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjMc()) || x.getLjMc().length() <= 0 ? "0.00" : x.getLjMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal ljwbmd = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjWbMd()) || x.getLjWbMd().length() <= 0 ? "0.00" : x.getLjWbMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal ljwbmc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjWbMc()) || x.getLjWbMc().length() <= 0 ? "0.00" : x.getLjWbMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal ljslmd = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjSlMd()) || x.getLjSlMd().length() <= 0 ? "0.00" : x.getLjSlMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal ljslmc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getLjSlMc()) || x.getLjSlMc().length() <= 0 ? "0.00" : x.getLjSlMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = v.stream().filter(obj -> {
                                    if (moji.equals("2")) {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                                && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                    } else {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                                && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                    }
                                }).collect(toList());

                                jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                            }

                            //期初余额
                            GeneralLedgerVo vo = new GeneralLedgerVo();
                            vo.setNumber(1);
                            vo.setNum("");
                            vo.setCcode(ck);
                            vo.setCcodeName(ckName);
                            vo.setName("期初余额");
                            vo.setDw(dw);
                            vo.setWb(wb);
                            vo.setSjnum(jnumqc);
                            vo.setSjwb(jwbqc);
                            vo.setSjmoney(jmdqc);
                            vo.setSdnum(dnumqc);
                            vo.setSdwb(dwbqc);
                            vo.setSdmoney(dmcqc);

                            vo.setSjnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setSjwb(new BigDecimal("0.00"));
                            vo.setSjmoney(new BigDecimal("0.00"));
                            vo.setSdnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setSdwb(new BigDecimal("0.00"));
                            vo.setSdmoney(new BigDecimal("0.00"));

                            //判断方向
                            if (jmdqc.compareTo(dmcqc) == 1) {
                                vo.setFx("借");
                                vo.setSymoney(jmdqc.subtract(dmcqc).setScale(2, RoundingMode.HALF_UP));
                                vo.setSywb(jwbqc.subtract(dwbqc).setScale(2, RoundingMode.HALF_UP));
                                vo.setSynum(jnumqc.subtract(dnumqc).setScale(jd, RoundingMode.HALF_UP));
                            } else if (jmdqc.compareTo(dmcqc) == -1) {
                                vo.setFx("贷");
                                vo.setSymoney(dmcqc.subtract(jmdqc).setScale(2, RoundingMode.HALF_UP));
                                vo.setSywb(dwbqc.subtract(jwbqc).setScale(2, RoundingMode.HALF_UP));
                                vo.setSynum(dnumqc.subtract(jnumqc).setScale(jd, RoundingMode.HALF_UP));
                            } else {
                                vo.setFx("平");
                                vo.setSywb(new BigDecimal(0.00));
                                vo.setSymoney(new BigDecimal(0.00));
                                vo.setSynum(new BigDecimal(0.00).setScale(jd, RoundingMode.HALF_UP));
                            }

                            //累计
                            vo.setLjmd(ljmd.add(jmdqc).subtract(jmdq).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjmc(ljmc.add(dmcqc).subtract(dmcq).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjwbmd(ljwbmd.add(jwbqc).subtract(jwbq).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjwbmc(ljwbmc.add(dwbqc).subtract(dwbq).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjslmd(ljslmd.add(jnumqc).subtract(jnumq).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjslmc(ljslmc.add(dnumqc).subtract(dnumq).setScale(jd, RoundingMode.HALF_UP));
                            list.add(vo);

                            //开始-结束符合的数据
                            List<GeneralLedgerRo> dataList = v.stream().filter(obj -> {
                                if (moji.equals("2")) {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                            && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                } else {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                            && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                }
                            }).collect(toList());

                            // dataList  对月份分组
                            Map<String, List<GeneralLedgerRo>> map = dataList.stream()
                                    .collect(Collectors.groupingBy(GeneralLedgerRo::getIyperiod));
                            //  对月份排序
                            Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                                    .sorted(Map.Entry.comparingByKey())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                            (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                            AtomicReference<Integer> i = new AtomicReference<>(2);
                            AtomicReference<Integer> index = new AtomicReference<>(0);
                            map2.forEach((k, value) -> {
                                //本月累计
                                GeneralLedgerVo obj = new GeneralLedgerVo();
                                obj.setNumber(i.getAndSet(i.get() + 1));
                                obj.setCcode(ck);
                                obj.setCcodeName(ckName);
                                obj.setNum(k);
                                obj.setName("本月累计");
                                obj.setDw(dw);
                                obj.setWb(wb);

                                BigDecimal jwb =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dwb = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jmd = value.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dmc = value.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jnum = value.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                                BigDecimal dnum = value.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                                obj.setSjnum(jnum);
                                obj.setSjwb(jwb);
                                obj.setSjmoney(jmd);
                                obj.setSdnum(dnum);
                                obj.setSdwb(dwb);
                                obj.setSdmoney(dmc);

                                GeneralLedgerVo listVo = list.get(2 * index.getAndSet(index.get()));//上个本年累计

                                index.getAndSet(index.get() + 1);

                                if ("借".equals(listVo.getFx())) {
                                    jmd = jmd.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                                    jwb = jwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                                } else {
                                    dmc = dmc.add(listVo.getSymoney().setScale(2, RoundingMode.HALF_UP));
                                    dwb = dwb.add(listVo.getSywb().setScale(2, RoundingMode.HALF_UP));
                                }

                                if (jmd.compareTo(dmc) == 1) {
                                    obj.setFx("借");
                                    obj.setSymoney(jmd.subtract(dmc).setScale(2, RoundingMode.HALF_UP));
                                    obj.setSywb(jwb.subtract(dwb).setScale(2, RoundingMode.HALF_UP));
                                    obj.setSynum((obj.getSjnum().subtract(obj.getSdnum())).setScale(jd, RoundingMode.HALF_UP));
                                } else if (jmd.compareTo(dmc) == -1) {
                                    obj.setFx("贷");
                                    obj.setSymoney(dmc.subtract(jmd).setScale(2, RoundingMode.HALF_UP));
                                    obj.setSywb(dwb.subtract(jwb).setScale(2, RoundingMode.HALF_UP));
                                    obj.setSynum(obj.getSdnum().subtract(obj.getSjnum()).setScale(jd, RoundingMode.HALF_UP));
                                } else {
                                    obj.setFx("平");
                                    obj.setSymoney(new BigDecimal("0.00"));
                                    obj.setSywb(new BigDecimal("0.00"));
                                    obj.setSynum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                }
                                list.add(obj);

                                // 本年累计 + 期初 md + mc
                                GeneralLedgerVo yearVo = new GeneralLedgerVo();
                                yearVo.setNumber(i.getAndSet(i.get() + 1));
                                yearVo.setNum(obj.getNum());
                                yearVo.setName("本年累计");
                                yearVo.setCcode(ck);
                                yearVo.setCcodeName(ckName);
                                yearVo.setDw(dw);
                                yearVo.setWb(wb);
                                yearVo.setFx(obj.getFx());

                                //期初
                                GeneralLedgerVo qc = list.get(0);

                                if (index.getAndSet(index.get()) == 1) {
                                    yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).add(qc.getLjmd()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmd()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).add(qc.getLjslmd()).setScale(jd, RoundingMode.HALF_UP));

                                    yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).add(qc.getLjmc()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).add(qc.getLjwbmc()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).add(qc.getLjslmc()).setScale(jd, RoundingMode.HALF_UP));
                                } else {
                                    yearVo.setSjmoney(obj.getSjmoney().add(listVo.getSjmoney()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSjwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSjnum(obj.getSjnum().add(listVo.getSjnum()).setScale(jd, RoundingMode.HALF_UP));

                                    yearVo.setSdmoney(obj.getSdmoney().add(listVo.getSdmoney()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSdwb(obj.getSdwb().add(listVo.getSdwb()).setScale(2, RoundingMode.HALF_UP));
                                    yearVo.setSdnum(obj.getSdnum().add(listVo.getSdnum()).setScale(jd, RoundingMode.HALF_UP));
                                }

                                yearVo.setSymoney(obj.getSymoney());
                                yearVo.setSywb(obj.getSywb());
                                yearVo.setSynum(obj.getSynum());
                                list.add(yearVo);
                            });
                        }
                    });
                    return list;
                })
                .map(o -> R.ok().setResult(o));
    }


    @PostMapping(value = "/findAllDept")
    @ApiOperation(value = "查询部门总账", notes = "查询部门总账")
    public Mono<R> findAllDept(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minCode = maps.get("minKm").toString();
        String maxCode = maps.get("maxKm").toString();
        String deptCode = maps.get("dept").toString();
        //币种
        String bz = maps.get("bz").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        //添加日志
        String str = strDate.split("-")[0] + "01";
        // 查询所有参数
        return accvoucherRepository.findAllByCcodeAndIyperiodAndDeptCode(deptCode, minCode, maxCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .flatMap(item -> {
                    //所有非末级科目 部门辅助核算科目
                    return codeKemuRepository.findAllByFlagAndIyearAndBendAndBdeptOrderByCcodeAsc("1",year,"0","1").collectList()
                            .map(o-> {
                                maps.put("kmList",o);
                                return item;
                            });
                })
                .map(v -> {
                    // dataList  对code分组
                    Map<String, List<GeneralLedgerRo>> map = v.stream().filter(o-> "0".equals(bz) ? true : o.getCurrencytype().equals(bz))
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                    //  对code排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    map2.forEach((k, vlist) -> {
                        // 期初00
                        List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> "21".equals(obj.getImonth())).collect(toList());

                        BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmcqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        //期初 01 -  xx-1
                        if (!strDate.contains("-01")) {
                            LocalDate parse = LocalDate.parse(strDate + "-01");
                            LocalDate localDate = parse.minusMonths(1);
                            String strDateQc = strDate.split("-")[0] + "01";
                            String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                            //符合条件的期初
                            //  是否墨迹   级次
                            List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                            }).collect(toList());

                            jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                            dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                            jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                            dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                        }

                        //发生的数据
                        List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                    && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                        }).collect(toList());
                        if(dataList.size() > 0){
                            //发生 -期间发生 用于计算期末
                            BigDecimal jwb =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwb = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmd = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmc = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                            ro.setCcode(k);
                            ro.setCname(vlist.get(0).getCcodeName());
                            ro.setDw(vlist.get(0).getMenterage());
                            ro.setWb(vlist.get(0).getCurrencytype());
                            //期初
                            ro.setQcJfwb(jwbqc);
                            ro.setQcJfnum(jnumqc);
                            ro.setQcJfMoney(jmdqc);
                            ro.setQcDfwb(dwbqc);
                            ro.setQcDfnum(dnumqc);
                            ro.setQcDfmoney(dmcqc);
                            //发生
                            ro.setJfwb(jwb);
                            ro.setJfnum(jnum);
                            ro.setJfMoney(jmd);
                            ro.setDfwb(dwb);
                            ro.setDfnum(dnum);
                            ro.setDfmoney(dmc);
                            //累计
                            ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                            ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                            //期末
                            if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                ro.setQmJfwb(new BigDecimal("0.00"));
                                ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(new BigDecimal("0.00"));
                                ro.setQmDfwb(new BigDecimal("0.00"));
                                ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(new BigDecimal("0.00"));
                            }
                            BigDecimal add = ro.getQcJfMoney().add(ro.getQcDfmoney()).add(ro.getJfMoney()).add(ro.getDfmoney())
                                    .add(ro.getLjJfMoney()).add(ro.getLjDfmoney());
                            //对于冲红 抵消为0的 不添加
                            if(!"0.00".equals(add.toString())){
                                list.add(ro);
                            }
                        }
                    });

                    //添加合计
                    DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                    vo.setCcode("合计");
                    vo.setCname(" ");
                    vo.setDw(" ");
                    vo.setWb(" ");
                    //期初
                    BigDecimal jwbqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal jnumqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal jmdqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dwbqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dnumqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dmcqc = list.stream().map(x -> {
                        return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    vo.setQcJfwb(jwbqc);
                    vo.setQcJfnum(jnumqc);
                    vo.setQcJfMoney(jmdqc);
                    vo.setQcDfwb(dwbqc);
                    vo.setQcDfnum(dnumqc);
                    vo.setQcDfmoney(dmcqc);

                    //发生
                    BigDecimal jwbqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal jnumqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal jmdqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dwbqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dnumqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal dmcqc2 = list.stream().map(x -> {
                        return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                    }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    vo.setJfwb(jwbqc2);
                    vo.setJfnum(jnumqc2);
                    vo.setJfMoney(jmdqc2);
                    vo.setDfwb(dwbqc2);
                    vo.setDfnum(dnumqc2);
                    vo.setDfmoney(dmcqc2);
                    //累计
                    vo.setLjJfwb(jwbqc.add(jwbqc2).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjJfnum(jnumqc.add(jnumqc2).setScale(jd, RoundingMode.HALF_UP));
                    vo.setLjJfMoney(jmdqc.add(jmdqc2).setScale(2, RoundingMode.HALF_UP));

                    vo.setLjDfwb(dwbqc.add(dwbqc2).setScale(2, RoundingMode.HALF_UP));
                    vo.setLjDfnum(dnumqc.add(dnumqc2).setScale(jd, RoundingMode.HALF_UP));
                    vo.setLjDfmoney(dmcqc.add(dmcqc2).setScale(2, RoundingMode.HALF_UP));
                    //期末
                    if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                            vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                        vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                        vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                        vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                    } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                            vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                        vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                        vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                        vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                    } else {
                        vo.setQmJfwb(new BigDecimal("0.00"));
                        vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                        vo.setQmJfMoney(new BigDecimal("0.00"));
                        vo.setQmDfwb(new BigDecimal("0.00"));
                        vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                        vo.setQmDfmoney(new BigDecimal("0.00"));
                    }
                    list.add(vo);
                    return list;
                })
                .map(deptlist->{
                    List<CodeKemu> dl = (List<CodeKemu>)maps.get("kmList");
                    //比对 非末级
                    // 1010101 1010102   然后统计父节点的值  10101  101
                    dl.forEach(v->{
                        //以父级编码开头
                        List<DeptGeneralLedgerVo> collect = list.stream().filter(o -> o.getCcode().startsWith(v.getCcode())).collect(toList());
                        if(collect.size() > 0 ){
                            DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                            vo.setCcode(v.getCcode());
                            vo.setCname(v.getCcodeName());
                            vo.setDw(collect.get(0).getDw());
                            vo.setWb(collect.get(0).getWb());

                            //期初
                            BigDecimal jwbqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jnumqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dwbqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dnumqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dmcqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            vo.setQcJfwb(jwbqc);
                            vo.setQcJfnum(jnumqc);
                            vo.setQcJfMoney(jmdqc);
                            vo.setQcDfwb(dwbqc);
                            vo.setQcDfnum(dnumqc);
                            vo.setQcDfmoney(dmcqc);

                            //发生
                            BigDecimal jwbqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jnumqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dwbqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dnumqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dmcqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            vo.setJfwb(jwbqc2);
                            vo.setJfnum(jnumqc2);
                            vo.setJfMoney(jmdqc2);
                            vo.setDfwb(dwbqc2);
                            vo.setDfnum(dnumqc2);
                            vo.setDfmoney(dmcqc2);
                            //累计
                            vo.setLjJfwb(jwbqc.add(jwbqc2).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjJfnum(jnumqc.add(jnumqc2).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfMoney(jmdqc.add(jmdqc2).setScale(2, RoundingMode.HALF_UP));

                            vo.setLjDfwb(dwbqc.add(dwbqc2).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjDfnum(dnumqc.add(dnumqc2).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfmoney(dmcqc.add(dmcqc2).setScale(2, RoundingMode.HALF_UP));
                            //期末
                            if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                                vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                                vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                vo.setQmJfwb(new BigDecimal("0.00"));
                                vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(new BigDecimal("0.00"));
                                vo.setQmDfwb(new BigDecimal("0.00"));
                                vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(new BigDecimal("0.00"));
                            }
                            //插入 第一个编码前面
                            int i = indexOf(list, v);
                            list.add(i, vo);
                        }
                    });
                    return list;
                })
                .map(flist -> {
                    //余额方向
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));

                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/findDeptsheetDate")
    @ApiOperation(value = "查询总账导出多sheet", notes = "查询总账导出多sheet")
    public Mono<R> findDeptsheetDate(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minCode = maps.get("minKm").toString();
        String maxCode = maps.get("maxKm").toString();
        String deptCode = maps.get("dept").toString();
        //币种
        String bz = maps.get("bz").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");

        Map<String,List<DeptGeneralLedgerVo>> rmap = new HashMap();

        String minDept = maps.get("minDept").toString();
        String maxDept = maps.get("maxDept").toString();
        String str = strDate.split("-")[0] + "01";
        // 查询所有参数
        return accvoucherRepository.findAllSheetData(minDept,maxDept, minCode, maxCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .flatMap(item -> {
                    //所有非末级科目 部门辅助核算科目
                    return codeKemuRepository.findAllByFlagAndIyearAndBendAndBdeptOrderByCcodeAsc("1",year,"0","1").collectList()
                            .map(o-> {
                                maps.put("kmList",o);
                                return item;
                            });
                })
                .map(v -> {
                    //先根据部门分组
                    Map<String, List<GeneralLedgerRo>> dmap = v.stream().collect(Collectors.groupingBy(GeneralLedgerRo::getDcode));
                    //  对code排序
                    Map<String, List<GeneralLedgerRo>> dmap2 = dmap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    dmap2.forEach((dcode, dl) -> {
                        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();

                        // dataList  对code分组
                        Map<String, List<GeneralLedgerRo>> map = dl.stream().filter(o-> "0".equals(bz) ? true : o.getCurrencytype().equals(bz))
                                .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                        //  对code排序
                        Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                        map2.forEach((k, vlist) -> {
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> "21".equals(obj.getImonth())).collect(toList());

                            BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                }).collect(toList());

                                jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                            }

                            //发生的数据
                            List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                            }).collect(toList());
                            if(dataList.size() > 0){
                                //发生 -期间发生 用于计算期末
                                BigDecimal jwb =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dwb = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jmd = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dmc = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                                BigDecimal dnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                                DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                                ro.setCcode(k);
                                ro.setCname(vlist.get(0).getCcodeName());
                                ro.setDw(vlist.get(0).getMenterage());
                                ro.setWb(vlist.get(0).getCurrencytype());
                                //期初
                                ro.setQcJfwb(jwbqc);
                                ro.setQcJfnum(jnumqc);
                                ro.setQcJfMoney(jmdqc);
                                ro.setQcDfwb(dwbqc);
                                ro.setQcDfnum(dnumqc);
                                ro.setQcDfmoney(dmcqc);
                                //发生
                                ro.setJfwb(jwb);
                                ro.setJfnum(jnum);
                                ro.setJfMoney(jmd);
                                ro.setDfwb(dwb);
                                ro.setDfnum(dnum);
                                ro.setDfmoney(dmc);
                                //累计
                                ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                                ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                                //期末
                                if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                    ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                                } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                    ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                                } else {
                                    ro.setQmJfwb(new BigDecimal("0.00"));
                                    ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(new BigDecimal("0.00"));
                                    ro.setQmDfwb(new BigDecimal("0.00"));
                                    ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(new BigDecimal("0.00"));
                                }
                                BigDecimal add = ro.getQcJfMoney().add(ro.getQcDfmoney()).add(ro.getJfMoney()).add(ro.getDfmoney())
                                        .add(ro.getLjJfMoney()).add(ro.getLjDfmoney());
                                //对于冲红 抵消为0的 不添加
                                if(!"0.00".equals(add.toString())){
                                    list.add(ro);
                                }
                            }
                        });

                        //添加合计
                        DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                        vo.setCcode("合计");
                        vo.setCname(" ");
                        vo.setDw(" ");
                        vo.setWb(" ");
                        //期初
                        BigDecimal jwbqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jnumqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dwbqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dnumqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dmcqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        vo.setQcJfwb(jwbqc);
                        vo.setQcJfnum(jnumqc);
                        vo.setQcJfMoney(jmdqc);
                        vo.setQcDfwb(dwbqc);
                        vo.setQcDfnum(dnumqc);
                        vo.setQcDfmoney(dmcqc);

                        //发生
                        BigDecimal jwbqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jnumqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dwbqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dnumqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dmcqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        vo.setJfwb(jwbqc2);
                        vo.setJfnum(jnumqc2);
                        vo.setJfMoney(jmdqc2);
                        vo.setDfwb(dwbqc2);
                        vo.setDfnum(dnumqc2);
                        vo.setDfmoney(dmcqc2);
                        //累计
                        vo.setLjJfwb(jwbqc.add(jwbqc2).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjJfnum(jnumqc.add(jnumqc2).setScale(jd, RoundingMode.HALF_UP));
                        vo.setLjJfMoney(jmdqc.add(jmdqc2).setScale(2, RoundingMode.HALF_UP));

                        vo.setLjDfwb(dwbqc.add(dwbqc2).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjDfnum(dnumqc.add(dnumqc2).setScale(jd, RoundingMode.HALF_UP));
                        vo.setLjDfmoney(dmcqc.add(dmcqc2).setScale(2, RoundingMode.HALF_UP));
                        //期末
                        if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                            vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                            vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                        } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                            vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                            vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            vo.setQmJfwb(new BigDecimal("0.00"));
                            vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmJfMoney(new BigDecimal("0.00"));
                            vo.setQmDfwb(new BigDecimal("0.00"));
                            vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmDfmoney(new BigDecimal("0.00"));
                        }
                        list.add(vo);
                        List<CodeKemu> klist = (List<CodeKemu>)maps.get("kmList");
                        //比对 非末级
                        // 1010101 1010102   然后统计父节点的值  10101  101
                        klist.forEach(obj->{
                            //以父级编码开头
                            List<DeptGeneralLedgerVo> collect = list.stream().filter(o -> o.getCcode().startsWith(obj.getCcode())).collect(toList());
                            if(collect.size() > 0 ){
                                DeptGeneralLedgerVo dvo = new DeptGeneralLedgerVo();
                                vo.setCcode(obj.getCcode());
                                vo.setCname(obj.getCcodeName());
                                vo.setDw(collect.get(0).getDw());
                                vo.setWb(collect.get(0).getWb());

                                //期初
                                BigDecimal fjwbqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fjnumqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fjmdqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdwbqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdnumqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdmcqc = collect.stream().map(x -> {
                                    return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                dvo.setQcJfwb(fjwbqc);
                                dvo.setQcJfnum(fjnumqc);
                                dvo.setQcJfMoney(fjmdqc);
                                dvo.setQcDfwb(fdwbqc);
                                dvo.setQcDfnum(fdnumqc);
                                dvo.setQcDfmoney(fdmcqc);

                                //发生
                                BigDecimal fjwbqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fjnumqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fjmdqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdwbqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdnumqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal fdmcqc2 = collect.stream().map(x -> {
                                    return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                dvo.setJfwb(fjwbqc2);
                                dvo.setJfnum(fjnumqc2);
                                dvo.setJfMoney(fjmdqc2);
                                dvo.setDfwb(fdwbqc2);
                                dvo.setDfnum(fdnumqc2);
                                dvo.setDfmoney(fdmcqc2);
                                //累计
                                dvo.setLjJfwb(fjwbqc.add(fjwbqc2).setScale(2, RoundingMode.HALF_UP));
                                dvo.setLjJfnum(fjnumqc.add(fjnumqc2).setScale(jd, RoundingMode.HALF_UP));
                                dvo.setLjJfMoney(fjmdqc.add(fjmdqc2).setScale(2, RoundingMode.HALF_UP));

                                dvo.setLjDfwb(fdwbqc.add(fdwbqc2).setScale(2, RoundingMode.HALF_UP));
                                dvo.setLjDfnum(fdnumqc.add(fdnumqc2).setScale(jd, RoundingMode.HALF_UP));
                                dvo.setLjDfmoney(fdmcqc.add(fdmcqc2).setScale(2, RoundingMode.HALF_UP));
                                //期末
                                if (dvo.getQcJfMoney().add(dvo.getJfMoney()).compareTo(
                                        dvo.getQcDfmoney().add(dvo.getDfmoney())) == 1) {
                                    dvo.setQmJfwb(dvo.getQcJfwb().add(dvo.getJfwb()).subtract(dvo.getQcDfwb().add(dvo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                    dvo.setQmJfnum(dvo.getQcJfnum().add(dvo.getJfnum()).subtract(dvo.getQcDfnum().add(dvo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    dvo.setQmJfMoney(dvo.getQcJfMoney().add(dvo.getJfMoney()).subtract(dvo.getQcDfmoney().add(dvo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                                } else if (dvo.getQcJfMoney().add(dvo.getJfMoney()).compareTo(
                                        dvo.getQcDfmoney().add(dvo.getDfmoney())) == -1) {
                                    dvo.setQmDfwb(dvo.getQcDfwb().add(dvo.getDfwb()).subtract(dvo.getQcJfwb().add(dvo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                    dvo.setQmDfnum(dvo.getQcDfnum().add(dvo.getDfnum()).subtract(dvo.getQcJfnum().add(dvo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    dvo.setQmDfmoney(dvo.getQcDfmoney().add(dvo.getDfmoney()).subtract(dvo.getQcJfMoney().add(dvo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                                } else {
                                    dvo.setQmJfwb(new BigDecimal("0.00"));
                                    dvo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    dvo.setQmJfMoney(new BigDecimal("0.00"));
                                    dvo.setQmDfwb(new BigDecimal("0.00"));
                                    dvo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    dvo.setQmDfmoney(new BigDecimal("0.00"));
                                }
                                //插入 第一个编码前面
                                int i = indexOf(list, obj);
                                list.add(i, vo);
                            }
                        });
                        rmap.put(dcode+"-"+dl.get(0).getDname(),list);
                    });
                    return rmap;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/findDeptsheetDateOne")
    @ApiOperation(value = "查询总账导出单sheet", notes = "查询总账导出单sheet")
    public Mono<R> findDeptsheetDateOne(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minCode = maps.get("minKm").toString();
        String maxCode = maps.get("maxKm").toString();
        String deptCode = maps.get("dept").toString();
        //币种
        String bz = maps.get("bz").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        String minDept = maps.get("minDept").toString();
        String maxDept = maps.get("maxDept").toString();
        String str = strDate.split("-")[0] + "01";
        // 查询所有参数
        return accvoucherRepository.findAllSheetData(minDept,maxDept, minCode, maxCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .flatMap(item -> {
                    //所有非末级科目 部门辅助核算科目
                    return codeKemuRepository.findAllByFlagAndIyearAndBendAndBdeptOrderByCcodeAsc("1",year,"0","1").collectList()
                            .map(o-> {
                                maps.put("kmList",o);
                                return item;
                            });
                })
                .map(v -> {

                    //先根据部门分组
                    Map<String, List<GeneralLedgerRo>> dmap = v.stream().collect(Collectors.groupingBy(GeneralLedgerRo::getDcode));
                    //  对code排序
                    Map<String, List<GeneralLedgerRo>> dmap2 = dmap.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                    dmap2.forEach((dcode, dl) -> {

                        // dataList  对code分组
                        Map<String, List<GeneralLedgerRo>> map = dl.stream().filter(o-> "0".equals(bz) ? true : o.getCurrencytype().equals(bz))
                                .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                        //  对code排序
                        Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                        map2.forEach((k, vlist) -> {
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> "21".equals(obj.getImonth())).collect(toList());

                            BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                }).collect(toList());

                                jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                            }

                            //发生的数据
                            List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                            }).collect(toList());
                            if(dataList.size() > 0){
                                //发生 -期间发生 用于计算期末
                                BigDecimal jwb =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dwb = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jmd = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dmc = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                                BigDecimal dnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                                DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                                ro.setCcode(k);
                                ro.setDcode(dcode);
                                ro.setDname(dl.get(0).getDname());
                                ro.setCname(vlist.get(0).getCcodeName());
                                ro.setDw(vlist.get(0).getMenterage());
                                ro.setWb(vlist.get(0).getCurrencytype());
                                //期初
                                ro.setQcJfwb(jwbqc);
                                ro.setQcJfnum(jnumqc);
                                ro.setQcJfMoney(jmdqc);
                                ro.setQcDfwb(dwbqc);
                                ro.setQcDfnum(dnumqc);
                                ro.setQcDfmoney(dmcqc);
                                //发生
                                ro.setJfwb(jwb);
                                ro.setJfnum(jnum);
                                ro.setJfMoney(jmd);
                                ro.setDfwb(dwb);
                                ro.setDfnum(dnum);
                                ro.setDfmoney(dmc);
                                //累计
                                ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                                ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                                //期末
                                if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                    ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                                } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                    ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                                } else {
                                    ro.setQmJfwb(new BigDecimal("0.00"));
                                    ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(new BigDecimal("0.00"));
                                    ro.setQmDfwb(new BigDecimal("0.00"));
                                    ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(new BigDecimal("0.00"));
                                }
                                BigDecimal add = ro.getQcJfMoney().add(ro.getQcDfmoney()).add(ro.getJfMoney()).add(ro.getDfmoney())
                                        .add(ro.getLjJfMoney()).add(ro.getLjDfmoney());
                                //对于冲红 抵消为0的 不添加
                                if(!"0.00".equals(add.toString())){
                                    list.add(ro);
                                }
                            }
                        });

                        //添加合计
                        DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                        vo.setCcode("合计");
                        vo.setDcode(dcode);
                        vo.setDname(dl.get(0).getDname());
                        vo.setCname(" ");
                        vo.setDw(" ");
                        vo.setWb(" ");
                        //期初
                        BigDecimal jwbqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jnumqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dwbqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dnumqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dmcqc = list.stream().map(x -> {
                            return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        vo.setQcJfwb(jwbqc);
                        vo.setQcJfnum(jnumqc);
                        vo.setQcJfMoney(jmdqc);
                        vo.setQcDfwb(dwbqc);
                        vo.setQcDfnum(dnumqc);
                        vo.setQcDfmoney(dmcqc);

                        //发生
                        BigDecimal jwbqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jnumqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dwbqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dnumqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal dmcqc2 = list.stream().map(x -> {
                            return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        vo.setJfwb(jwbqc2);
                        vo.setJfnum(jnumqc2);
                        vo.setJfMoney(jmdqc2);
                        vo.setDfwb(dwbqc2);
                        vo.setDfnum(dnumqc2);
                        vo.setDfmoney(dmcqc2);
                        //累计
                        vo.setLjJfwb(jwbqc.add(jwbqc2).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjJfnum(jnumqc.add(jnumqc2).setScale(jd, RoundingMode.HALF_UP));
                        vo.setLjJfMoney(jmdqc.add(jmdqc2).setScale(2, RoundingMode.HALF_UP));

                        vo.setLjDfwb(dwbqc.add(dwbqc2).setScale(2, RoundingMode.HALF_UP));
                        vo.setLjDfnum(dnumqc.add(dnumqc2).setScale(jd, RoundingMode.HALF_UP));
                        vo.setLjDfmoney(dmcqc.add(dmcqc2).setScale(2, RoundingMode.HALF_UP));
                        //期末
                        if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                            vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                            vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                        } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                            vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                            vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            vo.setQmJfwb(new BigDecimal("0.00"));
                            vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmJfMoney(new BigDecimal("0.00"));
                            vo.setQmDfwb(new BigDecimal("0.00"));
                            vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            vo.setQmDfmoney(new BigDecimal("0.00"));
                        }
                        list.add(vo);

                    });
                    return list;
                })
                .map(deptlist->{
                    List<CodeKemu> dl = (List<CodeKemu>)maps.get("kmList");
                    //比对 非末级
                    // 1010101 1010102   然后统计父节点的值  10101  101
                    dl.forEach(v->{
                        //以父级编码开头
                        List<DeptGeneralLedgerVo> collect = list.stream().filter(o -> o.getCcode().startsWith(v.getCcode())).collect(toList());
                        if(collect.size() > 0 ){
                            DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                            vo.setCcode(v.getCcode());
                            vo.setCname(v.getCcodeName());
                            vo.setDcode(collect.get(0).getDcode());
                            vo.setDname(collect.get(0).getDname());
                            vo.setDw(collect.get(0).getDw());
                            vo.setWb(collect.get(0).getWb());

                            //期初
                            BigDecimal jwbqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfwb()) ? new BigDecimal("0.00") : x.getQcJfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jnumqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfnum()) ? new BigDecimal("0.00") : x.getQcJfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcJfMoney()) ? new BigDecimal("0.00") : x.getQcJfMoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dwbqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfwb()) ? new BigDecimal("0.00") : x.getQcDfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dnumqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfnum()) ? new BigDecimal("0.00") : x.getQcDfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dmcqc = collect.stream().map(x -> {
                                return Objects.isNull(x.getQcDfmoney()) ? new BigDecimal("0.00") : x.getQcDfmoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            vo.setQcJfwb(jwbqc);
                            vo.setQcJfnum(jnumqc);
                            vo.setQcJfMoney(jmdqc);
                            vo.setQcDfwb(dwbqc);
                            vo.setQcDfnum(dnumqc);
                            vo.setQcDfmoney(dmcqc);

                            //发生
                            BigDecimal jwbqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfwb()) ? new BigDecimal("0.00") : x.getJfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jnumqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfnum()) ? new BigDecimal("0.00") : x.getJfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getJfMoney()) ? new BigDecimal("0.00") : x.getJfMoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dwbqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfwb()) ? new BigDecimal("0.00") : x.getDfwb().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dnumqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfnum()) ? new BigDecimal("0.00") : x.getDfnum().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal dmcqc2 = collect.stream().map(x -> {
                                return Objects.isNull(x.getDfmoney()) ? new BigDecimal("0.00") : x.getDfmoney().setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            vo.setJfwb(jwbqc2);
                            vo.setJfnum(jnumqc2);
                            vo.setJfMoney(jmdqc2);
                            vo.setDfwb(dwbqc2);
                            vo.setDfnum(dnumqc2);
                            vo.setDfmoney(dmcqc2);
                            //累计
                            vo.setLjJfwb(jwbqc.add(jwbqc2).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjJfnum(jnumqc.add(jnumqc2).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfMoney(jmdqc.add(jmdqc2).setScale(2, RoundingMode.HALF_UP));

                            vo.setLjDfwb(dwbqc.add(dwbqc2).setScale(2, RoundingMode.HALF_UP));
                            vo.setLjDfnum(dnumqc.add(dnumqc2).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfmoney(dmcqc.add(dmcqc2).setScale(2, RoundingMode.HALF_UP));
                            //期末
                            if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                                vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                                vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                vo.setQmJfwb(new BigDecimal("0.00"));
                                vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(new BigDecimal("0.00"));
                                vo.setQmDfwb(new BigDecimal("0.00"));
                                vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(new BigDecimal("0.00"));
                            }
                            //插入 第一个编码前面
                            int i = indexOf(list, v);
                            list.add(i, vo);
                        }
                    });
                    return list;
                })
                .map(flist -> {
                    //余额方向
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));

                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/findAllKmDept")
    @ApiOperation(value = "查询科目部门总账", notes = "查询科目部门总账")
    public Mono<R> findAllKmDept(@RequestBody Map maps) {

        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minDept = maps.get("minDept").toString();
        String maxDept = maps.get("maxDept").toString();
        String km = maps.get("km").toString()+ "%";
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());

        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        String str = strDate.split("-")[0] + "00";
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        // 查询所有参过滤
        return accvoucherRepository.findAllByKmAndDept(km, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () ->sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(v -> {
                    // dataList  对部门code分组
                    //过滤部门 
                    Map<String, List<GeneralLedgerRo>> map = v.stream()
                            .filter(obj -> {
                                return  ((minDept.compareTo(obj.getDcode()) < 0 && maxDept.compareTo(obj.getDcode()) > 0) || minDept.compareTo(obj.getDcode()) == 0 || maxDept.compareTo(obj.getDcode()) == 0);
                            })
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getDcode));
                    //  部门code排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    AtomicReference<Integer> index = new AtomicReference<>(1);

                    map2.forEach((k, vlist) -> {
                        // 期初00
                        List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> {
                            return "21".equals(obj.getImonth()) || "00".equals(obj.getImonth());
                        }).collect(toList());

                        BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jmdqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmcqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        //期初 01 -  xx-1
                        if (!strDate.contains("-01")) {
                            LocalDate parse = LocalDate.parse(strDate + "-01");
                            LocalDate localDate = parse.minusMonths(1);
                            String strDateQc = strDate.split("-")[0] + "01";
                            String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                            //符合条件的期初
                            //  是否墨迹   级次
                            List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                        && ((minDept.compareTo(obj.getDcode()) < 0 && maxDept.compareTo(obj.getDcode()) > 0) || minDept.compareTo(obj.getDcode()) == 0 || maxDept.compareTo(obj.getDcode()) == 0);
                            }).collect(toList());

                            jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                            dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                            jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                            dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                        }

                        //发生的数据
                        List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                    && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                    && ((minDept.compareTo(obj.getDcode()) < 0 && maxDept.compareTo(obj.getDcode()) > 0) || minDept.compareTo(obj.getDcode()) == 0 || maxDept.compareTo(obj.getDcode()) == 0);
                        }).collect(toList());

                        BigDecimal jwb =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwb = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmd = dataList.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmc = dataList.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnum = dataList.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnum = dataList.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                        ro.setNumber(index.getAndSet(index.get() + 1));
                        ro.setCcode(k);
                        ro.setCname(vlist.get(0).getDname());
                        ro.setDw(vlist.get(0).getMenterage());
                        ro.setWb(vlist.get(0).getCurrencytype());

                        //期初
                        ro.setQcJfwb(jwbqc);
                        ro.setQcJfnum(jnumqc);
                        ro.setQcJfMoney(jmdqc);
                        ro.setQcDfwb(dwbqc);
                        ro.setQcDfnum(dnumqc);
                        ro.setQcDfmoney(dmcqc);
                        //发生
                        ro.setJfwb(jwb);
                        ro.setJfnum(jnum);
                        ro.setJfMoney(jmd);
                        ro.setDfwb(dwb);
                        ro.setDfnum(dnum);
                        ro.setDfmoney(dmc);
                        //累计
                        ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                        ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                        ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                        ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                        ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                        ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                        //期末
                        if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                            ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                            ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                        } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                            ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                            ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            ro.setQmJfwb(new BigDecimal("0.00"));
                            ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmJfMoney(new BigDecimal("0.00"));
                            ro.setQmDfwb(new BigDecimal("0.00"));
                            ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmDfmoney(new BigDecimal("0.00"));
                        }
                        list.add(ro);
                    });
                    return list;
                })
                .map(flist->{
                    //余额方向
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    //筛选过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));

    }

    @PostMapping(value = "/findAllProject")
    @ApiOperation(value = "查询项目总账", notes = "查询项目总账")
    public Mono<R> findAllProject(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        //项目大类
        String pcCode = maps.get("pcCode").toString();
        //项目编码 list
        Object pro =  maps.get("project");
        //币种
        String bz = maps.get("bz").toString();
        //科目范围
        String minCode = maps.get("minKm").toString();
        String maxCode = maps.get("maxKm").toString();
        String dept = Objects.isNull(maps.get("dept")) ? "" : maps.get("dept").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        String str = strDate.split("-")[0] + "01";
        //权限过滤参数 
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        // 查询所有参过滤
        return accvoucherRepository.findAllByKmAndProject(pcCode, minCode, maxCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(v -> {
                    // dataList 过滤部门币种项目 对项目唯一码分组
                    Map<String, List<GeneralLedgerRo>> map = v.stream().filter(o-> {
                                boolean flg = true;
                                if(StrUtil.isNotEmpty(dept) && !"0".equals(dept)){
                                    flg = o.getCdeptId().equals(dept);
                                }
                                if(Objects.nonNull(pro)){
                                    List<String> project = castList(pro, String.class);
                                    if(project.size()>0){
                                        flg = project.contains(o.getPcode());
                                    }
                                }
                                return flg && ("0".equals(bz) ? true : o.getCurrencytype().equals(bz));
                            })
                            .collect(Collectors.groupingBy(GeneralLedgerRo::getPcode));
                    //  项目项目唯一码排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    AtomicReference<Integer> index = new AtomicReference<>(1);
                    map2.forEach((k, vlist) -> {
                        if("0".equals(bz)){//全部
                            //对币种分组添加
                            Map<String, List<GeneralLedgerRo>> bzmap = vlist.stream().collect(Collectors.groupingBy(GeneralLedgerRo::getCurrencytype));
                            bzmap.forEach((bzk, bzlist)->{
                                //循环添加
                                // 期初00
                                List<GeneralLedgerRo> qcListStr = bzlist.stream().filter(obj -> {
                                    return "21".equals(obj.getImonth()) && obj.getCurrencytype().equals(bz);
                                }).collect(toList());

                                BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                                BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                                //期初 01 -  xx-1
                                //9-10 17-18  26-30
                                if (!strDate.contains("-01")) {
                                    LocalDate parse = LocalDate.parse(strDate + "-01");
                                    LocalDate localDate = parse.minusMonths(1);
                                    String strDateQc = strDate.split("-")[0] + "01";
                                    String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                    //符合条件的期初
                                    // 是否墨迹
                                    List<GeneralLedgerRo> qcListEnd = bzlist.stream().filter(obj -> {
                                        if (moji.equals("2")) {
                                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                    && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                        } else {
                                            return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                    && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                        }
                                    }).collect(toList());

                                    jwbqc =  qcListStr.stream().map(x -> {
                                        return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                    dwbqc = qcListStr.stream().map(x -> {
                                        return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                    jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                        return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                    dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                        return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                    jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                        return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                    dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                        return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                                }

                                //发生的数据
                                List<GeneralLedgerRo> dataList = bzlist.stream().filter(obj -> {
                                    if (moji.equals("2")) {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                    } else {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                    }
                                }).collect(toList());

                                BigDecimal jwb =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dwb = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jmd = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal dmc = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                BigDecimal jnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                                BigDecimal dnum = dataList.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                                DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                                ro.setNumber(index.getAndSet(index.get() + 1));
                                ro.setCcode(k);
                                ro.setCname(vlist.get(0).getPname());
                                ro.setDw(vlist.get(0).getMenterage());
                                ro.setWb(bzk);

                                //期初
                                ro.setQcJfwb(jwbqc);
                                ro.setQcJfnum(jnumqc);
                                ro.setQcJfMoney(jmdqc);
                                ro.setQcDfwb(dwbqc);
                                ro.setQcDfnum(dnumqc);
                                ro.setQcDfmoney(dmcqc);
                                //发生
                                ro.setJfwb(jwb);
                                ro.setJfnum(jnum);
                                ro.setJfMoney(jmd);
                                ro.setDfwb(dwb);
                                ro.setDfnum(dnum);
                                ro.setDfmoney(dmc);
                                //累计
                                ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                                ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                                ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                                ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                                //期末
                                if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                    ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                                } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                        ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                    ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                    ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                                } else {
                                    ro.setQmJfwb(new BigDecimal("0.00"));
                                    ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmJfMoney(new BigDecimal("0.00"));
                                    ro.setQmDfwb(new BigDecimal("0.00"));
                                    ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                    ro.setQmDfmoney(new BigDecimal("0.00"));
                                }
                                list.add(ro);
                            });
                        }else{
                            //币种如果只有一种  筛选
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> {
                                return "21".equals(obj.getImonth()) && obj.getCurrencytype().equals(bz);
                            }).collect(toList());

                            BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            BigDecimal jwbq = jwbqc;
                            BigDecimal dwbq = dwbqc;
                            BigDecimal jmdq = jmdqc;
                            BigDecimal dmcq = dmcqc;
                            BigDecimal jnumq = jnumqc;
                            BigDecimal dnumq = dnumqc;

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                    if (moji.equals("2")) {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                && obj.getCurrencytype().equals(bz)
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                    } else {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                && obj.getCurrencytype().equals(bz)
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                    }
                                }).collect(toList());

                                jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                            }

                            //发生的数据
                            List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                                if (moji.equals("2")) {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && obj.getCurrencytype().equals(bz)
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                } else {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                            && obj.getCurrencytype().equals(bz)
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                }
                            }).collect(toList());

                            //发生

                            BigDecimal jwb =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwb = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmd = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmc = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                            ro.setNumber(index.getAndSet(index.get() + 1));
                            ro.setCcode(k);
                            ro.setCname(vlist.get(0).getPname());
                            ro.setDw(vlist.get(0).getMenterage());
                            ro.setWb(vlist.get(0).getCurrencytype());

                            //期初
                            ro.setQcJfwb(jwbqc);
                            ro.setQcJfnum(jnumqc);
                            ro.setQcJfMoney(jmdqc);
                            ro.setQcDfwb(dwbqc);
                            ro.setQcDfnum(dnumqc);
                            ro.setQcDfmoney(dmcqc);
                            //发生
                            ro.setJfwb(jwb);
                            ro.setJfnum(jnum);
                            ro.setJfMoney(jmd);
                            ro.setDfwb(dwb);
                            ro.setDfnum(dnum);
                            ro.setDfmoney(dmc);
                            //累计
                            ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                            ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                            //期末
                            if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                ro.setQmJfwb(new BigDecimal("0.00"));
                                ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(new BigDecimal("0.00"));
                                ro.setQmDfwb(new BigDecimal("0.00"));
                                ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(new BigDecimal("0.00"));
                            }
                            list.add(ro);
                        }
                    });
                    return list;
                })
                .map(flist->{
                    //余额方向
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    //筛选过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));

    }

    @PostMapping(value = "/findAllKmProject")
    @ApiOperation(value = "查询科目项目总账", notes = "查询项目总账")
    public Mono<R> findAllKmProject(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        //项目大类
        String pcCode = maps.get("pcCode").toString();
        //项目编码 list
        Object pro =  maps.get("project");
        //币种
        String bz = maps.get("bz").toString();
        //科目范围
        // 项目大类 项目分类
        String kmCode = maps.get("minKm").toString()+"%";
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        String str = strDate.split("-")[0] + "01";
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //添加日志
        // 查询所有参过滤
        return accvoucherRepository.findAllKmProject(pcCode, kmCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(v -> {
                    // dataList  对项目唯一码分组
                    Map<String, List<GeneralLedgerRo>> map = v.stream().filter(o-> {
                        //币种  项目范围
                        boolean flg = true;
                        List<String> project = castList(pro, String.class);
                        if(Objects.nonNull(project) && project.size()>0){
                            flg = project.contains(o.getPcode());
                        }
                        return ("0".equals(bz) ? true : o.getCurrencytype().equals(bz)) && flg;
                    }).collect(Collectors.groupingBy(GeneralLedgerRo::getPcode));
                    // 项目项目唯一码排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    AtomicReference<Integer> index = new AtomicReference<>(1);
                    map2.forEach((k, vlist) -> {
                        // 期初00
                        List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> {
                            return ("21".equals(obj.getImonth())) && ("0".equals(bz)? true : obj.getCurrencytype().equals(bz));
                        }).collect(toList());

                        BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwbqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmdqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmcqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnumqc = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        //期初 01 -  xx-1
                        if (!strDate.contains("-01")) {
                            LocalDate parse = LocalDate.parse(strDate + "-01");
                            LocalDate localDate = parse.minusMonths(1);
                            String strDateQc = strDate.split("-")[0] + "01";
                            String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                            //符合条件的期初
                            //  是否墨迹   级次
                            List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                if (moji.equals("2")) {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                } else {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                            && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                }
                            }).collect(toList());

                            jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                            dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                            jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                            dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                        }

                        //发生的数据
                        List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                            if (moji.equals("2")) {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                            } else {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                            }
                        }).collect(toList());

                        BigDecimal jwb =  qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dwb = qcListStr.stream().map(x -> {
                            return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal jmd = dataList.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal dmc = dataList.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                        BigDecimal jnum = dataList.stream().map(x -> {
                            return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                        BigDecimal dnum = dataList.stream().map(x -> {
                            return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                        DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                        ro.setNumber(index.getAndSet(index.get() + 1));
                        ro.setCcode(k);
                        ro.setCname(vlist.get(0).getPname());
                        ro.setDw(vlist.get(0).getMenterage());
                        ro.setWb(vlist.get(0).getCurrencytype());

                        //期初
                        ro.setQcJfwb(jwbqc);
                        ro.setQcJfnum(jnumqc);
                        ro.setQcJfMoney(jmdqc);
                        ro.setQcDfwb(dwbqc);
                        ro.setQcDfnum(dnumqc);
                        ro.setQcDfmoney(dmcqc);
                        //发生
                        ro.setJfwb(jwb);
                        ro.setJfnum(jnum);
                        ro.setJfMoney(jmd);
                        ro.setDfwb(dwb);
                        ro.setDfnum(dnum);
                        ro.setDfmoney(dmc);
                        //累计
                        ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                        ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                        ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                        ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                        ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                        ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                        //期末
                        if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                            ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                            ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                        } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                            ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                            ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            ro.setQmJfwb(new BigDecimal("0.00"));
                            ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmJfMoney(new BigDecimal("0.00"));
                            ro.setQmDfwb(new BigDecimal("0.00"));
                            ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                            ro.setQmDfmoney(new BigDecimal("0.00"));
                        }
                        list.add(ro);

                    });
                    //合计
                    if(list.size()>0){
                        DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                        vo.setNumber(index.getAndSet(index.get() + 1));
                        vo.setCcode("合计");
                        list.forEach(obj->{
                            //期初
                            vo.setQcJfwb(obj.getQcJfwb().add(Objects.isNull(vo.getQcJfwb())?new BigDecimal("0.00"):vo.getQcJfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcJfnum(obj.getQcJfnum().add(Objects.isNull(vo.getQcJfnum())?new BigDecimal("0.00"):vo.getQcJfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcJfMoney(obj.getQcJfMoney().add(Objects.isNull(vo.getQcJfMoney())?new BigDecimal("0.00"):vo.getQcJfMoney()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfwb(obj.getQcDfwb().add(Objects.isNull(vo.getQcDfwb())?new BigDecimal("0.00"):vo.getQcDfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfnum(obj.getQcDfnum().add(Objects.isNull(vo.getQcDfnum())?new BigDecimal("0.00"):vo.getQcDfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfmoney(obj.getQcDfmoney().add(Objects.isNull(vo.getQcDfmoney())?new BigDecimal("0.00"):vo.getQcDfmoney()).setScale(jd, RoundingMode.HALF_UP));
                            //发生
                            vo.setJfwb(obj.getJfwb().add(Objects.isNull(vo.getJfwb())?new BigDecimal("0.00"):vo.getJfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setJfnum(obj.getJfnum().add(Objects.isNull(vo.getJfnum())?new BigDecimal("0.00"):vo.getJfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setJfMoney(obj.getJfMoney().add(Objects.isNull(vo.getJfMoney())?new BigDecimal("0.00"):vo.getJfMoney()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfwb(obj.getDfwb().add(Objects.isNull(vo.getDfwb())?new BigDecimal("0.00"):vo.getDfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfnum(obj.getDfnum().add(Objects.isNull(vo.getDfnum())?new BigDecimal("0.00"):vo.getDfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfmoney(obj.getDfmoney().add(Objects.isNull(vo.getDfmoney())?new BigDecimal("0.00"):vo.getDfmoney()).setScale(jd, RoundingMode.HALF_UP));

                            //累计
                            vo.setLjJfwb(vo.getQcJfwb().add(obj.getLjJfwb().add(Objects.isNull(vo.getLjJfwb())?new BigDecimal("0.00"):vo.getLjJfwb())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfnum(vo.getQcJfnum().add(obj.getLjJfnum().add(Objects.isNull(vo.getLjJfnum())?new BigDecimal("0.00"):vo.getLjJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfMoney(vo.getQcJfMoney().add(obj.getLjJfMoney().add(Objects.isNull(vo.getLjJfMoney())?new BigDecimal("0.00"):vo.getLjJfMoney())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfwb(vo.getQcDfwb().add(obj.getLjDfwb().add(Objects.isNull(vo.getLjDfwb())?new BigDecimal("0.00"):vo.getLjDfwb())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfnum(vo.getQcDfnum().add(obj.getLjDfnum().add(Objects.isNull(vo.getLjDfnum())?new BigDecimal("0.00"):vo.getLjDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfmoney(vo.getQcDfmoney().add(obj.getLjDfmoney().add(Objects.isNull(vo.getLjDfmoney())?new BigDecimal("0.00"):vo.getLjDfmoney())).setScale(jd, RoundingMode.HALF_UP));

                            //期末
                            if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                                vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                                vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                vo.setQmJfwb(new BigDecimal("0.00"));
                                vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(new BigDecimal("0.00"));
                                vo.setQmDfwb(new BigDecimal("0.00"));
                                vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(new BigDecimal("0.00"));
                            }
                        });
                        list.add(vo);
                    }
                    return list;
                })
                .map(flist->{
                    //-结果过滤-
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));

    }

    @PostMapping(value = "/findAllKmMulti")
    @ApiOperation(value = "查询科目多栏账", notes = "查询科目多栏账")
    public Mono<R> findAllKmMulti(@RequestBody Map maps) {

        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String kmCode = maps.get("km").toString();
        String dw = Objects.isNull(maps.get("dw")) ? "" : maps.get("dw").toString();
        String wb = Objects.isNull(maps.get("wb")) ? "" : maps.get("wb").toString();
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //权限类型集合
        String accType=maps.get("pzType").toString();                     // 根据凭证类别

        ArrayList<GeneralLedgerMultiVo> list = new ArrayList<>();
        String finalKmCode = kmCode + "%";
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        //添加日志
        String str = strDate.split("-")[0] + "00";

        //查询科目下所有凭证;
        List<String> kmList = (List<String>) maps.get("kmList");
        return accvoucherRepository.findAllMulti(kmList, str, finalEndDate)
                .collectList()
                .map(v -> {
                    if(v.size() > 0 ){
                        DecimalFormat df = new DecimalFormat(",###,##0.00");

                        // 对科目分组
                        Map<String, List<GeneralLedgerRo>> map = v.stream()
                                .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                        //  对科目排序
                        Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                        List<GeneralLedgerMultiVo.Entry> elist = new ArrayList<>();
                        //期初余额
                        GeneralLedgerMultiVo vo = new GeneralLedgerMultiVo();
                        vo.setNumber(1);
                        vo.setCdigest("期初余额");
                        //分别统计期初
                        map2.forEach((k, value) -> {
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = value.stream().filter(obj -> "00".equals(obj.getImonth())).collect(toList());

                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdq = jmdqc;
                            BigDecimal dmcq = dmcqc;

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = value.stream().filter(obj -> {
                                    if (moji.equals("2")) {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                                && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                    } else {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0)
                                                && (!"1".equals(isType) ? true : accType.contains(obj.getCsign()));
                                    }
                                }).collect(toList());

                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                            }

                            GeneralLedgerMultiVo.Entry e = new GeneralLedgerMultiVo.Entry();
                            e.setKey("j" + k);
                            e.setName(value.get(0).getCcodeName());
                            //格式化千分位
                            e.setMoney(df.format(jmdqc));
                            elist.add(e);
                            GeneralLedgerMultiVo.Entry e2 = new GeneralLedgerMultiVo.Entry();
                            e2.setKey("d" + k);
                            e2.setName(value.get(0).getCcodeName());
                            //格式化千分位  
                            e2.setMoney(df.format(dmcqc));
                            //
                            elist.add(e2);
                        });
                        vo.setList(new ArrayList<>());
                        //期初判断方向 根据list 计算借贷哪一方大
                        BigDecimal j = vo.getList().stream()
                                .filter(obj -> "借".equals(obj.getFx()))
                                .map(o->{
                                    return Objects.isNull(o.getMoney())? new BigDecimal("0.00"):new BigDecimal(o.getMoney());
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal d = vo.getList().stream()
                                .filter(obj -> "贷".equals(obj.getFx()))
                                .map(o->{
                                    return Objects.isNull(o.getMoney())? new BigDecimal("0.00"):new BigDecimal(o.getMoney());
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        if (j.compareTo(d) == 1) {
                            vo.setFx("借");
                            vo.setSymoney(j.subtract(d).setScale(2, RoundingMode.HALF_UP));
                        } else if (j.compareTo(d) == -1) {
                            vo.setFx("贷");
                            vo.setSymoney(j.subtract(d).setScale(2, RoundingMode.HALF_UP));
                        } else {
                            vo.setFx("平");
                            vo.setSymoney(new BigDecimal("0.00"));
                        }
                        list.add(vo);

                        //列表
                        List<GeneralLedgerRo> dataList = v.stream().filter(obj -> {
                            if (moji.equals("2")) {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                        && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            } else {
                                return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                        && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0)
                                        && ("1".equals(isType) ? true : accType.contains(obj.getCsign()));
                            }
                        }).collect(toList());
                        //根据月分组
                        Map<String, List<GeneralLedgerRo>> map3 = dataList.stream()
                                .collect(Collectors.groupingBy(GeneralLedgerRo::getIyperiod));
                        //对月排序
                        Map<String, List<GeneralLedgerRo>> map4 = map3.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                        AtomicInteger index = new AtomicInteger(1);
                        AtomicReference<Integer> indexs = new AtomicReference<>(0);
                        map4.forEach((k,obj)->{
                            obj.forEach(o->{
                                //便利格式化
                                GeneralLedgerMultiVo mvo = new GeneralLedgerMultiVo();
                                mvo.setNumber(index.incrementAndGet());
                                mvo.setDbillDate(o.getDbillDate());
                                mvo.setInoId(o.getInoId());
                                mvo.setCdigest(o.getCdigest());
                                mvo.setWb(wb);
                                //获取上一个余额 方向
                                GeneralLedgerMultiVo  previous = list.get(indexs.getAndSet(indexs.get()));
                                indexs.getAndSet(indexs.get() + 1);//+1

                                if(Objects.isNull(o.getMd()) || "0.00".equals(o.getMd())){
                                    mvo.setSdmoney(new BigDecimal(o.getMc()));
                                    mvo.setFx("贷");
                                }else{
                                    mvo.setSjmoney(new BigDecimal(o.getMd()));
                                    mvo.setFx("借");
                                }
                                //同方向
                                if(Objects.isNull(previous.getSymoney())){
                                    previous.setSymoney(new BigDecimal("0.00"));
                                }
                                if(previous.getFx().equals(mvo.getFx())){
                                    mvo.setSymoney(previous.getSymoney().add(new BigDecimal(Objects.isNull(o.getMd()) || "0.00".equals(o.getMd())?o.getMc():o.getMd())));
                                }else{
                                    //上一个方向和本方向 不同则判断大小
                                    BigDecimal bigDecimal = new BigDecimal(Objects.isNull(o.getMd()) || "0.00".equals(o.getMd()) ? o.getMc() : o.getMd());
                                    if (bigDecimal.compareTo(previous.getSymoney()) == 1) {
                                        mvo.setFx("借");
                                        mvo.setSymoney(bigDecimal.subtract(previous.getSymoney()).setScale(2, RoundingMode.HALF_UP));
                                    } else if (bigDecimal.compareTo(previous.getSymoney()) == -1) {
                                        mvo.setFx("贷");
                                        mvo.setSymoney(previous.getSymoney().subtract(bigDecimal).setScale(2, RoundingMode.HALF_UP));
                                    } else {
                                        mvo.setFx("平");
                                        mvo.setSymoney(new BigDecimal("0.00"));
                                    }
                                }
                                List<GeneralLedgerMultiVo.Entry> entryList = new ArrayList<>();
                                GeneralLedgerMultiVo.Entry e = new GeneralLedgerMultiVo.Entry();
                                e.setKey(("借".equals(mvo.getFx())?"j":"d") + o.getCcode());
                                e.setName(o.getCcodeName());
                                //格式化千分位
                                e.setMoney(Objects.isNull(o.getMd()) || "0.00".equals(o.getMd())?new BigDecimal(o.getMc()).toString():new BigDecimal(o.getMd()).toString());
                                e.setMoney(df.format(new BigDecimal(e.getMoney())));
                                entryList.add(e);
                                mvo.setList(entryList);
                                list.add(mvo);
                            });
                            //本月累计
                            GeneralLedgerMultiVo monthLj = new GeneralLedgerMultiVo();
                            monthLj.setDbillDate(k);
                            monthLj.setNumber(index.incrementAndGet());

                            monthLj.setCdigest("本月累计");
                            BigDecimal jmd = obj.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmc = obj.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            monthLj.setSjmoney(jmd);
                            monthLj.setSdmoney(dmc);

                            if (jmd.compareTo(dmc) == 1) {
                                monthLj.setFx("借");
                                monthLj.setSymoney(jmd.subtract(dmc).setScale(2, RoundingMode.HALF_UP));
                            } else if (jmd.compareTo(dmc) == -1) {
                                monthLj.setFx("贷");
                                monthLj.setSymoney(dmc.subtract(jmd).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                monthLj.setFx("平");
                                monthLj.setSymoney(new BigDecimal("0.00"));
                            }
                            //计算各个科目总和
                            //根据科目分组
                            Map<String, List<GeneralLedgerRo>> kmap = obj.stream()
                                    .collect(Collectors.groupingBy(GeneralLedgerRo::getCcode));
                            //  对科目排序
                            Map<String, List<GeneralLedgerRo>> kmap2 = kmap.entrySet().stream()
                                    .sorted(Map.Entry.comparingByKey())
                                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                            (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                            //计算对应ccode的借贷总和添加list
                            List<GeneralLedgerMultiVo.Entry> entryList = new ArrayList<>();
                            kmap2.forEach((key,o)->{

                                BigDecimal kj = o.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal kd = o.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                GeneralLedgerMultiVo.Entry je = new GeneralLedgerMultiVo.Entry();
                                je.setKey("j"+key);
                                je.setName(o.get(0).getCcodeName());
                                //格式化千分位
                                je.setMoney(df.format(kj));
                                entryList.add(je);

                                GeneralLedgerMultiVo.Entry de = new GeneralLedgerMultiVo.Entry();
                                de.setKey("d"+key);
                                de.setName(o.get(0).getCcodeName());
                                //格式化千分位
                                de.setMoney(df.format(kd));
                                if("0.00".equals(de.getMoney())){
                                    de.setMoney("");
                                }
                                entryList.add(de);

                            });
                            monthLj.setList(entryList);
                            list.add(monthLj);

                            //本年累计= 之前月之和
                            List<GeneralLedgerMultiVo> collect = list.stream().filter(item -> "本月累计".equals(item.getCdigest())).collect(toList());
                            GeneralLedgerMultiVo yearLj = new GeneralLedgerMultiVo();
                            yearLj.setDbillDate(k);
                            yearLj.setCdigest("本年累计");
                            yearLj.setNumber(index.incrementAndGet());
                            BigDecimal yjmd = collect.stream().map(x -> x.getSjmoney()).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal ydmc = collect.stream().map(x -> x.getSdmoney()).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal yymc = collect.stream().map(x -> x.getSymoney()).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            yearLj.setSjmoney(yjmd);
                            yearLj.setSdmoney(ydmc);
                            if (yjmd.compareTo(ydmc) == 1) {
                                yearLj.setFx("借");
                                yearLj.setSymoney(yjmd.subtract(ydmc).setScale(2, RoundingMode.HALF_UP));
                            } else if (yjmd.compareTo(ydmc) == -1) {
                                yearLj.setFx("贷");
                                yearLj.setSymoney(ydmc.subtract(yjmd).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                yearLj.setFx("平");
                                yearLj.setSymoney(new BigDecimal("0.00"));
                            }
                            //等于所有本月累计 相同ccode的总和 key name moeny
                            Map<String,GeneralLedgerMultiVo.Entry> emap = new HashMap<>();
                            collect.forEach(ieym->{
                                ieym.getList().forEach(sd->{
                                    GeneralLedgerMultiVo.Entry e = new GeneralLedgerMultiVo.Entry();
                                    e.setKey(sd.getKey());
                                    e.setName(sd.getName());
                                    if(emap.containsKey(sd.getKey())){
                                        String money = emap.get(sd.getKey()).getMoney();
                                        try {
                                            String a = new DecimalFormat().parse("".equals(money)?"0.00":money).toString();
                                            String b = new DecimalFormat().parse("".equals(sd.getMoney())?"0.00":sd.getMoney()).toString();
                                            e.setMoney(df.format(new BigDecimal(a).add(new BigDecimal(b))));
                                        } catch (ParseException ex) {
                                            ex.printStackTrace();
                                        }
                                    }else{
                                        e.setMoney(sd.getMoney());
                                    }
                                    emap.put(sd.getKey(), e);
                                });
                            });
                            //便利map 添加
                            List<GeneralLedgerMultiVo.Entry> yearList = new ArrayList<>();
                            emap.forEach((i,o)->{
                                if("0.00".equals(o.getMoney())){
                                    o.setMoney("");
                                }
                                yearList.add(o);
                            });
                            yearLj.setList(yearList);
                            list.add(yearLj);
                        });
                    }
                    return list;
                })
                .map(item -> {
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.of("boozsoft-nc", "booznc-group",null)
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerMultiVo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            //dv = list.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(flist -> {
                    //过滤
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<GeneralLedgerMultiVo> filterList = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("fx")) {
                                if (!item.getFx().contains(value) && !item.getFx().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }).collect(toList());
                    return filterList;
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping(value = "/findAllKmProjectDept")
    @ApiOperation(value = "查询项目部门总账", notes = "查询项目部门总账")
    public Mono<R> findAllKmProjectDept(@RequestBody Map maps) {
        String strDate = maps.get("strDate").toString().replace(".", "-");
        String endDate = maps.get("endDate").toString().replace(".", "-");
        String minDept =  maps.get("minDept").toString();
        String maxDept = maps.get("maxDept").toString();
        //项目大类
        String pcCode = maps.get("projectCate").toString();
        //项目编码 list
        Object pro =  maps.get("project");
        //币种
        String bz = maps.get("bz").toString();
        //科目范围
        // 项目大类 项目分类
        //部门
        String kmCode = maps.get("minKm").toString()+"%";
        Boolean ibook = Objects.isNull(maps.get("ibook")) ? true : Boolean.parseBoolean(maps.get("ibook").toString());
        Integer jd = Objects.isNull(maps.get("jd")) ? 2 : Integer.parseInt(maps.get("jd").toString());
        String moji = Objects.isNull(maps.get("moji")) ? "" : maps.get("moji").toString();
        ArrayList<DeptGeneralLedgerVo> list = new ArrayList<>();
        String finalStrDate = strDate.replace("-", "");
        String finalEndDate = endDate.replace("-", "");
        String str = strDate.split("-")[0] + "01";
        //权限过滤参数
        String userId = maps.get("userId").toString();
        String year = maps.get("year").toString();
        String accId = maps.get("accId").toString();
        String isCode = maps.get("isCode").toString();
        String isType = maps.get("isType").toString();
        //添加日志
        // 查询所有参过滤
        return accvoucherRepository.findAllKmProjectDept(pcCode, kmCode, str, finalEndDate,accId+"-"+year)
                .collectList()
                .map(item -> {
                    //科目权限过滤
                    if(!"1".equals(isCode)){
                        routerLoader.bind(
                                () -> sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ccode",v);
                        });
                        //过滤科目权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccCodeAuth> ctypeList = (ArrayList<SysAccCodeAuth>) maps.get("ccode");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCcode())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(item -> {
                    //类别权限过滤
                    if(!"1".equals(isType)){
                        routerLoader.bind(
                                () -> sysAccTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(userId, accId, year),
                                R2dbcRouterBuilder.ofDefault()
                        ).collectList().map(v-> {
                            return maps.put("ctype",v);
                        });
                        //过滤类别权限
                        List<GeneralLedgerRo> dv = new ArrayList<>();
                        ArrayList<SysAccTypeAuth> ctypeList = (ArrayList<SysAccTypeAuth>) maps.get("ctype");
                        if(Objects.nonNull(ctypeList) && ctypeList.size() > 0){
                            dv = item.stream().filter(v -> ctypeList.contains(v.getCsign())).collect(toList());
                        }
                        return dv;
                    }else{
                        return item;
                    }
                })
                .map(v -> {
                    // dataList  对部门唯一码分组
                    Map<String, List<GeneralLedgerRo>> map = v.stream().filter(o-> {
                        //部门过滤
                        boolean deptflg =  ((minDept.compareTo(o.getDcode()) < 0 && maxDept.compareTo(o.getDcode()) > 0) || minDept.compareTo(o.getDcode()) == 0 || maxDept.compareTo(o.getDcode()) == 0);

                        //币种  项目范围
                        boolean flg = true;
                        List<String> project = castList(pro, String.class);
                        if(Objects.nonNull(project) && project.size()>0){
                            flg = project.contains(o.getPcode());
                        }
                        return ("0".equals(bz) ? true : o.getCurrencytype().equals(bz)) && flg && deptflg;
                    }).collect(Collectors.groupingBy(GeneralLedgerRo::getDcode));

                    // 部门唯一码排序
                    Map<String, List<GeneralLedgerRo>> map2 = map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    AtomicReference<Integer> index = new AtomicReference<>(1);
                    map2.forEach((dk, dlist) -> {

                        //根据项目分组
                        Map<String, List<GeneralLedgerRo>> pmap = dlist.stream().collect(Collectors.groupingBy(GeneralLedgerRo::getPcode));
                        // 部门唯一码排序
                        Map<String, List<GeneralLedgerRo>> pmap2 = pmap.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));

                        pmap2.forEach((k, vlist) -> {
                            // 期初00
                            List<GeneralLedgerRo> qcListStr = vlist.stream().filter(obj -> {
                                return ("21".equals(obj.getImonth())) && ("0".equals(bz)? true : obj.getCurrencytype().equals(bz));
                            }).collect(toList());

                            BigDecimal jwbqc =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwbqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmdqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmcqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnumqc = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            //期初 01 -  xx-1
                            if (!strDate.contains("-01")) {
                                LocalDate parse = LocalDate.parse(strDate + "-01");
                                LocalDate localDate = parse.minusMonths(1);
                                String strDateQc = strDate.split("-")[0] + "01";
                                String endDateQc = localDate.toString().substring(0, localDate.toString().lastIndexOf("-")).replace("-", "");
                                //符合条件的期初
                                //  是否墨迹   级次
                                List<GeneralLedgerRo> qcListEnd = vlist.stream().filter(obj -> {
                                    if (moji.equals("2")) {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                    } else {
                                        return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                                && ((strDateQc.compareTo(obj.getIyperiod()) < 0 && endDateQc.compareTo(obj.getIyperiod()) > 0) || strDateQc.compareTo(obj.getIyperiod()) == 0 || endDateQc.compareTo(obj.getIyperiod()) == 0);
                                    }
                                }).collect(toList());

                                jwbqc =  qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                                dwbqc = qcListStr.stream().map(x -> {
                                    return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                jmdqc = jmdqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);
                                dmcqc = dmcqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(2, RoundingMode.HALF_UP);

                                jnumqc = jnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);
                                dnumqc = dnumqc.add(qcListEnd.stream().map(x -> {
                                    return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                                }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add)).setScale(jd, RoundingMode.HALF_UP);

                            }

                            //发生的数据
                            List<GeneralLedgerRo> dataList = vlist.stream().filter(obj -> {
                                if (moji.equals("2")) {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getBend().equals("1") && obj.getIfrag().equals("0")
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                } else {
                                    return (ibook == true ? true : "1".equals(obj.getIbook())) && Objects.nonNull(obj.getIfrag()) && obj.getIfrag().equals("0")
                                            && ((finalStrDate.compareTo(obj.getIyperiod()) < 0 && finalEndDate.compareTo(obj.getIyperiod()) > 0) || finalStrDate.compareTo(obj.getIyperiod()) == 0 || finalEndDate.compareTo(obj.getIyperiod()) == 0);
                                }
                            }).collect(toList());

                            BigDecimal jwb =  qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMd()) || x.getNfratMd().length() <= 0 ? "0.00" : x.getNfratMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dwb = qcListStr.stream().map(x -> {
                                return Objects.isNull(x.getNfratMc()) || x.getNfratMc().length() <= 0 ? "0.00" : x.getNfratMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal jmd = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal dmc = dataList.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            BigDecimal jnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNdS()) || x.getNdS().length() <= 0 ? "0.00" : x.getNdS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);
                            BigDecimal dnum = dataList.stream().map(x -> {
                                return Objects.isNull(x.getNcS()) || x.getNcS().length() <= 0 ? "0.00" : x.getNcS();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(jd, RoundingMode.HALF_UP);

                            DeptGeneralLedgerVo ro = new DeptGeneralLedgerVo();
                            ro.setNumber(index.getAndSet(index.get() + 1));
                            ro.setCcode(k);
                            ro.setCname(vlist.get(0).getPname());
                            ro.setDw(vlist.get(0).getMenterage());
                            ro.setWb(vlist.get(0).getCurrencytype());
                            ro.setDcode(dk);
                            ro.setDname(vlist.get(0).getDname());
                            //期初
                            ro.setQcJfwb(jwbqc);
                            ro.setQcJfnum(jnumqc);
                            ro.setQcJfMoney(jmdqc);
                            ro.setQcDfwb(dwbqc);
                            ro.setQcDfnum(dnumqc);
                            ro.setQcDfmoney(dmcqc);
                            //发生
                            ro.setJfwb(jwb);
                            ro.setJfnum(jnum);
                            ro.setJfMoney(jmd);
                            ro.setDfwb(dwb);
                            ro.setDfnum(dnum);
                            ro.setDfmoney(dmc);
                            //累计
                            ro.setLjJfwb(jwbqc.add(jwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjJfnum(jnumqc.add(jnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjJfMoney(jmdqc.add(jmd).setScale(2, RoundingMode.HALF_UP));

                            ro.setLjDfwb(dwbqc.add(dwb).setScale(2, RoundingMode.HALF_UP));
                            ro.setLjDfnum(dnumqc.add(dnum).setScale(jd, RoundingMode.HALF_UP));
                            ro.setLjDfmoney(dmcqc.add(dmc).setScale(2, RoundingMode.HALF_UP));
                            //期末
                            if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == 1) {
                                ro.setQmJfwb(ro.getQcJfwb().add(ro.getJfwb()).subtract(ro.getQcDfwb().add(ro.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmJfnum(ro.getQcJfnum().add(ro.getJfnum()).subtract(ro.getQcDfnum().add(ro.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(ro.getQcJfMoney().add(ro.getJfMoney()).subtract(ro.getQcDfmoney().add(ro.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (ro.getQcJfMoney().add(ro.getJfMoney()).compareTo(
                                    ro.getQcDfmoney().add(ro.getDfmoney())) == -1) {
                                ro.setQmDfwb(ro.getQcDfwb().add(ro.getDfwb()).subtract(ro.getQcJfwb().add(ro.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                ro.setQmDfnum(ro.getQcDfnum().add(ro.getDfnum()).subtract(ro.getQcJfnum().add(ro.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(ro.getQcDfmoney().add(ro.getDfmoney()).subtract(ro.getQcJfMoney().add(ro.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                ro.setQmJfwb(new BigDecimal("0.00"));
                                ro.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmJfMoney(new BigDecimal("0.00"));
                                ro.setQmDfwb(new BigDecimal("0.00"));
                                ro.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                ro.setQmDfmoney(new BigDecimal("0.00"));
                            }
                            list.add(ro);
                        });
                    });
                    //合计
                    if(list.size()>0){
                        DeptGeneralLedgerVo vo = new DeptGeneralLedgerVo();
                        vo.setNumber(index.getAndSet(index.get() + 1));
                        vo.setDcode("合计");
                        list.forEach(obj->{
                            //期初
                            vo.setQcJfwb(obj.getQcJfwb().add(Objects.isNull(vo.getQcJfwb())?new BigDecimal("0.00"):vo.getQcJfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcJfnum(obj.getQcJfnum().add(Objects.isNull(vo.getQcJfnum())?new BigDecimal("0.00"):vo.getQcJfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcJfMoney(obj.getQcJfMoney().add(Objects.isNull(vo.getQcJfMoney())?new BigDecimal("0.00"):vo.getQcJfMoney()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfwb(obj.getQcDfwb().add(Objects.isNull(vo.getQcDfwb())?new BigDecimal("0.00"):vo.getQcDfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfnum(obj.getQcDfnum().add(Objects.isNull(vo.getQcDfnum())?new BigDecimal("0.00"):vo.getQcDfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setQcDfmoney(obj.getQcDfmoney().add(Objects.isNull(vo.getQcDfmoney())?new BigDecimal("0.00"):vo.getQcDfmoney()).setScale(jd, RoundingMode.HALF_UP));
                            //发生
                            vo.setJfwb(obj.getJfwb().add(Objects.isNull(vo.getJfwb())?new BigDecimal("0.00"):vo.getJfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setJfnum(obj.getJfnum().add(Objects.isNull(vo.getJfnum())?new BigDecimal("0.00"):vo.getJfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setJfMoney(obj.getJfMoney().add(Objects.isNull(vo.getJfMoney())?new BigDecimal("0.00"):vo.getJfMoney()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfwb(obj.getDfwb().add(Objects.isNull(vo.getDfwb())?new BigDecimal("0.00"):vo.getDfwb()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfnum(obj.getDfnum().add(Objects.isNull(vo.getDfnum())?new BigDecimal("0.00"):vo.getDfnum()).setScale(jd, RoundingMode.HALF_UP));
                            vo.setDfmoney(obj.getDfmoney().add(Objects.isNull(vo.getDfmoney())?new BigDecimal("0.00"):vo.getDfmoney()).setScale(jd, RoundingMode.HALF_UP));

                            //累计
                            vo.setLjJfwb(vo.getQcJfwb().add(obj.getLjJfwb().add(Objects.isNull(vo.getLjJfwb())?new BigDecimal("0.00"):vo.getLjJfwb())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfnum(vo.getQcJfnum().add(obj.getLjJfnum().add(Objects.isNull(vo.getLjJfnum())?new BigDecimal("0.00"):vo.getLjJfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjJfMoney(vo.getQcJfMoney().add(obj.getLjJfMoney().add(Objects.isNull(vo.getLjJfMoney())?new BigDecimal("0.00"):vo.getLjJfMoney())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfwb(vo.getQcDfwb().add(obj.getLjDfwb().add(Objects.isNull(vo.getLjDfwb())?new BigDecimal("0.00"):vo.getLjDfwb())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfnum(vo.getQcDfnum().add(obj.getLjDfnum().add(Objects.isNull(vo.getLjDfnum())?new BigDecimal("0.00"):vo.getLjDfnum())).setScale(jd, RoundingMode.HALF_UP));
                            vo.setLjDfmoney(vo.getQcDfmoney().add(obj.getLjDfmoney().add(Objects.isNull(vo.getLjDfmoney())?new BigDecimal("0.00"):vo.getLjDfmoney())).setScale(jd, RoundingMode.HALF_UP));

                            //期末
                            if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == 1) {
                                vo.setQmJfwb(vo.getQcJfwb().add(vo.getJfwb()).subtract(vo.getQcDfwb().add(vo.getDfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmJfnum(vo.getQcJfnum().add(vo.getJfnum()).subtract(vo.getQcDfnum().add(vo.getDfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(vo.getQcJfMoney().add(vo.getJfMoney()).subtract(vo.getQcDfmoney().add(vo.getDfmoney())).setScale(2, RoundingMode.HALF_UP));
                            } else if (vo.getQcJfMoney().add(vo.getJfMoney()).compareTo(
                                    vo.getQcDfmoney().add(vo.getDfmoney())) == -1) {
                                vo.setQmDfwb(vo.getQcDfwb().add(vo.getDfwb()).subtract(vo.getQcJfwb().add(vo.getJfwb())).setScale(2, RoundingMode.HALF_UP));
                                vo.setQmDfnum(vo.getQcDfnum().add(vo.getDfnum()).subtract(vo.getQcJfnum().add(vo.getJfnum())).setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(vo.getQcDfmoney().add(vo.getDfmoney()).subtract(vo.getQcJfMoney().add(vo.getJfMoney())).setScale(2, RoundingMode.HALF_UP));
                            } else {
                                vo.setQmJfwb(new BigDecimal("0.00"));
                                vo.setQmJfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmJfMoney(new BigDecimal("0.00"));
                                vo.setQmDfwb(new BigDecimal("0.00"));
                                vo.setQmDfnum(new BigDecimal("0.00").setScale(jd, RoundingMode.HALF_UP));
                                vo.setQmDfmoney(new BigDecimal("0.00"));
                            }
                        });
                        list.add(vo);
                    }
                    return list;
                })
                .map(flist->{
                    //-结果过滤-
                    boolean jfye = Objects.isNull(maps.get("jfye")) ? false : Boolean.parseBoolean(maps.get("jfye").toString());
                    boolean dfye = Objects.isNull(maps.get("dfye")) ? false : Boolean.parseBoolean(maps.get("dfye").toString());
                    String minYe = Objects.isNull(maps.get("minYe")) ? "" : maps.get("minYe").toString();
                    String maxYe = Objects.isNull(maps.get("maxYe")) ? "" : maps.get("maxYe").toString();
                    Map<String, String> searchMap = ((HashMap<String, String>) maps.get("searchConditon"));
                    Map<String, String> filterMap = ((HashMap<String, String>) maps.get("filterConditon"));
                    List<DeptGeneralLedgerVo> collect = flist.stream().filter(item -> {
                        if (StrUtil.isNotBlank(minYe) || StrUtil.isNotBlank(maxYe)) {
                            if (jfye == dfye) {
                                //全部控制
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && jfye) {
                                //控制借方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmJfMoney())? BigDecimal.ZERO :item.getQmJfMoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            } else if (jfye != dfye && dfye) {
                                //控制贷方余额
                                if (StrUtil.isNotBlank(minYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(minYe)) < 0) {
                                    return false;
                                }
                                if (StrUtil.isNotBlank(maxYe) && (Objects.isNull(item.getQmDfmoney())? BigDecimal.ZERO :item.getQmDfmoney()).compareTo(new BigDecimal(maxYe)) > 0) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            if (searchMap.get("requirement").trim().equals("cname")) {
                                if (!item.getCcode().contains(value) && !item.getCname().contains(value)) {
                                    return false;
                                }
                            } else {
                                String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }

                        if (StrUtil.isNotBlank(filterMap.get("amountMinJf")) && StrUtil.isNotBlank(filterMap.get("amountMaxJf"))) {
                            BigDecimal min = new BigDecimal(filterMap.get("amountMinJf"));
                            BigDecimal max = new BigDecimal(filterMap.get("amountMaxJf"));
                            if (item.getJfMoney().compareTo(min) < 0 || item.getJfMoney().compareTo(max) > 0) {
                                return false;
                            }
                        }

                        return true;
                    }).collect(toList());

                    return collect;
                })
                .map(o -> R.ok().setResult(o));

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

    public void copyList(Object obj, List<GeneralLedgerRo> list2, Class<GeneralLedgerRo> classObj) {
        if ((!Objects.isNull(obj)) && (!Objects.isNull(list2))) {
            List list1 = (List) obj;
            list1.forEach(item -> {
                try {
                    GeneralLedgerRo data = classObj.newInstance();
                    BeanUtils.copyProperties(item, data);
                    list2.add(data);
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                }
            });
        }
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }


    @PostMapping("/findXjllMx")
    @ApiOperation(value = "查询现金流量明细账", notes = "查询现金流量明细账")
    public Mono<R> findXjllMx(@RequestBody Map map) {
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String strDate = map.get("strDate").toString().replace(".", "");
        String endDate = map.get("endDate").toString().replace(".", "");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return accvoucherRepository.findXjlxMx(strDate,endDate)
                .collectList()
                .map(list->{
                    //计算小计
                    //分组求和 然后根据key查询list最后出现位置 插入
                    Map<String, List<AccvoucherXjlxMxVo>> m = list.stream().collect(Collectors.groupingBy(AccvoucherXjlxMxVo::getXjcode));
                    // 项目code排序
                    Map<String, List<AccvoucherXjlxMxVo>> map2 = m.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    map2.keySet().stream().forEach(item->{
                        AccvoucherXjlxMxVo v = new AccvoucherXjlxMxVo();
                        v.setXjcode("("+item+")"+"小计:");
                        List<AccvoucherXjlxMxVo> accvoucherXjlxMxVos = map2.get(item);
                        String fx = accvoucherXjlxMxVos.stream().findFirst().get().getFx();
                        if("1".equals(fx)){
                            BigDecimal md =  accvoucherXjlxMxVos.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            v.setMoney(md.toString());
                        }else{
                            BigDecimal mc =  accvoucherXjlxMxVos.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            v.setMoney(mc.toString());
                        }
                        list.add(list.stream().map(AccvoucherXjlxMxVo::getXjcode).collect(toList()).lastIndexOf(item)+1, v);
                    });
                    //合计
                    AccvoucherXjlxMxVo v = new AccvoucherXjlxMxVo();
                    v.setXjcode("合计:");
                    v.setFx(v.getFx());
                    BigDecimal md = list.stream().map(x -> {
                        return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal mc = list.stream().map(x -> {
                        return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    v.setMoney(md.subtract(mc).toString());
                    list.add(v);
                    return list;
                })
                .map(list -> splitList(list, page, pageSize, totalAR))
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    @PostMapping("/findXjllTj")
    @ApiOperation(value = "查询现金流量统计表", notes = "查询现金流量统计表")
    public Mono<R> findXjllTj(@RequestBody Map map) {
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String strDate = map.get("strDate").toString().replace(".", "");
        String endDate = map.get("endDate").toString().replace(".", "");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return accvoucherRepository.findXjlxMx(strDate,endDate)
                .collectList()
                .map(list->{
                    List<AccvoucherXjlxMxVo> list1 =  new ArrayList<>();
                    //计算小计
                    //分组求和 然后根据key查询list最后出现位置 插入
                    Map<String, List<AccvoucherXjlxMxVo>> m = list.stream().collect(Collectors.groupingBy(AccvoucherXjlxMxVo::getXjcode));
                    // 项目code排序
                    Map<String, List<AccvoucherXjlxMxVo>> map2 = m.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                    (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                    List<String> codeList = list.stream().map(AccvoucherXjlxMxVo::getXjcode).collect(toList());
                    map2.keySet().stream().forEach(item->{
                        List<AccvoucherXjlxMxVo> accvoucherXjlxMxVos = map2.get(item);
                        AccvoucherXjlxMxVo v = new AccvoucherXjlxMxVo();
                        v.setXjcode("("+item+")"+"小计:");
                        String  fx = accvoucherXjlxMxVos.stream().findFirst().get().getFx();
                        if("1".equals(fx)){
                            BigDecimal md =  accvoucherXjlxMxVos.stream().map(x -> {
                                return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            v.setMoney(md.toString());
                        }else{
                            BigDecimal mc =  accvoucherXjlxMxVos.stream().map(x -> {
                                return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                            }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                            v.setMoney(mc.toString());
                        }
                        v.setFx(v.getFx());
                        AccvoucherXjlxMxVo accvoucherXjlxMxVo = accvoucherXjlxMxVos.get(0);
                        accvoucherXjlxMxVo.setMoney(v.getMoney());
                        list1.add(accvoucherXjlxMxVo);
                        list1.add(v);
                    });
                    //合计
                    AccvoucherXjlxMxVo v = new AccvoucherXjlxMxVo();
                    v.setXjcode("合计:");
                    v.setFx(v.getFx());
                    BigDecimal md = list.stream().map(x -> {
                        return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal mc = list.stream().map(x -> {
                        return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                    }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
                    v.setMoney(md.subtract(mc).toString());
                    list1.add(v);
                    return list1;
                })
                .map(list -> splitList(list, page, pageSize, totalAR))
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }


    public static List<AccvoucherXjlxMxVo> splitList(List<AccvoucherXjlxMxVo> list, int pageNo, int pageSize, AtomicReference<Integer> titlesAR) {
        if (CollectionUtils.isEmpty(list)) {
            titlesAR.set(0);
            return new ArrayList<>();
        }
        int totalCount = list.size();
        titlesAR.set(totalCount);
        pageNo = pageNo - 1;
        int fromIndex = pageNo * pageSize;
        // 分页不能大于总数
        if (fromIndex >= totalCount) {
            return null;
        }
        int toIndex = ((pageNo + 1) * pageSize);
        if (toIndex > totalCount) {
            toIndex = totalCount;
        }
        return list.subList(fromIndex, toIndex);
    }

    public static int indexOf(List<DeptGeneralLedgerVo> list, CodeKemu predicate) {
        int idx = 0;
        for (Iterator iter = list.iterator(); iter.hasNext(); idx++) {
            DeptGeneralLedgerVo obj = (DeptGeneralLedgerVo) iter.next();
            if (obj.getCcode().startsWith(predicate.getCcode())) {
                return idx;
            }
        }
        return -1;
    }

}


