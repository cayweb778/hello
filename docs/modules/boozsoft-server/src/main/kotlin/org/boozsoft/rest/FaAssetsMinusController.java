package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.fa.*;
import org.boozsoft.domain.entity.group.GroupFaAccPeriod;
import org.boozsoft.repo.fa.*;
import org.boozsoft.repo.group.GroupFaAccPeriodRepository;
import org.boozsoft.util.ReflectionUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faCz")
public class FaAssetsMinusController {

    @Autowired
    private GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    /************************* 资产处置 **************************/
    @Autowired
    FaCardMasterRepository faCardMasterRepository;

    @Autowired
    FaChangeRepository faChangeRepository;

    @Autowired
    FaDepreciationRepository faDepreciationRepository;

    @Autowired
    FaPandianMasterRepository faPandianMasterRepository;

    @Autowired
    FaPandianSubRepository faPandianSubRepository;

    @Autowired
    FaAssetsMinusMasterRepository faAssetsMinusMasterRepository;

    @Autowired
    FaAssetsMinusSubRepository faAssetsMinusSubRepository;

    @PostMapping("findMinusListByCondition")
    public Mono<R> findMinusListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String code = map.get("code").toString();
        String yearMonth = map.get("yearMonth").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return faAssetsMinusMasterRepository.findAllByManageCodeAndCreateDateLikeOrderByCreateDateAscCzIdAsc(code, "%" + yearMonth + "%").collectList().map(list -> splitList(queryFilter(list, condition), page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    @PostMapping("findFaAssetListByCondition")
    public Mono<R> findFaAssetListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String qValue = map.get("qValue").toString();
        Flux<FaCardMaster> faCardMasterFlux = type.equals("one") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndSysIdLikeOrderBySysId(code, /*"1", "1", */date.substring(0, 7) + "-01", "%" + qValue + "%") : type.equals("two") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndCardCodeLikeOrderByCardCode(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%") : faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndFaNameLikeOrderBySysId(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%");
        return faCardMasterFlux.collectList().map(o -> R.ok(dataTransform(o)));
    }

    // 获取指定卡片编码的 参数
    @PostMapping("findFaAssetInfoByCode")
    public Mono<R> findFaAssetInfoByCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        return faCardMasterRepository.findByCardUniqueOrderBySysId(code).flatMap(dbEnity -> faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(dbEnity.getCardUnique()).zipWith(faDepreciationRepository.findFirstByManageCodeAndIyearAndImonth( dbEnity.getCardUnique()).switchIfEmpty(Mono.just(new FaDepreciation()))).flatMap(its -> {
            FaChange it = its.getT1();
            FaDepreciation t2 = its.getT2();
            dbEnity.setBeiyong1(it.getShuliang());
            dbEnity.setBeiyong2(it.getYuanzhi());
            if (null != t2.getZjLj()) {
                dbEnity.setBeiyong3(t2.getZjBy());
                dbEnity.setBeiyong4(t2.getZjLj());
            } else {
                dbEnity.setBeiyong3(it.getYuezhejiue());
                dbEnity.setBeiyong4(it.getLjzhejiu());
            }
            BigDecimal jz = new BigDecimal(StrUtil.isBlank(it.getYuanzhi()) ? "0" : it.getYuanzhi()).subtract(new BigDecimal(StrUtil.isBlank(dbEnity.getBeiyong4()) ? "0" : dbEnity.getBeiyong4()));
            dbEnity.setBeiyong5(jz.toString());
            return Mono.just(dbEnity);
        })).map(o -> R.ok(dataTransform(ListUtil.toList(o))));
    }

