package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import org.boozsoft.domain.entity.fa.*;
import org.boozsoft.domain.entity.group.GroupFaAccPeriod;
import org.boozsoft.repo.fa.*;
import org.boozsoft.repo.group.GroupFaAccPeriodRepository;
import org.boozsoft.util.ReflectionUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.jetbrains.annotations.NotNull;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faPg")
public class FaEvaluateController {

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


    @Autowired
    FaChangeHeadRepository faChangeHeadRepository;

    @Autowired
    FaChangeDeptRepository faChangeDeptRepository;

    @Autowired
    FaChangeProjectRepository faChangeProjectRepository;

    @Autowired
    FaEvaluateMasterRepository faEvaluateMasterRepository;

    @Autowired
    FaEvaluateSubRepository faEvaluateSubRepository;

    @PostMapping("findFaPingGuListByCondition")
    public Mono<R> findFaPingGuListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
 /*       int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());*/
        String code = map.get("code").toString();
        String year = map.get("year").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return faEvaluateMasterRepository.findAllByManageCodeAndPgDateLikeOrderByPgDateAscPgIdAsc(code, "%" + year + "-%").collectList().map(list -> queryFilter(list, condition)).flatMapMany(list -> Flux.fromIterable(list)).flatMap(eit -> getrMono(eit)).collectList()/*.map(list -> splitList(list, page, pageSize, totalAR))*/.map(list -> R.ok().setResult(/*CollectOfUtils.mapof("total", totalAR.get(), "items", list)*/list));
    }

    /**
     * 查询指定单据的明细
     * @param finalMaster
     * @return
     */
    @NotNull
    private Mono<FaEvaluateMaster> getrMono(FaEvaluateMaster finalMaster) {
        return faEvaluateSubRepository.findAllBySuperiorIdOrderByIyearDescImonthDesc(finalMaster.getId()).flatMap(dbEnity -> faChangeRepository.findFirstByCardUniqueAndCdateLessThanOrderByCdateDesc(dbEnity.getCardUnique(), dbEnity.getIyear() + "-" + dbEnity.getImonth() + "-01").zipWith(faDepreciationRepository.findFirstByManageCodeAndIyearAndImonth(dbEnity.getCardUnique()).switchIfEmpty(Mono.just(new FaDepreciation()))).flatMap(its -> {
            FaChange it = its.getT1();
            FaDepreciation t2 = its.getT2();
            dbEnity.setBeiyong2(it.getJingcanzhilv()); // 净残值率
            dbEnity.setBeiyong1(it.getYuanzhi());  // 原值
            dbEnity.setBeiyong3(it.getLife());//年限
            if (null != t2.getZjLj()) {
                dbEnity.setBeiyong4(t2.getZjLj()); // 累计折旧
            } else {
                dbEnity.setBeiyong4(it.getLjzhejiu());
            }
            BigDecimal jz = new BigDecimal(StrUtil.isBlank(it.getYuanzhi()) ? "0" : it.getYuanzhi()).subtract(new BigDecimal(StrUtil.isBlank(dbEnity.getBeiyong4()) ? "0" : dbEnity.getBeiyong4()));
            dbEnity.setBeiyong5(jz.toString());// 静止
            return Mono.just(dbEnity);
        })).collectList().flatMap(list -> {
            finalMaster.setBeiyong5(JSON.toJSONString(list));
            return Mono.just(finalMaster);
        });
    }

    @PostMapping("findFaAssetListByCondition")
    public Mono<R> findFaAssetListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String qValue = map.get("qValue").toString();
        Flux<FaCardMaster> faCardMasterFlux = type.equals("one") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndSysIdLikeOrderBySysId(code, /*"1", "1", */date.substring(0, 7) + "-01", "%" + qValue + "%") : type.equals("two") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndCardCodeLikeOrderByCardCode(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%") : faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndFaNameLikeOrderBySysId(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%");
        Mono<List<FaEvaluateSub>> dbListM = faEvaluateSubRepository.findAllByIyearAndImonthOrderByIyearDescImonthDesc(date.substring(0, 4), date.substring(5, 7)).collectList();
        return faCardMasterFlux.collectList().zipWith(dbListM).flatMap(tips -> {
            List<FaCardMaster> t1 = tips.getT1();
            List<String> dbs = tips.getT2().stream().map(it -> it.getCardUnique()).collect(Collectors.toList());
            return Mono.just(t1.stream().filter(it -> !dbs.contains(it.getCardUnique())).collect(Collectors.toList()));
        }).map(o -> R.ok(dataTransform2(o)));
    }

    // 获取指定卡片编码的 参数
    @PostMapping("findFaAssetInfoByCode")
    public Mono<R> findFaAssetInfoByCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString(); //
        String date = map.get("date").toString(); //
        return faCardMasterRepository.findByCardUniqueOrderBySysId(code).flatMap(dbEnity -> faChangeRepository.findFirstByCardUniqueAndCdateLessThanOrderByCdateDesc(dbEnity.getCardUnique(), date.substring(0, 7) + "-01").zipWith(faDepreciationRepository.findFirstByManageCodeAndIyearAndImonth(dbEnity.getCardUnique()).switchIfEmpty(Mono.just(new FaDepreciation()))).flatMap(its -> {
            FaChange it = its.getT1();
            FaDepreciation t2 = its.getT2();
            dbEnity.setBeiyong2(it.getJingcanzhilv()); // 净残值率
            dbEnity.setBeiyong1(it.getYuanzhi());  // 原值
            dbEnity.setBeiyong3(it.getLife());//年限
            if (null != t2.getZjLj()) {
                dbEnity.setBeiyong4(t2.getZjLj()); // 累计折旧
            } else {
                dbEnity.setBeiyong4(it.getLjzhejiu());
            }
            BigDecimal jz = new BigDecimal(StrUtil.isBlank(it.getYuanzhi()) ? "0" : it.getYuanzhi()).subtract(new BigDecimal(StrUtil.isBlank(dbEnity.getBeiyong4()) ? "0" : dbEnity.getBeiyong4()));
            dbEnity.setBeiyong5(jz.toString());// 静止
            return Mono.just(dbEnity);
        })).map(o -> R.ok(dataTransform(ListUtil.toList(o))));
    }

    @PostMapping("findFaPingGuByCondition")
    public Mono<R> findFaPingGuByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return faEvaluateMasterRepository.findAllByManageCodeOrderByPgDateAscPgIdAsc(code).collectList().cache().flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                FaEvaluateMaster master = null;
                switch (action) {
                    case "curr":
                        master = list.get((list.stream().map(e -> e.getPgId()).collect(Collectors.toList())).indexOf(currPdId));
                        break;
                    case "tail":
                        master = list.get(list.size() - 1);
                        break;
                    case "prev":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getPgId()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index == 0 ? 0 : index - 1;
                            master = list.get(index);
                        }
                        break;
                    case "next":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getPgId()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                            master = list.get(index);
                        }
                        break;
                    default:
                        master = list.get(0);
                        break;
                }
                FaEvaluateMaster finalMaster = master;
                return getrMono(finalMaster).map(o -> R.ok(o));
            }
        });
    }

    /**
     * 变动表新增 修改
     *
     * @param entity
     * @return
     */
    @PostMapping("save")
    @Transactional
    public Mono<R> save(@RequestBody FaEvaluateMaster entity) {
        List<FaEvaluateSub> entrys = JSON.parseArray(entity.getBeiyong5(), FaEvaluateSub.class);
        entity.setBeiyong5(null);
        boolean isAdd = null == entity.getId();
        if (isAdd) entity.setMakerTime(DateUtil.now());
        return faEvaluateMasterRepository.save(entity).flatMap(dbEntity -> {
            // 单纯添加 变动
            Mono<List<FaEvaluateSub>> entrySaveM = Mono.just("").flatMapMany(it -> Flux.fromIterable(entrys)).map(it -> {
                it.setId(null);
                it.setSuperiorId(dbEntity.getId());
                it.setIyear(dbEntity.getPgDate().substring(0, 4));
                it.setImonth(dbEntity.getPgDate().substring(5, 7));
                return it;
            }).collectList().flatMap(list -> faEvaluateSubRepository.saveAll(list).collectList());
            Mono<FaEvaluateMaster> delM = faEvaluateSubRepository.findAllBySuperiorId(dbEntity.getId()).collectList().flatMap(list -> faEvaluateSubRepository.deleteAll(list).thenReturn(dbEntity));
            if (isAdd) {
                return entrySaveM.flatMap(list -> Mono.just(dbEntity));
            } else {
                return delM.flatMap(tips -> entrySaveM.flatMap(list -> Mono.just(dbEntity)));
            }
        }).map(o -> R.ok(o));
    }

    @DeleteMapping("del")
    @Transactional
    public Mono<R> del(@RequestBody FaEvaluateMaster entity) {
        Mono<FaEvaluateMaster> delM = faEvaluateSubRepository.findAllBySuperiorId(entity.getId()).collectList().flatMap(list -> faEvaluateSubRepository.deleteAll(list).thenReturn(entity));
        return delM.flatMap(e -> faEvaluateMasterRepository.deleteById(entity.getId()).thenReturn(entity)).map(o -> R.ok(o));
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody FaEvaluateMaster entity) {
        return faEvaluateMasterRepository.findById(entity.getId()).flatMap(dbEntry -> {
            if (StrUtil.isBlank(dbEntry.getVerifyUsername())) {
                dbEntry.setVerifyUsername(entity.getVerifyUsername());
                dbEntry.setVerifyTime(DateUtil.now());
            } else {
                dbEntry.setVerifyUsername(null);
                dbEntry.setVerifyTime(null);
            }
            return faEvaluateMasterRepository.save(dbEntry).map(o -> R.ok(o));
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
                    dV = DateUtil.endOfMonth(DateUtil.parse(date.substring(0, 4) + "-" + (maxM > 9 ? "" + maxM : "0" + maxM) + "-" + "01").toJdkDate()).toDateStr();
                } else {
                    dV = date;
                }
                String finalDV = dV;
                return faEvaluateMasterRepository.findFirstByPgDateLikeOrderByPgIdDesc("%" + dV.substring(0, 7) + "%").switchIfEmpty(Mono.just(new FaEvaluateMaster())).flatMap(dbEntry2 -> {
                    String cV = "";
                    String yAm = finalDV.substring(0, 7);
                    if (null == dbEntry2.getPgId()) { // 第一次
                        cV = "PG" + yAm.replaceAll("-", "") + "0001";
                    } else {
                        cV = "PG" + yAm.replaceAll("-", "") + PdIncr(dbEntry2.getPgId().replace("PG" + yAm.replaceAll("-", ""), ""));
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

    private List<FaEvaluateMaster> queryFilter(List<FaEvaluateMaster> list, Map map) {
        if (list.size() > 0) {
            Map<String, String> searchConditon = (HashMap<String, String>) map.get("searchConditon");
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(searchConditon.get("requirement")) && StrUtil.isNotBlank(searchConditon.get("value"))) {
                    Object dbValue = ReflectionUtil.getValue(item, searchConditon.get("requirement").trim());
                    if (null != dbValue && !dbValue.toString().contains(searchConditon.get("value").trim()))
                        return false;
                }
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
    private List<FaEvaluateSub> dataTransform(List<FaCardMaster> list) {
        List<FaEvaluateSub> subs = new ArrayList<>();
        for (FaCardMaster car : list) {
            FaEvaluateSub sub = new FaEvaluateSub();
            sub.setCardUnique(car.getCardUnique());
            sub.setOriginalValue(car.getBeiyong1());
            sub.setAccumulatedDepreciation(car.getBeiyong4());
            sub.setNetWorth(car.getBeiyong5());
            sub.setServiceLife(car.getBeiyong3());
            sub.setNetWorthRate(car.getBeiyong2());
            subs.add(sub);
        }
        return subs;
    }

    private List<FaAssetsMinusSub> dataTransform2(List<FaCardMaster> list) {
        List<FaAssetsMinusSub> subs = new ArrayList<>();
        for (FaCardMaster car : list) {
            FaAssetsMinusSub sub = new FaAssetsMinusSub();
            sub.setAssetsUniqueCode(car.getCardUnique());
            sub.setSysId(car.getSysId());
            sub.setAssetsCode(car.getCardCode());
            sub.setAssetsName(car.getFaName());
            sub.setSpeciType(car.getSpeciType());
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
