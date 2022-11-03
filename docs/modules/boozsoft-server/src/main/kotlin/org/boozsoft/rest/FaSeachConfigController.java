package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.boozsoft.config.r2dbc.R2dbcRouterLoaderImpl;
import org.boozsoft.domain.entity.FaAssetType;
import org.boozsoft.domain.entity.KmCashFlow;
import org.boozsoft.domain.entity.SysAccCodeAuth;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.group.GroupFaAccPeriod;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;
import org.boozsoft.domain.ro.GdGeneralLedgerRo;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.vo.GdGeneralLedgerVo;
import org.boozsoft.domain.vo.GeneralLedgerVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.fa.FaCardMasterRepository;
import org.boozsoft.repo.group.GroupFaAccPeriodRepository;
import org.boozsoft.repo.group.GroupFaAccountRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


/**
 * 筛选配置
 *
 * @author lz
 */
@Slf4j
@RestController
@RequestMapping("/faSeachConfig")
@Api(value = "筛选配置", tags = "API系统：筛选配置")
public class FaSeachConfigController {

    @Autowired
    FaAssetTypeRepository faAssetTypeRepository;
    @Autowired
    GroupFaAccPeriodRepository faAccPeriodRepository;
    @Autowired
    GroupFaAccountRepository groupFaAccountRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;
    @Autowired
    SysPsnRepository sysPsnRepository;
    @Autowired
    FaCardMasterRepository faCardMasterRepository;
    //查询筛选条件
    @PostMapping(value = "/findAll")
    @ApiOperation(value = "查询总账", notes = "查询总账")
    public Mono<R> findAll(@RequestBody Map maps) {

        String type = maps.get("type").toString();
        String manageCode = maps.get("manageCode").toString();
        String year = maps.get("date").toString();
        String ecCode = maps.get("km").toString();
        String years = year + "-31";//月份最大日期
        //计次
        String minJc = maps.get("minJc").toString();
        String maxJc = maps.get("maxJc").toString();
        List<GdGeneralLedgerVo> glist =  new ArrayList<>();
        //根据类型去区分查询
        if("T1".equals(type)){//类别查询
            Mono<R> oneMap = faCardMasterRepository.findOneByLb(manageCode, year + "%", years, ecCode + "%").collectList()
                    .map(list -> {
                        //汇总 
                        BigDecimal yuanzhi = list.stream().map(x -> {
                            return Objects.isNull(x.getYuanzhi()) ? new BigDecimal("0.00") : new BigDecimal(x.getYuanzhi()).setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal ljzhejiu = list.stream().map(x -> {
                            return Objects.isNull(x.getLjzhejiu()) ? new BigDecimal("0.00") : new BigDecimal(x.getLjzhejiu()).setScale(2, RoundingMode.HALF_UP);
                        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        GdGeneralLedgerVo vo = new GdGeneralLedgerVo();
                        vo.setNumber(1);
                        vo.setLb(list.get(0).getEcname());
                        vo.setYye(yuanzhi.toString());
                        vo.setZye(ljzhejiu.toString());
                        vo.setJingzhi(yuanzhi.subtract(ljzhejiu).toString());
                        glist.add(vo);
                        return glist;
                    })
                    .map(o -> R.ok().setResult(o));


            Mono<R> allMap = faCardMasterRepository.findAllByLb(manageCode, year + "%", years).collectList()
                    .map(list -> {
                        //根据code分组排序后 添加计算
                        Map<String, List<GdGeneralLedgerRo>> map = list.stream()
                                .collect(Collectors.groupingBy(GdGeneralLedgerRo::getEccode));
                        //  对编码排序
                        Map<String, List<GdGeneralLedgerRo>> map2 = map.entrySet().stream()
                                .sorted(Map.Entry.comparingByKey())
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                        (oleValue, newValue) -> oleValue, LinkedHashMap::new));
                        map2.forEach((k,v)->{
                            //汇总
                            BigDecimal yuanzhi = v.stream().map(x -> {
                                return Objects.isNull(x.getYuanzhi()) ? new BigDecimal("0.00") : new BigDecimal(x.getYuanzhi()).setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            BigDecimal ljzhejiu = v.stream().map(x -> {
                                return Objects.isNull(x.getLjzhejiu()) ? new BigDecimal("0.00") : new BigDecimal(x.getLjzhejiu()).setScale(2, RoundingMode.HALF_UP);
                            }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                            GdGeneralLedgerVo vo = new GdGeneralLedgerVo();
                            vo.setNumber(1);
                            vo.setLb(v.get(0).getEcname());
                            vo.setLbCode(v.get(0).getEccode());
                            vo.setYye(yuanzhi.toString());
                            vo.setZye(ljzhejiu.toString());
                            vo.setJingzhi(yuanzhi.subtract(ljzhejiu).toString());
                            glist.add(vo);
                        });
                        return glist;
                    })
                    .flatMap(item->{
                        return  faAssetTypeRepository.findByFlagAndBendAndZjTypeOrderByEcCode("1","0","1").collectList();
                    })
                    .map(flist->{
                        //比对 非末级
                        // 全部
                        // 1010101 1010102   然后统计父节点的值  10101  101
                        flist.stream().forEach(v->{
                            //以父级编码开头
                            List<GdGeneralLedgerVo> collect = glist.stream().filter(o -> o.getLbCode().startsWith(v.getEcCode())).collect(toList());
                            if(collect.size() > 0 ){
                                //汇总
                                BigDecimal yye = collect.stream().map(x -> {
                                    return Objects.isNull(x.getYye()) ? new BigDecimal("0.00") : new BigDecimal(x.getYye()).setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal zye = collect.stream().map(x -> {
                                    return Objects.isNull(x.getZye()) ? new BigDecimal("0.00") : new BigDecimal(x.getZye()).setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                BigDecimal jye = collect.stream().map(x -> {
                                    return Objects.isNull(x.getJye()) ? new BigDecimal("0.00") : new BigDecimal(x.getJye()).setScale(2, RoundingMode.HALF_UP);
                                }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                GdGeneralLedgerVo vo = new GdGeneralLedgerVo();
                                vo.setNumber(1);
                                vo.setLb(v.getEcName());
                                vo.setLbCode(v.getEcCode());
                                vo.setYye(yye.toString());
                                vo.setZye(zye.toString());
                                vo.setJingzhi(jye.toString());

                                //插入 第一个编码前面
                                int i = indexOf(glist, v);
                                glist.add(i, vo);
                             }
                        });
                        return glist;
                    })
                    .map(o -> R.ok().setResult(o));

            return Objects.isNull(ecCode) || ecCode.length() < 1 || "0".equals(ecCode)? allMap : oneMap;
        }else if("T2".equals(type)){

        }else if("T3".equals(type)){

        }else if("T4".equals(type)){

        }else if("T5".equals(type)){

        }else if("T6".equals(type)){

        }else if("T7".equals(type)){

        }else if("T8".equals(type)){

        }else if("T9".equals(type)){

        }
        return null;
    }

    public static int indexOf(List<GdGeneralLedgerVo> list, FaAssetType predicate) {
        int idx = 0;
        for (Iterator iter = list.iterator(); iter.hasNext(); idx++) {
            GdGeneralLedgerVo obj = (GdGeneralLedgerVo) iter.next();
            if (obj.getLbCode().startsWith(predicate.getEcCode())) {
                return idx;
            }
        }
        return -1;
    }


    @GetMapping("/findAccountList")
    @ApiOperation(value = "查询核算码列表", notes = "查询核算码列表")
    public Mono<R> findAccountList(){
        return groupFaAccountRepository.findAllList().collectList()
                .map(o->R.ok().setResult(o));
    }

    @GetMapping("/findMaxJc")
    @ApiOperation(value = "查询类别级次范围", notes = "查询类别级次范围")
    public Mono<R> findMaxJc(){
        return faAssetTypeRepository.findMaxLeave().map(o->R.ok().setResult(o));
    }

    @GetMapping("/findPeriod/{uniqueCode}")
    @ApiOperation(value = "查询日期列表", notes = "查询日期列表")
    public Mono<R> findPeriod(@PathVariable String uniqueCode){
        return faAccPeriodRepository.findByUniqueCode(uniqueCode)
                .map(o->R.ok().setResult(o));
    }

    @GetMapping("/findProject")
    @ApiOperation(value = "查询项目", notes = "查询项目")
    public Mono<R> findProject() {
        return projectRepositoryBase.findAll().collectList().map(o->R.ok().setResult(o));
    }

    @GetMapping("/findPsnAll")
    @ApiOperation(value = "查询所有部门", notes = "查询所有部门")
    public Mono<R> findPsnAll() {
        return sysPsnRepository.findAllByFlag("1").collectList()
                .map(list->{
                    list.stream().forEach(v->{
                        v.setLabel(v.getPsnCode()+"-"+v.getPsnName());
                    });
                    return list;
                })
                .map(o->R.ok().setResult(o));
    }

    @GetMapping("/findFaAssetType")
    @ApiOperation(value = "查询资产类别", notes = "查询资产类别")
    public Mono<R> findFaAssetType() {
        return faAssetTypeRepository.findByFlagAndBendAndZjTypeOrderByEcCode("1","1","1").collectList().map(o->R.ok().setResult(o));
    }
}

