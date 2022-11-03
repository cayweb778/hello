package org.boozsoft.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.utils.CollectionUtils;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.fa.*;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/faPd")
public class FaPandianController {

    @Autowired
    private GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    /************************* 资产盘点 **************************/
    @Autowired
    FaCardMasterRepository faCardMasterRepository;

    @Autowired
    FaChangeRepository faChangeRepository;

    @Autowired
    FaPandianMasterRepository faPandianMasterRepository;

    @Autowired
    FaPandianSubRepository faPandianSubRepository;

    @PostMapping("findFaAssetByCondition")
    public Mono<R> findFaAssetByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());

        String code = map.get("code").toString();
        String expirationDate = map.get("expirationDate").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return faCardMasterRepository.findAllByManageCodeAndCreatTimeLike(code, "%" + expirationDate.substring(0, 7) + "%")
                .flatMap(dbEnity -> faChangeRepository.findByCardUniqueOrderByCdate(dbEnity.getCardUnique())
                        .map(it -> {
                            dbEnity.setBeiyong1(it.getShuliang());
                            Map<String, String> changeMap = new HashMap<>();
                            changeMap.put("faClass", it.getFaClass()); // 资产
                            changeMap.put("zerenUser", it.getZerenUser()); // 责任人
                            changeMap.put("addr", it.getAddr()); // 存放位置
                            changeMap.put("useType", it.getUseType()); // 使用状况
                            dbEnity.setBeiyong2(JSON.toJSONString(changeMap));
                            return dbEnity;
                        })
                )
                .collectList()
                .flatMap(dbList ->
                        faPandianSubRepository.findAllByIyearAndImonthOrderByIdAsc(expirationDate.split("-")[0], expirationDate.split("-")[1])
                                .map(it -> it.getCardUnique()).distinct().collectList()
                                .map(sets -> dbList.stream().filter(dt -> !sets.contains(dt.getCardCode())).collect(Collectors.toList()))
                                .map(list -> splitList(dataTransform(cardQueryFilter(list, condition)), page, pageSize, totalAR))
                                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)))
                );
    }


    @PostMapping("findFaPanDianListByCondition")
    public Mono<R> findFaPanDianListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        int page = Integer.parseInt(map.get("page").toString());
        int pageSize = Integer.parseInt(map.get("size").toString());
        String code = map.get("code").toString();
        String yearMonth = map.get("yearMonth").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        AtomicReference<Integer> totalAR = new AtomicReference();
        return faPandianMasterRepository.findAllByManageCodeAndMakerTimeLikeOrderByMakerTimeAscPdIdAsc(code, "%" + yearMonth + "%").collectList()
                .map(list -> splitList(queryFilter(list, condition), page, pageSize, totalAR))
                .map(list -> R.ok().setResult(CollectOfUtils.mapof("total", totalAR.get(), "items", list)));
    }

    @PostMapping("findFaAssetListByCondition")
    public Mono<R> findFaAssetListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String expirationDate = map.get("expirationDate").toString();
        Map<String, Object> condition = (HashMap<String, Object>) map.get("condition");
        return faCardMasterRepository.findAllByManageCodeAndCreatTimeLike(code,  "%" + expirationDate.substring(0, 7) + "%")
                .flatMap(dbEnity -> faChangeRepository.findByCardUniqueOrderByCdate(dbEnity.getCardUnique())
                        .map(it -> {
                            dbEnity.setBeiyong1(it.getShuliang());
                            Map<String, String> changeMap = new HashMap<>();
                            changeMap.put("faClass", it.getFaClass()); // 资产
                            changeMap.put("zerenUser", it.getZerenUser()); // 责任人
                            changeMap.put("addr", it.getAddr()); // 存放位置
                            changeMap.put("useType", it.getUseType()); // 使用状况
                            dbEnity.setBeiyong2(JSON.toJSONString(changeMap));
                            return dbEnity;
                        })
                )
                .collectList()
                .flatMap(dbList ->
                        faPandianSubRepository.findAllByIyearAndImonthOrderByIdAsc(expirationDate.split("-")[0], expirationDate.split("-")[1])
                                .map(it -> it.getCardUnique()).distinct().collectList()
                                .map(sets -> dbList.stream().filter(dt -> !sets.contains(dt.getCardCode())).collect(Collectors.toList()))
                                .map(list -> R.ok(dataTransform(cardQueryFilter(list, condition))))
                );
    }

    @PostMapping("findFaPanDianByCondition")
    public Mono<R> findFaPanDianByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String code = map.get("code").toString();
        String action = map.get("action").toString();
        String currPdId = map.containsKey("curr") ? map.get("curr").toString() : "";
        return faPandianMasterRepository.findAllByManageCodeOrderByMakerTimeAscPdIdAsc(code).collectList().cache()
                .flatMap(list -> {
                    if (list.size() == 0) {
                        return Mono.just(R.ok());
                    } else {
                        FaPandianMaster master = null;
                        switch (action) {
                            case "curr":
                                master = list.get((list.stream().map(e -> e.getPdId()).collect(Collectors.toList())).indexOf(currPdId));
                                break;
                            case "tail":
                                master = list.get(list.size() - 1);
                                break;
                            case "prev":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getPdId()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index == 0 ? 0 : index - 1;
                                    master = list.get(index);
                                }
                                break;
                            case "next":
                                if (StrUtil.isBlank(currPdId)) {
                                    master = list.get(0);
                                } else {
                                    int index = (list.stream().map(e -> e.getPdId()).collect(Collectors.toList())).indexOf(currPdId);
                                    index = index >= list.size() - 1 ? list.size() - 1 : index + 1;
                                    master = list.get(index);
                                }
                                break;
                            default:
                                master = list.get(0);
                                break;
                        }
                        FaPandianMaster finalMaster = master;
                        return faPandianSubRepository.findAllBySuperiorIdOrderByIdAsc(master.getId()).collectList()
                                .map(enlist -> {
                                    if (enlist.size() > 0) finalMaster.setBeiyong5(JSON.toJSONString(enlist));
                                    return R.ok(finalMaster);
                                });
                    }
                });
    }

    @PostMapping("save")
    @Transactional
    public Mono<R> save(@RequestBody FaPandianMaster entity) {
        List<FaPandianSub> entrys = JSON.parseArray(entity.getBeiyong5(), FaPandianSub.class);
        entity.setBeiyong5(null);
        boolean isAdd = null == entity.getId();
        return faPandianMasterRepository.save(entity).flatMap(dbEntity -> {
            if (isAdd) {
                entity.setMakerTime(DateUtil.now());
                entity.setIsPk("0");
                entity.setIsPy("0");
            } else {

            }
            Mono<FaPandianMaster> entryDelM = faPandianSubRepository.findAllBySuperiorIdOrderByIdAsc(dbEntity.getId()).collectList()
                    .flatMap(faPandianSubRepository::deleteAll).thenReturn(dbEntity);
            Mono<FaPandianMaster> entrySaveM = Flux.fromIterable(entrys).map(it -> {
                        it.setId(null);
                        it.setSuperiorId(dbEntity.getId());
                        return it;
                    }).collectList()
                    .flatMap(list -> faPandianSubRepository.saveAll(list).collectList().thenReturn(dbEntity));
            return isAdd ? entrySaveM : entryDelM.flatMap(e -> entrySaveM);
        }).map(o -> R.ok(o));
    }

    @DeleteMapping("del")
    @Transactional
    public Mono<R> del(@RequestBody FaPandianMaster entity) {
        Mono<FaPandianMaster> entryDelM = faPandianSubRepository.findAllBySuperiorIdOrderByIdAsc(entity.getId()).collectList()
                .flatMap(faPandianSubRepository::deleteAll).thenReturn(entity);
        return entryDelM.flatMap(e -> faPandianMasterRepository.deleteById(entity.getId()).thenReturn(entity))
                .map(o -> R.ok(o));
    }

    @PostMapping("review")
    @Transactional
    public Mono<R> review(@RequestBody FaPandianMaster entity) {
        return faPandianMasterRepository.findById(entity.getId()).flatMap(dbEntry -> {
            if (StrUtil.isBlank(dbEntry.getVerifyUsername())) {
                dbEntry.setVerifyUsername(entity.getVerifyUsername());
                dbEntry.setVerifyTime(DateUtil.now());
            } else {
                dbEntry.setVerifyUsername(null);
                dbEntry.setVerifyTime(null);
            }
            return faPandianMasterRepository.save(dbEntry).map(o -> R.ok(o));
        });
    }

    @PostMapping("generate")
    public Mono<R> findFaInvoiceByDate(@RequestBody Map map) {
        String date = map.get("date").toString();
        return faPandianMasterRepository.findFirstByEndDateOrderByPdIdDesc(date)
                .map(dbEntity -> R.ok("PD" + date.replaceAll("-", "") + PdIncr(dbEntity.getPdId().substring(dbEntity.getPdId().length() - 4, dbEntity.getPdId().length()))))
                .switchIfEmpty(Mono.just(R.ok("PD" + date.replaceAll("-", "") + "0001")));
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
                    if (
                            (type.equals("1") && (null == changeMap.get("faClass") || !values.contains(changeMap.get("faClass")))) ||
                                    (type.equals("2") && (null == changeMap.get("dept") || !values.contains(changeMap.get("dept")))) ||
                                    (type.equals("3") && (null == changeMap.get("project") || !values.contains(changeMap.get("project")))) ||
                                    (type.equals("4") && (null == changeMap.get("zerenUser") || !values.contains(changeMap.get("zerenUser")))) ||
                                    (type.equals("5") && (null == changeMap.get("addr") || !values.contains(changeMap.get("addr")))) ||
                                    (type.equals("6") && (null == changeMap.get("useType") || !values.contains(changeMap.get("useType"))))
                    ) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
            }
        }
        return list;
    }

    private List<FaPandianMaster> queryFilter(List<FaPandianMaster> list, Map map) {
        if (list.size() > 0) {
            Map<String, String> searchConditon = (HashMap<String, String>) map.get("searchConditon");
            List<String> treeConditon = (ArrayList<String>) map.get("treeConditon");
            list = list.stream().filter(item -> {
                if (StrUtil.isNotBlank(searchConditon.get("requirement")) && StrUtil.isNotBlank(searchConditon.get("value"))) {
                    Object dbValue = ReflectionUtil.getValue(item, searchConditon.get("requirement").trim());
                    if ( null != dbValue && !dbValue.toString().contains(searchConditon.get("value").trim()))
                        return false;
                }
                if (treeConditon.size() > 0 && !treeConditon.contains(item.getMakerTime().substring(0, 7))) return false;
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
    private List<FaPandianSub> dataTransform(List<FaCardMaster> list) {
        List<FaPandianSub> subs = new ArrayList<>();
        for (FaCardMaster car : list) {
            FaPandianSub sub = new FaPandianSub();
            String[] dates = car.getCreatTime().split("-");
            sub.setIyear(dates[0]);
            sub.setImonth(dates[1]);
            sub.setBookAmount(car.getBeiyong1());
            sub.setRealityAmount(car.getBeiyong1());
            sub.setCardUnique(car.getCardUnique());
            sub.setBeiyong3(car.getBeiyong2());
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

    @GetMapping("checkDate")
    @ApiOperation(value = "检查传入的日期是否存在与结转", notes = "传入code")
    public Mono<R> checkDate(String code, String date) {
        return groupFaAccPeriodRepository.findFirstByUniqueCodeAndIyearAndImonth(code, date.split("-")[0], date.split("-")[1])
                .map(o -> R.ok(o)).switchIfEmpty(Mono.just(R.ok()));
    }

    /************************* 资产盘点 **************************/

}
