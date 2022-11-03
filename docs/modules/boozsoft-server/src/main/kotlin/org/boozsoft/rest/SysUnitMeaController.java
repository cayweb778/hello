package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.FaLocation;
import org.boozsoft.domain.entity.SysUnitOfMea;
import org.boozsoft.domain.entity.SysUnitOfMeaList;
import org.boozsoft.domain.entity.SysUnitOfMeaMany;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMea;
import org.boozsoft.domain.entity.group.GroupSysUnitOfMeaMany;
import org.boozsoft.domain.ro.SysUnitOfMeaManyRo;
import org.boozsoft.domain.vo.stock.SysUnitOfMeaListExcelVo;
import org.boozsoft.repo.SysUnitOfMeaListRepository;
import org.boozsoft.repo.SysUnitOfMeaManyRepository;
import org.boozsoft.repo.SysUnitOfMeaRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaListRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaManyRepository;
import org.boozsoft.repo.group.GroupSysUnitOfMeaRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 计量单位
 */
@RestController
@RequestMapping("/sys/unitmea")
public class SysUnitMeaController {

    @Autowired
    SysUnitOfMeaRepository sysUnitOfMeaRepository;

    @Autowired
    GroupSysUnitOfMeaRepository groupSysUnitOfMeaRepository;

    @Autowired
    SysUnitOfMeaManyRepository sysUnitOfMeaManyRepository;

    @Autowired
    GroupSysUnitOfMeaManyRepository groupSysUnitOfMeaManyRepository;

    @Autowired
    SysUnitOfMeaListRepository sysUnitOfMeaTypeRepository;

    @Autowired
    GroupSysUnitOfMeaListRepository groupSysUnitOfMeaTypeRepository;

    @GetMapping("findAllSysUnitOfMea")
    public Mono<R> findAllSysUnitOfMea(){
        return sysUnitOfMeaRepository.findAll().collectList()
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(@RequestBody Map maps){
        return sysUnitOfMeaRepository.findAllOrderById().collectList()
                .map(list->{
                    return list.stream().filter(v->{
                        if(maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())){
                            return false;
                        }
                        return true;
                    });
                })
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("findAllSysUnitOfMeaList")
    public Mono<R> findAllSysUnitOfMeaList(@RequestBody Map maps) {
        String manyCode = maps.get("manyCode").toString();
        return sysUnitOfMeaTypeRepository.findAllByManyCode(manyCode).collectList().map(list -> {
            return list.stream().filter(v -> {
                if (maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())) {
                    return false;
                }
                return true;
            });
        }).map(item -> R.ok().setResult(item));
    }

    @PostMapping("findUnitInfoById")
    public Mono<R> findUnitInfoById(@RequestBody Map maps) {
        return sysUnitOfMeaManyRepository.findById(maps.get("id").toString()).flatMap(db -> sysUnitOfMeaTypeRepository.findAllByManyCodeOrderByUnitCodeAsc(db.getUnitCode()).collectList().map(list -> {
            db.setDetail(JSON.toJSONString(list));
            return db;
        })).map(R::ok);
    }

    @PostMapping("findUnitInfoList")
    public Mono<R> findUnitInfoList() {
        return sysUnitOfMeaManyRepository.findAll().flatMap(db -> sysUnitOfMeaTypeRepository.findAllByManyCodeOrderByUnitCodeAsc(db.getUnitCode()).collectList().map(list -> {
            db.setDetail(JSON.toJSONString(list));
            return db;
        })).collectList().map(R::ok);
    }
    @PostMapping("findUnitAssociationList")
    public Mono<R> findUnitAssociationList() {
        return sysUnitOfMeaTypeRepository.findAll().collectList().map(R::ok);
    }
    @PostMapping("save")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono save(@RequestBody SysUnitOfMea object) {
        return sysUnitOfMeaManyRepository.findAll()
                .collectList()
                .flatMap(lists->{
                    return sysUnitOfMeaRepository.findAll().collectList().flatMap(list -> {
                        //验证单位名称不能重复
                        Optional<SysUnitOfMea> first = list.stream().filter(v -> object.getUnitName().equals(v.getUnitName())).findFirst();
                        if (first.isPresent()) {
                            return Mono.just("600");
                        }
                        //验证单计量单位名称不能重复
                        Optional<SysUnitOfMeaMany> sysUnitOfMea = lists.stream().filter(v -> object.getUnitName().equals(v.getUnitName())).findFirst();
                        if(sysUnitOfMea.isPresent()){
                            return Mono.just("600");
                        }

                        object.setCreateDate(LocalDateTime.now().toString());
                        object.setFlag("1");
                        String num = "001";
                        if (list.size() > 1) {
                            num = list.get(list.size() - 1).getUnitCode();
                        }
                        //object.setUnitCode(addOne(num));
                        object.setUnitCode(UUID.randomUUID().toString());
                        return sysUnitOfMeaRepository.save(object);
                    });
                })
                .map(o -> R.ok().setResult(o))
                .defaultIfEmpty(R.error());
    }

