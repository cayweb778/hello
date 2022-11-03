package org.boozsoft.rest;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import com.alibaba.nacos.common.utils.MapUtils;
import com.alibaba.nacos.common.utils.UuidUtils;
import org.boozsoft.domain.entity.Accvoucher;
import org.boozsoft.domain.entity.CashFlow;
import org.boozsoft.domain.entity.KmCashFlow;
import org.boozsoft.domain.entity.WriteOff;
import org.boozsoft.domain.ro.GeneralLedgerRo;
import org.boozsoft.domain.ro.WriteOffRo;
import org.boozsoft.domain.vo.CashFlowVo;
import org.boozsoft.domain.vo.ProjectCashCodeVo;
import org.boozsoft.repo.*;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 往来核销
 * @author lz
 */
@RestController
@RequestMapping("/writeOff")
public class WriteOffController {

    @Autowired
    WriteOffRepository writeOffFlowRepository;
    @Autowired
    AccvoucherRepository accvoucherRepository;

    /**
     * 个人往来核销
     * @param map
     * @return
     */
    @PostMapping("/findAllAccvoucherHexiaoGeren")
    public Mono<R> findAllAccvoucherHexiaoGeren(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String km = map.get("km").toString();
        String jpzDate = map.containsKey("jpzDate") ? map.get("jpzDate").toString(): "";
        String dpzDate = map.containsKey("dpzDate") ? map.get("dpzDate").toString(): "";
        String type =  map.get("type").toString();

        AtomicReference<Integer> totalAR = new AtomicReference();
        AtomicReference<Integer> index = new AtomicReference(1);
        return accvoucherRepository.findAllByCodeGeren(km).collectList()//1-16 21
                .map(list -> {
                    //分开 借贷凭证 然后先过滤 分别分页过滤
                    List<WriteOffRo> accList =  new ArrayList<>();
                    if("j".equals(type)){
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMc()) && new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }

                            if(StrUtil.isNotEmpty(jpzDate)){
                                String str = jpzDate.split("=")[0];
                                String end = jpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }else{
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMd()) && new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }
                            if(StrUtil.isNotEmpty(dpzDate)){
                                String str = dpzDate.split("=")[0];
                                String end = dpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }
                    return splitList(countFilter(accList, 8), page, pageSize, totalAR);
                })
                .map(list -> {
                    //序号
                    list.forEach(v ->{
                        v.setCdfine30(index.getAndSet(index.get() + 1).toString());
                        //计算剩余核销金额
                        if(StrUtil.isNotEmpty(v.getMd()) && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMd()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMd());
                            }
                        }else if(StrUtil.isNotEmpty(v.getMc()) && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMc()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMc());
                            }
                        }
                    });
                    return list;
                })
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    /**
     * 客户往来核销
     * @param map
     * @return
     */
    @PostMapping("/findAllAccvoucherHexiao")
    public Mono<R> findAllAccvoucherHexiao(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String km = map.get("km").toString();
        String jpzDate = map.containsKey("jpzDate") ? map.get("jpzDate").toString(): "";
        String dpzDate = map.containsKey("dpzDate") ? map.get("dpzDate").toString(): "";
        String type =  map.get("type").toString();

        AtomicReference<Integer> totalAR = new AtomicReference();
        AtomicReference<Integer> index = new AtomicReference(1);
        return accvoucherRepository.findAllByCode(km).collectList()//1-16 21
                .map(list -> {
                    //分开 借贷凭证 然后先过滤 分别分页过滤
                    List<WriteOffRo> accList =  new ArrayList<>();
                    if("j".equals(type)){
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMc()) && new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }

                            if(StrUtil.isNotEmpty(jpzDate)){
                                String str = jpzDate.split("=")[0];
                                String end = jpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }else{
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMd()) && new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }
                            if(StrUtil.isNotEmpty(dpzDate)){
                                String str = dpzDate.split("=")[0];
                                String end = dpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }
                    return splitList(countFilter(accList, 8), page, pageSize, totalAR);
                })
                .map(list -> {
                    //序号
                    list.forEach(v ->{
                         v.setCdfine30(index.getAndSet(index.get() + 1).toString());
                        //计算剩余核销金额
                        if(StrUtil.isNotEmpty(v.getMd()) && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMd()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMd());
                            }
                        }else if(StrUtil.isNotEmpty(v.getMc()) && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMc()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMc());
                            }
                        }
                    });
                    return list;
                })
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    /**
     * 供应商往来核销
     * @param map
     * @return
     */
    @PostMapping("/findAllAccvoucherHexiaoGys")
    public Mono<R> findAllAccvoucherHexiaoGys(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String km = map.get("km").toString();
        String jpzDate = map.containsKey("jpzDate") ? map.get("jpzDate").toString(): "";
        String dpzDate = map.containsKey("dpzDate") ? map.get("dpzDate").toString(): "";
        String type =  map.get("type").toString();

        AtomicReference<Integer> totalAR = new AtomicReference();
        AtomicReference<Integer> index = new AtomicReference(1);
        return accvoucherRepository.findAllByCodeGys(km).collectList()//1-16 21
                .map(list -> {
                    //分开 借贷凭证 然后先过滤 分别分页过滤
                    List<WriteOffRo> accList =  new ArrayList<>();
                    if("j".equals(type)){
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMc()) && new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }

                            if(StrUtil.isNotEmpty(jpzDate)){
                                String str = jpzDate.split("=")[0];
                                String end = jpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }else{
                        accList = list.stream().filter(item -> {
                            if(StrUtil.isNotEmpty(item.getMd()) && new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                                return false;
                            }
                            if(StrUtil.isNotEmpty(dpzDate)){
                                String str = dpzDate.split("=")[0];
                                String end = dpzDate.split("=")[1];
                                if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                    return false;
                                }
                            }
                            return true;
                        }).collect(Collectors.toList());
                    }
                    return splitList(countFilter(accList, 8), page, pageSize, totalAR);
                })
                .map(list -> {
                    //序号
                    list.forEach(v ->{
                        v.setCdfine30(index.getAndSet(index.get() + 1).toString());
                        //计算剩余核销金额
                        if(StrUtil.isNotEmpty(v.getMd()) && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMd()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMd());
                            }
                        }else if(StrUtil.isNotEmpty(v.getMc()) && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMc()).subtract(v.getMoney()).toString());
                            }else{
                                v.setRemainMoney(v.getMc());
                            }
                        }
                    });
                    return list;
                })
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    /**
     * 核销历史
     * @param map
     * @return
     */
    @PostMapping("/findAllHexiaoHistory")
    public Mono<R> findAllHexiaoHistory(@RequestBody Map map) {
        //条件查询
        if (map.keySet().size() == 2) {
            return Mono.just(R.ok().setResult(CollectOfUtils.mapof("total", 0, "items", new ArrayList<>())));
        }
        String queryMark = map.get("queryMark").toString();
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String km = map.get("km").toString();
        String jpzDate = map.containsKey("jpzDate") ? map.get("jpzDate").toString(): "";
        String dpzDate = map.containsKey("dpzDate") ? map.get("dpzDate").toString(): "";

        AtomicReference<Integer> totalAR = new AtomicReference();
        AtomicReference<Integer> index = new AtomicReference(1);
        return accvoucherRepository.findHistoryByCode(km).collectList()//1-16 21
                .map(list -> {
                    List<WriteOffRo> accList =  new ArrayList<>();
                    accList = list.stream().filter(item -> {
                        if(StrUtil.isNotEmpty(jpzDate)){
                            String str = jpzDate.split("=")[0];
                            String end = jpzDate.split("=")[1];
                            if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                return false;
                            }
                        }
                        if(StrUtil.isNotEmpty(dpzDate)){
                            String str = dpzDate.split("=")[0];
                            String end = dpzDate.split("=")[1];
                            if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList());

                    return splitList(countFilter(accList, 8), page, pageSize, totalAR);
                })
                .map(list -> {
                    //序号
                    list.forEach(v ->{
                        v.setCdfine30(index.getAndSet(index.get() + 1).toString());
                        if(StrUtil.isNotEmpty(v.getMc()) && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                            //借
                            v.setJmoney(v.getMoney());
                        }else if(StrUtil.isNotEmpty(v.getMd()) && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                            //贷
                            v.setDmoney(v.getMoney());
                        }
                    });
                    return list;
                })
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }


    //往来核销自动分配
    @PostMapping("/autoWriteOff")
    public Mono<R> autoWriteOff(@RequestBody HashMap map) {

        String km = map.get("km").toString();
        String jpzDate = map.containsKey("jpzDate") ? map.get("jpzDate").toString(): "";
        String dpzDate = map.containsKey("dpzDate") ? map.get("dpzDate").toString(): "";
        String createCode =  map.get("createCode").toString();
        String tenantId =  map.get("tenantId").toString();

        return accvoucherRepository.findAllByCode(km).collectList()
                        .flatMap(list->{
                            map.put("maxCode", "1");
                            return writeOffFlowRepository.findMaxHxCode().map(maxCode->{
                                //核销码+1
                                map.put("maxCode", Integer.parseInt(maxCode) + 1+"");
                                return list;
                            }).defaultIfEmpty(list);
                        })
                        .map(list -> {
                            final String[] maxCode = {map.get("maxCode").toString()};
                             // 同一科目下 分配同一单位  注意借贷方负数问题
                             List<WriteOff> wlist =  new ArrayList<>();
                             // 过滤日期对单位分组
                             Map<String, List<WriteOffRo>> m = list.stream().filter(item -> {
                                 if(StrUtil.isNotEmpty(jpzDate)){
                                     String str = jpzDate.split("=")[0];
                                     String end = jpzDate.split("=")[1];
                                     if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                         return false;
                                     }
                                 }
                                 if(StrUtil.isNotEmpty(dpzDate)){
                                     String str = dpzDate.split("=")[0];
                                     String end = dpzDate.split("=")[1];
                                     if(item.getDbillDate().compareTo(str) < 0 && item.getDbillDate().compareTo(end) > 0){
                                         return false;
                                     }
                                 }
                                 return true;
                             }).collect(Collectors.groupingBy(WriteOffRo::getCcusId));

                             //遍历单位
                             m.forEach((k,v)->{
                                 //如果 v 长度小于等于1 不分配  大于1 分配
                                 if(v.size() > 1){
                                     //分 借 正负   贷 正负   1借正贷正 2借负贷负 3借 正负 贷正负
                                     List<Accvoucher> jList  = v.stream().filter(item -> {
                                         if(StrUtil.isNotEmpty(item.getMc()) && new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> zjlist = jList.stream().filter(item -> {
                                         if (new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) <= 0) {
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> fjlist = jList.stream().filter(item -> {
                                         if (new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) > -1) {
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());

                                     List<Accvoucher> dList  = v.stream().filter(item -> {
                                         if(StrUtil.isNotEmpty(item.getMd()) && new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> zdlist = dList.stream().filter(item -> {
                                         if (new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) <= 0) {
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> fdlist = dList.stream().filter(item -> {
                                         if (new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) > -1) {
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());

                                     //1 借 正 贷 正      相同
                                     if(zjlist.size() > 0 && zdlist.size() > 0){
                                         zjlist.forEach(item->{
                                             zdlist.forEach(a->{
                                                 if(item.getMd().equals(a.getMc())){
                                                     WriteOff w = new WriteOff();
                                                     w.setHxMoney(item.getMd());
                                                     w.setTenantId(tenantId);
                                                     w.setHxDate(LocalDate.now().toString());
                                                     w.setHxCode(maxCode[0]);
                                                     w.setCreateCode(createCode);
                                                     w.setVouchUnCode(item.getVouchUnCode());
                                                     w.setUniqueCode(item.getUniqueCode());
                                                     w.setIsEnd("1");
                                                     wlist.add(w);

                                                     WriteOff w2 = new WriteOff();
                                                     w2.setHxMoney(a.getMd());
                                                     w2.setTenantId(tenantId);
                                                     w2.setHxDate(LocalDate.now().toString());
                                                     w2.setHxCode(maxCode[0]);
                                                     w2.setCreateCode(createCode);
                                                     w2.setVouchUnCode(a.getVouchUnCode());
                                                     w2.setUniqueCode(a.getUniqueCode());
                                                     w2.setIsEnd("1");
                                                     wlist.add(w2);
                                                     maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                                     return;
                                                 }
                                             });
                                         });
                                     }
                                     //2 借 负 贷 负      相同
                                     if(fjlist.size() > 0 && fdlist.size() > 0){
                                         fjlist.forEach(item->{
                                             fdlist.forEach(a->{
                                                 if(item.getMd().equals(a.getMc())){
                                                     WriteOff w = new WriteOff();
                                                     w.setHxMoney(item.getMd());
                                                     w.setTenantId(tenantId);
                                                     w.setHxDate(LocalDate.now().toString());
                                                     w.setHxCode(maxCode[0]);
                                                     w.setCreateCode(createCode);
                                                     w.setVouchUnCode(item.getVouchUnCode());
                                                     w.setUniqueCode(item.getUniqueCode());
                                                     w.setIsEnd("1");
                                                     wlist.add(w);

                                                     WriteOff w2 = new WriteOff();
                                                     w2.setTenantId(tenantId);
                                                     w2.setHxMoney(a.getMd());
                                                     w2.setHxDate(LocalDate.now().toString());
                                                     w2.setHxCode(maxCode[0]);
                                                     w2.setCreateCode(createCode);
                                                     w2.setVouchUnCode(a.getVouchUnCode());
                                                     w2.setUniqueCode(a.getUniqueCode());
                                                     w.setIsEnd("1");
                                                     wlist.add(w2);
                                                     maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                                     return;
                                                 }
                                             });
                                         });
                                     }
                                     //3 借 负 贷 负      不相同
                                     List<Accvoucher> fj  = fjlist.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> fd  = fdlist.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     if(fj.size() >0 && fd.size() >0){
                                         BigDecimal j =  fj.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         BigDecimal d =  fd.stream().map(x -> {
                                             return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         if(j.compareTo(d) == 0){
                                             fj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             fd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                         }else if(j.abs().compareTo(d.abs()) == 1){
                                             fd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {j.abs()};
                                             fj.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }else{
                                             fj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {d.abs()};
                                             fd.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].abs().toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }
                                         maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                     }
                                     //4 将剩余负数和本方向正数核销
                                     List<Accvoucher> ffj  = fj.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> ffd  = fd.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     if(ffj.size() > ffd.size()){
                                         // 本方向正负数核销
                                         List<Accvoucher> zj  = zjlist.stream().filter(item -> {
                                             if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                                 return false;
                                             }
                                             return true;
                                         }).collect(Collectors.toList());
                                         BigDecimal zzj =  zj.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         BigDecimal f =  ffj.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         if(zzj.compareTo(f.abs()) == 0){
                                             zj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             ffj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                         }else if(zzj.compareTo(f.abs()) == 1){
                                             ffj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {f.abs()};
                                             zj.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }else{
                                             zj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {zzj.abs()};
                                             ffj.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(maxCode[0]);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }
                                         maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";

                                     }else if(ffj.size() < ffd.size()){
                                         // 本方向正负数核销
                                         List<Accvoucher> zd  = zdlist.stream().filter(item -> {
                                             if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                                 return false;
                                             }
                                             return true;
                                         }).collect(Collectors.toList());
                                         BigDecimal zzd =  zd.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         BigDecimal d =  ffd.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         if(zzd.compareTo(d.abs()) == 0){
                                             String s = maxCode[0];
                                             zd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             ffd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                         }else if(zzd.compareTo(d.abs()) == 1){
                                             String s = maxCode[0];
                                             ffj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {d.abs()};
                                             zd.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }else{
                                             String s = maxCode[0];
                                             zd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {zzd.abs()};
                                             ffd.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney("-"+dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney("-"+md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }
                                         maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";

                                     }
                                     //5 正正最后核销剩余不相等正数
                                     List<Accvoucher> zj  = zjlist.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     List<Accvoucher> zd  = zdlist.stream().filter(item -> {
                                         if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                             return false;
                                         }
                                         return true;
                                     }).collect(Collectors.toList());
                                     if(zj.size() > 0 && zd.size() > 0){
                                         BigDecimal j =  zj.stream().map(x -> {
                                             return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         BigDecimal d =  zd.stream().map(x -> {
                                             return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                                         }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                                         if(j.compareTo(d) == 0){
                                             String s = maxCode[0];

                                             zd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             zj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                         }else if(j.compareTo(d) == 1){
                                             String s = maxCode[0];
                                             zd.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {d};
                                             zj.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney(dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney(md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }else{
                                             String s = maxCode[0];
                                             zj.stream().forEach(obj->{
                                                 WriteOff w = new WriteOff();
                                                 w.setTenantId(tenantId);
                                                 w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 w.setIsEnd("1");
                                                 wlist.add(w);
                                             });
                                             final BigDecimal[] dmoney = {j};
                                             zd.stream().forEach(obj->{
                                                 if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                                     return;
                                                 }
                                                 WriteOff w = new WriteOff();
                                                 w.setHxDate(LocalDate.now().toString());
                                                 w.setHxCode(s);
                                                 w.setCreateCode(createCode);
                                                 w.setTenantId(tenantId);
                                                 w.setVouchUnCode(obj.getVouchUnCode());
                                                 w.setUniqueCode(obj.getUniqueCode());
                                                 BigDecimal md = new BigDecimal(obj.getMc());
                                                 if(md.compareTo(dmoney[0]) > -1){
                                                     w.setHxMoney(dmoney[0].toString());
                                                     dmoney[0] = md.subtract(dmoney[0]);
                                                 }else{
                                                     w.setIsEnd("1");
                                                     w.setHxMoney(md.toString());
                                                     dmoney[0] = dmoney[0].subtract(md);
                                                 }
                                                 wlist.add(w);
                                             });
                                         }
                                         maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                     }
                                 }
                             });
                             return wlist;
                         })
                        .flatMap(list->{
                            return writeOffFlowRepository.saveAll(list).collectList();
                        })
                        .flatMap(list->{
                            List<String> vlist = list.stream().filter(v-> "1".equals(v.getIsEnd())).map(v -> v.getVouchUnCode()).collect(Collectors.toList());
                            return accvoucherRepository.updateHxStatueByVouchUnCode(vlist, "1");
                        })
                        .then(Mono.just(R.ok()));

    }

    //手动分配
    @PostMapping("/save")
    public Mono<R> save(@RequestBody HashMap map) {
        String createCode =  map.get("createCode").toString();
        String tenantId =  map.get("tenantId").toString();
        List<WriteOff> list = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(map.get("list").toString());
        jsonArray.stream().forEach(v->{
            list.add(JSON.parseObject(v.toString(), WriteOff.class));
        });
        List<String> vlist = new ArrayList<>();
        List<String> vlist2 = new ArrayList<>();
        return writeOffFlowRepository.findMaxHxCode()
                .defaultIfEmpty("1")
                .flatMap(maxCode -> {
                    list.forEach(v->{
                        v.setHxDate(LocalDate.now().toString());
                        v.setHxCode(maxCode);
                        v.setTenantId(tenantId);
                        v.setCreateCode(createCode);
                        if(v.getRemainMoney().equals(v.getHxMoney())){
                            vlist.add(v.getVouchUnCode());
                        }else{
                            vlist2.add(v.getVouchUnCode());
                        }
                    });
                    return writeOffFlowRepository.saveAll(list).collectList();
                })
                .flatMap(obj->{
                    return accvoucherRepository.updateHxStatueByVouchUnCode(vlist,"1").zipWith(accvoucherRepository.updateHxStatueByVouchUnCode(vlist2,"2"));
                })
                .then(Mono.just(R.ok()));
    }

    //手动默认分配
    @PostMapping("/manualWriteOff")
    public Mono<R> manualWriteOff(@RequestBody HashMap map) throws Exception {
        String createCode =  map.get("createCode").toString();
        String tenantId =  map.get("tenantId").toString();
        List<Accvoucher> jList = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(map.get("jlist").toString());
        jsonArray.stream().forEach(v->{
            jList.add(JSON.parseObject(v.toString(), Accvoucher.class));
        });
        List<Accvoucher> dList = new ArrayList<>();
        JSONArray jsonArray2 = JSONArray.parseArray(map.get("dlist").toString());
        jsonArray2.stream().forEach(v->{
            dList.add(JSON.parseObject(v.toString(), Accvoucher.class));
        });
        //List<Accvoucher> jList =  (List<Accvoucher>) map.get("jlist");
        //List<Accvoucher> dList =  (List<Accvoucher>) map.get("dlist");
        List<WriteOff> wlist =  new ArrayList<>();
        HashMap maps =  new HashMap();
        AtomicReference<Integer> index = new AtomicReference(1);
        AtomicReference<Integer> index2 = new AtomicReference(1);
        //生成一个核销码code
        return writeOffFlowRepository.findMaxHxCode()
                .defaultIfEmpty("1")
                .map(code -> {
                    final String[] maxCode = {code};
                    //分 借 正负   贷 正负   1借正贷正 2借负贷负 3借 正负 贷正负
                    List<Accvoucher> zjlist = jList.stream().filter(item -> {
                        if (new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) <= 0) {
                            return false;
                        }
                        return true;
                     }).collect(Collectors.toList());
                    List<Accvoucher> fjlist = jList.stream().filter(item -> {
                        if (new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) > -1) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());


                     List<Accvoucher> zdlist = dList.stream().filter(item -> {
                        if (new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) <= 0) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Accvoucher> fdlist = dList.stream().filter(item -> {
                        if (new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) > -1) {
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());

                    //1 借 正 贷 正      相同
                    if(zjlist.size() > 0 && zdlist.size() > 0){
                        zjlist.forEach(item->{
                            zdlist.forEach(a->{
                                if(item.getMd().equals(a.getMc())){
                                    WriteOff w = new WriteOff();
                                    w.setHxMoney(item.getMd());
                                    w.setTenantId(tenantId);
                                    w.setHxDate(LocalDate.now().toString());
                                    w.setHxCode(maxCode[0]);
                                    w.setCreateCode(createCode);
                                    w.setVouchUnCode(item.getVouchUnCode());
                                    w.setUniqueCode(item.getUniqueCode());
                                    w.setIsEnd("1");
                                    w.setDbillDate(item.getDbillDate());
                                    w.setInoId(item.getInoId());
                                    w.setCdigest(item.getCdigest());
                                    w.setMd(item.getMd());
                                    w.setMc(item.getMc());
                                    wlist.add(w);

                                    WriteOff w2 = new WriteOff();
                                    w2.setHxMoney(a.getMd());
                                    w2.setTenantId(tenantId);
                                    w2.setHxDate(LocalDate.now().toString());
                                    w2.setHxCode(maxCode[0]);
                                    w2.setCreateCode(createCode);
                                    w2.setVouchUnCode(a.getVouchUnCode());
                                    w2.setUniqueCode(a.getUniqueCode());
                                    w2.setIsEnd("1");
                                    w2.setDbillDate(item.getDbillDate());
                                    w2.setInoId(item.getInoId());
                                    w2.setCdigest(item.getCdigest());
                                    w2.setMd(item.getMd());
                                    w2.setMc(item.getMc());
                                    wlist.add(w2);
                                    maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                    return;
                                }
                            });
                        });
                    }
                    //2 借 负 贷 负      相同
                    if(fjlist.size() > 0 && fdlist.size() > 0){
                        fjlist.forEach(item->{
                            fdlist.forEach(a->{
                                if(item.getMd().equals(a.getMc())){
                                    WriteOff w = new WriteOff();
                                    w.setHxMoney(item.getMd());
                                    w.setTenantId(tenantId);
                                    w.setHxDate(LocalDate.now().toString());
                                    w.setHxCode(maxCode[0]);
                                    w.setCreateCode(createCode);
                                    w.setVouchUnCode(item.getVouchUnCode());
                                    w.setUniqueCode(item.getUniqueCode());
                                    w.setIsEnd("1");
                                    w.setDbillDate(item.getDbillDate());
                                    w.setInoId(item.getInoId());
                                    w.setCdigest(item.getCdigest());
                                    w.setMd(item.getMd());
                                    w.setMc(item.getMc());
                                    wlist.add(w);

                                    WriteOff w2 = new WriteOff();
                                    w2.setTenantId(tenantId);
                                    w2.setHxMoney(a.getMd());
                                    w2.setHxDate(LocalDate.now().toString());
                                    w2.setHxCode(maxCode[0]);
                                    w2.setCreateCode(createCode);
                                    w2.setVouchUnCode(a.getVouchUnCode());
                                    w2.setUniqueCode(a.getUniqueCode());
                                    w2.setIsEnd("1");
                                    w2.setDbillDate(item.getDbillDate());
                                    w2.setInoId(item.getInoId());
                                    w2.setCdigest(item.getCdigest());
                                    w2.setMd(item.getMd());
                                    w2.setMc(item.getMc());
                                    wlist.add(w2);
                                    maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                                    return;
                                }
                            });
                        });
                    }
                    //3 借 负 贷 负      不相同
                    List<Accvoucher> fj  = fjlist.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Accvoucher> fd  = fdlist.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    if(fj.size() >0 && fd.size() >0){
                        BigDecimal j =  fj.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal d =  fd.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        if(j.compareTo(d) == 0){
                            fj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            fd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                        }else if(j.abs().compareTo(d.abs()) == 1){
                            fd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {j.abs()};
                            fj.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }else{
                            fj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {d.abs()};
                            fd.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].abs().toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }
                        maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                    }
                    //4 将剩余负数和本方向正数核销
                    List<Accvoucher> ffj  = fj.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Accvoucher> ffd  = fd.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    if(ffj.size() > ffd.size()){
                        // 本方向正负数核销
                        List<Accvoucher> zj  = zjlist.stream().filter(item -> {
                            if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                return false;
                            }
                            return true;
                        }).collect(Collectors.toList());
                        BigDecimal zzj =  zj.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal f =  ffj.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        if(zzj.compareTo(f.abs()) == 0){
                            zj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            ffj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                wlist.add(w);
                            });
                        }else if(zzj.compareTo(f.abs()) == 1){
                            ffj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {f.abs()};
                            zj.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }else{
                            zj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {zzj.abs()};
                            ffj.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(maxCode[0]);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }
                        maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";

                    }else if(ffj.size() < ffd.size()){
                        // 本方向正负数核销
                        List<Accvoucher> zd  = zdlist.stream().filter(item -> {
                            if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                                return false;
                            }
                            return true;
                        }).collect(Collectors.toList());
                        BigDecimal zzd =  zd.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal d =  ffd.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        if(zzd.compareTo(d.abs()) == 0){
                            String s = maxCode[0];
                            zd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            ffd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                        }else if(zzd.compareTo(d.abs()) == 1){
                            String s = maxCode[0];
                            ffj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {d.abs()};
                            zd.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }else{
                            String s = maxCode[0];
                            zd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {zzd.abs()};
                            ffd.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMc()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney("-"+dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney("-"+md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }
                        maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";

                    }
                    //5 正正最后核销剩余不相等正数
                    List<Accvoucher> zj  = zjlist.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    List<Accvoucher> zd  = zdlist.stream().filter(item -> {
                        if(wlist.stream().map(WriteOff::getVouchUnCode).collect(Collectors.toList()).contains(item.getVouchUnCode())){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());
                    if(zj.size() > 0 && zd.size() > 0){
                        BigDecimal j =  zj.stream().map(x -> {
                            return Objects.isNull(x.getMd()) || x.getMd().length() <= 0 ? "0.00" : x.getMd();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        BigDecimal d =  zd.stream().map(x -> {
                            return Objects.isNull(x.getMc()) || x.getMc().length() <= 0 ? "0.00" : x.getMc();
                        }).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);

                        if(j.compareTo(d) == 0){
                            String s = maxCode[0];

                            zd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            zj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                        }else if(j.compareTo(d) == 1){
                            String s = maxCode[0];
                            zd.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {d};
                            zj.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMd()).abs();
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney(dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney(md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }else{
                            String s = maxCode[0];
                            zj.stream().forEach(obj->{
                                WriteOff w = new WriteOff();
                                w.setTenantId(tenantId);
                                w.setHxMoney(obj.getMc().equals("0.00")? obj.getMd(): obj.getMc());
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setIsEnd("1");
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                wlist.add(w);
                            });
                            final BigDecimal[] dmoney = {j};
                            zd.stream().forEach(obj->{
                                if (dmoney[0].compareTo(new BigDecimal("0.00")) == 0) {
                                    return;
                                }
                                WriteOff w = new WriteOff();
                                w.setHxDate(LocalDate.now().toString());
                                w.setHxCode(s);
                                w.setCreateCode(createCode);
                                w.setTenantId(tenantId);
                                w.setVouchUnCode(obj.getVouchUnCode());
                                w.setUniqueCode(obj.getUniqueCode());
                                w.setDbillDate(obj.getDbillDate());
                                w.setInoId(obj.getInoId());
                                w.setCdigest(obj.getCdigest());
                                w.setMd(obj.getMd());
                                w.setMc(obj.getMc());
                                BigDecimal md = new BigDecimal(obj.getMc());
                                if(md.compareTo(dmoney[0]) > -1){
                                    w.setHxMoney(dmoney[0].toString());
                                    dmoney[0] = md.subtract(dmoney[0]);
                                }else{
                                    w.setIsEnd("1");
                                    w.setHxMoney(md.toString());
                                    dmoney[0] = dmoney[0].subtract(md);
                                }
                                wlist.add(w);
                            });
                        }
                        maxCode[0] = Integer.parseInt(maxCode[0]) + 1+"";
                    }
                    return wlist;
                })
                .map(list -> {

                    List<WriteOff> j  = list.stream().filter(item -> {
                        if(StrUtil.isNotEmpty(item.getMc()) && new BigDecimal(item.getMc()).compareTo(new BigDecimal("0.00")) == 1){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());

                    List<WriteOff> d  = list.stream().filter(item -> {
                        if(StrUtil.isNotEmpty(item.getMd()) && new BigDecimal(item.getMd()).compareTo(new BigDecimal("0.00")) == 1){
                            return false;
                        }
                        return true;
                    }).collect(Collectors.toList());

                    //序号
                    j.forEach(v ->{
                        v.setCdfine30(index.getAndSet(index.get() + 1).toString());
                        //计算剩余核销金额
                        if(StrUtil.isNotEmpty(v.getMd())  && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getRemainMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMd()).subtract(new BigDecimal(v.getRemainMoney())).toString());
                            }else{
                                v.setRemainMoney(v.getMd());
                            }
                        }else if(StrUtil.isNotEmpty(v.getMc())  && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getRemainMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMc()).subtract(new BigDecimal(v.getRemainMoney())).toString());
                            }else{
                                v.setRemainMoney(v.getMc());
                            }
                        }
                    });

                    d.forEach(v ->{
                        v.setCdfine30(index2.getAndSet(index2.get() + 1).toString());
                        //计算剩余核销金额
                        if(StrUtil.isNotEmpty(v.getMd())  && new BigDecimal(v.getMd()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getRemainMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMd()).subtract(new BigDecimal(v.getRemainMoney())).toString());
                            }else{
                                v.setRemainMoney(v.getMd());
                            }
                        }else if(StrUtil.isNotEmpty(v.getMc())  && new BigDecimal(v.getMc()).compareTo(new BigDecimal("0.00")) != 0){
                            if(Objects.nonNull(v.getRemainMoney())){
                                v.setRemainMoney(new BigDecimal(v.getMc()).subtract(new BigDecimal(v.getRemainMoney())).toString());
                            }else{
                                v.setRemainMoney(v.getMc());
                            }
                        }
                    });
                    maps.put("jlist",j);
                    maps.put("dlist",d);
                    maps.put("list",list);
                    return maps;
                })
                .map(o-> R.ok().setResult(o));
    }

    //反核销
    @PostMapping("/backHx")
    public Mono<R> backHx(@RequestBody HashMap map) {
        List<String> list = (List<String>) map.get("list");
        Mono<Void> mono1 = writeOffFlowRepository.deleteByVouchUnCodes(list);
        Mono<Void> mono2 = accvoucherRepository.updateHxStatueByVouchUnCode(list, "2");
        return mono1.zipWith(mono2).then(Mono.just(R.ok()));
    }

    //根据核销管理凭证唯一码查询是否存在核销记录
    @PostMapping("countHxRecord")
    public Mono<R> countHxRecord(@RequestBody HashMap map) {
        List<String> list = JSON.parseArray(map.get("list").toString(),String.class);
        return writeOffFlowRepository.countAllByVouchUnCodeIn(list).map(o->R.ok(o));
    }

    public static List<WriteOffRo> splitList(List<WriteOffRo> list, int pageNo, int pageSize, AtomicReference<Integer> titlesAR) {
        if (CollectionUtils.isEmpty(list)) {
            titlesAR.set(0);
            return new ArrayList<>();
        }
        // 后端排序
        //List<Accvoucher> sortList = ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getInoId()).compareTo(Integer.valueOf(o2.getInoId())));
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

    private List<WriteOffRo> queryFilter(List<WriteOffRo> list, Map map) {
        String queryMark = map.get("queryMark").toString();
        String queryType = map.get("biZhong").toString();
        Map<String, Object> variableMap = ((HashMap<String, HashMap<String, Object>>) map.get("condition")).get("variable");
        HashMap<String, Object> authorityMap = ((HashMap<String, HashMap<String, Object>>) map.get("condition")).get("authority");
        Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
        Map<String, String> filterMap = ((HashMap<String, String>) map.get("filterConditon"));
        if (list.size() > 0) {
            String startV = variableMap.containsKey("voucherNumberStart") ? variableMap.get("voucherNumberStart").toString() : "";
            String endV = variableMap.containsKey("voucherNumberEnd") ? variableMap.get("voucherNumberEnd").toString() : "";
            String type = variableMap.containsKey("voucherType") ? variableMap.get("voucherType").toString() : "";
            String summary = variableMap.containsKey("summary") ? variableMap.get("summary").toString() : "";
            String subjectNumber = variableMap.containsKey("subjectNumber") ? variableMap.get("subjectNumber").toString() : "";
            String amountMin = variableMap.containsKey("amountMin") ? variableMap.get("amountMin").toString() : "";
            String amountMax = variableMap.containsKey("amountMax") ? variableMap.get("amountMax").toString() : "";
            String direction = variableMap.containsKey("direction") ? variableMap.get("direction").toString() : "";

            String preparedMan = variableMap.containsKey("preparedMan") ? variableMap.get("preparedMan").toString() : "";
            String checkMan = variableMap.containsKey("checkMan") ? variableMap.get("checkMan").toString() : "";
            String cashierMan = variableMap.containsKey("cashierMan") ? variableMap.get("cashierMan").toString() : "";
            String bookMan = variableMap.containsKey("bookMan") ? variableMap.get("bookMan").toString() : "";
            String reviewMan = variableMap.containsKey("reviewMan") ? variableMap.get("reviewMan").toString() : "";

            String reviewStatus = variableMap.containsKey("reviewStatus") ? variableMap.get("reviewStatus").toString() : "";
            String bookStatus = variableMap.containsKey("bookStatus") ? variableMap.get("bookStatus").toString() : "";
            String ifrag = variableMap.containsKey("ifrag") ? variableMap.get("ifrag").toString() : "";
            Set<String> codes = new HashSet<>((List<String>) authorityMap.get("codes"));
            Set<String> types = new HashSet<>((List<String>) authorityMap.get("types"));

            // 辅助核算 内容
            Map<String, List<String>> assistsMap = variableMap.containsKey("assists") ? (Map<String, List<String>>) variableMap.get("assists") : null;
            // 外币 外币金额
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(startV) && StrUtil.isNotBlank(endV)) {
                    if ((Integer.valueOf(item.getInoId())).compareTo(Integer.valueOf(startV)) < 0 || (Integer.valueOf(item.getInoId()).compareTo((Integer.valueOf(endV))) > 0))
                        return false;
                }
                if (StrUtil.isNotBlank(type) && !type.equals(item.getCsign())) return false;
                if (StrUtil.isNotBlank(summary) && !item.getCdigest().contains(summary)) return false;
                if (StrUtil.isNotBlank(bookStatus) && (StrUtil.isNotBlank(item.getIbook()) ? !bookStatus.equals(item.getIbook()) : (bookStatus.equals("1") || bookStatus.equals("2"))))
                    return false;
                if (StrUtil.isNotBlank(reviewStatus) && (StrUtil.isNotBlank(item.getCcheck()) ? !reviewStatus.equals(item.getCcheck()) : (reviewStatus.equals("1") || reviewStatus.equals("2"))))
                    return false;
                if (StrUtil.isNotBlank(ifrag) && !ifrag.equals(item.getIfrag())) return false;
                if (StrUtil.isNotBlank(subjectNumber) && !subjectNumber.equals(item.getCcode())) return false;

                if (StrUtil.isNotBlank(preparedMan) && !preparedMan.equals(item.getCbill())) return false;
                if (StrUtil.isNotBlank(checkMan) && !checkMan.equals(item.getCcheck())) return false;
                if (StrUtil.isNotBlank(cashierMan) && !cashierMan.equals(item.getCcashier())) return false;
                if (StrUtil.isNotBlank(bookMan) && !bookMan.equals(item.getCbook())) return false;
                if (StrUtil.isNotBlank(reviewMan) && !reviewMan.equals(item.getCdirector())) return false;
                if (StrUtil.isNotBlank(amountMin) && StrUtil.isNotBlank(amountMax)) {
                    BigDecimal md = StrUtil.isBlank(item.getMd()) ? new BigDecimal(0) : new BigDecimal(item.getMd());
                    BigDecimal mc = StrUtil.isBlank(item.getMc()) ? new BigDecimal(0) : new BigDecimal(item.getMc());
                    if (StrUtil.isNotBlank(direction) && direction.equals("jf")) {
                        if (md.compareTo(new BigDecimal(amountMin)) < 0 || md.compareTo(new BigDecimal(amountMax)) > 0)
                            return false;
                    } else if (StrUtil.isNotBlank(direction) && direction.equals("df")) {
                        if (mc.compareTo(new BigDecimal(amountMin)) < 0 || mc.compareTo(new BigDecimal(amountMax)) > 0)
                            return false;
                    } else if (StrUtil.isBlank(direction) && (md != new BigDecimal(0) || mc != new BigDecimal(0))) {
                        if (md != new BigDecimal(0)) {
                            if (md.compareTo(new BigDecimal(amountMin)) < 0 || md.compareTo(new BigDecimal(amountMax)) > 0)
                                return false;
                        } else if (mc != new BigDecimal(0)) {
                            if (mc.compareTo(new BigDecimal(amountMin)) < 0 || mc.compareTo(new BigDecimal(amountMax)) > 0)
                                return false;
                        }
                    }
                }
                if (queryMark.equals("1") && codes.size() > 0 && !(new HashSet<>(codes).contains(item.getCcode())))
                    return false;
                if (queryMark.equals("1") && types.size() > 0 && !(new HashSet<>(types).contains(item.getCsign())))
                    return false;

                // 辅助核算内容过滤
                if (null != assistsMap && assistsMap.keySet().size() > 0) {
                    for (String key : assistsMap.keySet()) {
                        Set<String> vals = new HashSet<>(assistsMap.get(key));
                        if (vals.size() > 0) {
                            String dbValue = getFieldValueByFieldName(key, item);
                            if (StrUtil.isBlank(dbValue) || !vals.contains(dbValue)) return false; // 空值 或 不包含辅助项
                        }
                    }
                }
                // 搜索过滤
                if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                    String value = searchMap.get("value");
                    if (searchMap.get("requirement").trim().equals("inoId")) {
                        if (!item.getCsign().contains(value) && !item.getInoId().contains(value)) return false;
                    } else {
                        String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                        if (null != dbValue && !dbValue.contains(value)) return false;
                    }
                }
                // 查询后过滤
                if (StrUtil.isNotBlank(filterMap.get("amountMin")) && StrUtil.isNotBlank(filterMap.get("amountMax"))) {
                    BigDecimal min = new BigDecimal(filterMap.get("amountMin"));
                    BigDecimal max = new BigDecimal(filterMap.get("amountMax"));
                    BigDecimal md = StrUtil.isBlank(item.getMd()) ? new BigDecimal(0) : new BigDecimal(item.getMd());
                    BigDecimal mc = StrUtil.isBlank(item.getMc()) ? new BigDecimal(0) : new BigDecimal(item.getMc());
                    if (md != new BigDecimal(0)) {
                        if (md.compareTo(min) < 0 || md.compareTo(max) > 0) return false;
                    } else if (mc != new BigDecimal(0)) {
                        if (mc.compareTo(min) < 0 || mc.compareTo(max) > 0) return false;
                    }
                }
                if (StrUtil.isNotBlank(filterMap.get("pzNumberMin")) && StrUtil.isNotBlank(filterMap.get("pzNumberMax"))) {
                    if (item.getInoId().compareTo(filterMap.get("pzNumberMin")) < 0 || item.getInoId().compareTo(filterMap.get("pzNumberMax")) > 0)
                        return false;
                }
                if (StrUtil.isNotBlank(filterMap.get("cashProject")) && StrUtil.isNotBlank(filterMap.get("cashProject"))) {
                    return filterMap.get("cashProject").equals(item.getCashProject());
                }
                // 当为 审核时
                if (StrUtil.isNotBlank(queryType)) { // 过滤
                    // 补充必须是出纳凭证
                    if (queryType.equals("cashier") && (/*StrUtil.isNotBlank(item.getCcheck()) || StrUtil.isNotBlank(item.getCdirector()) ||*/  StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;
                    if (queryType.equals("review") && (/*StrUtil.isNotBlank(item.getCdirector()) ||*/ StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;// 已主管与未记账 签字
                    if (queryType.equals("sign") && (/*StrUtil.isBlank(item.getCcheck()) ||*/ StrUtil.equals(item.getIbook(), "1") || StrUtil.isNotBlank(item.getCbook())))
                        return false;// 已审核与未记账
                    if (queryType.equals("book") && (StrUtil.isBlank(item.getCcheck())))
                        return false;// 已审核
                }
                return true;
            }).collect(Collectors.toList());
        }
        return list;
    }

    private List<WriteOffRo> countFilter(List<WriteOffRo> list, int maxNumber) {
        List<WriteOffRo> filterList = new ArrayList<>();
        int count = 0;
        String thisOPZNumber = "";
        for (WriteOffRo accvoucher : list) {
            if (!accvoucher.getInoId().equals(thisOPZNumber)) {
                thisOPZNumber = accvoucher.getInoId();
                count = 0;
            } else {
                ++count;
            }
            if (count < (maxNumber)) filterList.add(accvoucher);
        }
        for (WriteOffRo accvoucher : filterList) {
            accvoucher.setInoId(Integer.parseInt(accvoucher.getInoId()) + "");
        }
        return filterList;
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

    /**
     * Map转实体类共通方法
     *
     * @param type 实体类class
     * @param map map
     * @return Object
     * @throws Exception
     */
    public Object convertMap(Class type, Map map) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        Object obj = type.newInstance();
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (map.containsKey(propertyName)) {
                Object value = map.get(propertyName);
                descriptor.getWriteMethod().invoke(obj, value);
            }
        }
        return obj;
    }
}
