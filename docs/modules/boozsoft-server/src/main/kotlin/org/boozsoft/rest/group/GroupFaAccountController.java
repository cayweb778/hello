package org.boozsoft.rest.group;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.group.GroupAccPrint;
import org.boozsoft.domain.entity.group.GroupFaAccPeriod;
import org.boozsoft.domain.entity.group.GroupFaAccount;
import org.boozsoft.domain.entity.group.GroupSysAccount;
import org.boozsoft.repo.group.GroupFaAccPeriodRepository;
import org.boozsoft.repo.group.GroupFaAccountRepository;
import org.jetbrains.annotations.NotNull;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group/faAccount")
public class GroupFaAccountController {

    @Autowired
    GroupFaAccountRepository accountRepository;

    @Autowired
    GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    @GetMapping("findByAccId")
    @ApiOperation(value = "根据公司唯一码查询", notes = "根据公司唯一码查询")
    public Mono findByAccId(String uniqueCode) {
        //return accountRepository.findByUniqueCode(uniqueCode).map(o -> R.ok().setResult(o));
        return accountRepository.findByUniqueCode(uniqueCode).map(eneity -> assemblyMapData(eneity)).map(o -> R.ok().setResult(o));

    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupFaAccount object) {
        object.setZhejiuPeriod(object.getZhejiuPeriod().replace("月份", ""));
        return accountRepository.save(object).map(o -> R.ok().setResult(o));
    }


    private Map<String, Object> assemblyMapData(GroupFaAccount eneity) {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        List<String> list1 = new ArrayList<>();

        Map<String, String> map2 = new HashMap<>();

        if (Objects.equals(eneity.getIsZzpz(), "1")) list1.add("isZzpz");
        if (Objects.equals(eneity.getIsFilledIn(), "1")) list1.add("isFilledIn");
        if (Objects.equals(eneity.getIsZhejiu(), "1")) list1.add("isZhejiu");
        if (Objects.equals(eneity.getIsGlfl(), "1")) list1.add("isGlfl");
        if (Objects.equals(eneity.getIsCreateZhejiu(), "1")) list1.add("isCreateZhejiu");
        if (Objects.equals(eneity.getIsZjtt(), "1")) list1.add("isZjtt");
        if (Objects.equals(eneity.getIsYuanzhi(), "1")) list1.add("isYuanzhi");
        if (Objects.equals(eneity.getIsLeijizjtz(), "1")) list1.add("isLeijizjtz");
        if (Objects.equals(eneity.getIsJcanzhi(), "1")) list1.add("isJcanzhi");
        if (Objects.equals(eneity.getIsZjlb(), "1")) list1.add("isZjlb");

        map.put("basisMap", map1);
        map.put("zhiDanList", list1);
        map.put("dateMap", map2);
        map.put("obj", eneity);
        return map;
    }

    @GetMapping("findFaAccountByAccId")
    @ApiOperation(value = "根据公司唯一码查询", notes = "根据公司唯一码查询")
    public Mono findFaAccountByAccId(String accId) {
        return accountRepository.findByUniqueCodeOrderByFaAccId(accId).collectList().map(o -> R.ok().setResult(o));

    }

    @PostMapping("getFixedAssetsModel")
    @ApiOperation(value = "根据公司唯一码查询", notes = "根据公司唯一码查询")
    public Mono getFixedAssetsModel(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.error(new Exception("缺少必要参数错误!"));
        return accountRepository.findByUniqueCodeOrderByFaAccId(map.get("code").toString()).collectList().flatMap(list -> {
            Set<String> codes = new HashSet<>(list.stream().map(it -> it.getId()).collect(Collectors.toList()));
            return codes.size() == 0 ? Mono.just(R.ok()) : groupFaAccPeriodRepository.findAllByUniqueCodeInOrderByIyearDescImonthAsc(codes).collectList().flatMap(perList -> {
                Map<String, Object> yearModels = getStringObjectMap(codes, perList);
                return Mono.just(R.ok(MapUtil.of(new Object[][]{{"list", list}, {"yearModel", yearModels}})));
            });
        });
    }

    @NotNull
    private Map<String, Object> getStringObjectMap(Set<String> codes, List<GroupFaAccPeriod> perList) {
        Map<String, Object> yearModels = MapUtil.newHashMap(codes.size());
        String[] todays = DateUtil.today().split("-");
        for (String code : codes) {
            List<GroupFaAccPeriod> codeYears = perList.stream().filter(it -> it.getUniqueCode().equals(code)).collect(Collectors.toList());
            Set<String> years = new HashSet<>(codeYears.stream().map(it -> it.getIyear()).collect(Collectors.toList()));
            List<Object> elems = new ArrayList<>(years.size());
            for (String year : years) {
                List<GroupFaAccPeriod> months = codeYears.stream().filter(it -> it.getIyear().equals(year) && it.getIsSettle().equals("0")).collect(Collectors.toList());
                String theMonth = months.size() > 0 ? months.get(0).getImonth() : null;
                String date = null;
                Boolean isYearClose = false; // 整年结账
                if (null == theMonth) { // 都未结账
                    date = year + "-01-01";
                } else {//未结账
                    if (theMonth.equals("12")) isYearClose = true;
                    if (year.equals(todays[0]) && theMonth.equals(todays[1])) {//同年同月
                        date = year + "-" + theMonth + "-" + todays[2];
                    } else {
                        date = DateUtil.endOfMonth(DateUtil.parse(year + "-" + theMonth + "-" + "01").toJdkDate()).toDateStr();
                    }
                }
                elems.add(MapUtil.of(new Object[][]{{"year", year}, {"date", date},{"isYearClose", isYearClose}}));
            }
            yearModels.put(code, elems);
        }
        return yearModels;
    }
}
