package org.boozsoft.rest.group;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.*;
import org.boozsoft.domain.entity.group.GroupStockAccount;
import org.boozsoft.repo.GroupSysCorpRepository;
import org.boozsoft.repo.SysAccountPeriodRepository;
import org.boozsoft.repo.group.GroupStockAccountRepository;
import org.boozsoft.repo.group.GroupStockPeriodRepository;
import org.boozsoft.repo.group.GroupSysOrgDataaccessRepository;
import org.boozsoft.service.DatabaseInitService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/group/stockAccount")
public class GroupStockAccountController {

    @Autowired
    private GroupStockAccountRepository groupStockAccountRepository;

    @Autowired
    private GroupStockPeriodRepository groupStockPeriodRepository;

    @Autowired
    private SysAccountPeriodRepository accountPeriodRepository;

    @Autowired
    GroupSysOrgDataaccessRepository groupSysOrgDataaccessRepository;

    @Autowired
    GroupSysCorpRepository groupSysCorpRepository;

    @Autowired
    DatabaseInitService databaseInitService;

    @GetMapping("list")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> assetsList(Pageable pageable,String userId) {
        Flux<GroupStockAccount> flux = userId == null ? groupStockAccountRepository.findAll() : groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(userId).map(it -> it.getOrgUniqueCode()).collectList().flatMap(codes -> groupSysCorpRepository.findAllByAccGroupIn(codes).filter(it -> it.getIsDel().equals("0")).map(it -> it.getId()).collectList().flatMap(
                ids -> groupStockAccountRepository.findAllByUniqueCodeIn(ids).collectList()
        )).flatMapMany(i -> Flux.fromIterable(i));
        return flux.collectList().map(list -> list.stream().filter(it->it.getIsDel().equals("0")).sorted(Comparator.comparing(GroupStockAccount::getUniqueCode)).collect(Collectors.toList())).flatMap(item -> groupStockAccountRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改存货管理账", notes = "传入code")
    public Mono<R> saveAssets(@RequestBody GroupStockAccount entity) {
 /*       return null == entity.getId() ? groupStockAccountRepository.save(entity).flatMap(dbEntity -> groupStockPeriodRepository.saveAll(generateAccountPeriod(dbEntity)).collectList().thenReturn(R.ok().setResult(dbEntity))) :
                groupStockAccountRepository.save(entity).map(dbEntity -> R.ok().setResult(dbEntity));*/
        boolean b = null == entity.getId();
        String letter = PinyinUtil.getFirstLetter(entity.getStockAccId().length() > 8 ? entity.getStockAccId().substring(0, 8) : entity.getStockAccId(), "");
        if (b) entity.setStockAccId(letter + "-001");
        Mono<GroupStockAccount> entityMono = Mono.just(entity).flatMap(it -> !entity.getCoCode().equals(entity.getAssociateCoCode()) ? accountPeriodRepository.findAllByAccountIdLikeOrderByAccountIdDesc("%" + letter + "-%").collectList().flatMap(list -> {
            String defulat = "001";
            if (list.size() > 0) {
                int num = (Integer.parseInt(list.get(0).getAccountId().split("-")[1]) + 1);
                if (num < 10) {
                    defulat = "00" + num;
                } else if (9 < num && num < 100) {
                    defulat = "0" + num;
                } else {
                    defulat = "" + num;
                }
            }
            return Mono.just(defulat);
        }).map(num -> {
            it.setStockAccId(letter + "-" + num);
            return it;
        }) : Mono.just(it));
        return b ? (entityMono.flatMap(it -> groupStockAccountRepository.save(initializeDefaultParameters(entity)))).flatMap(dbEntity -> databaseInitService.insertStockAccountsData(dbEntity).flatMap(num1 -> {dbEntity.setBeiyong1(num1);;return Mono.just(R.ok().setResult(dbEntity));})) : groupStockAccountRepository.save(entity).map(dbEntity -> R.ok().setResult(dbEntity));
    }

    private GroupStockAccount initializeDefaultParameters(GroupStockAccount entity) {
        // 销售
        entity.setXsShXkd("1");
        entity.setXsThXhd("1");
        entity.setXsSaveCheck("1");
        entity.setXsPriceCxkh("1");
        entity.setXsPriceZjyc("1");
        entity.setXsNumWs("2");
        entity.setXsPriceWs("2");
        entity.setXsRateWs("2");
        entity.setXsRate("13");
        entity.setXsDhRc("0.06");
        entity.setXsZdRc("0.36");
        entity.setXsLiucheng("1");
        // 采购
        entity.setCgShDhd("1");
        entity.setCgThDhd("1");
        entity.setCgSaveCheck("1");
        entity.setCgPriceIsRate("1");
        entity.setCgPriceZjyc("1");
        entity.setCgNumWs("2");
        entity.setCgPriceWs("2");
        entity.setCgRateWs("2");
        entity.setCgRate("13");
        entity.setCgLiucheng("1");
        // 成本
        entity.setKcCgrkSaveCheck("1");
        entity.setKcXsckSaveCheck("1");
        entity.setKcJcjrCheck("1");
        entity.setKcCgrkCheck("1");
        entity.setKcXsckCheck("1");
        entity.setKcQtcrkCheck("1");
        entity.setKcNumWs("2");
        entity.setKcPriceWs("2");
        entity.setKcCostAccounting("1");
        entity.setKcEstimated("1");
        return entity;
    }


    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String id, String accNameCn) {
        return groupStockAccountRepository.findAllByStockAccName(accNameCn.trim()).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }


    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String id, String code) {
        return groupStockAccountRepository.findAllByCoCode(code.trim()).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("used")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> used() {
        return groupStockAccountRepository.findAll().filter(it->it.getIsDel().equals("0")).map(it -> it.getUniqueCode()).collectList().flatMapMany(i -> Flux.fromIterable(i)).distinct().collectList().map(o -> R.ok(o));
    }

    @DeleteMapping("del")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteAssets(@RequestBody GroupFaAccount entity) {
        return groupStockAccountRepository.findById(entity.getId())
                .map(it->{it.setIsDel("1");it.setDelName(entity.getDelName());it.setDelDate(DateUtil.now());return it;})
                .flatMap(o->groupStockAccountRepository.save(o).map(e->R.ok()));
    }

    @DeleteMapping("delTrue")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteAssetsTrue(@RequestBody GroupFaAccount entity) {
        Mono<GroupFaAccount> delM = groupStockPeriodRepository.findAllByUniqueCode(entity.getId()).collectList().flatMap(groupStockPeriodRepository::deleteAll).thenReturn(entity);
        return groupStockAccountRepository.findById(entity.getId()).flatMap(dbEntity -> databaseInitService.removeStockAccountsData(dbEntity).flatMap(e -> groupStockAccountRepository.deleteById(dbEntity.getId()).flatMap(it -> Mono.just(R.ok()))));
    }

    @GetMapping("authorAccountList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> authorAccountList(String userId) {

        return groupStockAccountRepository.findAllByFlagOrderByCoCodeAsc("1").filter(it->it.getIsDel().equals("0")).flatMap(en -> accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(en.getCoCode()).collectList().map(list -> {
            en.setBeiyong1(JSON.toJSONString(list.stream().map(it -> it.getAccountYear()).collect(Collectors.toList())));
            return en;
        })).collectList().cache().map(R::ok);
    }

    @PostMapping("reduction")
    public Mono<R> reduction(@RequestBody GroupStockAccount entity) {
        return groupStockAccountRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> groupStockAccountRepository.save(e).map(o -> R.ok()));
    }

