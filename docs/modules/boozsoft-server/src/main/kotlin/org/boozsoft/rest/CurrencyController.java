package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SysCurrency;
import org.boozsoft.repo.CurrencyRepository;
import org.boozsoft.util.ChineseAndEnglish;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/9 6:00 下午
 */
@Slf4j
@RestController
@RequestMapping("/sys/currency")
@Api(value = "币种信息", tags = "API系统：币种信息")
public class CurrencyController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    CurrencyRepository currencyRepository;

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
    @PostMapping("/findAll")
    public Mono<R> findAll(@RequestBody Map map) {
        System.out.println(map);
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        String requirement=searchMap.get("requirement");
        String str=searchMap.get("value");
        return currencyRepository.findAllByOrderByNumAsc().collectList()
                .flatMap(list->{
                    List<SysCurrency> newlist=list;
                    if(StrUtil.isNotBlank(str)){
                        if(requirement.equals("currencyZhCnName")){
                            newlist=newlist.stream().filter(item->item.getCurrencyZhCnName().equals(str)).collect(Collectors.toList());
                        }
                    }
                    return Mono.just(newlist);
                })
                .map(R::page);
    }

    @GetMapping("/getone")
    public Flux<Map<String, Object>> getOne() {
        return client.sql("select * from dept where id = '1' ").fetch().all();
    }


    @PostMapping("/save")
    public Mono save(@RequestBody SysCurrency currency) {
        return currencyRepository.save(currency).map(o-> R.ok().setResult(o));
    }

    @PostMapping("/del")
    public Mono delete(String id,@RequestBody SysCurrency currency) {
        // 往
        return currencyRepository.deleteById(id).then(Mono.just(R.ok()));
    }

    @PostMapping("/sysimportCurrency")
    public Mono<R> sysimportCurrency(@RequestPart("file") FilePart filePartParm) throws Exception {
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
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    list = excelReader.getExcelObj(tempFilePath.toString(), new String[]{"国际货币名称", "货币单位", "国际代码","换算率",""}, 0);
                    Arrays.stream(list.get(0)).forEach(v->{
                        if(v!=null){
                            column.addAndGet(1);
                        }
                    });
                    assert tempFilePath != null;
                    try {
                        Files.delete(tempFilePath);
                    } catch (IOException e) {}
                    return Mono.just(list);
                }).flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("code", "200");
                    return currencyRepository.findAll().collectList().flatMap(ite -> {
                        mapArr.put("currencylist", ite);
                        return Mono.just(mapArr);
                    });
                })
                .flatMap(mapArr -> {
                    List<SysCurrency> newlist=new ArrayList<>();
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    List<SysCurrency> currencylist= (List<SysCurrency>) mapArr.get("currencylist");

                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {
                        int a=column.get();
                        Object[] obj=excellist.get(i);
                        int finalI = i;
                        List<String>errorText=new ArrayList<>();
                        // 国际货币名称 是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString())) {
                            List<SysCurrency> collect = currencylist.stream().filter(c ->c.getCurrencyZhCnName()!=null &&c.getCurrencyZhCnName().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("国际货币名称与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("国际货币名称不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 货币单位 是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<SysCurrency> collect = currencylist.stream().filter(c ->c.getCurrencyUnit()!=null && c.getCurrencyUnit().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("货币单位与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("货币单位不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 国际代码 是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {

                            if(ChineseAndEnglish.isEnglish(excellist.get(finalI)[2].toString().trim())){
                                if(excellist.get(finalI)[2].toString().trim().length()!=3){
                                    errorText.add("国际代码必须等于3位");
                                    obj[a]=errorText.toString();
                                }else{
                                    List<SysCurrency> collect = currencylist.stream().filter(c ->c.getAbbreviation()!=null &&c.getAbbreviation().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                                    if (collect.size() > 0) {
                                        errorText.add("国际代码与当前已存在档案重复");
                                        obj[a]=errorText.toString();
                                    }
                                }
                            }else{
                                errorText.add("国际代码必须全部是字母");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("国际代码不能为空");
                            obj[a]=errorText.toString();
                        }

                        if(errorText.size()==0){
                            SysCurrency s=new SysCurrency();
                            s.setUniqueCode(IdUtil.objectId());
                            s.setCurrencyZhCnName(excellist.get(finalI)[0].toString().trim());
                            s.setCurrencyUnit(excellist.get(finalI)[1].toString().trim());
                            s.setAbbreviation(excellist.get(finalI)[2].toString().trim());
                            if (StringUtils.isBlank(excellist.get(finalI)[3].toString())) {
                                s.setFractionalCurrency(excellist.get(finalI)[3].toString().trim());
                            }
                            newlist.add(s);
                        }else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                    }
                    // 进行去重
                    List<String> listStr=new ArrayList<>();
                    for (int i = 1; i < excellist.size(); i++) {
                        if(StrUtil.isNotBlank(excellist.get(i)[0].toString().trim())){
                            listStr.add(excellist.get(i)[0].toString().trim());    // 国际货币名称
                        }
                        if(StrUtil.isNotBlank(excellist.get(i)[1].toString().trim())){
                            listStr.add(excellist.get(i)[1].toString().trim());    // 货币单位
                        }
                        if(StrUtil.isNotBlank(excellist.get(i)[2].toString().trim())){
                            listStr.add(excellist.get(i)[2].toString().trim());    // 国际代码
                        }
                        if(StrUtil.isNotBlank(excellist.get(i)[3].toString().trim())){
                            listStr.add(excellist.get(i)[3].toString().trim());    // 换算率
                        }
                    }
                    // 去重后list
                    long doublesize1=listStr.stream().distinct().count();
                    if(doublesize1<listStr.size()){
                        mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    mapArr.put("list", newlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<SysCurrency> newlist= (List<SysCurrency>) mapArr.get("list");
                    return mapArr.get("code").equals("401")?Mono.just(mapArr):newlist.size()==0?Mono.just(mapArr):currencyRepository.saveAll(newlist).map(t->mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }
}
