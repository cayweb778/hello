package org.boozsoft.rest;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.boozsoft.domain.entity.group.*;
import org.boozsoft.domain.entity.SysAccCodeAuth;
import org.boozsoft.domain.entity.SysAccTypeAuth;
import org.boozsoft.domain.vo.AccountVo;
import org.boozsoft.domain.vo.SysAccAuthVo;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.entity.User;
import org.boozsoft.repo.entity.UserRole;
import org.boozsoft.repo.group.*;
import org.boozsoft.service.DatabaseInitService;
import org.boozsoft.service.UserServiceImplement;
import org.boozsoft.util.FtpUtil;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/account")
public class SysAccountController {

    @Autowired
    GroupSysCorpRepository groupSysCorpRepository;
    @Autowired
    GroupSysAccountRepository sysAccountRepository;
    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;
    @Autowired
    SysUserAccountRepository sysUserAccountRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserServiceImplement userServiceImplement;

    @Autowired
    SysAccAuthRepository sysAccAuthRepository;
    @Autowired
    SysPeriodRepository sysPeriodRepository;
    @Autowired
    AccStyleRepository accStyleRepository;

    @Autowired
    GroupStockAccountRepository groupStockAccountRepository;

    @Autowired
    GroupSysOrgRepository groupSysOrgRepository;

    @Autowired
    GroupSysUserRepository groupSysUserRepository;

