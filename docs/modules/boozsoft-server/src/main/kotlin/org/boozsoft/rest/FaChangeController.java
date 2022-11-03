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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faBd")
public class FaChangeController {

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


    @PostMapping("findFaChangeListByCondition")
    public Mono<R> findMinusListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String code = map.get("code").toString();
        String yearMonth = map.get("yearMonth").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return faChangeHeadRepository.findAllByManageCodeAndChangeDateLikeOrderByChangeDateAscChangeCodeAsc(code, "%" + yearMonth + "%").collectList().map(list -> queryFilter(list, condition)).flatMapMany(list -> Flux.fromIterable(list)).flatMap(eit -> getrMono(eit)).collectList().map(list -> splitList(list, page, pageSize, totalAR)).map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    /**
     * 查询指定单据的明细
     *
     * @param finalMaster
     * @return
     */
    @NotNull
    private Mono<FaChangeHead> getrMono(FaChangeHead finalMaster) {
        return faChangeRepository.findAllBySuperiorIdLikeOrderByCdateDesc("%" + finalMaster.getId() + "%").collectList().zipWith(faChangeRepository.findAllBySuperiorIdNotLikeOrderByCdateDesc("%" + finalMaster.getId() + "%").collectList()).flatMap(enlist -> {
            List<String> yess = JSON.parseArray(finalMaster.getChangeContent(), String.class).stream().filter(v -> v.equals("DEPT") || v.equals("PRO")).collect(Collectors.toList());
            if (yess.size() > 0) { // 查询
                Mono<List<FaChangeDept>> lastDM = faChangeDeptRepository.findAllBySuperiorIdOrderByCdateDesc(finalMaster.getId()).collectList();
                Mono<List<FaChangeProject>> lastPM = faChangeProjectRepository.findAllBySuperiorIdOrderByCdateDesc(finalMaster.getId()).collectList();
                Mono<List<FaChangeDept>> oldDM = faChangeDeptRepository.findAllBySuperiorIdNotAndCdateLessThanOrCdateLessThanAndSuperiorIdIsNullOrderByCdateDesc(finalMaster.getId(), finalMaster.getChangeDate(), finalMaster.getChangeDate()).collectList();
                Mono<List<FaChangeProject>> oldPM = faChangeProjectRepository.findAllBySuperiorIdNotAndCdateLessThanOrCdateLessThanAndSuperiorIdIsNullOrderByCdateDesc(finalMaster.getId(), finalMaster.getChangeDate(), finalMaster.getChangeDate()).collectList();
                return Mono.zip(lastDM, lastPM, oldDM, oldPM).flatMap(tips -> {
                    List<FaChange> t1 = enlist.getT1();
                    if (t1.size() > 0 && (tips.getT1().size() > 0 || tips.getT2().size() > 0)) {
                        for (FaChange faChange : t1) {
                            faChange.setBeiyong1(JSON.toJSONString(tips.getT1().stream().filter(it -> it.getCardUnique().equals(faChange.getCardUnique())).collect(Collectors.toList())));
                            faChange.setBeiyong2(JSON.toJSONString(tips.getT2().stream().filter(it -> it.getCardUnique().equals(faChange.getCardUnique())).collect(Collectors.toList())));
                        }
                    } else {
                        if (tips.getT1().size() > 0 || tips.getT2().size() > 0) {
                            // 部门与项目分组
                            List<String> dcodes = tips.getT1().stream().map(it -> it.getCardUnique()).collect(Collectors.toList());
                            dcodes.addAll(tips.getT2().stream().map(it -> it.getCardUnique()).collect(Collectors.toList()));
                            Set<String> codes = new HashSet<>(dcodes);
                            // 唯一码相同 月
                            for (String st : codes) {
                                FaChange change = new FaChange();
                                change.setCardUnique(st);
                                change.setIyear(change.getIyear());
                                change.setImonth(change.getImonth());
                                change.setBeiyong1(JSON.toJSONString(tips.getT1().stream().filter(it -> it.getCardUnique().equals(st)).collect(Collectors.toList())));
                                change.setBeiyong2(JSON.toJSONString(tips.getT2().stream().filter(it -> it.getCardUnique().equals(st)).collect(Collectors.toList())));
                                t1.add(change);
                            }
                        }
                    }
                    List<FaChange> t2 = enlist.getT2();
                    String dLastYm = tips.getT3().size() > 0 ? tips.getT3().stream().map(dt -> dt.getIyear() + '-' + dt.getImonth()).collect(Collectors.toList()).get(0) : null;
                    String pLastYm = tips.getT4().size() > 0 ? tips.getT4().stream().map(dt -> dt.getIyear() + '-' + dt.getImonth()).collect(Collectors.toList()).get(0) : null;
                    List<FaChangeDept> des = null != dLastYm ? tips.getT3().stream().filter(it -> it.getIyear().equals(dLastYm.split("-")[0]) && it.getImonth().equals(dLastYm.split("-")[1])).collect(Collectors.toList()) : new ArrayList<>();
                    List<FaChangeProject> pes = null != pLastYm ? tips.getT2().stream().filter(it -> it.getIyear().equals(pLastYm.split("-")[0]) && it.getImonth().equals(pLastYm.split("-")[1])).collect(Collectors.toList()) : new ArrayList<>();
                    if (t2.size() > 0 && (tips.getT3().size() > 0 || tips.getT4().size() > 0)) {
                        for (FaChange faChange : t2) {
                            if (null != dLastYm) faChange.setBeiyong1(JSON.toJSONString(des));
                            if (null != pLastYm) faChange.setBeiyong2(JSON.toJSONString(pes));
                        }
                    } else {
                        if (tips.getT3().size() > 0 || tips.getT4().size() > 0) {
                            // 部门与项目分组
                            List<String> dcodes = des.stream().map(it -> it.getCardUnique()).collect(Collectors.toList());
                            dcodes.addAll(pes.stream().map(it -> it.getCardUnique()).collect(Collectors.toList()));
                            Set<String> codes = new HashSet<>(dcodes);
                            // 唯一码相同 月
                            for (String st : codes) {
                                FaChange change = new FaChange();
                                change.setCardUnique(st);
                                change.setIyear(change.getIyear());
                                change.setImonth(change.getImonth());
                                if (null != dLastYm)
                                    change.setBeiyong1(JSON.toJSONString(des.stream().filter(it -> it.getCardUnique().equals(st)).collect(Collectors.toList())));
                                if (null != pLastYm)
                                    change.setBeiyong2(JSON.toJSONString(pes.stream().filter(it -> it.getCardUnique().equals(st)).collect(Collectors.toList())));
                                t2.add(change);
                            }
                        }
                    }
                    if (t1.size() != t2.size()) { // 数量不想等 去重 获取最新数据
                        List<FaChange> c2 = new ArrayList<>();
                        for (FaChange faChange : t1) {
                            for (FaChange change : t2) {
                                if (faChange.getCardUnique().equals(change.getCardUnique())) {
                                    c2.add(change);
                                    break;
                                }
                            }
                        }
                        t2 = c2;
                    }
                    finalMaster.setBeiyong5(JSON.toJSONString(t1));// 变动后的值
                    finalMaster.setBeiyong4(JSON.toJSONString(t2));// 变动前的值
                    return Mono.just(finalMaster);
                });
            } else {
                List<FaChange> t1 = enlist.getT1();
                List<FaChange> t2 = enlist.getT2();
                if (t1.size() != t2.size()) { // 数量不想等 去重 获取最新数据
                    List<FaChange> c2 = new ArrayList<>();
                    for (FaChange faChange : t1) {
                        for (FaChange change : t2) {
                            if (faChange.getCardUnique().equals(change.getCardUnique()) && Integer.parseInt(change.getImonth()) < Integer.parseInt(faChange.getImonth())) {
                                c2.add(change);
                                break;
                            }
                        }
                    }
                    t2 = c2;
                }
                finalMaster.setBeiyong5(JSON.toJSONString(t1));// 变动后的值
                finalMaster.setBeiyong4(JSON.toJSONString(t2));// 变动前的值
                return Mono.just(finalMaster);
            }
        });
    }

