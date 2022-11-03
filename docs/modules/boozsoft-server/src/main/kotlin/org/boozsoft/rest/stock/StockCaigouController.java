package org.boozsoft.rest.stock;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysZone;
import org.boozsoft.domain.entity.stock.Stock;
import org.boozsoft.repo.*;
import org.boozsoft.repo.group.GroupSysUserRepository;
import org.boozsoft.repo.project.base.ProjectRepositoryBase;
import org.boozsoft.repo.stock.*;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/caigou")
public class StockCaigouController {

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    SysDepartmentRepository sysDepartmentRepository;

    @Autowired
    SysPsnRepository sysPsnRepository;

    @Autowired
    StockClassRepository stockClassRepository;

    @Autowired
    CurrencyRepository currencyRepository;

    @Autowired
    private StockCangkuRepository stockCangkuRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private GroupSysUserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerClassRepository customerClassRepository;

    @Autowired
    private ProjectRepositoryBase projectRepositoryBase;
    @Autowired
    private StockShouFaTypeRepository shouFaTypeRepository;
    @Autowired
    private StockBomRepository bomRepository;


    //供应商
    @GetMapping("findSupplierList")
    public Mono<R> findSupplierList() {
        return supplierRepository.findAllByFlag("1").collectList().map(a -> {
            a.forEach(v -> {
                v.setLabel(v.getCustAbbname());
                v.setValue(v.getCustCode());
            });
            return R.ok().setResult(a);
        });
    }

    //仓库
    @GetMapping("findStockCangkuList")
    public Mono<R> findStockCangkuList() {
        return stockCangkuRepository.findAllOrderByCreatTime().collectList().map(a -> {
            a.forEach(v -> {
                v.setLabel(v.getCkName());
                v.setValue(v.getCkNum());
            });
            return R.ok().setResult(a);
        });
    }


    @GetMapping("findStockCaiGouList")
    public Mono<R> findStockCaiGouList(String types) {
        return Flux.fromIterable(Arrays.asList(types.split(","))).distinct().flatMap(s -> {
            Mono<Map> m = null;
            switch (s) {
                case ("supplierJs"):
                    m = supplierRepository.findAllByNoEmptyUniqueCodeCcus("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getCustAbbname());
                            v.setTitle(v.getCustCode()+" "+v.getCustAbbname());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("supplier"):
                    m = supplierRepository.findAllByFlag("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getCustAbbname());
                            v.setTitle(v.getCustCode()+" "+v.getCustAbbname());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("warehouse"):
                    m = stockCangkuRepository.findAllOrderByCreatTime().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getCkName());
                            v.setTitle(v.getCkNum()+" "+v.getCkName());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("dept"):
                    m = sysDepartmentRepository.findAllDeptCodeOrDeptNameByFlag().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getDeptName());
                            v.setTitle(v.getDeptCode()+" "+v.getDeptName());
                            v.setValue(v.getUniqueCode());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("user"):
                    m = sysPsnRepository.findAllByFlag("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getPsnName());
                            v.setTitle(v.getPsnCode() + " " +v.getPsnName());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("whClass"):
                    m = stockClassRepository.findAll().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getStockCclassName());
                            v.setValue(v.getUniqueStockclass());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("operator"):
                    m = userRepository.findAllByflag("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getRealName());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("cust"):
                    m = customerRepository.findAllByFlag("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getCustAbbname());
                            v.setTitle(v.getCustCode() + " " +v.getCustAbbname());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("custClass"):
                    m = customerClassRepository.findAll().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getCusCclassName());
                            v.setValue(v.getUniqueCustclass());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("stock"):
                    m = stockRepository.findAll().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getStockName());
                            v.setTitle(v.getStockNum() + " " +v.getStockName());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("project"):
                    m = projectRepositoryBase.findAll().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getProjectName());
                            v.setTitle(v.getProjectCode() + " " +v.getProjectName());
                            v.setValue(v.getId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("shoufa"):
                    m = shouFaTypeRepository.findAllByIsAccrual("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getEcName());
                            v.setTitle(v.getEcCode() + " " +v.getEcName());
                            v.setValue(v.getEcName());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("bom"):
                    m = bomRepository.findByBcheck("1").collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getBomName());
                            v.setTitle(v.getBomId() + " " +v.getBomName());
                            v.setValue(v.getBomUniqueId());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                case ("ywcom"):
                    //根据类型查询
                    m = stockRepository.findAll().collectList().map(a -> {
                        a.forEach(v -> {
                            v.setLabel(v.getStockName());
                            v.setTitle(v.getStockName());
                            v.setValue(v.getStockName());
                        });
                        return MapUtil.of(s, a);
                    });
                    break;
                default:
                    m = Mono.just(MapUtil.of(s, ""));
                    break;
            }
            return m;
        }).collectList().map(list -> {
            Map<String, Object> r = new HashMap<>();
            for (Map<String, Object> s : list) {
                String k = s.keySet().toArray()[0].toString();
                r.put(k, s.get(k));
            }
            return R.ok(r);
        });
    }


    @PostMapping("findCunHuoListByCondition")
    public Mono<R> findFaAssetListByCondition(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        String type = map.get("type").toString();
        String qValue = map.get("qValue").toString();
        Flux<Stock> stockMasterFlux = type.equals("one") ? stockRepository.findAllByCreateTimeLessThanAndStockNumLikeOrderByStockNum(date/*.substring(0, 7) + "-01"*/, "%" + qValue + "%") : stockRepository.findAllByCreateTimeLessThanAndStockNameLikeOrderByStockNum(date/*.substring(0, 7) + "-01"*/, "%" + qValue + "%");
        return stockMasterFlux.collectList().map(o -> R.ok(o));
    }

    @PostMapping("findCunHuoAllList")
    public Mono<R> findCunHuoAllList(@RequestBody Map map) {
        if (map.keySet().size() == 0) return Mono.just(R.error());
        String date = map.get("date").toString();
        Flux<Stock> stockMasterFlux = stockRepository.findAll();// stockRepository.findAllByCreateTimeLessThanEqualOrderByStockNum(date);
        return stockMasterFlux.collectList().map(o -> R.ok(o));
    }

    public List<Map<String, Object>> getChild(Integer pid, List<SysZone> allList) throws Exception {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (SysZone ms : allList) {
            if (ms.getPid().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("value", String.valueOf(ms.getId()));
                map.put("label", ms.getZoneName());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(Integer.valueOf(map.get("value").toString()), allList);
            map.put("children", tList);
        }
        return childList;
    }


}