    @PostMapping("findFaPanDianByCondition")
    public Mono<R> findFaPanDianByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return faAssetsMinusMasterRepository.findAllByManageCodeOrderByCreateDateAscCzIdAsc(code).collectList().cache().flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                FaAssetsMinusMaster master = null;
                switch (action) {
                    case "curr":
                        master = list.get((list.stream().map(e -> e.getCzId()).collect(Collectors.toList())).indexOf(currPdId));
                        break;
                    case "tail":
                        master = list.get(list.size() - 1);
                        break;
                    case "prev":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getCzId()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index == 0 ? 0 : index - 1;
                            master = list.get(index);
                        }
                        break;
                    case "next":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getCzId()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                            master = list.get(index);
                        }
                        break;
                    default:
                        master = list.get(0);
                        break;
                }
                FaAssetsMinusMaster finalMaster = master;
                return faAssetsMinusSubRepository.findAllBySuperiorIdOrderByAssetsCode(master.getId()).collectList().map(enlist -> {
                    if (enlist.size() > 0) finalMaster.setBeiyong5(JSON.toJSONString(enlist));
                    return R.ok(finalMaster);
                });
            }
        });
    }

    @PostMapping("save")
    @Transactional
    public Mono<R> save(@RequestBody FaAssetsMinusMaster entity) {
        List<FaAssetsMinusSub> entrys = JSON.parseArray(entity.getBeiyong5(), FaAssetsMinusSub.class);
        entity.setBeiyong5(null);
        boolean isAdd = null == entity.getId();
        if (isAdd) entity.setCreateDate(DateUtil.now());
        return faAssetsMinusMasterRepository.save(entity).flatMap(dbEntity -> {
            Set<String> entryCodes = new HashSet<>(entrys.stream().map(it -> it.getAssetsUniqueCode()).collect(Collectors.toList()));
            Mono<FaAssetsMinusMaster> entryDelM = faAssetsMinusSubRepository.findAllBySuperiorIdOrderByAssetsCode(dbEntity.getId()).collectList().flatMap(lis -> faAssetsMinusSubRepository.deleteAll(lis).thenReturn(lis).map(dSens -> dSens.stream().filter(it -> !entryCodes.contains(it.getAssetsUniqueCode())).map(dSen -> dSen.getAssetsUniqueCode()).collect(Collectors.toList())).flatMapMany(Flux::fromIterable).flatMap(code -> faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(code).flatMap(dCarEnt -> {
                dCarEnt.setJianshao("0");
                return faChangeRepository.save(dCarEnt);
            })).collectList().thenReturn(dbEntity)).thenReturn(dbEntity);
            Mono<FaAssetsMinusMaster> entrySaveM = Flux.fromIterable(entrys).map(it -> {
                it.setId(null);
                it.setSuperiorId(dbEntity.getId());
                return it;
            }).collectList().flatMap(list -> faAssetsMinusSubRepository.saveAll(list).map(dSen -> dSen.getAssetsUniqueCode()).flatMap(code -> faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(code).flatMap(dCarEnt -> {
                dCarEnt.setJianshao("1");
                return faChangeRepository.save(dCarEnt);
            })).collectList().thenReturn(dbEntity));
            return isAdd ? entrySaveM : entryDelM.flatMap(e -> entrySaveM);
        }).map(o -> R.ok(o));
    }

    @DeleteMapping("del")
    @Transactional
    public Mono<R> del(@RequestBody FaAssetsMinusMaster entity) {
        Mono<FaAssetsMinusMaster> entryDelM = faAssetsMinusSubRepository.findAllBySuperiorIdOrderByAssetsCode(entity.getId()).collectList().flatMap(lis -> faAssetsMinusSubRepository.deleteAll(lis).thenReturn(lis).map(dSens -> dSens.stream().map(dSen -> dSen.getAssetsUniqueCode()).collect(Collectors.toList())).flatMapMany(Flux::fromIterable).flatMap(code -> faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(code).flatMap(dCarEnt -> {
            dCarEnt.setJianshao("0");
            return faChangeRepository.save(dCarEnt);
        })).collectList().thenReturn(entity)).thenReturn(entity);
        return entryDelM.flatMap(e -> faAssetsMinusMasterRepository.deleteById(entity.getId()).thenReturn(entity)).map(o -> R.ok(o));
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody FaAssetsMinusMaster entity) {
        return faAssetsMinusMasterRepository.findById(entity.getId()).flatMap(dbEntry -> {
            if (StrUtil.isBlank(dbEntry.getVerifyUsername())) {
                dbEntry.setVerifyUsername(entity.getVerifyUsername());
                dbEntry.setVerifyTime(DateUtil.now());
            } else {
                dbEntry.setVerifyUsername(null);
                dbEntry.setVerifyTime(null);
            }
            return faAssetsMinusMasterRepository.save(dbEntry).map(o -> R.ok(o));
        });
    }

    @PostMapping("generate")
    public Mono<R> findFaInvoiceByDate(@RequestBody Map map) {
        String date = map.get("date").toString();
        String code = map.get("code").toString();
        // 查询最新未结账月 是否与当前保存一致
        return groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthAsc(code, date.substring(0, 4), "0").switchIfEmpty(Mono.just(new GroupFaAccPeriod())).flatMap(dbEntry -> {
            if (null == dbEntry.getImonth()) {
                return Mono.just(R.ok(CollectOfUtils.mapof("isError", true)));
            } else {
                int maxM = Integer.parseInt(dbEntry.getImonth()); // 结账最大月
                int tM = Integer.parseInt(date.substring(5, 7)); // 日期当前月
                String dV = "";
                if (maxM != tM) {
                    dV = DateUtil.endOfDay(DateUtil.parse(date.substring(0, 4) + "-" + (maxM > 9 ? "" + maxM : "0" + maxM) + "-" + "01").toJdkDate()).toDateStr();
                } else {
                    dV = date;
                }
                String finalDV = dV;
                return faAssetsMinusMasterRepository.findFirstByHandleDateLikeOrderByCzIdDesc("%" + dV.substring(0, 7) + "%").switchIfEmpty(Mono.just(new FaAssetsMinusMaster())).flatMap(dbEntry2 -> {
                    String cV = "";
                    String yAm = finalDV.substring(0, 7);
                    if (null == dbEntry2.getCzId()) { // 第一次
                        cV = "CZ" + yAm.replaceAll("-", "") + "0001";
                    } else {
                        cV = "CZ" + yAm.replaceAll("-", "") + PdIncr(dbEntry2.getCzId().replace("CZ" + yAm.replaceAll("-", ""), ""));
                    }
                    return Mono.just(R.ok(CollectOfUtils.mapof("code", cV, "date", finalDV, "isError", false)));
                });
            }
        });
    }

    private String PdIncr(String substring) {
        int v = Integer.parseInt(substring) + 1;
        return v > 999 ? v + "" : v > 99 ? "0" + v : v > 9 ? "00" + v : "000" + v;
    }


    /**
     * 卡片查询过滤
     *
     * @param list
     * @param map
     * @return
     */
    private List<FaCardMaster> cardQueryFilter(List<FaCardMaster> list, Map map) {
        if (list.size() > 0 && map.containsKey("choose") && !map.get("choose").toString().equals("0")) {
            String type = map.get("choose").toString();
            List<String> values = (List<String>) map.get("values");
            if (values.size() > 0) {
                list = list.stream().filter(item -> {
                    Map<String, String> changeMap = JSON.parseObject(item.getBeiyong2(), Map.class);
                    if ((type.equals("1") && (null == changeMap.get("faClass") || !values.contains(changeMap.get("faClass")))) || (type.equals("2") && (null == changeMap.get("dept") || !values.contains(changeMap.get("dept")))) || (type.equals("3") && (null == changeMap.get("project") || !values.contains(changeMap.get("project")))) || (type.equals("4") && (null == changeMap.get("zerenUser") || !values.contains(changeMap.get("zerenUser")))) || (type.equals("5") && (null == changeMap.get("addr") || !values.contains(changeMap.get("addr")))) || (type.equals("6") && (null == changeMap.get("useType") || !values.contains(changeMap.get("useType"))))) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
            }
        }
        return list;
    }

    private List<FaAssetsMinusMaster> queryFilter(List<FaAssetsMinusMaster> list, Map map) {
        if (list.size() > 0) {
            Map<String, String> searchConditon = (HashMap<String, String>) map.get("searchConditon");
            List<String> treeConditon = (ArrayList<String>) map.get("treeConditon");
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(searchConditon.get("requirement")) && StrUtil.isNotBlank(searchConditon.get("value"))) {
                    Object dbValue = ReflectionUtil.getValue(item, searchConditon.get("requirement").trim());
                    if (null != dbValue && !dbValue.toString().contains(searchConditon.get("value").trim()))
                        return false;
                }
                if (treeConditon.size() > 0 && !treeConditon.contains(item.getCreateDate().substring(0, 7)))
                    return false;
                return true;
            }).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 卡片数据转化为盘点内容
     *
     * @param list
     * @return
     */
    private List<FaAssetsMinusSub> dataTransform(List<FaCardMaster> list) {
        List<FaAssetsMinusSub> subs = new ArrayList<>();
        for (FaCardMaster car : list) {
            FaAssetsMinusSub sub = new FaAssetsMinusSub();
            sub.setAssetsUniqueCode(car.getCardUnique());
            sub.setSysId(car.getSysId());
            sub.setAssetsCode(car.getCardCode());
            sub.setAssetsName(car.getFaName());
            sub.setSpeciType(car.getSpeciType());
            sub.setBookAmount(car.getBeiyong1());
            sub.setMeasureUnit(car.getUnitId());
            sub.setYuanzhi(car.getBeiyong2());
            sub.setJtBy(car.getBeiyong3());
            sub.setZjLj(car.getBeiyong4());
            sub.setJingzhi(car.getBeiyong5());
            subs.add(sub);
        }
        return subs;
    }

    private static List splitList(List list, int pageNo, int pageSize, AtomicReference<Integer> titlesAR) {
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

    /************************* 资产处置 **************************/

}