    @Autowired
    GroupSysOrgDataaccessRepository groupSysOrgDataaccessRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return sysAccountRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByUserAccount")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByUserAccount(Long userId) {
        if (userId == null || userId.equals("")) {
            return sysAccountRepository.findAll().collectList().map(o -> R.ok().setResult(o));
        }
        return sysAccountRepository.findByUserAccount(userId).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysAccount object) {
        if (object.getUniqueCode() == null || object.getUniqueCode().equals("")) {
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag() == null || object.getFlag().equals("")) {
            object.setFlag("1");
        }
        return sysAccountRepository.save(object).map(o -> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(String id) {
        return sysAccountRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("saveUserAccount")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveUserAccount(String userId, String accountList) {
        System.out.println(userId);
        System.out.println(accountList);
        Mono mono1 = sysUserAccountRepository.deleteByUserId(Long.parseLong(userId));
        List<SysUserAccount> list = new ArrayList<SysUserAccount>();
        String[] str = accountList.split(",");

        for (int i = 0; i < str.length; i++) {
            SysUserAccount item = new SysUserAccount();
            item.setUserId(userId);
            item.setAccountId(str[i]);
            list.add(item);
        }

        Mono mono2 = Mono.just(list).flatMapMany(sysUserAccountRepository::saveAll).collectList();
        return mono1.zipWith(mono2).map(o -> R.ok().setResult(o));
    }

    public Flux<User> findAllUserByRoleId(String roleId) {
        Flux<String> userIdFlux = userRoleRepository.findByRoleId(roleId).map(UserRole::getUserId);
        return userRepository.findAllById(userIdFlux);
    }

    @GetMapping("findUserByRoleId")
    public Flux<R> findUserByRoleId(String roleId) {
        return userServiceImplement.findAllUserByRoleId(roleId).map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByAccId")
    public Mono<R> findByAccId(String accontId) {
        return sysAccountRepository.findByAccId(accontId).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    /**
     * @return reactor.core.publisher.Mono<org.springbooz.core.tool.result.R>
     * @Description 判断是否有此账套模式名称
     * @Author myh
     * @Date 17:14 2021/7/17
     * @Param [accontId]
     **/
    @PostMapping("countByAccountMode")
    public Mono<R> countByAccountMode(String accountMode) {
        return accountPeriodRepository.countByAccountMode(accountMode).map(o -> R.ok().setResult(o));
    }

    /********************* Mr. Ye *******************/
    @GetMapping("findAllInfo")
    @ApiOperation(value = "查询所以", notes = "传入code")
    public Mono<R> findAllInfo() {
        return sysAccountRepository.findAllByOrderByIdAsc().collectList().map(R::ok);
    }

    @PostMapping("findNeatOne")
    @ApiOperation(value = "获取一条干净数据", notes = "传入accId")
    public Mono<R> findNeatOne(@RequestBody Map map) {
        return sysAccountRepository.findFirstByAccIdOrderByAccStartDateDesc(map.get("accId").toString()).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("findOne")
    @ApiOperation(value = "获取一条数据", notes = "传入accId")
    public Mono<R> findOne(@RequestBody Map map) {
        return sysAccountRepository.findFirstByAccIdOrderByAccStartDateDesc(map.get("accId").toString()).map(eneity -> assemblyMapData(eneity)).map(o -> R.ok().setResult(o));
    }

    @PostMapping("modifyByOne")
    @ApiOperation(value = "获取一条数据", notes = "传入accId")
    public Mono<R> modifyByOne(@RequestBody Map map) {
        return sysAccountRepository.findFirstByAccIdOrderByAccStartDateDesc(map.get("accId").toString()).map(eneity -> modifyEntityData(eneity, map.get("modifyModel").toString())).flatMap(entity -> sysAccountRepository.save(entity)).map(o -> R.ok());
    }

    private Map<String, Object> assemblyMapData(GroupSysAccount eneity) {
        Map<String, Object> map = new HashMap<>();
        Map<String, String> map1 = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        List<String> list3 = new ArrayList<>();
        List<String> list4 = new ArrayList<>();
        Map<String, String> map2 = new HashMap<>();
        Map<String, String> map3 = new HashMap<>();
        map1.put("accStandard", eneity.getAccStandard());
        map1.put("ccodeLevel", eneity.getCcodeLevel());
        map1.put("currency", eneity.getCurrency()/*+"--"+eneity.getCurrencyCh()+"--"+eneity.getCurrencyName()*/);
        map1.put("taxClass", StringUtils.isBlank(eneity.getTaxClass()) ? "1" : eneity.getTaxClass());
        map1.put("typeClass", eneity.getVoucherTypeNum());
        map1.put("accName", eneity.getAccNameCn());
        map1.put("unitName", StrUtil.isBlank(eneity.getPrintUnitName()) ? eneity.getAccNameCn() : eneity.getPrintUnitName());

        map2.put("addAccDate", StringUtils.isBlank(eneity.getAddAccDate()) ? "1" : eneity.getAddAccDate());
        map2.put("cashhierDate", StringUtils.isBlank(eneity.getCashhierDate()) ? "1" : eneity.getCashhierDate());
        map2.put("verifyDate", StringUtils.isBlank(eneity.getVerifyDate()) ? "1" : eneity.getVerifyDate());
        map2.put("managerDate", StringUtils.isBlank(eneity.getVerifyDate()) ? "1" : eneity.getVerifyDate());
        map2.put("bookDate", StringUtils.isBlank(eneity.getBookDate()) ? "1" : eneity.getBookDate());
        map2.put("wotDate", StringUtils.isBlank(eneity.getWotDate()) ? "1" : eneity.getWotDate());

        if (Objects.equals(eneity.getIdeficit(), "1")) list1.add("ideficit");
        if (Objects.equals(eneity.getIcashFlow(), "1")) list1.add("icashFlow");
        if (Objects.equals(eneity.getIsettlement(), "1")) list1.add("isettlement");
        if (Objects.equals(eneity.getIbill(), "1")) list1.add("ibill");
        if (Objects.equals(eneity.getIar(), "1")) list1.add("iar");
        if (Objects.equals(eneity.getIap(), "1")) list1.add("iap");
        if (Objects.equals(eneity.getPrice(), "1")) list1.add("price");
        if (Objects.equals(eneity.getInum(), "1")) list1.add("inum");
        if (Objects.equals(eneity.getIsave(), "1")) list1.add("isave");
        if (Objects.equals(eneity.getIexchange(), "1")) list1.add("iexchange");
        if (Objects.equals(eneity.getIfreecorp(), "1")) list1.add("ifreecorp");

        if (Objects.equals(eneity.getIchronological(), "1")) list2.add("ichronological");
        if (Objects.equals(eneity.getIautoCode(), "1")) list2.add("iautoCode");
        if (Objects.equals(eneity.getIbreakCode(), "1")) list2.add("ibreakCode");
        if (Objects.equals(eneity.getIyearCode(), "1")) list2.add("iyearCode");

        if (Objects.equals(eneity.getIverify(), "1")) list3.add("iverify");
        if (Objects.equals(eneity.getIverifyCancel(), "1")) list3.add("iverifyCancel");
        if (Objects.equals(eneity.getIcashier(), "1")) list3.add("icashier");
        if (Objects.equals(eneity.getImanager(), "1")) list3.add("imanager");
        if (Objects.equals(eneity.getIfVerify(), "1")) list3.add("ifVerify");
        if (Objects.equals(eneity.getIotherAccvouch(), "1")) list3.add("iotherAccvouch");
        if (Objects.equals(eneity.getIotherAccvouchDel(), "1")) list3.add("iotherAccvouchDel");
        if (Objects.equals(eneity.getIcashierNo(), "1")) list3.add("icashierNo");
        if (Objects.equals(eneity.getIcdirectorNo(), "1")) list3.add("icdirectorNo");

        if (Objects.equals(eneity.getIfreeConfirm(), "1")) list4.add("ifreeConfirm");
        if (Objects.equals(eneity.getIbook(), "1")) list4.add("ibook");
        if (Objects.equals(eneity.getIcheckOut(), "1")) list4.add("icheckOut");
        if (Objects.equals(eneity.getIcash(), "1")) list4.add("icash");
        if (Objects.equals(eneity.getIprofit(), "1")) list4.add("iprofit");
        if (Objects.equals(eneity.getIcheckOutNext(), "1")) list4.add("icheckOutNext");
        if (Objects.equals(eneity.getIexchangeRateControl(), "1")) list4.add("iexchangeRateControl");
        if (Objects.equals(eneity.getProjectClassCtl(), "1")) list4.add("projectClassCtl");

        map3.put("statementFangxiang", eneity.getStatementFangxiang());
        map3.put("cashBankDayBookSort", eneity.getCashBankDayBookSort());
        map3.put("bankStatementSort", eneity.getBankStatementSort());

        map.put("basisMap", map1);
        map.put("zhiDanList", list1);
        map.put("pingZhenNumberList", list2);
        map.put("pingZhenControlList", list3);
        map.put("settingList", list4);
        map.put("dateMap", map2);

        map.put("statementMap", map3);

//        map.put("currentEntity", eneity);
        return map;
    }

    private GroupSysAccount modifyEntityData(GroupSysAccount eneity, String json) {
        JSONObject jsonObject = JSONUtil.parseObj(json);
        HashMap<String, String> basisMap = jsonObject.get("basisMap", HashMap.class);
        eneity.setAccStandard(basisMap.get("accStandard"));
        eneity.setCcodeLevel(basisMap.get("ccodeLevel"));
        eneity.setCurrency(basisMap.get("currency"));
        //eneity.setCurrencyCh("");
        //eneity.setCurrencyName(basisMap.get("currency"));
        eneity.setTaxClass(basisMap.get("taxClass"));
        eneity.setPrintUnitName(basisMap.get("unitName"));
        HashMap<String, String> dateMap = jsonObject.get("dateMap", HashMap.class);
        eneity.setAccStartDate(dateMap.get("addAccDate"));
        eneity.setCashhierDate(dateMap.get("cashhierDate"));
        eneity.setVerifyDate(dateMap.get("verifyDate"));
        eneity.setManagerDate(dateMap.get("managerDate"));
        eneity.setBookDate(dateMap.get("bookDate"));
        eneity.setWotDate(dateMap.get("wotDate"));

        HashMap<String, String> statementMap = jsonObject.get("statementMap", HashMap.class);
        eneity.setStatementFangxiang(statementMap.get("statementFangxiang"));
        eneity.setCashBankDayBookSort(statementMap.get("cashBankDayBookSort"));
        eneity.setBankStatementSort(statementMap.get("bankStatementSort"));

        List<String> zhiDanList = jsonObject.get("zhiDanList", ArrayList.class);

        if (zhiDanList.contains("ideficit")) {
            eneity.setIdeficit("1");
        } else {
            eneity.setIdeficit("0");
        }
        if (zhiDanList.contains("icashFlow")) {
            eneity.setIcashFlow("1");
        } else {
            eneity.setIcashFlow("0");
        }
        if (zhiDanList.contains("isettlement")) {
            eneity.setIsettlement("1");
        } else {
            eneity.setIsettlement("0");
        }
        if (zhiDanList.contains("ibill")) {
            eneity.setIbill("1");
        } else {
            eneity.setIbill("0");
        }
        if (zhiDanList.contains("iar")) {
            eneity.setIar("1");
        } else {
            eneity.setIar("0");
        }
        if (zhiDanList.contains("iap")) {
            eneity.setIap("1");
        } else {
            eneity.setIap("0");
        }
        if (zhiDanList.contains("price")) {
            eneity.setPrice("1");
        } else {
            eneity.setPrice("0");
        }
        if (zhiDanList.contains("inum")) {
            eneity.setInum("1");
        } else {
            eneity.setInum("0");
        }
        if (zhiDanList.contains("isave")) {
            eneity.setIsave("1");
        } else {
            eneity.setIsave("0");
        }
        if (zhiDanList.contains("iexchange")) {
            eneity.setIexchange("1");
        } else {
            eneity.setIexchange("0");
        }
        if (zhiDanList.contains("ifreecorp")) {
            eneity.setIfreecorp("1");
        } else {
            eneity.setIfreecorp("0");
        }
        List<String> pingZhenNumberList = jsonObject.get("pingZhenNumberList", ArrayList.class);
        if (pingZhenNumberList.contains("ichronological")) {
            eneity.setIchronological("1");
        } else {
            eneity.setIchronological("");
        }
        if (pingZhenNumberList.contains("iautoCode")) {
            eneity.setIautoCode("1");
        } else {
            eneity.setIautoCode("0");
        }
        if (pingZhenNumberList.contains("ibreakCode")) {
            eneity.setIbreakCode("1");
        } else {
            eneity.setIbreakCode("0");
        }
        if (pingZhenNumberList.contains("iyearCode")) {
            eneity.setIyearCode("1");
        } else {
            eneity.setIyearCode("0");
        }

        List<String> pingZhenControlList = jsonObject.get("pingZhenControlList", ArrayList.class);
        if (pingZhenControlList.contains("iverify")) {
            eneity.setIverify("1");
        } else {
            eneity.setIverify("0");
        }
        if (pingZhenControlList.contains("iverifyCancel")) {
            eneity.setIverifyCancel("1");
        } else {
            eneity.setIverifyCancel("0");
        }
        if (pingZhenControlList.contains("icashier")) {
            eneity.setIcashier("1");
        } else {
            eneity.setIcashier("0");
        }
        if (pingZhenControlList.contains("imanager")) {
            eneity.setImanager("1");
        } else {
            eneity.setImanager("0");
        }
        if (pingZhenControlList.contains("ifVerify")) {
            eneity.setIfVerify("1");
        } else {
            eneity.setIfVerify("0");
        }
        if (pingZhenControlList.contains("iotherAccvouch")) {
            eneity.setIotherAccvouch("1");
        } else {
            eneity.setIotherAccvouch("0");
        }
        if (pingZhenControlList.contains("iotherAccvouchDel")) {
            eneity.setIotherAccvouchDel("1");
        } else {
            eneity.setIotherAccvouchDel("0");
        }
        if (pingZhenControlList.contains("icashierNo")) {
            eneity.setIcashierNo("1");
        } else {
            eneity.setIcashierNo("0");
        }
        if (pingZhenControlList.contains("icdirectorNo")) {
            eneity.setIcdirectorNo("1");
        } else {
            eneity.setIcdirectorNo("0");
        }

        List<String> settingList = jsonObject.get("settingList", ArrayList.class);
        if (settingList.contains("ifreeConfirm")) {
            eneity.setIfreeConfirm("1");
        } else {
            eneity.setIfreeConfirm("0");
        }
        if (settingList.contains("ibook")) {
            eneity.setIbook("1");
        } else {
            eneity.setIbook("0");
        }
        if (settingList.contains("icheckOut")) {
            eneity.setIcheckOut("1");
        } else {
            eneity.setIcheckOut("0");
        }
        if (settingList.contains("icash")) {
            eneity.setIcash("1");
        } else {
            eneity.setIcash("0");
        }
        if (settingList.contains("iprofit")) {
            eneity.setIprofit("1");
        } else {
            eneity.setIprofit("0");
        }
        if (settingList.contains("icheckOutNext")) {
            eneity.setIcheckOutNext("1");
        } else {
            eneity.setIcheckOutNext("0");
        }
        if (settingList.contains("iexchangeRateControl")) {
            eneity.setIexchangeRateControl("1");
        } else {
            eneity.setIexchangeRateControl("0");
        }
        if (settingList.contains("projectClassCtl")) {
            eneity.setProjectClassCtl("1");
        } else {
            eneity.setProjectClassCtl("0");
        }
        return eneity;
    }

    @Autowired
    SysAccAuthRepository accAuthRepository;

    @Autowired
    SysAccCodeAuthRepository accCodeAuthRepository;

    @Autowired
    SysAccTypeAuthRepository accTypeAuthRepository;

    @PostMapping("modifyAuthor")
    @ApiOperation(value = "更新用户账套权限 旧业务作废", notes = "传入用户标识")
    @Transactional
    public Mono<R> modifyAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just("请求参数异常").map(m -> R.error());
        String thisAccId = map.get("accId").toString();
        String thisYear = map.get("year").toString();
        Set<String> supervisors = new HashSet<>(((ArrayList<String>) map.get("supervisors")));
        Set<String> authorizes = new HashSet<>(((ArrayList<String>) map.get("authorizes")));
        return accAuthRepository.findAllByAccIdAndIyear(thisAccId, thisYear).collectList().flatMap(list -> {// 已存在删除
            //List<GroupSysAccAuth> dels = list.stream().filter(item -> !supervisors.contains(item.getUserNum()) && !authorizes.contains(item.getUserNum())).collect(Collectors.toList());
            return list.size() > 0 ? accAuthRepository.deleteAll(list).thenReturn("") : Mono.just("");
        }).flatMap(o -> {
            List<GroupSysAccAuth> saveList = new ArrayList<>();
            if (supervisors.size() > 0) {
                for (String supervisor : supervisors) {
                    saveList.add(new GroupSysAccAuth().setUserNum(supervisor).setAccId(thisAccId).setIyear(thisYear).setCcodeAll("1").setAccvocherType("1").setSupervisor("1"));
                }
            }
            if (authorizes.size() > 0) {
                for (String authorize : authorizes) {
                    saveList.add(new GroupSysAccAuth().setUserNum(authorize).setAccId(thisAccId).setIyear(thisYear).setCcodeAll("1").setAccvocherType("1"));
                }
            }
            return saveList.size() > 0 ? accAuthRepository.saveAll(saveList).collectList().map(R::ok) : Mono.just("").map(R::ok);
        });
    }

    /**
     * 获取当前登录用户的账套权限List
     */
    @PostMapping("findAccAuths")
    @ApiOperation(value = "获取登录用户账套权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> findAccAuths(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just("请求参数异常").map(m -> R.error());
        return accAuthRepository.findAllByUserNumOrderByIyearDescAccIdAsc(map.get("userId").toString()).flatMap(entity -> {
            SysAccAuthVo vo = new SysAccAuthVo();
            BeanUtil.copyProperties(entity, vo);
            if ((null != vo.getSupervisor() && vo.getSupervisor().equals("1")) || (vo.getCcodeAll().equals("1") && vo.getAccvocherType().equals("1"))) {
                return Mono.just(vo);
            } else {
                return Mono.just(vo).flatMap(item -> {
                    Mono<SysAccAuthVo> typeMono = accTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(item.getUserNum(), item.getAccId(), item.getIyear()).collectList().map(list2 -> {
                        Set<String> typeSets = new HashSet<>();
                        for (SysAccTypeAuth typeEntity : list2) {
                            typeSets.add(typeEntity.getAccvocherType());
                        }
                        item.setVoucherTypeJson(typeSets.size() > 0 ? JSONUtil.toJsonStr(typeSets) : null);
                        return item;
                    });
                    Mono<Object> codeMono = accCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(item.getUserNum(), item.getAccId(), item.getIyear()).collectList().map(list -> {
                        if (list.size() == 0) return Mono.just(item);
                        Set<String> codeSets = new HashSet<>();
                        for (SysAccCodeAuth codeEntity : list) {
                            codeSets.add(codeEntity.getCodeNum());
                        }
                        item.setCodeListJson(codeSets.size() > 0 ? JSONUtil.toJsonStr(codeSets) : null);
                        return item;
                    });
                    if (!vo.getAccvocherType().equals("1") && vo.getCcodeAll().equals("1")) {
                        return typeMono;
                    } else if (vo.getAccvocherType().equals("1") && !vo.getCcodeAll().equals("1")) {
                        return codeMono;
                    } else {
                        return codeMono.flatMap(i -> typeMono);
                    }
                });
            }
        }).collectList().cache().map(R::ok);
    }

    @PostMapping("resetDefaultAccountOld")
    @ApiOperation(value = "重置默认登入账套", notes = "传入用户标识")
    @Transactional
    public Mono<R> resetDefaultAccountOld(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just("请求参数异常").map(m -> R.error());
        return accAuthRepository.findAllByUserNumAndDefaultLogin(map.get("userId").toString(), "1").map(entiry -> {
            entiry.setDefaultLogin(null);
            return entiry;
        }).collectList().flatMap(list -> accAuthRepository.saveAll(list).collectList()).flatMap(list -> accAuthRepository.findFirstByAccIdAndUserNumOrderByIyearDesc(map.get("accId").toString(), map.get("userId").toString()).map(entity -> {
            entity.setDefaultLogin("1");
            return entity;
        }).flatMap(ent -> accAuthRepository.save(ent)).map(R::ok));
    }

    @PostMapping("resetDefaultAccount")
    @ApiOperation(value = "重置默认登入账套", notes = "传入用户标识")
    @Transactional
    public Mono<R> resetDefaultAccount(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just("请求参数异常").map(m -> R.error());
        return groupUserOperatAuthZtRepository.findAllByUserUniqueCodeAndDefaultLogin(map.get("userId").toString(), "1").map(entiry -> {
            entiry.setDefaultLogin(null);
            return entiry;
        }).collectList().flatMap(list -> groupUserOperatAuthZtRepository.saveAll(list).collectList()).flatMap(list -> groupUserOperatAuthZtRepository.findAllByUserUniqueCodeAndZtUniqueCode(map.get("userId").toString(),map.get("coCode").toString()).map(entity -> {
            entity.setDefaultLogin("1");
            return entity;
        }).collectList().flatMap(ent -> groupUserOperatAuthZtRepository.saveAll(ent).collectList()).map(R::ok));
    }

    @PostMapping("findAuthors")
    @ApiOperation(value = "获取登录用户账套权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> findAuthors(@RequestBody Map map) {
        if (map.keySet().size() != 2) return Mono.just("请求参数异常").map(m -> R.error());
        return accAuthRepository.findAllByAccIdAndIyear(map.get("accId").toString(), map.get("year").toString()).flatMap(entity -> {
            SysAccAuthVo vo = new SysAccAuthVo();
            BeanUtil.copyProperties(entity, vo);
            if (vo.getCcodeAll().equals("1") && vo.getAccvocherType().equals("1")) {
                return Mono.just(vo);
            } else {
                return Mono.just(vo).flatMap(item -> accCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(item.getUserNum(), item.getAccId(), item.getIyear()).collectList().flatMap(list -> {
                    Set<String> codeSets = new HashSet<>();
                    for (SysAccCodeAuth codeEntity : list) {
                        codeSets.add(codeEntity.getCodeNum());
                    }
                    item.setCodeListJson(codeSets.size() > 0 ? JSONUtil.toJsonStr(codeSets) : null);
                    return accTypeAuthRepository.findAllByUserNumAndAccIdAndIyear(item.getUserNum(), item.getAccId(), item.getIyear()).collectList().map(list2 -> {
                        Set<String> typeSets = new HashSet<>();
                        for (SysAccTypeAuth typeEntity : list2) {
                            typeSets.add(typeEntity.getAccvocherType());
                        }
                        item.setVoucherTypeJson(typeSets.size() > 0 ? JSONUtil.toJsonStr(typeSets) : null);
                        return item;
                    });
                }));
            }
        }).collectList().map(R::ok);
    }

    /********************* Mr. Ye *******************/


    /*********new 公司信息*********/
    @Autowired
    DatabaseInitService databaseInitService;

    @GetMapping("findNew")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass1(Pageable pageable, String accGroup) {
        Flux<GroupSysAccount> fun = null;
        if (null == accGroup || StrUtil.isBlank(accGroup) || accGroup.equals("0")) {
            fun = sysAccountRepository.findAll().filter(it->it.getIsDel().equals("0"));
        } else {
            fun = sysAccountRepository.findAllByAccGroup(accGroup).filter(it->it.getIsDel().equals("0"));
        }
        return fun.collectList().flatMap(item -> sysAccountRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }


    @GetMapping("findNew/createList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> createlist(Pageable pageable,String userId) {
        Flux<GroupSysAccount> flux = userId == null ? sysAccountRepository.findAll() : groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(userId).map(it -> it.getOrgUniqueCode()).collectList().flatMap(code -> sysAccountRepository.findAllByAccGroupIn(code).collectList()).flatMapMany(a -> Flux.fromIterable(a));
        return flux.filter(it->it.getIsDel().equals("0")).filter(item -> StrUtil.isNotBlank(item.getAccStandard()) && StrUtil.isNotBlank(item.getStartPeriod()))/*.flatMap(item -> sysAccAuthRepository.findAllByAccId(item.getAccId()).map(dbAuthEneity -> dbAuthEneity.getUserNum()).collectList().map(list -> {
            if (list.size() > 0) item.setBeiyong3(JSON.toJSONString(list));
            return item;
        }))*/.collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysAccount::getCoCode)).collect(Collectors.toList())).flatMap(item -> sysAccountRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }

    @GetMapping("findNew/listNo")
    @ApiOperation(value = "查询可用的列表", notes = "传入code")
    public Mono<R> listNo() {
        return sysAccountRepository.findAllByFlagOrderByCoCodeAsc("1").filter(it->it.getIsDel().equals("0")).filter(item -> item.getIndependent().equals("0")).collectList().map(R::ok);
    }

    @GetMapping("findNew/available")
    @ApiOperation(value = "查询可用的列表", notes = "传入code")
    public Mono<R> available() {
        return sysAccountRepository.findAllByFlagOrderByCoCodeAsc("1").filter(it->it.getIsDel().equals("0")).collectList().cache().map(R::ok);
    }

    @GetMapping("findNew/availableDel")
    @ApiOperation(value = "查询可用的列表", notes = "传入code")
    public Mono<R> availableDel() {
        return sysAccountRepository.findAllByFlagOrderByCoCodeAsc("1").filter(it->it.getIsDel().equals("1")).collectList().cache().map(R::ok);
    }

    @PostMapping("resetCode")
    public Mono<R> resetCode(@RequestBody GroupSysAccount entity) {
        return databaseInitService.resetKemuCode(entity).map(o -> R.ok());
    }
 /*   @GetMapping("findNew/list")
    @ApiOperation(value = "查询账套列表", notes = "传入code")
    public Mono<R> all(Pageable pageable, String accGroup, String queryMark, String pageMark) {
        Flux<GroupSysAccount> fun = null;
        if (null == accGroup || StrUtil.isBlank(accGroup) || accGroup.equals("-1")) {
            fun = sysAccountRepository.findAll();
        } else if (accGroup.equals("0")) {
            fun = sysAccountRepository.findAllByAccGroupIsNull();
        } else {
            fun = sysAccountRepository.findAllByAccGroup(accGroup);
        }
        return fun.filter(item -> { // 过滤
            if (item.getFlag().equals("-1")) return false;
            if (null != queryMark && !queryMark.equals("all") && ((queryMark.equals("1") && item.getFlag().equals("0")) || (queryMark.equals("0") && item.getFlag().equals("1"))))
                return false;
            if (null != pageMark && !pageMark.equals("1") && StrUtil.isBlank(item.getAccGroup())) return false;
            return true;
        }).flatMap(item -> {
            if (StrUtil.isNotBlank(item.getBeiyong1())) {
                item.setBeiyong2(item.getBeiyong1());
                item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group", item.getBeiyong1()));
            }
            return Mono.just(item);
        }).flatMap(item -> sysAccAuthRepository.findAllByAccId(item.getAccId()).map(dbAuthEneity -> dbAuthEneity.getUserNum()).collectList().map(list -> {
            if (list.size() > 0) item.setBeiyong3(JSON.toJSONString(list));
            return item;
        })).collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysAccount::getCoCode)).collect(Collectors.toList())).flatMap(item -> sysAccountRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }*/

    /************************ 公司 *************************/
    @GetMapping("findNew/corpAvailable")
    @ApiOperation(value = "查询所以公司", notes = "传入code")
    public Mono<R> corpAvailable() {
        return groupSysCorpRepository.findAll().filter(it->it.getIsDel().equals("0")).collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysCorp::getCoCode)).collect(Collectors.toList())).cache().map(R::ok);
    }
    @GetMapping("findNew/corpDel")
    @ApiOperation(value = "查询所以公司", notes = "传入code")
    public Mono<R> corpDel() {
        return groupSysCorpRepository.findAll().filter(it->it.getIsDel().equals("1")).collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysCorp::getCoCode)).collect(Collectors.toList())).cache().map(R::ok);
    }
    @GetMapping("findNew/list")
    @ApiOperation(value = "查询公司列表", notes = "传入code")
    public Mono<R> all(Pageable pageable, String accGroup, String queryMark, String pageMark,String userId) {
        Flux<GroupSysCorp> fun = null;
        if (null == accGroup || StrUtil.isBlank(accGroup) || accGroup.equals("-1")) {
            fun = groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(userId).map(it->it.getOrgUniqueCode()).collectList().flatMap(
                    codes-> (userId == null?groupSysCorpRepository.findAll():
                            groupSysCorpRepository.findAllByAccGroupIn(codes)).filter(it->it.getIsDel().equals("0")).collectList()
            ).flatMapMany(a->Flux.fromIterable(a)).filter(it->it.getIsDel().equals("0"));
        } else if (accGroup.equals("0")) {
            fun = groupSysCorpRepository.findAllByAccGroupIsNull().filter(it->it.getIsDel().equals("0"));
        } else {
            fun = groupSysCorpRepository.findAllByAccGroup(accGroup).filter(it->it.getIsDel().equals("0"));
        }
        return fun.filter(item -> { // 过滤
            if (null != pageMark && !pageMark.equals("1") && StrUtil.isBlank(item.getAccGroup())) return false;
            return true;
        })/*.flatMap(item -> {
            if (StrUtil.isNotBlank(item.getBeiyong1())) {
                item.setBeiyong2(item.getBeiyong1());
                item.setBeiyong1(FtpUtil.ImgToBase64("/ncpz/group", item.getBeiyong1()));
            }
            return Mono.just(item);
        })*/.collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupSysCorp::getCoCode)).collect(Collectors.toList())).flatMap(item -> groupSysCorpRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }




    @GetMapping("findByCode")
    @ApiOperation(value = "查询公司编码", notes = "传入code")
    public Mono<R> findByCode(String id, String coCode) {
        return groupSysCorpRepository.findAllByCoCode(coCode.trim()).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询公司简称", notes = "传入code")
    public Mono<R> findByName(String id, String accNameCn) {
        return groupSysCorpRepository.findAllByAccNameCn(accNameCn.trim()).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @PostMapping("saveNew")
    @ApiOperation(value = "新增或修改公司", notes = "传入code")
    @Transactional
    public Mono<R> saveNew(@RequestBody GroupSysCorp entity) {
        AtomicReference<Boolean> totalAR = new AtomicReference(null == entity.getId());
//        List<String> supers = StrUtil.isNotBlank(entity.getBeiyong3()) ? JSON.parseArray(entity.getBeiyong3(), String.class) : null;
        entity.setBeiyong3(null);
        return Mono.just(totalAR.get())/*.flatMap(finalIsAdd -> {
            if (finalIsAdd) {
                entity.setFlag("1");
                String letter = PinyinUtil.getFirstLetter(entity.getAccName().length() > 8 ? entity.getAccName().substring(0, 8) : entity.getAccName(), "");
                return accountPeriodRepository.findAllByAccountIdLikeOrderByAccountIdDesc("%" + letter + "-%").collectList().flatMap(list -> {
                    String defulat = "001";
                    if (list.size() > 0) {
                        int num = (Integer.parseInt(list.get(0).getAccId().split("-")[1]) + 1);
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
                    entity.setAccId(letter + "-" + num);
                    return entity;
                });
            }
            return Mono.just(entity);
        })*/.flatMap(item -> groupSysCorpRepository.save(entity).flatMap(dbEntity -> {
                          /*  if (totalAR.get()) {
                                return databaseInitService.insertAccountsData(dbEntity, supers).flatMap(num -> Mono.just(R.ok().setResult(dbEntity)));
                            }*/
            return Mono.just(R.ok().setResult(dbEntity));
        }));
    }


    @DeleteMapping("deleteNew")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteNew(@RequestBody GroupSysCorp entity) {
        return groupSysCorpRepository.findById(entity.getId()).flatMap(dbEntity -> {
            Mono<Long> longOne = sysAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longTwo = groupFaAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longThree = groupSysPlantRepository.countAllByCorpUniqueCode(dbEntity.getId());
            Mono<Long> longFour = groupStockAccountRepository.countAllByUniqueCode(dbEntity.getId());
            return Mono.zip(longOne, longTwo, longThree, longFour).flatMap(tSize -> {
                if (tSize.getT1() > 0 || tSize.getT2() > 0 || tSize.getT3() > 0 || tSize.getT4() > 0) {
                    return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", tSize.getT2() > 0 ? "02" : tSize.getT3() > 0 ? "03" : tSize.getT4() > 0 ? "04" : "01")));
                } else {
                   /* Mono<Boolean> delImgMono = Mono.just(StrUtil.isBlank(dbEntity.getBeiyong1())).flatMap(flag -> {
                        if (!flag) return Mono.just(FtpUtil.delFile("ncpz/group", dbEntity.getBeiyong2()));
                        return Mono.just(true);
                    });
                    Mono<Void> delEntityMono = groupSysCorpRepository.deleteById(dbEntity.getId());
                    return Mono.zip(delImgMono, delEntityMono).then(Mono.just(R.ok()));*/
                   return groupSysCorpRepository.findById(dbEntity.getId()).map(e->{e.setIsDel("1");e.setDelName(entity.getDelName());e.setDelDate(DateUtil.now());return e;}).flatMap(e->groupSysCorpRepository.save(e).map(o->R.ok()));
                }
            });
        });
    }

    @PostMapping("reductionCorp")
    public Mono<R> reductionCorp(@RequestBody GroupSysCorp entity) {
        return groupSysCorpRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> groupSysCorpRepository.save(e).map(o -> R.ok()));
    }

    @DeleteMapping("deleteNewTrue")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteNewTrue(@RequestBody GroupSysCorp entity) {
        return groupSysCorpRepository.findById(entity.getId()).flatMap(dbEntity -> {
            Mono<Long> longOne = sysAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longTwo = groupFaAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longThree = groupSysPlantRepository.countAllByCorpUniqueCode(dbEntity.getId());
            Mono<Long> longFour = groupStockAccountRepository.countAllByUniqueCode(dbEntity.getId());
            return Mono.zip(longOne, longTwo, longThree, longFour).flatMap(tSize -> {
                if (tSize.getT1() > 0 || tSize.getT2() > 0 || tSize.getT3() > 0 || tSize.getT4() > 0) {
                    return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isUsed", true, "type", tSize.getT2() > 0 ? "02" : tSize.getT3() > 0 ? "03" : tSize.getT4() > 0 ? "04" : "01")));
                } else {
                    Mono<Boolean> delImgMono = Mono.just(StrUtil.isBlank(dbEntity.getBeiyong1())).flatMap(flag -> {
                        if (!flag) return Mono.just(FtpUtil.delFile("ncpz/group", dbEntity.getBeiyong2()));
                        return Mono.just(true);
                    });
                    Mono<Void> delEntityMono = groupSysCorpRepository.deleteById(dbEntity.getId());
                    return Mono.zip(delImgMono, delEntityMono).then(Mono.just(R.ok()));
                }
            });
        });
    }

    @PostMapping("checkUsed")
    @Transactional
    @ApiOperation(value = "指定公司是否已使用", notes = "传入code")
    public Mono<R> checkUsed(@RequestBody GroupSysCorp entity) {
        return groupSysCorpRepository.findById(entity.getId()).flatMap(dbEntity -> {
            Mono<Long> longOne = sysAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longTwo = groupFaAccountRepository.countAllByUniqueCode(dbEntity.getId());
            Mono<Long> longThree = groupSysPlantRepository.countAllByCorpUniqueCode(dbEntity.getId());
            Mono<Long> longFour = groupStockAccountRepository.countAllByUniqueCode(dbEntity.getId());
            return Mono.zip(longOne, longTwo, longThree, longFour).flatMap(tSize -> {
                if (tSize.getT1() > 0 || tSize.getT2() > 0 || tSize.getT3() > 0 || tSize.getT4() > 0) {
                    return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isUsed", true, "type", tSize.getT2() > 0 ? "02" : tSize.getT3() > 0 ? "03" : tSize.getT4() > 0 ? "04" : "01")));
                } else {
                    return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isUsed", false, "type", "")));
                }
            });
        });
    }

    /************************ 公司 *************************/
    @GetMapping("findByCode2")
    @ApiOperation(value = "查询公司编码", notes = "传入code")
    public Mono<R> findByCode2(String id, String coCode) {
        return sysAccountRepository.findAllByCoCode(coCode.trim()).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("findLastValByCoCode")
    @ApiOperation(value = "查询公司编码", notes = "传入code")
    public Mono<R> findLastValByCoCode(String id, String coCode) {
        return sysAccountRepository.findAllByUniqueCodeAndCoCodeLike(id, coCode.trim() + "%").collectList().map(list -> {
            if (list.size() == 0) return R.ok().setResult("0");
            String deVars = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
            List<String> vars = list.stream().filter(it -> StrUtil.isNotBlank(it.getCoCode())).map(it -> it.getCoCode().substring(2, 3)).collect(Collectors.toList());
            String v = Arrays.stream(deVars.split("")).filter(s -> !vars.contains(s)).collect(Collectors.toList()).get(0);
            return R.ok().setResult(v);
        });
    }

    @PostMapping("saveUnitTwo")
    @ApiOperation(value = "新增账套", notes = "传入code")
    public Mono<R> saveUnitTwo(@RequestBody GroupSysAccount entity) {
        if (StrUtil.isBlank(entity.getId())) {
            return Mono.just(R.error());
        }
        List<String> supers = StrUtil.isNotBlank(entity.getBeiyong3()) ? JSON.parseArray(entity.getBeiyong3(), String.class) : null;
        return groupSysCorpRepository.findById(entity.getId()).flatMap(dbEntry -> {
            entity.setId(null);
            entity.setFlag("1");
            entity.setUniqueCode(dbEntry.getId());
            entity.setAccName(dbEntry.getAccName());
            entity.setAccNameCn(dbEntry.getAccNameCn());
            entity.setCorpCode(dbEntry.getCorpCode());
            entity.setUniqueCodeZone(dbEntry.getUniqueCodeZone());
            entity.setIndustryclassCode(dbEntry.getIndustryclassCode());
            entity.setCountryId(dbEntry.getCountryId());
            entity.setTaxCode(dbEntry.getTaxCode());
            entity.setAddress(dbEntry.getAddress());
            entity.setWebsite(dbEntry.getWebsite());
            entity.setContacts(dbEntry.getContacts());
            entity.setTelephone(dbEntry.getTelephone());
            entity.setRemarks(dbEntry.getRemarks());
            entity.setInvoiceLookUp(dbEntry.getInvoiceLookUp());
            entity.setInvoiceIdentifier(dbEntry.getInvoiceIdentifier());
            entity.setInvoiceAddressPhone(dbEntry.getInvoiceAddressPhone());
            entity.setInvoiceBanks(dbEntry.getInvoiceBanks());
            String letter = PinyinUtil.getFirstLetter(dbEntry.getAccName().length() > 8 ? dbEntry.getAccName().substring(0, 8) : entity.getAccName(), "");
            return accountPeriodRepository.findAllByAccountIdLikeOrderByAccountIdDesc("%" + letter + "-%").collectList().flatMap(list -> {
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
                entity.setAccId(letter + "-" + num);
                return entity;
            });
        }).flatMap(it -> sysAccountRepository.save(it).flatMap(dbEntity -> databaseInitService.insertAccountsData(dbEntity, supers).flatMap(num -> Mono.just(R.ok().setResult(dbEntity)))));
    }


    @PostMapping("copyUnit")
    @ApiOperation(value = "复制账套", notes = "传入code")
    public Mono<R> copyUnit(@RequestBody GroupSysAccount entity) {
        if (StrUtil.isBlank(entity.getId())) {
            return Mono.just(R.error());
        }
        List<String> supers = StrUtil.isNotBlank(entity.getBeiyong3()) ? JSON.parseArray(entity.getBeiyong3(), String.class) : null;
        String letter = PinyinUtil.getFirstLetter(entity.getAccName().length() > 8 ? entity.getAccName().substring(0, 8) : entity.getAccName(), "");
        return accountPeriodRepository.findAllByAccountIdLikeOrderByAccountIdDesc("%" + letter + "-%").collectList().flatMap(list -> {
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
        }).flatMap(num -> {
            entity.setAccId(letter + "-" + num);
            return sysAccountRepository.findById(entity.getId()).flatMap(dbIt -> {
                GroupSysAccount it = new GroupSysAccount();
                BeanUtil.copyProperties(dbIt, it);
                it.setId(null);
                it.setYearStartDate(entity.getYearStartDate());
                it.setYearEndDate(entity.getYearEndDate());
                it.setStartPeriod(entity.getStartPeriod());
                it.setCoCode(entity.getCoCode());
                it.setAccId(entity.getAccId());
                it.setAccNameFull(entity.getAccNameFull());
                it.setCcodeLevel(entity.getCcodeLevel());
                return sysAccountRepository.save(it).flatMap(dbEntity -> databaseInitService.copyAccountsData(dbEntity, dbIt, supers).flatMap(num1 -> Mono.just(R.ok().setResult(dbEntity))));
            });
        });

    }


    @DeleteMapping("deleteNew2")
    @Transactional
    @ApiOperation(value = "删除账套", notes = "传入code")
    public Mono<R> deleteNew2(@RequestBody GroupSysAccount entity) {
        return sysAccountRepository.findById(entity.getId()).map(e->{e.setIsDel("1");e.setDelName(entity.getDelName());e.setDelDate(DateUtil.now());return e;}).flatMap(e->sysAccountRepository.save(e).map(o->R.ok()));

    }

    @DeleteMapping("deleteNew2/true")
    @Transactional
    @ApiOperation(value = "删除账套", notes = "传入code")
    public Mono<R> deleteNew2True(@RequestBody GroupSysAccount entity) {
        return sysAccountRepository.findById(entity.getId()).flatMap(dbEntity -> databaseInitService.removeAccountsData(dbEntity).flatMap(v -> sysAccountRepository.delete(dbEntity).thenReturn(R.ok())));
    }

    @PostMapping("reductionZz")
    public Mono<R> reductionZz(@RequestBody GroupSysAccount entity) {
        return sysAccountRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> sysAccountRepository.save(e).map(o -> R.ok()));
    }

    @PostMapping("findNew/status-bus")
    @ApiOperation(value = "业务删除账套", notes = "传入code")
    public Mono<R> statusNew2(@RequestBody GroupSysAccount entity) {
        return sysAccountRepository.countAllByCorpCodeAndFlagNotOrUniqueCodeAndFlagNot(entity.getId(), "-1", entity.getId(), "-1").flatMap(size -> {
            if (size > 0) {
                return Mono.just(R.ok().setResult(CollectOfUtils.mapof("isOk", false, "type", "D01")));
            } else {
                return sysAccountRepository.findById(entity.getId()).flatMap(dbEneity -> {
                    dbEneity.setFlag(entity.getFlag());
                    return sysAccountRepository.save(dbEneity).flatMap(o -> accAuthRepository.findAllByAccId(dbEneity.getAccId()).collectList().flatMap(list -> list.size() > 0 ? accAuthRepository.deleteAll(list).thenReturn(R.ok()) : Mono.just(R.ok())));
                });
            }
        });
    }

    @GetMapping("findDataBase")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDataBase(String accId, String year) {
        return accountPeriodRepository.findByAccountIdAndAccountYear(accId, year).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @GetMapping("findDataBase1")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDataBase1(String accId, String year) {
        return accountPeriodRepository.findByCoCodeAndAccountYear(accId, year).map(o -> R.ok().setResult(o)).defaultIfEmpty(R.ok().setResult(""));
    }

    @GetMapping("findDataBase2")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDataBase2(String accId, String year) {
        return accountPeriodRepository.countByAccountIdAndAccountYear(accId, year).flatMap(sum -> {
            return sum == 0 ? Mono.just(CollectOfUtils.mapof("sum", sum, "list", "")) : accountPeriodRepository.findByAccountIdAndAccountYear(accId, year).flatMap(o -> Mono.just(CollectOfUtils.mapof("sum", sum, "list", o)));
        }).map(o -> R.ok().setResult(o));
    }

    /**
     * 查询所有属于集团账套的
     *
     * @return
     */
    @GetMapping("findByAllSysAccPeriodGroupDataBase")
    public Mono<R> findByAllSysAccPeriod() {
        return accountPeriodRepository.findByAllSysAccPeriodGroupDataBase().collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("findNew/status")
    @ApiOperation(value = "改变指定公司的状态", notes = "传入code")
    public Mono<R> statusNew(@RequestBody GroupSysAccount entity) {
        return sysAccountRepository.findById(entity.getId()).flatMap(dbEneity -> {
            dbEneity.setFlag(entity.getFlag());
            return sysAccountRepository.save(dbEneity).map(o -> R.ok(o));
        });
    }

    @PostMapping("findByAccIdIyearGroupBy")
    public Mono<R> findByAccIdIyearGroupBy(String accId) {
        return sysPeriodRepository.findByAccIdIyearGroupBy(accId).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 查询凭证类别权限
     *
     * @return
     */
    @PostMapping("company_findByAccTypeAuth")
    public Mono<R> company_findByAccTypeAuth(String userId, String iyear) {
        return accTypeAuthRepository.findAllByUserNumAndIyear(userId, iyear).collectList().map(o -> R.ok().setResult(o));
    }

    /*********************************固定资产账************************************/
    @Autowired
    private GroupFaAccountRepository groupFaAccountRepository;

    @Autowired
    private GroupSysPlantRepository groupSysPlantRepository;

    @Autowired
    private GroupFaAccPeriodRepository groupFaAccPeriodRepository;

    @Autowired
    private GroupFaAccAuthRepository groupFaAccAuthRepository;

    @Autowired
    private GroupFaManageClassRepository groupFaManageClassRepository;

    @GetMapping("assets/list")
    @ApiOperation(value = "查询固定资产账列表", notes = "传入code")
    public Mono<R> assetsList(Pageable pageable,String userId) {
        Flux<GroupFaAccount> flux = userId == null ? groupFaAccountRepository.findAll() : groupSysOrgDataaccessRepository.findAllByUniqueCodeOrderById(userId).map(it -> it.getOrgUniqueCode()).collectList().flatMap(codes -> groupSysCorpRepository.findAllByAccGroupIn(codes).filter(it -> it.getIsDel().equals("0")).map(it -> it.getId()).collectList().flatMap(
                ids -> groupFaAccountRepository.findAllByUniqueCodeIn(ids).collectList()
        )).flatMapMany(i -> Flux.fromIterable(i));
        return flux.collectList().map(list -> list.stream().filter(it->it.getIsDel().equals("0")).sorted(Comparator.comparing(GroupFaAccount::getUniqueCode)).collect(Collectors.toList())).flatMap(item -> groupFaAccountRepository.countAllBy().map(total -> R.page(item, pageable, total)));
    }


    @PostMapping("assets/save")
    @ApiOperation(value = "新增或修改固定资产账", notes = "传入code")
    public Mono<R> saveAssets(@RequestBody GroupFaAccount entity) {
        boolean b = null == entity.getId();
        String letter = PinyinUtil.getFirstLetter(entity.getFaAccId().length() > 8 ? entity.getFaAccId().substring(0, 8) : entity.getFaAccId(), "");
        if (b) entity.setFaAccId(letter + "-001");
        Mono<GroupFaAccount> entityMono = Mono.just(entity).flatMap(it -> !entity.getCoCode().equals(entity.getAssociateCoCode()) ? accountPeriodRepository.findAllByAccountIdLikeOrderByAccountIdDesc("%" + letter + "-%").collectList().flatMap(list -> {
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
            it.setFaAccId(letter + "-" + num);
            return it;
        }) : Mono.just(it));
        return b ? (entityMono.flatMap(it -> groupFaAccountRepository.save(entity))).flatMap(dbEntity -> databaseInitService.insertFaAccountsData(dbEntity).flatMap(num1 -> Mono.just(R.ok().setResult(dbEntity)))) : groupFaAccountRepository.findById(entity.getId()).flatMap(e -> {
            entity.setFaAccId(e.getFaAccId());
            return groupFaAccountRepository.save(entity).map(dbEntity -> R.ok().setResult(dbEntity));
        });
    }


    @GetMapping("assets/findByCode")
    @ApiOperation(value = "查询编码重复", notes = "传入code")
    public Mono<R> findByCodeAssets(String id, String coCode) {
        return groupFaAccountRepository.findAllByCoCode(coCode).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("assets/findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByNameAssets(String id, String accNameCn, String accId) {
        return groupFaAccountRepository.findAllByFaAccNameAndUniqueCode(accNameCn.trim(), accId).collectList().map(list -> {
            if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                return R.ok().setResult(0);
            } else {
                return R.ok().setResult(list.size());
            }
        });
    }

    @GetMapping("assets/used")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> used() {
        return groupFaAccountRepository.findAll().filter(it->it.getIsDel().equals("0")).map(it -> it.getUniqueCode()).collectList().flatMapMany(i -> Flux.fromIterable(i)).distinct().collectList().map(o -> R.ok(o));
    }

    @GetMapping("assets/codes")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> codes(String code) {
        return groupFaAccountRepository.findAll().filter(it->it.getIsDel().equals("0")).filter(it -> it.getUniqueCode().equals(code)).map(it -> it.getId() + "==" + it.getFaAccId() + "==" + it.getFaAccName()).collectList().flatMapMany(i -> Flux.fromIterable(i)).distinct().collectList().map(o -> R.ok(o));
    }

    @DeleteMapping("assets/del")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteAssets(@RequestBody GroupFaAccount entity) {
        // 删除账套
        return groupFaAccountRepository.findById(entity.getId()).map(e->{e.setIsDel("1");e.setDelName(entity.getDelName());e.setDelDate(DateUtil.now());return e;}).flatMap(e->groupFaAccountRepository.save(e).map(o->R.ok()));
    }

    @DeleteMapping("assets/delTrue")
    @Transactional
    @ApiOperation(value = "删除公司", notes = "传入code")
    public Mono<R> deleteAssetsTrue(@RequestBody GroupFaAccount entity) {
        // 删除账套
        return groupFaAccountRepository.findById(entity.getId()).flatMap(dbEntity -> databaseInitService.removeFaAccountsData(dbEntity).flatMap(integer -> groupFaAccountRepository.deleteById(dbEntity.getId())).then(Mono.just(R.ok())));
    }
    @GetMapping("assets/allDel")
    @ApiOperation(value = "查询所以公司", notes = "传入code")
    public Mono<R> faAllDel() {
        return groupFaAccountRepository.findAll().filter(it->it.getIsDel().equals("1")).collectList().map(list -> list.stream().sorted(Comparator.comparing(GroupFaAccount::getCoCode)).collect(Collectors.toList())).cache().map(R::ok);
    }
    @PostMapping("assets/reduction")
    public Mono<R> reductionFa(@RequestBody GroupFaAccount entity) {
        return groupFaAccountRepository.findById(entity.getId()).map(it -> {
            it.setIsDel("0");
            it.setDelName(null);
            it.setDelDate(null);
            return it;
        }).flatMap(e -> groupFaAccountRepository.save(e).map(o -> R.ok()));
    }

    @GetMapping("assets/author")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> author(String code) {
        return groupFaAccAuthRepository.findAllByUserNumOrderByManageUniqueCodeAsc(code).collectList().map(o -> R.ok(o));
    }

    @GetMapping("assets/authorAccountList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> authorAccountList(String userId) {
        // 固定资产账
        return groupFaAccAuthRepository.findAllByUserNumOrderByManageUniqueCodeAsc(userId).collectList().cache().flatMap(list -> {
            HashSet<String> ids = new HashSet<>(list.stream().map(it -> it.getManageUniqueCode()).collect(Collectors.toList()));
            return groupFaAccountRepository.findAllByIdInOrderByCoCodeAsc(ids).filter(it->it.getIsDel().equals("0")).flatMap(en -> {
                en.setBeiyong1(JSON.toJSONString(list.stream().filter(it -> it.getManageUniqueCode().equals(en.getId())).map(it -> it.getIyear()).collect(Collectors.toList())));
                return groupFaManageClassRepository.findAllBySuperiorIdOrderByManageCodeAsc(en.getId()).collectList().map(li -> {
                    en.setBeiyong2(JSON.toJSONString(li));
                    return en;
                });
            }).collectList().cache().map(R::ok);
        });
    }


    @PostMapping("assets/modifyManageAuthor")
    @ApiOperation(value = "更新用户固定资产账权限", notes = "传入用户标识")
    @Transactional
    public Mono<R> modifyManageAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just("请求参数异常").map(m -> R.error());
        String userNum = map.get("userNum").toString();
        String supervisors = map.get("supervisors").toString();
        return groupFaAccAuthRepository.findAllByUserNumOrderByManageUniqueCodeAsc(userNum).collectList().flatMap(list -> list.size() > 0 ? groupFaAccAuthRepository.deleteAll(list).thenReturn("") : Mono.just("")).flatMap(o -> {
            List<GroupFaAccAuth> saveList = new ArrayList<>();
            if (supervisors.equals("1")) {
                saveList.add(new GroupFaAccAuth().setSupervisor(supervisors).setUserNum(userNum));
            } else {
                Set<String> authorizes = new HashSet<>(((ArrayList<String>) map.get("authorizes")));
                for (String manCode : authorizes) {
                    saveList.add(new GroupFaAccAuth().setSupervisor(supervisors).setUserNum(userNum).setManageUniqueCode(manCode));
                }
            }
            return saveList.size() > 0 ? groupFaAccAuthRepository.saveAll(saveList).collectList().map(R::ok) : Mono.just("").map(R::ok);
        });
    }


    @GetMapping("assets/period")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> period(String code, String iyear) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(iyear)) return Mono.just("请求参数异常").map(m -> R.error());
        return groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthAsc(code, iyear, "0").map(o -> R.ok(o)).switchIfEmpty(groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthDesc(code, iyear, "1").map(o -> R.ok(o)));
    }

    @GetMapping("assets/unPeriod")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> unPeriod(String code, String iyear) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(iyear)) return Mono.just("请求参数异常").map(m -> R.error());
        return groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthDesc(code, iyear, "1").map(o -> R.ok(o)).switchIfEmpty(groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthAsc(code, iyear, "0").map(o -> R.ok(o)));
    }

    @GetMapping("assets/date")
    @ApiOperation(value = "查询资产日期", notes = "传入code")
    public Mono<R> assetsDate(String code, String iyear) {
        if (StringUtils.isBlank(code) || StringUtils.isBlank(iyear)) return Mono.just("请求参数异常").map(m -> R.error());
        return groupFaAccPeriodRepository.findFirstByClassUniqueCodeAndIyearAndIsSettleOrderByImonthAsc(code, iyear, "0").switchIfEmpty(Mono.just(new GroupFaAccPeriod())).map(o -> {
            String[] todays = DateUtil.today().split("-");
            String date = null;
            Boolean isClose = false;
            if (null == o.getImonth()) { // 都已结账
                date = DateUtil.endOfYear(DateUtil.parse(iyear + "-12-01").toJdkDate()).toDateStr();
                isClose = true;
            } else {//未结账
                if (o.getIyear().equals(todays[0]) && o.getImonth().equals(todays[1])) {//同年同月
                    date = o.getIyear() + "-" + o.getImonth() + "-" + todays[2];
                } else {
                    date = DateUtil.endOfMonth(DateUtil.parse(o.getIyear() + "-" + o.getImonth() + "-" + "01").toJdkDate()).toDateStr();
                }
            }
            return R.ok(MapUtil.of(new Object[][]{{"year", iyear}, {"date", date}, {"isYearClose", isClose}}));
        });
    }

    @ApiOperation(value = "资产账结账", notes = "传入code")
    @PostMapping("assets/closeBill")
    public Mono<R> period(@RequestBody Map map) {
        if (map.keySet().size() != 2) return Mono.just("请求参数异常").map(m -> R.error());
        String id = map.get("id").toString();
        String value = map.get("value").toString();
        return groupFaAccPeriodRepository.findById(id).map(dbEntry -> {
            dbEntry.setIsSettle(value);
            return dbEntry;
        }).flatMap(dbEntry -> groupFaAccPeriodRepository.save(dbEntry).thenReturn(dbEntry)).flatMap(entry -> {
            entry.setId(null);
            entry.setIsSettle("0");
            entry.setIsZhejiu("0");
            entry.setIsFilledIn("0");
            if (entry.getImonth().equals("12")) {
                entry.setIyear((Integer.parseInt(entry.getIyear()) + 1) + "");
                entry.setImonth("01");
            } else {
                int m = Integer.parseInt(entry.getImonth()) + 1;
                entry.setImonth(m > 9 ? "" + m : "0" + m);
            }
            return value.equals("1") ? groupFaAccPeriodRepository.findFirstByUniqueCodeAndIyearAndImonth(entry.getUniqueCode(), entry.getIyear(), entry.getImonth()).switchIfEmpty(Mono.just(new GroupFaAccPeriod())).flatMap(db -> null != db.getImonth() ? Mono.just(R.ok(entry)) : groupFaAccPeriodRepository.save(entry).map(o -> R.ok(entry))) : Mono.just(R.ok(entry));
        });
    }
    /*********************************固定资产账************************************/


    /**
     * 根据账套编码 查询 科目类型
     *
     * @param accId
     * @return
     */
    @PostMapping("findByAccIdAccStyleList")
    public Mono<R> findByAccIdAccStyleList(String accId) {
        return accStyleRepository.findByAccIdAccStyleList(accId).collectList().map(o -> R.ok().setResult(o));
    }

    /**
     * 根据账套编码 查询 只有损益、权益 科目类型
     *
     * @param accId
     * @return
     */
    @PostMapping("findByAccIdSunYiAndQuanYi")
    public Mono<R> findByAccIdSunYiAndQuanYi(String accId) {
        return accStyleRepository.findByAccIdSunYiAndQuanYi(accId).collectList().map(o -> R.ok().setResult(o));
    }

    /****************************************************/


    @PostMapping("findAllByAccCountPartColumn")
    public Mono<R> findAllByAccCountPartColumn(String accId){
        return sysAccountRepository.findAllByAccCountPartColumn(accId)
            .flatMap(a->{
                return accStyleRepository.findAllStyleByUnique(a.getAccStyleUnique()).collectList()
                    .flatMap(stylist->{
                        a.setStylist(stylist);
                        return Mono.just(a);
                    });
            }).map(a->R.ok().setResult(a));
    }

    private List<GroupUserOperatAuth> assemblyIds(List<GroupUserOperatAuthZt> oldList, List<GroupUserOperatAuthZt> lastList, List<GroupUserOperatAuth> list) {
        HashSet<String> ids = new HashSet<>(list.stream().map(it -> it.getUsrAuthId()).collect(Collectors.toList()));
        List<String> lastIds = ids.stream().map(oldId -> {
            GroupUserOperatAuthZt oldInfo = oldList.stream().filter(it -> it.getId().equals(oldId)).collect(Collectors.toList()).get(0);
            GroupUserOperatAuthZt lastInfo = lastList.stream().filter(it -> it.getZtYear().equals(oldInfo.getZtYear()) && it.getZtUniqueCode().equals(oldInfo.getZtUniqueCode())).collect(Collectors.toList()).get(0);
            return oldId + "==" + lastInfo.getId();
        }).collect(Collectors.toList());
        for (GroupUserOperatAuth groupUserOperatAuth : list) {
            String nId = lastIds.stream().filter(k -> k.startsWith(groupUserOperatAuth.getUsrAuthId())).collect(Collectors.toList()).get(0).split("==")[1];
            groupUserOperatAuth.setId(null);
            groupUserOperatAuth.setUsrAuthId(nId);
        }
        return list;
    }
    private List<GroupRoleOperatAuth> assemblyIds2(List<GroupRoleOperatAuthZt> oldList, List<GroupRoleOperatAuthZt> lastList, List<GroupRoleOperatAuth> list) {
        HashSet<String> ids = new HashSet<>(list.stream().map(it -> it.getUsrAuthId()).collect(Collectors.toList()));
        List<String> lastIds = ids.stream().map(oldId -> {
            GroupRoleOperatAuthZt oldInfo = oldList.stream().filter(it -> it.getId().equals(oldId)).collect(Collectors.toList()).get(0);
            GroupRoleOperatAuthZt lastInfo = lastList.stream().filter(it -> it.getZtYear().equals(oldInfo.getZtYear()) && it.getZtUniqueCode().equals(oldInfo.getZtUniqueCode())).collect(Collectors.toList()).get(0);
            return oldId + "==" + lastInfo.getId();
        }).collect(Collectors.toList());
        for (GroupRoleOperatAuth groupUserOperatAuth : list) {
            String nId = lastIds.stream().filter(k -> k.startsWith(groupUserOperatAuth.getUsrAuthId())).collect(Collectors.toList()).get(0).split("==")[1];
            groupUserOperatAuth.setId(null);
            groupUserOperatAuth.setUsrAuthId(nId);
        }
        return list;
    }
    private Map<String, Object> assembleData(List<GroupSysAccount> cws, List<GroupFaAccount> zcs, List<GroupStockAccount> chs, List<GroupUserOperatAuthZt> dblist) {
        Map<String, Object> map = new HashMap<>();
        List<String> zCodes = cws.stream().map(it -> it.getCoCode()).collect(Collectors.toList());
        List<SysAccAuthVo> zauthors = dblist.stream().filter(it -> zCodes.contains(it.getZtUniqueCode())).map(it->{
            SysAccAuthVo vo = new SysAccAuthVo();
            vo.setUserNum(it.getUserUniqueCode());
            vo.setAccId(cws.stream().filter(v->it.getZtUniqueCode().equals(v.getCoCode())).collect(Collectors.toList()).get(0).getAccId());
            vo.setIyear(it.getZtYear());
            vo.setCcodeAll("1");
            vo.setAccvocherType("1");
            vo.setSupervisor(it.getSupervisor());
            vo.setDefaultLogin(it.getDefaultLogin());
            return vo;
        }).collect(Collectors.toList());
        List<AccountVo> list = new ArrayList<>();
        for (GroupSysAccount cw : cws) {
            List<AccountVo> exists = list.stream().filter(it -> it.getCoCode().equals(cw.getCoCode())).collect(Collectors.toList());
            if (exists.size() > 0) {
                for (AccountVo accountVo : list) {
                    if (accountVo.getCoCode().equals(exists.get(0).getCoCode())) {
                        accountVo.setZtStyle(accountVo.getZtStyle() + "," + "ZZ");
                    }
                }
            } else {
                List<GroupUserOperatAuthZt> auths = dblist.stream().filter(it -> it.getZtUniqueCode().equals(cw.getCoCode())).collect(Collectors.toList());
                list.add(new AccountVo(cw.getCoCode(), cw.getAccNameFull(), "ZZ", auths.get(0).getDefaultLogin(), ListUtil.toList(auths.stream().map(it -> it.getZtYear()).collect(Collectors.toList())), cw.getAccId()));
            }
        }
        for (GroupFaAccount zc : zcs) {
            List<AccountVo> exists = list.stream().filter(it -> it.getCoCode().equals(zc.getCoCode())).collect(Collectors.toList());
            List<GroupUserOperatAuthZt> auths = dblist.stream().filter(it -> it.getZtUniqueCode().equals(zc.getCoCode())).collect(Collectors.toList());
            List<String> years = ListUtil.toList(auths.stream().map(it -> it.getZtYear()).collect(Collectors.toList()));
            zc.setBeiyong1(JSON.toJSONString(years));
            if (exists.size() > 0) {
                for (AccountVo accountVo : list) {
                    if (accountVo.getCoCode().equals(exists.get(0).getCoCode())) {
                        accountVo.setZtStyle(accountVo.getZtStyle() + "," + "GD");
                    }
                }
            } else {
                list.add(new AccountVo(zc.getCoCode(), zc.getFaAccName(), "GD", auths.get(0).getDefaultLogin(), years, zc.getFaAccId()));
            }
        }
        for (GroupStockAccount ch : chs) {
            List<AccountVo> exists = list.stream().filter(it -> it.getCoCode().equals(ch.getCoCode())).collect(Collectors.toList());
            List<GroupUserOperatAuthZt> auths = dblist.stream().filter(it -> it.getZtUniqueCode().equals(ch.getCoCode())).collect(Collectors.toList());
            List<String> years = ListUtil.toList(auths.stream().map(it -> it.getZtYear()).collect(Collectors.toList()));
            ch.setBeiyong1(JSON.toJSONString(years));
            if (exists.size() > 0) {
                for (AccountVo accountVo : list) {
                    if (accountVo.getCoCode().equals(exists.get(0).getCoCode())) {
                        accountVo.setZtStyle(accountVo.getZtStyle() + "," + "CH");
                    }
                }
            } else {
                list.add(new AccountVo(ch.getCoCode(), ch.getStockAccName(), "CH", auths.get(0).getDefaultLogin(), years, ch.getStockAccId()));
            }
        }
        map.put("zzAccount",cws);
        map.put("zzAuthors",zauthors);
        map.put("zcAccount",zcs);
        map.put("chAccount",chs);
        map.put("allAccount",list);
        return map;
    }
    /****************************************************/

    /********************** 账套授权 **********************/
    @Autowired
    GroupUserOperatAuthZtRepository groupUserOperatAuthZtRepository;

    @Autowired
    GroupUserOperatAuthRepository groupUserOperatAuthRepository;

    @GetMapping("allAuthorAccountList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> allAuthorAccountList(String userId) {
        if (StrUtil.isBlank(userId)) return Mono.just(R.error());
        Flux<GroupUserOperatAuthZt> authFlux = userId.equals("1") ? groupUserOperatAuthZtRepository.findAllByOrderByZtYearDesc() : groupUserOperatAuthZtRepository.findAllByUserUniqueCodeOrderByZtYearDesc(userId);
        return authFlux.collectList().cache().flatMap(list -> {
            List<String> ids = new ArrayList<>(new HashSet<>(list.stream().map(it -> it.getZtUniqueCode()).collect(Collectors.toList())));
            Mono<List<GroupSysAccount>> cwMono = sysAccountRepository.findAllByFlagAndCoCodeInOrderByCoCodeAsc("1", ids).collectList().cache();
            Mono<List<GroupFaAccount>> zcMono = groupFaAccountRepository.findAllByCoCodeInOrderByCoCodeAsc(ids)
                    .flatMap(en -> groupFaManageClassRepository.findAllBySuperiorIdOrderByManageCodeAsc(en.getId()).collectList().map(li -> {
                        en.setBeiyong2(JSON.toJSONString(li));
                        return en;
                    })).collectList().cache();
            Mono<List<GroupStockAccount>> chMono = groupStockAccountRepository.findAllByFlagAndCoCodeInOrderByCoCodeAsc("1", ids).collectList().cache();
            return Mono.zip(cwMono, zcMono, chMono).flatMap(tips -> {
                List<GroupSysAccount> cws = tips.getT1();
                List<GroupFaAccount> zcs = tips.getT2();
                List<GroupStockAccount> chs = tips.getT3();
                return Mono.just(R.ok(assembleData(cws, zcs, chs, list)));
            });
        });
    }


    @PostMapping("findUserAuthor")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findUserAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String userId = map.get("userId").toString();
        String coCode = map.get("coCode").toString();
        String iyear = map.get("iyear").toString();
        Mono<GroupUserOperatAuthZt> authFlux = groupUserOperatAuthZtRepository.findFirstByUserUniqueCodeAndZtUniqueCodeAndZtYearOrderByZtYear(userId, coCode, iyear);
        return authFlux.flatMap(dbEntity -> {
            dbEntity.setDefaultLogin(null);
            if (!dbEntity.getSupervisor().equals("1")) {
                return groupUserOperatAuthRepository.findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId()).collectList().flatMap(list -> {
                    dbEntity.setDefaultLogin(JSON.toJSONString(list));
                    return Mono.just(R.ok(dbEntity));
                });
            }
            return Mono.just(R.ok(dbEntity));
        }).switchIfEmpty(Mono.just(R.ok()));
    }


    @PostMapping("saveAuthor")
    @Transactional
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> saveAuthor(@RequestBody GroupUserOperatAuthZt entity) {
        List<GroupUserOperatAuth> entrys = null;
        if (entity.getSupervisor().equals("1")) {
            entrys=new ArrayList<>();
        } else {
            entrys = JSON.parseArray(entity.getDefaultLogin(), GroupUserOperatAuth.class);
            entity.setDefaultLogin(null);
        }
        List<GroupUserOperatAuth> finalEntrys = entrys;
        return groupUserOperatAuthZtRepository.save(entity).flatMap(dbEntity -> (dbEntity.getSupervisor().equals("1")?groupUserOperatAuthRepository.findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId()):groupUserOperatAuthRepository.findAllByUsrAuthIdAndPlatformMarkOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId(),finalEntrys.get(0).getPlatformMark())).collectList().flatMap(list -> {
            for (GroupUserOperatAuth finalEntry : finalEntrys) finalEntry.setUsrAuthId(dbEntity.getId());
            if (list.size() > 0) {
                return groupUserOperatAuthRepository.deleteAll(list).thenReturn("").flatMap(v -> null != finalEntrys ? groupUserOperatAuthRepository.saveAll(finalEntrys).collectList().thenReturn(entity) : Mono.just(entity));
            } else {
                return null != finalEntrys ? groupUserOperatAuthRepository.saveAll(finalEntrys).collectList().thenReturn(entity) : Mono.just(entity);
            }
        })).map(o -> R.ok(o));
    }

    @PostMapping("emptyAuthor")
    @Transactional
    @ApiOperation(value = "清空某模块权限", notes = "传入code")
    public Mono<R> emptyAuthor(@RequestBody GroupUserOperatAuthZt entity) {
        String pt = entity.getDefaultLogin();
       return groupUserOperatAuthRepository.findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(entity.getId()).collectList().flatMap(list->{
            List<GroupUserOperatAuth> del = list.stream().filter(it -> it.getPlatformMark().equals(pt)).collect(Collectors.toList());
            if (pt.equals("all") || (del.size() == list.size())){
                return groupUserOperatAuthRepository.deleteAll(list).thenReturn("").flatMap(e->groupUserOperatAuthZtRepository.deleteById(entity.getId())).thenReturn(R.ok());
            }else {
                return groupUserOperatAuthRepository.deleteAll(list).thenReturn(R.ok());
            }
        });
    }

    @PostMapping("copyAuthor")
    @Transactional
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> copyAuthor(@RequestBody GroupUserOperatAuthZt entity) {
        String userUniqueCode = entity.getUserUniqueCode();
        String targetUniqueCode = entity.getDefaultLogin();
        if (StrUtil.isBlank(userUniqueCode) || StrUtil.isBlank(targetUniqueCode)) return Mono.just(R.error());
        return groupUserOperatAuthZtRepository.findAllByUserUniqueCodeOrderByZtYearDesc(targetUniqueCode).collectList().flatMap(list -> {
            if (list.size() > 0) {
                List<String> ids = list.stream().map(it -> it.getId()).collect(Collectors.toList());
                return groupUserOperatAuthRepository.findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(ids).collectList().flatMap(groupUserOperatAuthRepository::deleteAll).thenReturn(list).flatMap(groupUserOperatAuthZtRepository::deleteAll).thenReturn(targetUniqueCode);
            }
            return Mono.just(targetUniqueCode);
        }).flatMap(v -> groupUserOperatAuthZtRepository.findAllByUserUniqueCodeOrderByZtYearDesc(userUniqueCode).collectList().flatMap(list -> {
            if (list.size() > 0) {
                List<GroupUserOperatAuthZt> oldList = JSON.parseArray(JSON.toJSONString(list), GroupUserOperatAuthZt.class);
                List<String> ids = list.stream().map(it -> it.getId()).collect(Collectors.toList());
                List<GroupUserOperatAuthZt> saveList = list.stream().map(it -> {it.setId(null);it.setUserUniqueCode(targetUniqueCode);return it;}).collect(Collectors.toList());
                return groupUserOperatAuthZtRepository.saveAll(saveList).collectList().flatMap(dbList -> groupUserOperatAuthRepository.findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(ids).collectList().map(list2 -> assemblyIds(oldList, dbList, list2)).flatMap(eaList -> groupUserOperatAuthRepository.saveAll(eaList).collectList()).thenReturn(R.ok()));
            }
            return Mono.just(R.ok());
        }));
    }

    @Autowired
    private GroupUserOperatAuthColumnRepository groupUserOperatAuthColumnRepository;

    @Autowired
    private GroupUserOperatAuthMenuRepository groupUserOperatAuthMenuRepository;

    @PostMapping("getPlatformColumn")
    public Mono<R> getPlatformColumn(@RequestBody Map map){
        if (map.keySet().size() != 1)return Mono.just(R.error());
        String mark = map.get("mark").toString();
        return groupUserOperatAuthColumnRepository.findAllByPlatformMarkOrderByIdAsc(mark).collectList().map(R::ok);
    }

    @PostMapping("savePlatformColumn")
    public Mono<R> savePlatformColumn(@RequestBody Map map){
        if (map.keySet().size() != 1)return Mono.just(R.error());
        List<GroupUserOperatAuthColumn> entries = JSON.parseArray(map.get("entries").toString(),GroupUserOperatAuthColumn.class);
        return groupUserOperatAuthColumnRepository.saveAll(entries).collectList().map(R::ok);
    }


    @GetMapping("getPlatformAndMenu")
    public Mono<R> getPlatformAndMenu(){
        return groupUserOperatAuthMenuRepository.findAllByOrderByParentIdAscSortNoAsc().collectList().map(R::ok);
    }

    @PostMapping("savePlatformAndMenu")
    public Mono<R> savePlatformAndMenu(@RequestBody Map map){
        if (map.keySet().size() != 1)return Mono.just(R.error());
        List<GroupUserOperateAuthMenu> entries = JSON.parseArray(map.get("entries").toString(),GroupUserOperateAuthMenu.class);
        return groupUserOperatAuthMenuRepository.saveAll(entries).collectList().map(R::ok);
    }

    /********************** 账套授权 **********************/

    /********************** 账套角色授权 **********************/
    @Autowired
    GroupRoleOperatAuthZtRepository groupRoleOperatAuthZtRepository;

    @Autowired
    GroupRoleOperatAuthRepository groupRoleOperatAuthRepository;



    @PostMapping("findUserAuthor2")
    @ApiOperation(value = "角色查询列表", notes = "传入code")
    public Mono<R> findUserAuthor2(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String userId = map.get("userId").toString();
        String coCode = map.get("coCode").toString();
        String iyear = map.get("iyear").toString();
        Mono<GroupRoleOperatAuthZt> authFlux = groupRoleOperatAuthZtRepository.findFirstByRoleUniqueCodeAndZtUniqueCodeAndZtYearOrderByZtYear(userId, coCode, iyear);
        return authFlux.flatMap(dbEntity -> {
            dbEntity.setDefaultLogin(null);
            if (!dbEntity.getSupervisor().equals("1")) {
                return groupRoleOperatAuthRepository.findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId()).collectList().flatMap(list -> {
                    dbEntity.setDefaultLogin(JSON.toJSONString(list));
                    return Mono.just(R.ok(dbEntity));
                });
            }
            return Mono.just(R.ok(dbEntity));
        }).switchIfEmpty(Mono.just(R.ok()));
    }


    @PostMapping("saveAuthor2")
    @Transactional
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> saveAuthor2(@RequestBody GroupRoleOperatAuthZt entity) {
        List<GroupRoleOperatAuth> entrys = null;
        if (entity.getSupervisor().equals("1")) {
            entrys=new ArrayList<>();
        } else {
            entrys = JSON.parseArray(entity.getDefaultLogin(), GroupRoleOperatAuth.class);
            entity.setDefaultLogin(null);
        }
        List<GroupRoleOperatAuth> finalEntrys = entrys;
        return groupRoleOperatAuthZtRepository.save(entity).flatMap(dbEntity -> (dbEntity.getSupervisor().equals("1")?groupRoleOperatAuthRepository.findAllByUsrAuthIdOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId()):groupRoleOperatAuthRepository.findAllByUsrAuthIdAndPlatformMarkOrderByPlatformMarkAscFunctionIdAsc(dbEntity.getId(),finalEntrys.get(0).getPlatformMark())).collectList().flatMap(list -> {
            for (GroupRoleOperatAuth finalEntry : finalEntrys) finalEntry.setUsrAuthId(dbEntity.getId());
            if (list.size() > 0) {
                return groupRoleOperatAuthRepository.deleteAll(list).thenReturn("").flatMap(v -> null != finalEntrys ? groupRoleOperatAuthRepository.saveAll(finalEntrys).then(Mono.just(entity)) : Mono.just(entity));
            } else {
                return null != finalEntrys ? groupRoleOperatAuthRepository.saveAll(finalEntrys).then(Mono.just(entity)) : Mono.just(entity);
            }
        })).map(o -> R.ok(o));
    }

    @PostMapping("copyAuthor2")
    @Transactional
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> copyAuthor2(@RequestBody GroupRoleOperatAuthZt entity) {
        String userUniqueCode = entity.getRoleUniqueCode();
        String targetUniqueCode = entity.getDefaultLogin();
        if (StrUtil.isBlank(userUniqueCode) || StrUtil.isBlank(targetUniqueCode)) return Mono.just(R.error());
        return groupRoleOperatAuthZtRepository.findAllByRoleUniqueCodeOrderByZtYearDesc(targetUniqueCode).collectList().flatMap(list -> {
            if (list.size() > 0) {
                List<String> ids = list.stream().map(it -> it.getId()).collect(Collectors.toList());
                return groupRoleOperatAuthRepository.findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(ids).collectList().flatMap(groupRoleOperatAuthRepository::deleteAll).thenReturn(list).flatMap(groupRoleOperatAuthZtRepository::deleteAll).thenReturn(targetUniqueCode);
            }
            return Mono.just(targetUniqueCode);
        }).flatMap(v -> groupRoleOperatAuthZtRepository.findAllByRoleUniqueCodeOrderByZtYearDesc(userUniqueCode).collectList().flatMap(list -> {
            if (list.size() > 0) {
                List<GroupRoleOperatAuthZt> oldList = JSON.parseArray(JSON.toJSONString(list), GroupRoleOperatAuthZt.class);
                List<String> ids = list.stream().map(it -> it.getId()).collect(Collectors.toList());
                List<GroupRoleOperatAuthZt> saveList = list.stream().map(it -> {it.setId(null);it.setRoleUniqueCode(targetUniqueCode);return it;}).collect(Collectors.toList());
                return groupRoleOperatAuthZtRepository.saveAll(saveList).collectList().flatMap(dbList -> groupRoleOperatAuthRepository.findAllByUsrAuthIdInOrderByPlatformMarkAscFunctionIdAsc(ids).collectList().map(list2 -> assemblyIds2(oldList, dbList, list2)).flatMap(eaList -> groupRoleOperatAuthRepository.saveAll(eaList).collectList()).thenReturn(R.ok()));
            }
            return Mono.just(R.ok());
        }));
    }
    /********************** 账套角色授权 **********************/


    @PostMapping("countAllByAccStandard")
    public Mono<R> countAllByAccStandard(String templateId) {
        return sysAccountRepository.countAllByAccStandard(templateId).map(o -> R.ok().setResult(o));
    }
    @PostMapping("findAllByAccGroup")
    public Mono<R> findAllByAccGroup(String orgUnique) {
        return sysAccountRepository.findAllByAccGroup(orgUnique).collectList().map(o -> R.ok().setResult(o));
    }


    @GetMapping("findGroupDownCount")
    public Mono<R> findGroupDownCount() {
        Mono<Long> one = groupSysOrgRepository.countAllBy();
        Mono<Long> two = groupSysCorpRepository.countAllBy();
        Mono<Long> three = groupSysUserRepository.countAllBy();
        Mono<Long> four = sysAccountRepository.countAllBy();
        Mono<Long> five = groupFaAccountRepository.countAllBy();
        Mono<Long> six = groupStockAccountRepository.countAllBy();
        return Mono.zip(one, two, three, four, five, six).flatMap(ts->Mono.just(R.ok(ts)));
    }

    /***************************Miao*****************************/
    /**
     * 获取操作员已授权的期间
     * @param userUnique    操作员Id
     * @param ztCode        账套代码
     * @return
     */
    @PostMapping("findAllByAuthPeriod")
    public Mono<R> findAllByAuthPeriod(String userUnique,String ztCode){
        return groupUserOperatAuthZtRepository.findAllByAuthPeriod(userUnique,ztCode).collectList()
                .flatMap(list->{
                    list.forEach(a->{
                        a.setValue(a.getIyear()+"-"+a.getIperiodNum());
                    });
                    return Mono.just(list);
                })
                .map(a->R.ok().setResult(a));
    }
}