    @PostMapping("saveList")
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono saveList(@RequestBody Map maps) {
        List<String> list =  (List<String>)maps.get("ids");
        String typeCode =  maps.get("typeCode").toString();
        return  sysUnitOfMeaRepository.updateTypeCode(typeCode, list)
                .then(Mono.just(R.ok()));
    }

    public  static String addOne(String testStr){
        String[] strs = testStr.split( "[^0-9]" ); //根据不是数字的字符拆分字符串
        String numStr = strs[strs.length- 1 ]; //取出最后一组数字  
        if (numStr !=  null  && numStr.length()> 0 ){ //如果最后一组没有数字(也就是不以数字结尾)，抛NumberFormatException异常
            int  n = numStr.length(); //取出字符串的长度
            int  num = Integer.parseInt(numStr)+ 1 ; //将该数字加一
            String added = String.valueOf(num);
            n = Math.min(n, added.length());
            //拼接字符串
            return  testStr.subSequence( 0 , testStr.length()-n)+added;
        } else {
            throw  new  NumberFormatException();
        }
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody SysUnitOfMea object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return sysUnitOfMeaRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("delMany")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delMany(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaManyRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("delManyList")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delManyList(@RequestBody Map maps){
        List<String> list =  (List<String>)maps.get("ids");
        return sysUnitOfMeaTypeRepository.deleteByIds(list)
                .then(Mono.just(R.ok()));
    }

    /**
     * 查询所有分类list
     * @return
     */
    @PostMapping("findAllSysUnitOfMeaMany")
    public Mono<R> findAllSysUnitOfMeaMany(@RequestBody Map maps){
        return sysUnitOfMeaManyRepository.findAll().collectList()
                .map(list->{
                    return list.stream().filter(v->{
                        if(maps.containsKey("unitName") && !v.getUnitName().contains(maps.get("unitName").toString())){
                            return false;
                        }
                        return true;
                    });
                })
                .map(item->R.ok().setResult(item));
    }

    @GetMapping("findAllByUnitName")
    @ApiOperation(value = "只获取计量单位名称", notes = "传入code")
    public Mono findAllByUnitName(){
        return sysUnitOfMeaRepository.findAllByUnitName().collectList().map(item->R.ok().setResult(item));
    }


    @GetMapping("delType/{id}")
    public Mono<R> delType(@PathVariable String id){
        return sysUnitOfMeaTypeRepository.deleteById(id)
                .thenReturn(R.ok());
    }

    @PostMapping("saveManyList")
    @ApiOperation(value = "新增", notes = "传入code")
    public Mono saveManyList(@RequestBody Map maps){

        String unitName = maps.get("unitName").toString();
        String conversionType = maps.get("conversionType").toString();
        String unitType = maps.get("unitType").toString();

        String list = JSONObject.toJSONString(maps.get("list"));
        List<SysUnitOfMeaList> list1 = JSONObject.parseArray(list, SysUnitOfMeaList.class);

        return sysUnitOfMeaRepository.findAll()
                .collectList()
                .flatMap(slist->{
                    return  sysUnitOfMeaManyRepository.findAll()
                            .collectList()
                            .flatMap(lists->{
                                //验证单计量单位名称不能重复
                                Optional<SysUnitOfMea> sysUnitOfMea = slist.stream().filter(v -> unitName.equals(v.getUnitName())).findFirst();
                                if(sysUnitOfMea.isPresent()){
                                    return Mono.just("600");
                                }
                                //验证多计量单位名称不能重复
                                Optional<SysUnitOfMeaMany> first = lists.stream().filter(v -> unitName.equals(v.getUnitName())).findFirst();
                                if(first.isPresent()){
                                    return Mono.just("600");
                                }
                                SysUnitOfMeaMany so =  new SysUnitOfMeaMany();
                                so.setUnitName(unitName);
                                so.setConversionType(conversionType);
                                so.setFlag("1");
                                so.setCreateDate(LocalDate.now().toString());
                                so.setUnitType(unitType);
                                String num = "001";
                                if(lists.size() > 1){
                                    num = lists.get(lists.size()-1).getUnitCode();
                                }
                                //so.setUnitCode(addOne(num));
                                so.setUnitCode(UUID.randomUUID().toString());

                                return sysUnitOfMeaManyRepository.save(so)
                                        .zipWith(
                                                sysUnitOfMeaTypeRepository.findMaxCode()
                                                        .defaultIfEmpty("001")
                                                        .map(obj->{
                                                            AtomicReference<String> a = new AtomicReference<>(addOne(obj));
                                                            list1.forEach(v->{
                                                                v.setManyCode(so.getUnitCode());
                                                                v.setUnitCode(a.get());
                                                                a.set(addOne(a.get()));
                                                            });
                                                            return list1;
                                                        }).flatMap(list2->{
                                                            return sysUnitOfMeaTypeRepository.saveAll(list2).collectList();
                                                        })
                                        );
                            });
                })
                .map(o -> R.ok().setResult(o))
                .defaultIfEmpty(R.error());

    }


    @PostMapping("editManyList")
    @ApiOperation(value = "修改", notes = "传入code")
    public Mono editManyList(@RequestBody Map maps){

        String unitCode = maps.get("unitCode").toString();
        String unitName = maps.get("unitName").toString();
        String conversionType = maps.get("conversionType").toString();
        String unitType = maps.get("unitType").toString();
        String list = JSONObject.toJSONString(maps.get("list"));
        List<SysUnitOfMeaList> list1 = JSONObject.parseArray(list, SysUnitOfMeaList.class);

        return sysUnitOfMeaRepository.findAll()
                .collectList()
                .flatMap(slist->{
                    return  sysUnitOfMeaManyRepository.findAll()
                            .collectList()
                            .flatMap(lists->{
                                //验证单计量单位名称不能重复
                                Optional<SysUnitOfMea> sysUnitOfMea = slist.stream().filter(v -> unitName.equals(v.getUnitName())).findFirst();
                                if(sysUnitOfMea.isPresent()){
                                    return Mono.just("600");
                                }
                                //验证多计量单位名称不能重复
                                SysUnitOfMeaMany so = lists.stream().filter(v -> unitCode.equals(v.getUnitCode())).findFirst().get();
                                if(!so.getUnitName().equals(unitName)){
                                    //验证单位名称不能重复
                                    Optional<SysUnitOfMeaMany> first = lists.stream().filter(v -> unitName.equals(v.getUnitName())).findFirst();
                                    if(first.isPresent()){
                                        return Mono.just("600");
                                    }
                                }
                                so.setUnitName(unitName);
                                so.setUnitType(unitType);
                                so.setConversionType(conversionType);
                                return sysUnitOfMeaManyRepository.save(so)
                                        .zipWith(
                                                sysUnitOfMeaTypeRepository.findMaxCode()
                                                        .defaultIfEmpty("001")
                                                        .flatMap(obj->{
                                                            return sysUnitOfMeaTypeRepository.deleteByManyCode(unitCode).then(Mono.just(obj));
                                                        })
                                                        .map(obj->{
                                                            AtomicReference<String> a = new AtomicReference<>(addOne(obj));
                                                            list1.forEach(v->{
                                                                v.setManyCode(so.getUnitCode());
                                                                v.setUnitCode(a.get());
                                                                a.set(addOne(a.get()));
                                                            });
                                                            return list1;
                                                        })
                                                        .flatMap(list2->{
                                                            return sysUnitOfMeaTypeRepository.saveAll(list2).collectList();
                                                        })
                                        );
                            });
                })
                .map(o -> R.ok().setResult(o))
                .defaultIfEmpty(R.error());

    }

    @PostMapping("introduceData")
    @ApiOperation(value = "引入单计量", notes = "传入ids")
    public Mono introduceData(@RequestBody List<GroupSysUnitOfMea> ids){
        return Mono.just(ids)
                .flatMap(list->{
                    List<SysUnitOfMea> l =  new ArrayList<>();
                    list.forEach(v->{
                        SysUnitOfMea b =  new SysUnitOfMea();
                        BeanUtils.copyProperties(v,b);
                        b.setId(null);
                        l.add(b);
                    });
                    return  sysUnitOfMeaRepository.saveAll(l).then(Mono.just("200"));
                }).map(o->R.ok().setResult(o));
    }

    @PostMapping("introduceDatas")
    @ApiOperation(value = "引入多计量", notes = "传入ids")
    public Mono introduceDatas(@RequestBody List<GroupSysUnitOfMeaMany> ids){
        return Mono.just(ids)
                .flatMap(list->{
                    List<SysUnitOfMeaMany> l =  new ArrayList<>();
                    list.forEach(v->{
                        SysUnitOfMeaMany b =  new SysUnitOfMeaMany();
                        BeanUtils.copyProperties(v,b);
                        b.setId(null);
                        l.add(b);
                    });
                    return  sysUnitOfMeaManyRepository.saveAll(l).then(Mono.just(list));
                })
                .flatMap(list->{
                    List<String> collect = list.stream().map(v -> v.getUnitCode()).collect(Collectors.toList());
                    return groupSysUnitOfMeaTypeRepository.findAllByManyCodes(collect).collectList();
                })
                .flatMap(list->{
                    //查询下属数据
                    List<SysUnitOfMeaList> l =  new ArrayList<>();
                    list.forEach(v->{
                        SysUnitOfMeaList b =  new SysUnitOfMeaList();
                        BeanUtils.copyProperties(v,b);
                        b.setId(null);
                        l.add(b);
                    });
                    return  sysUnitOfMeaTypeRepository.saveAll(l).then(Mono.just("200"));
                })
                .map(o->R.ok().setResult(o));
    }

    @GetMapping("findInAll")
    @ApiOperation(value = "单剂量引入列表", notes = "引入列表")
    public Mono<R> findInAll(Pageable pageable){
        return groupSysUnitOfMeaRepository.findAll().collectList()
                .flatMap(list->{
                    return sysUnitOfMeaRepository.findAll().collectList()
                            .map(v->v.stream().map(t-> t.getUnitName()).collect(Collectors.toList()))
                            .map(v->{
                                return list.stream().filter(o-> !v.contains(o.getUnitName()));
                            });
                })
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findInsAll")
    @ApiOperation(value = "多剂量引入列表", notes = "引入列表")
    public Mono<R> findInsAll(Pageable pageable){
        return groupSysUnitOfMeaManyRepository.findAll().collectList()
                .flatMap(list->{
                    return sysUnitOfMeaManyRepository.findAll().collectList()
                            .map(v->v.stream().map(t-> t.getUnitName()).collect(Collectors.toList()))
                            .map(v->{
                                return list.stream().filter(o-> !v.contains(o.getUnitName()));
                            });
                })
                .map(o-> R.ok().setResult(o));
    }

    //单剂量
    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<SysUnitOfMea> object){
        object.stream().forEach(v->{
            v.setUnitCode(UUID.randomUUID().toString());
            v.setCreateDate(LocalDate.now().toString());
            v.setFlag("1");
        });
        return sysUnitOfMeaRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }
    //多剂量
    @PostMapping("excels")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excels(@RequestBody List<SysUnitOfMeaManyRo> object){
        List<SysUnitOfMeaMany> slist = new ArrayList<>();
        List<SysUnitOfMeaList> lists = new ArrayList<>();

        object.stream().forEach(v->{
            SysUnitOfMeaMany sm = new SysUnitOfMeaMany();
            sm.setUnitCode(UUID.randomUUID().toString());
            sm.setUnitName(v.getUnitNameg());
            sm.setUnitType(v.getUnitType());
            sm.setCreateDate(LocalDate.now().toString());
            sm.setFlag("1");
            slist.add(sm);

            SysUnitOfMeaList sl = new SysUnitOfMeaList();
            sl.setManyCode(sm.getUnitCode());
            sl.setIsMain("true");
            sl.setUnitCode(UUID.randomUUID().toString());
            sl.setConversionRate("1");
            sl.setUnitName(v.getUnitName());
            lists.add(sl);

            if(Objects.nonNull(v.getUnitName1())){
                SysUnitOfMeaList sl1 = new SysUnitOfMeaList();
                sl1.setManyCode(sm.getUnitCode());
                sl1.setIsMain("false");
                sl1.setUnitCode(UUID.randomUUID().toString());
                sl1.setConversionRate(v.getConversionRate1());
                sl1.setConversionExplain(v.getConversionExplain1());
                sl1.setUnitName(v.getUnitName1());
                lists.add(sl1);
            }

            if(Objects.nonNull(v.getUnitName2())){
                SysUnitOfMeaList sl2 = new SysUnitOfMeaList();
                sl2.setManyCode(sm.getUnitCode());
                sl2.setIsMain("false");
                sl2.setUnitCode(UUID.randomUUID().toString());
                sl2.setConversionRate(v.getConversionRate2());
                sl2.setConversionExplain(v.getConversionExplain2());
                sl2.setUnitName(v.getUnitName2());
                lists.add(sl2);
            }

        });
        return sysUnitOfMeaManyRepository.saveAll(slist)
                .collectList()
                .flatMap(list-> {
                    return sysUnitOfMeaTypeRepository.saveAll(lists).collectList();
                })
                .map(o-> R.ok().setResult(o));
    }

    @GetMapping("findMeaTypeList")
    @ApiOperation(value = "多计量列表", notes = "多计量列表")
    public Mono<R> findMeaTypeList(){
        return sysUnitOfMeaTypeRepository.findAll().collectList()
                .map(o-> R.ok().setResult(o));
    }
    @GetMapping("findMeaTypeAll")
    @ApiOperation(value = "多计量组列表", notes = "多计量组列表")
    public Mono<R> findMeaTypeAll(){
        return sysUnitOfMeaManyRepository.findAll().collectList()
                .map(o-> R.ok().setResult(o));
    }


    @GetMapping("/findListByTypeAndId/{type}/{id}")
    @ApiOperation(value = "获取计量单位信息", notes = "获取计量单位信息")
    public Mono<R> findListByTypeAndId(@PathVariable String type, @PathVariable String id){
        Mono<R> m1 = sysUnitOfMeaRepository.findById(id)
                .map(v -> R.ok().setResult(v));

        Mono<R> m2 =sysUnitOfMeaTypeRepository.findAllByManyid(id)
                .collectList()
                .map(v -> R.ok().setResult(v));

        return "多计量".equals(type)? m2: m1;
    }

    @GetMapping("/getExcelData")
    @ApiOperation(value = "获取计量单位信息", notes = "获取计量单位信息")
    public Mono<R> getExcelData(){
        return sysUnitOfMeaManyRepository.findAll().collectList()
                .flatMap(list->{
                    return sysUnitOfMeaTypeRepository.findAll().collectList()
                            .map(lists->{
                                //汇总成一条 	计量单位组名称', '计量类型', '主计量','计量1名称','换算率1','计量2名称','换算率2
                                List<SysUnitOfMeaListExcelVo> seList =  new ArrayList<>();
                                list.forEach(o->{
                                    List<SysUnitOfMeaList> collect = lists.stream().filter(v -> o.getUnitCode().equals(v.getManyCode())).collect(Collectors.toList());
                                    SysUnitOfMeaListExcelVo  se = new SysUnitOfMeaListExcelVo();
                                    se.setStr1(o.getUnitName());
                                    se.setStr2(o.getUnitType());
                                    se.setStr3(collect.get(0).getUnitName());
                                    if(collect.size() > 1) {
                                        se.setStr4(collect.get(1).getUnitName());
                                        se.setStr5(collect.get(1).getConversionRate());
                                    }
                                    if(collect.size() > 2){
                                        se.setStr6(collect.get(2).getUnitName());
                                        se.setStr7(collect.get(2).getConversionRate());
                                    }
                                    seList.add(se);
                                });
                                return seList;
                            });
                })
                .map(v -> R.ok().setResult(v));

    }

}
