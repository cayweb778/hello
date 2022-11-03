package org.boozsoft.rest.stock;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.stock.StockShouFaType;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.repo.stock.StockShouFaTypeRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sys/shouFaType")
public class StockShouFaTypeController {

    @Autowired
    private StockShouFaTypeRepository groupExpenditureClassRepository;


    @GetMapping("treeDept")
    public Mono<Map> treeDept(String id, String flag) {
        return groupExpenditureClassRepository.findByIdOrderByDeptCode(id)
                .collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(flag)) {
                        return list.stream().filter(item -> flag.contains(item.getFlag())).collect(Collectors.toList());
                    }
                    return list;
                })
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("treeDeptByFlag")
    public Mono<Map> treeDeptByFlag() {
        return groupExpenditureClassRepository.findByFlagOrderByEcCode("1")
                .collectList()
                .map(list -> TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("treeDeptByIsEnd")
    public Mono<Map> treeDeptByIsEnd() {
        return groupExpenditureClassRepository.findByFlagAndBendOrderByEcCode("1", "1")
                .collectList()
                .map(list -> TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return groupExpenditureClassRepository.findAllByOrderByEcCode().collectList().map(R::page);
    }
    @GetMapping("findAll2")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll2() {
        return groupExpenditureClassRepository.findAllByOrderByEcCode2().collectList().map(R::page);
    }
    @GetMapping("all")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllDept() {
        return groupExpenditureClassRepository.findAllDeptCodeOrDeptNameByFlag().collectList().map(R::ok);
    }

    @GetMapping("allMinus")
    @ApiOperation(value = "查询减少末级科目列表", notes = "传入code")
    public Mono<R> allMinus() {
        return groupExpenditureClassRepository.findAllByFlagAndBendAndIsAccrualOrderByEcCodeAsc("1", "1", "2").collectList().map(R::ok);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id) {
        Mono mono = groupExpenditureClassRepository.findById(id);
        return mono;
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody StockShouFaType object) {
        if (object.getUniqueCode() == null || object.getUniqueCode().equals("")) {
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag() == null || object.getFlag().equals("")) {
            object.setFlag("1");
        }

        //1级 无上级
        if (object.getParentId() == null || object.getParentId().equals("")) {
            object.setParentId("0");
            object.setBend("1");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object.getCreateDate() == null || object.getCreateDate().equals("")) {
            object.setCreateDate(sdf.format(new Date()));
        }

        //有上级不是末级时
        if (object.getParentId() != null && !object.getParentId().equals("") && object.getBend().equals("0")) {
            object.setBend("1");
        }
        //有上级是末级时
        Mono<StockShouFaType> sysDeptClassMono = null;
        if (object.getParentId() != null && !object.getParentId().equals("") && !object.getParentId().equals("0") && object.getBend().equals("1")) {
            object.setBend("1");
            sysDeptClassMono = groupExpenditureClassRepository.findById(object.getParentId())
                    .map(v -> {
                        v.setBend("0");
                        return v;
                    })
                    .flatMap(groupExpenditureClassRepository::save);
        }
        //增加
        if (object.getId() == null || object.getId().equals("")) {
            return Objects.isNull(sysDeptClassMono) ? groupExpenditureClassRepository.save(object)
                    .map(o -> R.ok().setResult(o)) : sysDeptClassMono.zipWith(groupExpenditureClassRepository.save(object)
                    .map(o -> R.ok().setResult(o))).thenReturn(R.ok());
        }

        return Objects.isNull(sysDeptClassMono) ? groupExpenditureClassRepository.save(object)
                .map(o -> R.ok().setResult(o)) : sysDeptClassMono.zipWith(groupExpenditureClassRepository.save(object)
                .map(o -> R.ok().setResult(o))).thenReturn(R.ok());
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody List<StockShouFaType> object) {
        return groupExpenditureClassRepository.deleteAll(object).then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody StockShouFaType object) {
        if (object.getFlag().equals("1")) {
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return groupExpenditureClassRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<StockShouFaType> object) {
        object.stream().forEach(v -> {
            v.setUniqueCode(IdUtil.objectId());
            v.setCreateDate(LocalDate.now().toString());
        });
        return groupExpenditureClassRepository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("/getMaxCode")
    @ApiOperation(value = "获取最大code", notes = "获取最大code")
    public Mono getMaxCode(String id) {
        return groupExpenditureClassRepository.findMaxCodeByPid(id).collectList()
            .flatMap(list -> {
                List<Integer> list2=new ArrayList<>();
                for (int i = 0; i < 100; i++) { list2.add(i+1); }
                // 查找科目编码 中间 不连续，得出最小值;等于0 说明到头了【最大99】
                int minCode = list2.stream().filter(item -> !list.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                String str=minCode<10?"0"+minCode:String.valueOf(minCode);
                return Mono.just(str);
            })
            .map(v -> R.ok().setResult(v));
    }
    @GetMapping("/getMaxCode2")
    @ApiOperation(value = "获取最大code", notes = "获取最大code")
    public Mono getMaxCode2(String id) {
        return groupExpenditureClassRepository.findMaxCodeByPid2(id).map(v -> R.ok().setResult(v));
    }

    @PostMapping("saveShouAndFa")
    public Mono saveShouAndFa() {
        List<StockShouFaType> list=new ArrayList<>();
        StockShouFaType v=new StockShouFaType();
        v.setEcCode("1");
        v.setUniqueCode("1");
        v.setEcName("收");
        v.setBend("1");
        v.setFlag("1");
        v.setParentId("0");
        v.setIsAccrual("1");
        v.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        list.add(v);
        StockShouFaType v2=new StockShouFaType();
        v2.setEcCode("2");
        v2.setUniqueCode("2");
        v2.setEcName("发");
        v2.setBend("1");
        v2.setFlag("1");
        v2.setParentId("0");
        v2.setIsAccrual("2");
        v2.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        list.add(v2);
        return groupExpenditureClassRepository.saveAll(list).collectList().then(Mono.just(""));
    }
    @PostMapping("findByShouAndFa")
    public Mono findByShouAndFa() {
        return groupExpenditureClassRepository.findByShouAndFa().map(v -> R.ok().setResult(v));
    }

    @PostMapping("findByLikeEcName")
    public Mono findByShouAndFa(String ecName){
        return groupExpenditureClassRepository.findByLikeEcName(ecName).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @GetMapping("initShouAndFa")
    public Mono initShouAndFa() {

        String[] str1 = {"101-采购入库","102-其他入库","103-产成品入库","104-盘盈入库","105-转换入库","106-借入入库","107-借入还回","108-组装入库","109-拆卸入库","110-调拨入库",};
        String[] str2 = {"201-销售出库","202-其他出库","203-材料领用","204-盘亏出库","205-转换出库","206-组装出库","207-拆卸出库","208-调拨出库"};


        return groupExpenditureClassRepository.findAll().collectList()
                .flatMap(item->{
                    if(item.size() > 0){
                        return Mono.just("0");
                    }else{
                        List<StockShouFaType> list2 = new ArrayList<>();
                        StockShouFaType shou2 = new StockShouFaType();
                        shou2.setEcCode("1");
                        shou2.setEcName("收");
                        shou2.setBend("0");
                        shou2.setParentId("0");
                        String ss = UUID.randomUUID().toString().replaceAll("-", "");
                        shou2.setUniqueCode(ss);
                        shou2.setCreateDate(LocalDate.now().toString());
                        shou2.setFlag("1");
                        shou2.setIsAccrual("1");
                        shou2.setZjType("1");
                        list2.add(shou2);

                        StockShouFaType fa2 = new StockShouFaType();
                        fa2.setEcCode("2");
                        fa2.setEcName("发");
                        //ssf.setEcLevel();
                        fa2.setBend("0");
                        fa2.setParentId("0");
                        String ff = UUID.randomUUID().toString().replaceAll("-", "");
                        fa2.setUniqueCode(ff);
                        fa2.setCreateDate(LocalDate.now().toString());
                        fa2.setFlag("1");
                        fa2.setIsAccrual("2");
                        fa2.setZjType("1");
                        list2.add(fa2);
                        return groupExpenditureClassRepository.saveAll(list2)
                                .collectList()
                                .flatMap(clist->{
                                    List<StockShouFaType> list = new ArrayList<>();
                                    Optional<StockShouFaType> first = clist.stream().filter(v -> "收".equals(v.getEcName())).findFirst();
                                    Arrays.stream(str1).forEach(v->{
                                        StockShouFaType ssf = new StockShouFaType();
                                        String[] split = v.split("-");
                                        ssf.setEcCode(split[0]);
                                        ssf.setEcName(split[1]);
                                        //ssf.setEcLevel();
                                        ssf.setBend("1");
                                        ssf.setParentId(first.get().getId());
                                        ssf.setUniqueCode(UUID.randomUUID().toString().replaceAll("-", ""));
                                        ssf.setCreateDate(LocalDate.now().toString());
                                        ssf.setFlag("1");
                                        ssf.setIsAccrual("1");
                                        ssf.setZjType("1");
                                        list.add(ssf);
                                    });

                                    Optional<StockShouFaType> first1 = clist.stream().filter(v -> "发".equals(v.getEcName())).findFirst();
                                    Arrays.stream(str2).forEach(v->{
                                        StockShouFaType ssf = new StockShouFaType();
                                        String[] split = v.split("-");
                                        ssf.setEcCode(split[0]);
                                        ssf.setEcName(split[1]);
                                        //ssf.setEcLevel();
                                        ssf.setBend("1");
                                        ssf.setParentId(first1.get().getId());
                                        ssf.setUniqueCode(UUID.randomUUID().toString().replaceAll("-", ""));
                                        ssf.setCreateDate(LocalDate.now().toString());
                                        ssf.setFlag("1");
                                        ssf.setIsAccrual("2");
                                        ssf.setZjType("1");
                                        list.add(ssf);
                                    });
                                    return groupExpenditureClassRepository.saveAll(list)
                                            .collectList()
                                            .map(v -> Mono.just("1"));
                                });

                    }
                })
                .map(v -> R.ok().setResult(v));
    }
}
