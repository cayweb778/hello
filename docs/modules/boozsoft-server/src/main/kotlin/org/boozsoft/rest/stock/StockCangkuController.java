package org.boozsoft.rest.stock;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.common.utils.MapUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.stock.StockCangku;
import org.boozsoft.domain.entity.stock.StockCangkuLevel;
import org.boozsoft.domain.entity.stock.StockCangkuLevelRecord;
import org.boozsoft.domain.vo.stock.StockCangkuLevelRecordVo;
import org.boozsoft.repo.stock.StockCangkuLevelRecordRepository;
import org.boozsoft.repo.stock.StockCangkuLevelRepository;
import org.boozsoft.repo.stock.StockCangkuRepository;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cangku")
public class StockCangkuController {

    @Autowired
    private StockCangkuRepository stockCangkuRepository;
    @Autowired
    StockCangkuLevelRecordRepository stockCangkuLevelRecordRepository;


    private String getFieldValueByFieldName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value.toString();
        } catch (Exception e) {
            return null;
        }
    }
    @PostMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findAll(@RequestBody Map map, Pageable pageable){
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockCangkuRepository.findAllOrderByCreatTime()
                .collectList().flatMap(item2->{
                    List<StockCangku> list=item2.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());


                    long total=list.size();
                    totalAR.set(total);
                    return Mono.just(list.stream().filter(item->{
                        // 按条件过滤
                        if (StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if(searchMap.get("requirement").trim().equals("custCode")){
                                if (Objects.nonNull(dbValue) && !dbValue.equals(value)) {
                                    return false;
                                }
                            }else{
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }

    @PostMapping("findByAll")
    @ApiOperation(value = "查询列表", notes = "传入类型")
    public Mono<R> findByAll(@RequestBody Map map, Pageable pageable){
        String ckIsDuli=map.get("ckIsDuli").toString();
        // 当前页
        int page= map.get("page")==null?1:Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= map.get("size")==null?10000:Integer.parseInt(map.get("size").toString());
        AtomicReference<Long> totalAR = new AtomicReference(0);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        return stockCangkuRepository.findAllOrderByCreatTime()
                .collectList().flatMap(item2->{
                    // 0是全部
                    if(!ckIsDuli.equals("")){
                        item2=item2.stream().filter(f->f.getCkIsDuli().equals(ckIsDuli)).collect(Collectors.toList());
                    }
                    List<StockCangku> list=item2.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());


                    long total=list.size();
                    totalAR.set(total);
                    return Mono.just(list.stream().filter(item->{
                        // 按条件过滤
                        if (null !=searchMap && StringUtils.isNotBlank(searchMap.get("requirement")) && StringUtils.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), item);
                            if(searchMap.get("requirement").trim().equals("custCode")){
                                if (Objects.nonNull(dbValue) && !dbValue.equals(value)) {
                                    return false;
                                }
                            }else{
                                if (Objects.nonNull(dbValue) && !dbValue.contains(value)) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }));
                })
                .map(a -> R.page(a.collect(Collectors.toList()),pageable,(totalAR.get())));
    }


    @PostMapping("ckSave")
    public Mono<R> ckSave(@RequestBody StockCangku entity) {
        entity.setCreatTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
         return stockCangkuRepository.save(entity).map(o -> R.ok().setResult(o));
    }

    // 删除仓库同时删除仓库级别档案表
    @PostMapping("delById")
    public Mono<R> delById(@RequestBody List<StockCangku> list) {
        List<String> id = list.stream().map(StockCangku::getId).collect(Collectors.toList());
        return Mono.zip(stockCangkuRepository.delAllById(id),stockCangkuLevelRecordRepository.deleteByCangkuId(id)).then(Mono.just(R.ok()));
    }

    @PostMapping("findByCkNum")
    public Mono<R> findByCkNum(String ckNum) {
        return stockCangkuRepository.countByCkNum(ckNum.trim()).map(a->R.ok().setResult(a));
    }
    @PostMapping("findByCkName")
    public Mono<R> findByCkName(String ckName) {
        return stockCangkuRepository.countByCkName(ckName.trim()).map(a->R.ok().setResult(a));
    }

    @PostMapping("findByCkIsDuli")
    public Mono<R> findByCkIsDuli(String ckIsDuli) {
        return stockCangkuRepository.findByCkIsDuli(ckIsDuli).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("findByCkId")
    public Mono<R> findByCkId(String id) {
        return stockCangkuRepository.findAll().collectList()
                .flatMap(list->{
                    return stockCangkuLevelRecordRepository.findAll().collectList()
                            .flatMap(recordList->{
                                StockCangku ck=new StockCangku();
                                List<StockCangku> collect = list.stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList());
                                if(collect.size()>0){
                                    ck=collect.get(0);
                                }else{
                                    List<StockCangkuLevelRecord> collect1 = recordList.stream().filter(a -> a.getId().equals(id)).collect(Collectors.toList());
                                    if(collect1.size()>0){
                                        ck.setId(collect1.get(0).getId());
                                        ck.setCkIsDuli("0");
                                        ck.setCkNum(collect1.get(0).getId());
                                        ck.setCkName(collect1.get(0).getRecordName());
                                    }
                                }
                                return Mono.just(ck);
                            });
                }).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
//        return stockCangkuRepository.findById(id).map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("importCangku")
    public Mono<R> importCangku(@RequestPart("file") FilePart filePartParm,@RequestPart("ckLevelList") String ckLevelStrt) throws Exception {
        // 仓库级别
        List<Map<String,String>> cangkuLevelList = (List<Map<String,String>>) JSONArray.parse(ckLevelStrt);
        // 获取表头个数
        AtomicInteger column= new AtomicInteger();
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                // 上传到临时目录
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .doOnComplete(() -> log.info("上传成功"))
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })
                .flatMap(temp->{
                    return stockCangkuRepository.findAll().collectList()
                            .flatMap(cklist->  {
                                return Mono.just(cklist);
                            });
                })
                .flatMap(item->{
                    List<StockCangku> cklist = item;

                    String[] str = new String[]{"仓库编码","仓库名称","仓库级别(*\\*)","管理属性","所在城市","仓库注释",""};
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    List<Object[]> list = excelReader.getExcelObj(tempFilePath.toString(), str, 0);

                    column.set(str.length-1);
                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {}

                    Map mapArr = new HashMap();
                    mapArr.put("code", "200");
                    mapArr.put("error", "");
                    mapArr.put("excellist",list);
                    mapArr.put("cklist",cklist);
                    return Mono.just(mapArr);
                })
                .flatMap(mapArr->{
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<StockCangku> cklist = (List<StockCangku>) mapArr.get("cklist");

                    List<StockCangku> codelist = new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        int a=column.get();
                        Object[] obj=excellist.get(i);
                        int finalI = i;
                        List<String>errorText=new ArrayList<>();

                        // 编码是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())){
                            List<StockCangku> collect = cklist.stream().filter(v -> v.getCkNum().equals(excellist.get(finalI)[0].toString())).collect(Collectors.toList());
                            if(collect.size()>0){
                                errorText.add("仓库编码与当前已存在档案重复");
                                obj[a]=errorText.toString();if(codelist.size()>0){if(codelist.size()>0){codelist.remove(0);}}
                            }
                        }else{
                            errorText.add("仓库编码不能为空");
                            obj[a]=errorText.toString();if(codelist.size()>0){codelist.remove(0);}
                        }
                        // 名称是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())){
                            List<StockCangku> collect = cklist.stream().filter(v -> v.getCkName().equals(excellist.get(finalI)[1].toString())).collect(Collectors.toList());
                            if(collect.size()>0){
                                errorText.add("仓库名称与当前已存在档案重复");
                                obj[a]=errorText.toString();if(codelist.size()>0){if(codelist.size()>0){codelist.remove(0);}}
                            }
                        }else{
                            errorText.add("仓库名称不能为空");
                            obj[a]=errorText.toString();if(codelist.size()>0){codelist.remove(0);}
                        }

                        // 仓库级别
                        if(StrUtil.isNotBlank(excellist.get(finalI)[2].toString().trim())){
                            List<Map> collect = cangkuLevelList.stream().filter(cl -> cl.get("title").equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            if(collect.size()==0){
                                errorText.add("请在系统中添加仓库级别");
                                obj[a]=errorText.toString();if(codelist.size()>0){codelist.remove(0);}
                            }
                        }else{
                            errorText.add("仓库级别不能为空");
                            obj[a]=errorText.toString();if(codelist.size()>0){codelist.remove(0);}
                        }

                        if(errorText.size()==0){
                            StockCangku ck=new StockCangku();
                            ck.setCkFlag("1");
                            ck.setCkNum(excellist.get(finalI)[0].toString().trim());
                            ck.setCkName(excellist.get(finalI)[1].toString().trim());
                            String level="";
                            String levelOrder="1";
                            String type="1";
                            List<Map> collect = cangkuLevelList.stream().filter(cl -> cl.get("title").equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            level=collect.get(0).get("value").toString();
                            levelOrder=collect.get(0).get("levelOrder").toString();

                            if(StrUtil.isNotBlank(excellist.get(finalI)[3].toString().trim())){
                                if(excellist.get(finalI)[3].toString().trim().equals("自有仓库")){
                                    level="1";
                                }else if(excellist.get(finalI)[3].toString().trim().equals("供应商直发库")){
                                    level="2";
                                }
                            }
                            ck.setCkLevel(level);
                            ck.setCkType(type);
                            ck.setCkIsDuli(String.valueOf(Integer.valueOf(levelOrder)>1?0:1));
                            ck.setCkCity(excellist.get(finalI)[4].toString().trim());
                            ck.setCkRemark(excellist.get(finalI)[5].toString().trim());
                            ck.setCountry("中国");
                            ck.setCreatTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            codelist.add(ck);
                        }
                        else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                    }
                    // 进行去重
                    List<String> listStr=new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        listStr.add(excellist.get(i)[0].toString().trim());    // 编码
//                        listStr.add(excellist.get(i)[1].toString().trim());    // 名称
                    }
                    // 去重后list
                    long doublesize=listStr.stream().distinct().count();
                    if(doublesize<listStr.size()){
                        mapArr.put("error", "文件中科目编码有重复数据，请仔细检查再进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    /**************************************END*************************************/
                    mapArr.put("list", codelist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<StockCangku> codelist= (List<StockCangku>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) :codelist.size()==0?Mono.just(mapArr): stockCangkuRepository.saveAll(codelist)
                            .collectList().map(a->mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByCkNoIsDuliTree")
    public Mono<R> findByCkNoIsDuliTree(){
        return stockCangkuRepository.findByCkIsDuli("0").collectList()
        .flatMap(list->{
            return stockCangkuLevelRecordRepository.findAllRecord().collectList()
            .flatMap(recordlist->{
                Map map = new HashMap();
                map.put("list",list);
                map.put("recordlist",recordlist);
                return Mono.just(map);
            });
        })
        .flatMap(maparr->{
            List<StockCangku> ckList= (List<StockCangku>) maparr.get("list");
            List<StockCangkuLevelRecordVo> recordlist= (List<StockCangkuLevelRecordVo>) maparr.get("recordlist");
            List<Map>newlist=new ArrayList<>();
            ckList.forEach(item->{
                item.setCangkuLevelOrder("1");
                item.setParentId("0");
                item.setCangkuId(item.getId());
                Map map=new HashMap();
                List<Map<String, Object>> child = getChild(item.getId(), recordlist.stream().filter(a->a.getParentId().equals("0")&& a.getRecordBend().equals("0")).collect(Collectors.toList()));
                if(child.size()>0){
                    map.put("children", child);
                }else{
                    map.put("children", new Object[]{});
                }
                map.put("title", item.getCkName());
                map.put("value", item);
                map.put("type", "jibie");
                map.put("key", item.getId());
                newlist.add(map);
            });
            return Mono.just(newlist);
        })
        .map(a->R.ok().setResult(a));
    }


    @GetMapping("findByNewCkNum")
    public Mono<R> findByNewCkNum(){
        return stockCangkuRepository.findAll().collectList()
                .flatMap(list->{
                    List<String> ckNumList = list.stream().map(a->a.getCkNum()).collect(Collectors.toList());
                    List<Integer> intlist=new ArrayList<>();
                    List<Integer> list2 = new ArrayList<>();

                    ckNumList.forEach(a->{
                        if(isDigit(a)){
                            intlist.add(Integer.valueOf(a));
                        }
                    });
                    for (int i = 0; i < 1000; i++) {
                        list2.add(i + 1);
                    }

                    int minCode = list2.stream().filter(item -> !intlist.contains(item)).mapToInt(Integer::intValue).min().orElse(0);
                    return Mono.just(minCode);
                }).map(a->R.ok().setResult(a));
    }

    public List<Map<String, Object>> getChild(String pid, List<StockCangkuLevelRecordVo> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (StockCangkuLevelRecordVo ms : allList) {
            if (ms.getCangkuId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                ms.setParentId(ms.getId());
                Map<String, Object> map = new HashMap<>();
                map.put("title", ms.getRecordName());
                map.put("value", ms);
                map.put("type", "jibie");
                map.put("key", ms.getId());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(String.valueOf(map.get("key")), allList);
            map.put("children", tList);
        }
        return childList;
    }
    /**
     * 使用类型转换判断是否为数字
     */
    public static boolean isDigit(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 查询存货档案
    @PostMapping("findSearchAll")
    public Mono<R> findSearchAll(String search){
        return stockCangkuRepository.findAll().collectList()
                .flatMap(list->{
                    List<Map> listmap=new ArrayList<>();
                    if(StrUtil.isNotBlank(search)&&search.length()>0){
                        list=list.stream().filter(f->f.getCkNum().indexOf(search)!=-1 || f.getCkName().indexOf(search)!=-1 ).collect(Collectors.toList());
                    }
                    list.forEach(a->{
                        Map map = new HashMap();
                        map.put("label",a.getCkNum()+"-"+a.getCkName());
                        map.put("key",a.getCkNum());
                        map.put("id",a.getId());
                        listmap.add(map);
                    });
                    return Mono.just(listmap);
                })
                .map(a->R.ok().setResult(a));
    }
}
