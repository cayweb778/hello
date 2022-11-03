package org.boozsoft.rest;//package org.boozsoft.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.config.r2dbc.R2dbcRouterLoaderImpl;
import org.boozsoft.domain.entity.KmCashFlow;
import org.boozsoft.domain.entity.group.GroupSysAccAuth;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.repo.*;
import org.boozsoft.repo.accstyle.AccStyleRepository;
import org.boozsoft.repo.codekemu.CodeKemuRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.springbooz.core.tool.result.R;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;


/**
 * 筛选配置
 *
 * @author lz
 */
@Slf4j
@RestController
@RequestMapping("/seachConfig")
@Api(value = "筛选配置", tags = "API系统：筛选配置")
public class SeachConfigController {
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
    SysAccountPeriodRepository sysAccountPeriodRepository;
    @Autowired
    SysPeriodRepository sysPeriodRepository;
    @Autowired
    CodeKemuRepository codeKemuRepository;
    @Autowired
    SysCurrencyCorpRepository sysCurrencyCorpRepository;
    @Autowired
    SysDepartmentRepository sysDepartmentRepository;
    @Autowired
    SysAccTypeAuthRepository sysAccTypeAuthRepository;
    @Autowired
    SysAccCodeAuthRepository sysAccCodeAuthRepository;
    @Autowired
    SysAccAuthRepository sysAccAuthRepository;
    @Autowired
    ProjectCategoryRepository projectCategoryRepository;
    @Autowired
    ProjectClassRepository projectClassRepository;
    @Autowired
    ProjectRepositoryBase projectRepositoryBase;
    @Autowired
    KmCashFlowRepository kmCashFlowRepository;
    @Autowired
    AccStyleRepository accStyleRepository;
    @Autowired
    SysPsnRepository sysPsnRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    SupplierRepository supplierRepository;
    @Autowired
    SysDataAuthSwithRepository sysDataAuthSwithRepository;