    @PostMapping("findFaAssetListByCondition")
    public Mono<R>  findFaAssetListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String qValue = map.get("qValue").toString();
        Flux<FaCardMaster> faCardMasterFlux = type.equals("one") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndSysIdLikeOrderBySysId(code, /*"1", "1", */date.substring(0, 7) + "-01", "%" + qValue + "%") : type.equals("two") ? faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndCardCodeLikeOrderByCardCode(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%") : faCardMasterRepository.findAllByManageCodeAndCreatTimeLessThanAndFaNameLikeOrderBySysId(code, /*"1", "1",*/ date.substring(0, 7) + "-01", "%" + qValue + "%");
        return faCardMasterFlux.collectList().map(o -> R.ok(dataTransform(o)));
    }

    @PostMapping("findLastFaChangeInfoByCode")
    public Mono<R> findLastFaChangeInfoByCode(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        boolean isExpand = Boolean.parseBoolean(map.get("isExpand").toString()); // 查询对应卡片最新的部门与项目
        return faChangeRepository.findFirstByCardUniqueOrderByCdateDesc(code).flatMap(o -> {
            Mono<R> expandM = faChangeDeptRepository.findByCardUniqueOrderByCdateDesc(o.getCardUnique()).collectList().zipWith(faChangeProjectRepository.findByCardUniqueOrderByCdateDesc(o.getCardUnique()).collectList()).flatMap(tips -> {
                String ym = tips.getT1().size() > 0 ? tips.getT1().stream().map(it -> it.getIyear() + '-' + it.getImonth()).collect(Collectors.toList()).get(0) : tips.getT2().size() > 0 ? tips.getT2().stream().map(it -> it.getIyear() + '-' + it.getImonth()).collect(Collectors.toList()).get(0) : "";
                o.setBeiyong1(JSON.toJSONString(tips.getT1().stream().filter(it -> it.getIyear().equals(ym.split("-")[0]) && it.getImonth().equals(ym.split("-")[1])).collect(Collectors.toList())));
                o.setBeiyong2(JSON.toJSONString(tips.getT2().stream().filter(it -> it.getIyear().equals(ym.split("-")[0]) && it.getImonth().equals(ym.split("-")[1])).collect(Collectors.toList())));
                return Mono.just(o);
            }).map(e -> R.ok(e));
            return isExpand ? expandM : Mono.just(R.ok(o));
        });
    }