    @GetMapping("allDel")
    @ApiOperation(value = "查询所有", notes = "传入code")
    public Mono<R> allDel() {
        return groupStockAccountRepository.findAll().filter(it -> it.getIsDel().equals("1")).collectList().map(R::ok);
    }

    @PostMapping("periodInfoByYm")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> periodInfoByYm(@RequestBody Map map) {
        String code = map.get("code").toString();
        String[] dates = map.get("date").toString().split("-");
       return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(code,dates[0]).collectList().map(R::ok);
    }


    @PostMapping("findByStockAccountId")
    public Mono<R> findByStockAccountId(String id){
        return groupStockAccountRepository.findById(id).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }
    @PostMapping("findByStockAccountAccId")
    public Mono<R> findByStockAccountAccId(String accId){
        return groupStockAccountRepository.findByStockAccId(accId).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }


    @GetMapping("period/list")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> periodList(Pageable pageable,String id,String year) {
        Flux<GroupStockPeriod> flux = year.equals("all") ? groupStockPeriodRepository.findAllByUniqueCode(id):groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year);
        return flux.collectList()
                .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getStockYear()+""+o1.getStockMonth()).compareTo(Integer.valueOf(o2.getStockYear()+""+o2.getStockMonth()))))
                .flatMap(item -> groupStockPeriodRepository.countAllBy().map(total -> R.page(item, pageable, year.equals("all")?total:item.size())));
    }

    @GetMapping("period/tree")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> periodList(String id) {
        Flux<GroupStockPeriod> flux = groupStockPeriodRepository.findAllByUniqueCode(id);
        return flux.map(it->it.getStockYear()).distinct().collectList().map(R::ok);
    }

    @PostMapping("period/latest")
    @ApiOperation(value = "获取最新结账或未结账期间", notes = "传入code")
    public Mono<R> latestMonth(@RequestBody Map map) {
        String id = map.get("id").toString();
        String action = map.get("action").toString();
        Flux<GroupStockPeriod> flux = groupStockPeriodRepository.findAllByUniqueCode(id);
        return flux.collectList() .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getStockYear()+""+o1.getStockMonth()).compareTo(Integer.valueOf(o2.getStockYear()+""+o2.getStockMonth()))))
                .map(list->
                    list.stream().filter(it -> action.equals("1") ?null != it.getIstock() && it.getIstock().equals("1") : null == it.getIstock() || !it.getIstock().equals("1")).collect(Collectors.toList()).get(0)
                ).map(R::ok);
    }
    @PostMapping("period/closeBill")
    @ApiOperation(value = "获取最新结账或未结账期间", notes = "传入code")
    public Mono<R> closeBill(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just("请求参数异常").map(m -> R.error());
        String id = map.get("id").toString();
        String value = map.get("value").toString();
        String user = map.get("user").toString();
        return groupStockPeriodRepository.findById(id).flatMap(dbEntry -> {
            if (value.equals("1")){
                dbEntry.setIstock(value);
                dbEntry.setIstockTime(DateUtil.now());
                dbEntry.setIstockUser(user);
            }else {
                dbEntry.setIstock(null);
                dbEntry.setIstockTime(null);
                dbEntry.setIstockUser(null);
            }
            return groupStockPeriodRepository.save(dbEntry).map(R::ok);
        });
    }


    @PostMapping("period/generate")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    @Transactional
    public Mono<R> periodGenerate(@RequestBody Map map) {
        String id = map.get("id").toString();
        String year = map.get("year").toString();
        return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year).collectList()
                .flatMap(list->list.size() == 0?groupStockPeriodRepository.saveAll(generateAccountPeriod(new GroupStockAccount().setId(id).setStartDate(year+"-01"))).collectList().thenReturn(R.ok()):Mono.just(R.ok()));
    }
    @PostMapping("period/yearList")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> yearList(@RequestBody Map map) {
        String id = map.get("id").toString();
        String year = map.get("year").toString();
        return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year).collectList()
                .map(R::ok);
    }
    @PostMapping("period/resetStart")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> periodReset(@RequestBody Map map) {
        // 检查单据 以及 起初月
        String id = map.get("id").toString();
        String year = map.get("year").toString();
        String month = map.get("month").toString();
        return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year).collectList()
                .flatMap(list->{
                    for (GroupStockPeriod period : list) {
                        period.setStockFlag(null);
                        if (month.equals(period.getStockMonth()))period.setStockFlag("1");
                    }
                    return groupStockPeriodRepository.saveAll(list).collectList().thenReturn(R.ok());
                });
    }

    @PostMapping("period/delYear")
    @ApiOperation(value = "查询存货管理账列表", notes = "传入code")
    public Mono<R> delYear(@RequestBody Map map) {
        String id = map.get("id").toString();
        String year = map.get("year").toString();
        return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year).collectList()
                .flatMap(list-> groupStockPeriodRepository.deleteAll(list).thenReturn(R.ok()));
    }
    @PostMapping("periods")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> periods(@RequestBody Map map) {
        return groupStockPeriodRepository.findAllByUniqueCode(map.get("id").toString()).collectList()
                .map(list -> ListUtil.sort(list, (o1, o2) -> Integer.valueOf(o1.getStockYear()+""+o1.getStockMonth()).compareTo(Integer.valueOf(o2.getStockYear()+""+o2.getStockMonth()))))
                .map(R::ok);
    }
    @PostMapping("findPeriodDateByYear")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findPeriodDateByYear(@RequestBody Map map) {
        String id = map.get("id").toString();
        String year = map.get("date").toString().substring(0,4);
        return groupStockPeriodRepository.findAllByUniqueCodeAndStockYearOrderByStockMonth(id,year).collectList().flatMap(list->{
            List<GroupStockPeriod> collect = list.stream().filter(it -> null != it.getStockFlag() && it.getStockFlag().equals("1")).collect(Collectors.toList());
            if (collect.size() == 0 || collect.get(0).getStockMonth().equals("01")){
                return Mono.just(R.ok((Integer.parseInt(year)-1)+"12-31"));
            }else {
                int m = Integer.parseInt( collect.get(0).getStockMonth())-1;
                return Mono.just(R.ok(DateUtil.endOfMonth(DateUtil.parseDate((year+"-"+(m>9?""+m:"0"+m) +"-01"))).toDateStr()));
            }
        });
    }

    @PostMapping("period/checkMonth")
    @ApiOperation(value = "查询指定期间是否结账", notes = "传入code")
    public Mono<R> checkMonth(@RequestBody Map map) {
        String id = map.get("id").toString();
        String[] dates = map.get("date").toString().split("-");
        return groupStockPeriodRepository.findByUniqueCodeAndStockYearAndStockMonth(id,dates[0],dates[1]).switchIfEmpty(Mono.just(new GroupStockPeriod())).flatMap(db->
            Mono.just(R.ok(null !=db.getIstock() && db.getIstock().equals("1")))
        );
    }

    @PostMapping("period/findIyearByUniqueCode")
    public Mono<R> findIyearByUniqueCode(String id) {
        return groupStockPeriodRepository.findIyearByUniqueCode(id).collectList().map(R::ok);
    }

    private List<GroupStockPeriod> generateAccountPeriod(GroupStockAccount dbEntity) {
        String[] dates = dbEntity.getStartDate().split("-");
        String id = dbEntity.getId();
        List<GroupStockPeriod> list = new ArrayList<>();
        for (int i = Integer.parseInt(dates[1]); i <= 12; i++) {
            String num = i > 9 ? "" + i : "0" + i;
            GroupStockPeriod period = new GroupStockPeriod();
            period.setUniqueCode(id);
            period.setStockYear(dates[0]);
            period.setStockMonth(num);
            list.add(period);
        }
        return list;
    }

    @GetMapping("findByStockAccId")
    public Mono<R> findByStockAccId(String accId){
        return groupStockAccountRepository.findByStockAccId(accId)
                .map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("saveStocckAccount")
    public Mono<R> saveStocckAccount(@RequestBody GroupStockAccount object){
        return groupStockAccountRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

}