    @GetMapping("/findPeriodByAccontId/{accontId}")
    @ApiOperation(value = "查询会计区间条件", notes = "查询会计区间条件")
    public Mono<R> findPeriodByAccontId(@PathVariable String accontId) {
        //获取从最小的年度启用日期 到最大的年度
        return sysPeriodRepository.findAllByAccountId(accontId).collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setValue(v.getIyear() + "-" + v.getIperiodNum());
            });
            // 获取指定数据在数组中的下标 截取开始年度以后的
            int index = IntStream.range(0, list.size()).filter(i -> list.get(i).getEnablePeriod().equals("1")).findFirst().orElse(-1);
            return index >= 0 ? list.subList(index, list.size()) : new ArrayList<>();
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findPeriodByaccIdAndYear/{accontId}/{iyaer}")
    @ApiOperation(value = "查询会计区间条件", notes = "查询会计区间条件")
    public Mono<R> findPeriodByAccIdAndIyaer(@PathVariable String accontId, @PathVariable String iyaer) {
        //获取从最小的年度启用日期 到最大的年度
        return sysPeriodRepository.findAllByAccountIdAndIyear(accontId, iyaer).collectList().cache().map(list -> {
            list.stream().forEach(v -> {
                v.setValue(v.getIyear() + "-" + v.getIperiodNum());
            });

            int index = IntStream.range(0, list.size()).filter(i -> list.get(i).getEnablePeriod().equals("1")).findFirst().orElse(-1);
            return index >= 0 ? list.subList(index, list.size()) : new ArrayList<>();
        }).map(o -> R.ok().setResult(o));
    }

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @GetMapping("/findCodeKmByPeriod/{strDate}/{endDate}/{accontId}/{userId}")
    @ApiOperation(value = "根据会计区间查询对应会计科目", notes = "根据会计区间查询对应会计科目")
    public Mono<R> findCodeKmByPeriod(@PathVariable String strDate, @PathVariable String endDate, @PathVariable String accontId, @PathVariable String userId) {
        String year = strDate.split("-")[0];
        //跨年暂不考虑
        HashMap map = new HashMap();
        List<CodeKemu> list = new ArrayList<>();

        return codeKemuRepository.findAllByIyearAndTenandId(year, accontId + "-" + year).cache().collectList().flatMap(clist -> {
            //是否开启权限
            return sysDataAuthSwithRepository.findAllByRecordNumAndIyear(accontId, year).map(v -> {
                return Objects.isNull(v.getIsCcode()) || !"1".equals(v.getIsCcode()) ? "0" : "1";
            }).defaultIfEmpty("0").map(v -> {
                map.put("isSetup", v);
                return clist;
            });
        }).flatMap(clist -> {
            if (map.containsKey("isSetup") && "1".equals(map.get("isSetup").toString())) {
                //授权信息
                return sysAccAuthRepository.findAll().collectList().map(v -> {
                    List<GroupSysAccAuth> collect = v.stream().filter(item -> item.getUserNum().equals(userId) && item.getAccId().equals(accontId) && item.getIyear().equals(year)).collect(toList());
                    String ccodeAll = collect.get(0).getCcodeAll();
                    map.put("iscode", ccodeAll);
                    map.put("list", clist);
                    return clist;
                });
            } else {
                return Mono.just(clist);
            }
        }).map(item -> {
            //若设置授权则过滤授权
            if (map.containsKey("iscode")) {
                if (!"1".equals(map.get("iscode").toString())) {
                    sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accontId, year).collectList().map(clist -> {
                        //过滤科目权限
                        if (Objects.nonNull(clist) && clist.size() > 0) {
                            List<CodeKemu> collect = item.stream().filter(obj -> clist.contains(obj.getCcode())).collect(toList());
                            list.addAll(collect);
                        }
                        return list;
                    });
                    return list;
                } else {
                    return item;
                }
            } else {
                return item;
            }
        }).map(item -> {
            item.stream().forEach(v -> {
                v.setValue(v.getCcode() + "-" + v.getCcodeName());
            });
            return item;
        }).map(o -> R.ok().setResult(o));
    }


    @GetMapping("/findCodeKmAll")
    @ApiOperation(value = "根据会计区间查询对应会计科目", notes = "根据会计区间查询对应会计科目")
    public Mono<R> findCodeKmAll() {
        return codeKemuRepository.findAllCodeOrderByAsc().collectList().map(item -> {
            item.stream().forEach(v -> {
                v.setValue(v.getCcode() + "-" + v.getCcodeName());
            });
            return item;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findCodeKmLastByYear/{iyear}/{accontId}/{userId}")
    @ApiOperation(value = "根据会计区间查询对应会计科目", notes = "根据会计区间查询对应会计科目")
    public Mono<R> findCodeKmByPeriod(@PathVariable String iyear, @PathVariable String accontId, @PathVariable String userId) {
        String year = iyear;
        //跨年暂不考虑
        HashMap map = new HashMap();
        List<CodeKemu> list = new ArrayList<>();
        return codeKemuRepository.findAllByIyearAndBendAndFlag(year, accontId).collectList().flatMap(clist -> {
            //授权信息
//                    sysAccAuthRepository.findAllByUserIdAmdAccIdAndYear(userId, accontId, year)
            return sysAccAuthRepository.findAll().collectList().map(v -> {
                List<GroupSysAccAuth> collect = v.stream().filter(item -> item.getUserNum().equals(userId) && item.getAccId().equals(accontId) && item.getIyear().equals(year)).collect(toList());
                String ccodeAll = collect.get(0).getCcodeAll();
                map.put("iscode", ccodeAll);
                map.put("list", clist);
                return map;
            });
        }).map(item -> {
            if (map.containsKey("iscode")) {
                if (!"1".equals(map.get("iscode").toString())) {
                    sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accontId, year).collectList().map(clist -> {
                        //过滤科目权限
                        if (Objects.nonNull(clist) && clist.size() > 0) {
                            List<CodeKemu> collect = list.stream().filter(obj -> clist.contains(obj.getCcode())).collect(toList());
                            list.addAll(collect);
                        }
                        return list;
                    });
                    return list;
                } else {
                    list.addAll((Collection<? extends CodeKemu>) map.get("list"));
                    return list;
                }
            } else {
                return list;
            }
        }).map(item -> {
            item.stream().forEach(v -> {
                v.setValue(v.getCcode() + "-" + v.getCcodeName());
            });
            return item;
        }).map(o -> R.ok().setResult(o));
    }


    @GetMapping("/findMaxJc/{accontId}/{databaseyear}")
    @ApiOperation(value = "查询级次范围", notes = "查询级次范围")
    public Mono<R> findMaxJc(@PathVariable String accontId, @PathVariable String databaseyear) {
        return sysAccountPeriodRepository.findByAccountIdAndAccountYear(accontId, databaseyear).map(item -> {
            return item.getJiciLength();
        }).map(o -> R.ok().setResult(o));
    }


    @GetMapping("/findBzAll")
    @ApiOperation(value = "查询所有币种", notes = "查询所有币种")
    public Mono<R> findAll() {
        return sysCurrencyCorpRepository.findAllByCurrFlag("1").collectList().map(list -> {
            list.forEach(v -> {
                if ("1".equals(v.getStandCurr())) {
                    v.setCurrChName(v.getCurrChName() + "(本位币)");
                }
            });
            return list;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findDeptAll")
    @ApiOperation(value = "查询所有部门", notes = "查询所有部门")
    public Mono<R> findDeptAll() {
        return sysDepartmentRepository.findByFlagAndIsDelOrderByDeptCode("1", "0").collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setLabel(v.getDeptCode() + "-" + v.getDeptName());
            });
            return list;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findProjectCategory")
    @ApiOperation(value = "查询所有项目大类", notes = "查询所有项目大类")
    public Mono<R> findProjectCategory() {
        return projectCategoryRepository.findBySuccessStateAndFlagOrderById("1", "1").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findProjectClass/{projectCate}")
    @ApiOperation(value = "根据大类查询项目分类", notes = "根据大类查询项目分类")
    public Mono<R> findProjectClass(@PathVariable String projectCate) {
        return projectClassRepository.findByProjectCateCodeOrderByProjectClassCode(projectCate).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findProject/{projectCate}")
    @ApiOperation(value = "根据大类分类查询项目", notes = "根据大类分类查询项目")
    public Mono<R> findProject(@PathVariable String projectCate) {
        return projectRepositoryBase.findByProjectCateCodeAndIsDelOrderByProjectCode(projectCate, "0").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findCodeKmDept/{strDate}/{endDate}/{accontId}/{userId}")
    @ApiOperation(value = "部门会计科目", notes = "部门会计科目")
    public Mono<R> findCodeKmDept(@PathVariable String strDate, @PathVariable String endDate, @PathVariable String accontId, @PathVariable String userId) {
        //会计区间查询对应授权会计科目
        //跨年暂不考虑
        String year = strDate.split("-")[0];
        HashMap map = new HashMap();
        List<CodeKemu> list = new ArrayList<>();
        return codeKemuRepository.findAllByIyearAndDept(year, accontId + "-" + year).collectList().flatMap(clist -> {
            //授权信息
            return sysAccAuthRepository.findAllByUserIdAmdAccIdAndYear(userId, accontId, year).collectList().map(v -> {
                String ccodeAll = v.get(0).getCcodeAll();
                map.put("iscode", ccodeAll);
                map.put("list", clist);
                return map;
            });
        }).map(item -> {
            if (map.containsKey("iscode")) {
                if (!"1".equals(map.get("iscode").toString())) {
                    sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accontId, year).collectList().map(clist -> {
                        //过滤科目权限
                        if (Objects.nonNull(clist) && clist.size() > 0) {
                            List<CodeKemu> collect = list.stream().filter(obj -> clist.contains(obj.getCcode())).collect(toList());
                            list.addAll(collect);
                        }
                        return list;
                    });
                    return list;
                } else {
                    list.addAll((Collection<? extends CodeKemu>) map.get("list"));
                    return list;
                }
            } else {
                return list;
            }
        }).map(item -> {
            item.stream().forEach(v -> {
                v.setValue(v.getCcode() + "-" + v.getCcodeName());
            });
            return item;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findCodeProject/{strDate}/{endDate}/{accontId}/{userId}")
    @ApiOperation(value = "项目会计科目", notes = "项目会计科目")
    public Mono<R> findCodeProject(@PathVariable String strDate, @PathVariable String endDate, @PathVariable String accontId, @PathVariable String userId) {
        //会计区间查询对应授权会计科目
        //跨年暂不考虑

        System.out.println("8888");
        String year = strDate.split("-")[0];
        HashMap map = new HashMap();
        List<CodeKemu> list = new ArrayList<>();
        return codeKemuRepository.findAllByIyearAndProject(year, accontId + "-" + year).collectList().flatMap(clist -> {

            System.out.println("88881");
            //授权信息
            return routerLoader.bind(() -> sysAccAuthRepository.findAllByUserIdAmdAccIdAndYear(userId, accontId, year), R2dbcRouterBuilder.ofDefault()).collectList().map(v -> {
                System.out.println("1111");
                String ccodeAll = v.get(0).getCcodeAll();
                map.put("iscode", ccodeAll);
                map.put("list", clist);
                return map;
            });
        }).map(item -> {
            System.out.println("2222");
            if (map.containsKey("iscode")) {
                if (!"1".equals(map.get("iscode").toString())) {
                    System.out.println("3333");
                    sysAccCodeAuthRepository.findAllByUserNumAndAccIdAndIyearOrderByCodeNumAsc(userId, accontId, year).collectList().map(clist -> {
                        //过滤科目权限
                        if (Objects.nonNull(clist) && clist.size() > 0) {
                            System.out.println("4444");
                            List<CodeKemu> collect = list.stream().filter(obj -> clist.contains(obj.getCcode())).collect(toList());
                            list.addAll(collect);
                        }
                        return list;
                    });
                    return list;
                } else {
                    System.out.println("5555");
                    list.addAll((Collection<? extends CodeKemu>) map.get("list"));
                    return list;
                }
            } else {
                return list;
            }
        }).map(item -> {
            System.out.println("1111");
            item.stream().forEach(v -> {
                v.setValue(v.getCcode() + "-" + v.getCcodeName());
            });
            return item;
        }).map(o -> R.ok().setResult(o));
    }


    @GetMapping("/findPeriod/{accontId}")
    @ApiOperation(value = "查询会计年度区间", notes = "查询会计年度区间")
    public Mono<R> findPeriod(@PathVariable String accontId) {
        return sysPeriodRepository.findYearAccountId(accontId).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findPeriodYears/{accontId}")
    @ApiOperation(value = "查询会计年度区间", notes = "查询会计年度区间")
    public Mono<R> findPeriodYears(@PathVariable String accontId) {
        return sysPeriodRepository.findAllYearAccountId(accontId).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findYearMinDate/{account_id}/{iyear}")
//@Param("account_id") String account_id, @Param("iyear") String iyear
    @ApiOperation(value = "查询账套最小日期", notes = "查询账套最小日期")
    public Mono<R> findYearMinDate(@PathVariable String account_id, @PathVariable String iyear) {
        return sysPeriodRepository.findYearMinDate(account_id, iyear + "%").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findYearMaxDate/{account_id}/{iyear}")
//@Param("account_id") String account_id, @Param("iyear") String iyear
    @ApiOperation(value = "查询账套大日期", notes = "查询账套大日期")
    public Mono<R> findYearMaxDate(@PathVariable String account_id, @PathVariable String iyear) {
        return sysPeriodRepository.findYearMaxDate(account_id, iyear + "%").collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findAllKmCashFlow/{accontId}")
    @ApiOperation(value = "查询会计年度区间", notes = "查询会计年度区间")
    public Mono<R> findAllKmCashFlow(@PathVariable String accontId) {
        return kmCashFlowRepository.findAllByTenantId(accontId).collectList().map(o -> R.ok().setResult(o));
    }

    @PostMapping("/findAllCodeKm")
    @ApiOperation(value = "查询对应会计科目", notes = "查询对应会计科目")
    public Mono<R> findAllCodeKm(@RequestBody Map maps) {
        String year = maps.get("iyear").toString();
        String flag = maps.get("flag").toString();
        return codeKemuRepository.findAllByIyearAndBendAndFlagAndClass(year, "资产").collectList().map(list -> list.stream().filter(v -> flag.equals(v.getBcash()))).map(o -> R.ok().setResult(o));
    }

    @PostMapping("/addKmCashFlow")
    @ApiOperation(value = "项目会计科目", notes = "项目会计科目")
    public Mono<R> addKmCashFlow(@RequestBody Map maps) {
        String flg = maps.get("flg").toString();
        ArrayList<String> list = Objects.isNull(maps.get("ids")) ? new ArrayList<String>() : (ArrayList<String>) maps.get("ids");
        return codeKemuRepository.updateBcashByIds(flg, list).thenReturn(R.ok());
    }

    @GetMapping("/findPsnAll")
    @ApiOperation(value = "查询所有部门", notes = "查询所有部门")
    public Mono<R> findPsnAll() {
        return sysPsnRepository.findAllByFlag("1").collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setLabel(v.getPsnCode() + "-" + v.getPsnName());
            });
            return list;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findCusAll")
    public Mono<R> findCusAll() {
        return customerRepository.findAllByFlag("1").collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setLabel(v.getCustCode() + "-" + v.getCustName());
            });
            return list;
        }).map(o -> R.ok().setResult(o));
    }

    @GetMapping("/findSupAll")
    public Mono<R> findSupAll() {
        return supplierRepository.findAllByFlag("1").collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setLabel(v.getCustCode() + "-" + v.getCustName());
            });
            return list;
        }).map(o -> R.ok().setResult(o));
    }

    @Autowired
    private GroupUserOperatAuthZtRepository groupUserOperatAuthZtRepository;

    @PostMapping("/findAllPeriodByUserAndCode")
    @ApiOperation(value = "查询已授权会计区间条件", notes = "查询会计区间条件")
    public Mono<R> findAllPeriodByUserAndCode(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String code = map.get("code").toString();
        String accId = map.get("accId").toString();
        String user = map.get("user").toString();
        Mono<List<String>> yearsMone = groupUserOperatAuthZtRepository.findAllByUserUniqueCodeAndZtUniqueCode(user, code).map(it -> it.getZtYear()).distinct().collectList();
        return yearsMone.flatMap(years -> sysPeriodRepository.findAllByAccountIdAndIyearInOrderByIyearAscIperiodNumAsc(accId, years).collectList().map(list -> {
            list.stream().forEach(v -> {
                v.setValue(v.getIyear() + "-" + v.getIperiodNum());
            });
            // 获取指定数据在数组中的下标 截取开始年度以后的
            int index = IntStream.range(0, list.size()).filter(i -> list.get(i).getEnablePeriod().equals("1")).findFirst().orElse(-1);
            return index >= 0 ? list.subList(index, list.size()) : new ArrayList<>();
        }).map(o -> R.ok().setResult(o)));
    }
}