    @PostMapping("findFaChangeByCondition")
    public Mono<R> findFaChangeByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return faChangeHeadRepository.findAllByManageCodeOrderByChangeDateAscChangeCodeAsc(code).collectList().cache().flatMap(list -> {
            if (list.size() == 0) {
                return Mono.just(R.ok());
            } else {
                FaChangeHead master = null;
                switch (action) {
                    case "curr":
                        master = list.get((list.stream().map(e -> e.getChangeCode()).collect(Collectors.toList())).indexOf(currPdId));
                        break;
                    case "tail":
                        master = list.get(list.size() - 1);
                        break;
                    case "prev":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getChangeCode()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index == 0 ? 0 : index - 1;
                            master = list.get(index);
                        }
                        break;
                    case "next":
                        if (StrUtil.isBlank(currPdId)) {
                            master = list.get(0);
                        } else {
                            int index = (list.stream().map(e -> e.getChangeCode()).collect(Collectors.toList())).indexOf(currPdId);
                            index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                            master = list.get(index);
                        }
                        break;
                    default:
                        master = list.get(0);
                        break;
                }
                FaChangeHead finalMaster = master;
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
    public Mono<R> save(@RequestBody FaChangeHead entity) {
        List<FaChange> entrys = JSON.parseArray(entity.getBeiyong5(), FaChange.class);
        entity.setBeiyong5(null);
        boolean isAdd = null == entity.getId();
        List<String> changeContents = JSON.parseArray(entity.getChangeContent(), String.class);
        if (isAdd) {
            entity.setMakerTime(DateUtil.now());
            if (changeContents.size() > 0) {
                entity.setChangeTitle1(changeContents.get(0));
                if (changeContents.size() > 1) {
                    entity.setChangeTitle2(changeContents.get(1));
                    if (changeContents.size() > 2) entity.setChangeTitle3(changeContents.get(2));
                }
            }
            if (StrUtil.isNotBlank(entity.getChangeDate())) {
                String[] dates = entity.getChangeDate().split("-");
                entity.setIyear(dates[0]);
                entity.setImonth(dates[1]);
            }
        }
        return faChangeHeadRepository.save(entity).flatMap(dbEntity -> {
            List<String> nons = changeContents.stream().filter(v -> !v.equals("DEPT") && !v.equals("PRO")).collect(Collectors.toList());
            List<String> yess = changeContents.stream().filter(v -> v.equals("DEPT") || v.equals("PRO")).collect(Collectors.toList());
            // 单纯添加 变动
            Mono<List<FaChange>> entrySaveM = Mono.just("").flatMapMany(it -> Flux.fromIterable(entrys)).flatMap(it -> faChangeRepository.findFirstByCardUniqueAndCdateLessThanEqualOrderByCdateDesc(it.getCardUnique(),dbEntity.getChangeDate()).map(dbChange -> {
                if (!dbChange.getIyear().equals(dbEntity.getIyear()) || (dbChange.getIyear().equals(dbEntity.getIyear()) && !dbChange.getImonth().equals(dbEntity.getImonth()))) {//不同年 同年不同意当月存在替换
                    dbChange.setId(null);
                    // 同月 追加 不同月 新建
                    if (!dbChange.getImonth().equals(dbEntity.getImonth()))dbChange.setSuperiorId(null);
                    dbChange.setIyear(dbEntity.getIyear());
                    dbChange.setImonth(dbEntity.getImonth());
                }
                for (String changeContent : nons) {
                    ReflectionUtil.setValue(dbChange, changeContent, ReflectionUtil.getValue(it, changeContent));
                }
                dbChange.setCdate(dbEntity.getChangeDate());
                dbChange.setChangeCause(it.getChangeCause());
                Set<String> sets = null;
                if (null == dbChange.getSuperiorId() || StrUtil.isBlank(dbChange.getSuperiorId())) {// 第一次
                    sets = new HashSet<>();
                } else {//多次修改
                    sets = new HashSet<>(JSON.parseArray(dbChange.getSuperiorId(), String.class));
                }
                sets.add(dbEntity.getId());
                dbChange.setSuperiorId(JSON.toJSONString(sets));
                return dbChange;
            })).collectList().flatMap(list -> faChangeRepository.saveAll(list).collectList());

            // 修改的话 添加前删除
            Mono<FaChangeHead> delDM = faChangeDeptRepository.findAllBySuperiorIdOrderByCdateDesc(dbEntity.getId()).collectList().flatMap(ls -> ls.size()>0? faChangeDeptRepository.deleteAll(ls).thenReturn(dbEntity):Mono.just(dbEntity));
            Mono<FaChangeHead> delPM = faChangeProjectRepository.findAllBySuperiorIdOrderByCdateDesc(dbEntity.getId()).collectList().flatMap(ls -> ls.size()>0? faChangeProjectRepository.deleteAll(ls).thenReturn(dbEntity):Mono.just(dbEntity));
            // 单纯添加 其他变动
            Mono<List<FaChange>> entrySaveEtcM = Mono.just("").flatMapMany(it -> Flux.fromIterable(entrys)).flatMap(it -> {
                String beiyong1 = it.getBeiyong1();
                it.setBeiyong1(null);
                String beiyong2 = it.getBeiyong2();
                it.setBeiyong2(null);
                List<FaChangeDept> depts = StrUtil.isBlank(beiyong1) ? new ArrayList<>() : JSON.parseArray(beiyong1, FaChangeDept.class);
                List<FaChangeProject> pros = StrUtil.isBlank(beiyong2) ? new ArrayList<>() : JSON.parseArray(beiyong2, FaChangeProject.class);
                Mono<List<FaChangeDept>> listMono = faChangeDeptRepository.findByCardUniqueOrderByCdateDesc(it.getCardUnique()).collectList().flatMap(dbDeptList -> faChangeDeptRepository.saveAll(depts.stream().map(t -> {
                    t.setSuperiorId(dbEntity.getId());
                    return t;
                }).collect(Collectors.toList())).collectList());
                Mono<List<FaChangeProject>> listMono1 = faChangeProjectRepository.findByCardUniqueOrderByCdateDesc(it.getCardUnique()).collectList().flatMap(dbDeptList -> faChangeProjectRepository.saveAll(pros.stream().map(t -> {
                    t.setSuperiorId(dbEntity.getId());
                    return t;
                }).collect(Collectors.toList())).collectList());
                return (depts.size() > 0 ?  listMono  : Mono.just(new ArrayList<>())).zipWith((pros.size() > 0 ? ( listMono1 ) : Mono.just(new ArrayList<>()))).flatMap(tips -> Mono.just(it));
            }).collectList();
            if (nons.size() > 0 && yess.size() == 0) {
                return entrySaveM.flatMap(list -> Mono.just(dbEntity));
            } else if (nons.size() == 0 && yess.size() > 0) {
                if (isAdd){
                    return entrySaveEtcM.flatMap(list -> Mono.just(dbEntity));
                }else {
                    return Mono.zip(delDM,delPM).flatMap(tips->entrySaveEtcM.flatMap(list -> Mono.just(dbEntity)));
                }
            } else {
                if (isAdd){
                    return entrySaveM.zipWith(entrySaveEtcM).flatMap(list -> Mono.just(dbEntity));
                }else {
                    return entrySaveM.zipWith(Mono.zip(delDM,delPM).flatMap(tips->entrySaveEtcM.flatMap(list -> Mono.just(dbEntity)))).flatMap(list -> Mono.just(dbEntity));
                }
            }
        }).map(o -> R.ok(o));
    }

    @DeleteMapping("del")
    @Transactional
    public Mono<R> del(@RequestBody FaChangeHead entity) {
        Mono<FaChangeHead> delDM = faChangeDeptRepository.findAllBySuperiorIdOrderByCdateDesc(entity.getId()).collectList().flatMap(ls -> faChangeDeptRepository.deleteAll(ls).thenReturn(entity));
        Mono<FaChangeHead> delPM = faChangeProjectRepository.findAllBySuperiorIdOrderByCdateDesc(entity.getId()).collectList().flatMap(ls -> faChangeProjectRepository.deleteAll(ls).thenReturn(entity));
        Mono<FaChangeHead> entryDelM = faChangeRepository.findAllBySuperiorIdLikeOrderByCdateDesc("%" + entity.getId() + "%").collectList().flatMap(list -> {
            if (list.size() > 0) {
                List<FaChange> dels = new ArrayList<>();
                List<FaChange> changes = new ArrayList<>();
                for (FaChange faChange : list) {
                    List<String> strings = JSON.parseArray(faChange.getSuperiorId(), String.class);
                    if (strings.size() == 1) {
                        dels.add(faChange);
                    } else {
                        faChange.setSuperiorId(JSON.toJSONString(strings.stream().filter(s -> !s.equals(entity.getId())).collect(Collectors.toList())));
                        changes.add(faChange);
                    }
                }
                return faChangeRepository.deleteAll(dels).thenReturn("").zipWith(faChangeRepository.saveAll(changes).collectList()).flatMap(ts -> Mono.just(""));
            } else {
                return Mono.just("");
            }
        }).thenReturn(entity);
        return Mono.zip(entryDelM, delDM, delPM).flatMap(e -> faChangeHeadRepository.deleteById(entity.getId()).thenReturn(entity)).map(o -> R.ok(o));
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
                    dV = DateUtil.endOfMonth(DateUtil.parse(date.substring(0, 4) + "-" + (maxM > 9 ? "" + maxM : "0" + maxM) + "-" + "01").toJdkDate()).toDateStr();
                } else {
                    dV = date;
                }
                String finalDV = dV;
                return faChangeHeadRepository.findFirstByChangeDateLikeOrderByChangeCodeDesc("%" + dV.substring(0, 7) + "%").switchIfEmpty(Mono.just(new FaChangeHead())).flatMap(dbEntry2 -> {
                    String cV = "";
                    String yAm = finalDV.substring(0, 7);
                    if (null == dbEntry2.getChangeCode()) { // 第一次
                        cV = "BD" + yAm.replaceAll("-", "") + "0001";
                    } else {
                        cV = "BD" + yAm.replaceAll("-", "") + PdIncr(dbEntry2.getChangeCode().replace("BD" + yAm.replaceAll("-", ""), ""));
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

    @PostMapping("canChangeContent")
    @Transactional
    public Mono<R> canChangeContent(@RequestBody Map map) {
        String code = map.get("code").toString();
        String date = map.get("date").toString();
        String[] dates = date.split("-");
        // 已结账 或 启用月 没有变动
        return groupFaAccPeriodRepository.findAllByUniqueCodeOrderByIyearAscImonthAsc(code).collectList().flatMap(list -> {
            Map<String, Object> m = new HashMap<>();
            List<GroupFaAccPeriod> ymList = list.stream().filter(it -> it.getIyear().equals(dates[0]) && it.getImonth().equals(dates[1])).collect(Collectors.toList());
            if (list.get(0).getIyear().equals(dates[0]) && list.get(0).getImonth().equals(dates[1])) {
                // 第一个月
                m.put("isError", true);
                m.put("type", "01");
            } else if (ymList.size() > 0 && ymList.get(0).getIsSettle().equals("1")) {
                // 已结账
                m.put("isError", true);
                m.put("type", "02");
            }
            if (m.keySet().size() > 0) return Mono.just(R.ok(m));
            return faChangeHeadRepository.findAllByManageCodeAndIyearAndImonthOrderByChangeDateAsc(code, dates[0], dates[1]).collectList().flatMap(list2 -> {
                m.put("isError", false);
                if (list2.size() > 0) {
                    Set<String> contents = new HashSet<String>();
                    for (FaChangeHead faChangeHead : list2) {
                        contents.addAll(JSON.parseArray(faChangeHead.getChangeContent(), String.class));
                    }
                    m.put("contents", contents);
                } else {
                    m.put("contents", new ArrayList<>());
                }
                return Mono.just(R.ok(m));
            });
        });
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

    private List<FaChangeHead> queryFilter(List<FaChangeHead> list, Map map) {
        if (list.size() > 0) {
            Map<String, String> searchConditon = (HashMap<String, String>) map.get("searchConditon");
            List<String> treeConditon = (ArrayList<String>) map.get("treeConditon");
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(searchConditon.get("requirement")) && StrUtil.isNotBlank(searchConditon.get("value"))) {
                    Object dbValue = ReflectionUtil.getValue(item, searchConditon.get("requirement").trim());
                    if (null != dbValue && !dbValue.toString().contains(searchConditon.get("value").trim()))
                        return false;
                }
                if (treeConditon.size() > 0 && !treeConditon.contains(item.getChangeDate().substring(0, 7)))
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
            sub.setBeiyong1(car.getZhejiuType());
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
