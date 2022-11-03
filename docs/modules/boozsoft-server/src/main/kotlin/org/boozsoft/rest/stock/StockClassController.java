package org.boozsoft.rest.stock;//package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.codekemu.CodeKemu;
import org.boozsoft.domain.entity.stock.Stock;
import org.boozsoft.domain.entity.stock.StockClass;
import org.boozsoft.repo.stock.StockClassRepository;
import org.boozsoft.repo.stock.StockRepository;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/stockclass")
@Api(value = "存货分类档案", tags = "存货分类档案：客户分类档案")
public class StockClassController {
    @Autowired
    StockClassRepository stockClassRepository;
    @Autowired
    StockRepository stockRepository;


    @PostMapping("verifyStockClass")
    public Mono<R> verifyStockClass(String stockClass) {
        return stockClassRepository.countByStockClass(stockClass).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyStockClassName")

    public Mono<R> verifyStockClassName(String parentId, String stockClassName) {
        return StringUtils.isNotBlank(parentId) ?
                stockClassRepository.countByParentIdAndStockCclassName(parentId, stockClassName).map(o -> R.ok().setResult(o)) :
                stockClassRepository.countByStockCclassName(stockClassName).map(o -> R.ok().setResult(o));
    }

    @GetMapping("treeStockClass")
    public Mono<R> treeStockClass() {
        return stockClassRepository.findAll()
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @GetMapping("findAllStockClassBend1")
    public Mono<R> findAllStockClassBend1() {
        return stockClassRepository.findAllByStockBend("1")
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @PostMapping("treeStockClass")
    public Mono<R> treeStockClass(@RequestBody Map map) {
        Map<String, String> searchMap = ((HashMap<String,  String>) map.get("searchConditon"));
        String searchValue=map.get("searchValue").toString();
        return stockClassRepository.findAll()
                .collectList()
                .flatMap(list->{
                    if(StrUtil.isNotBlank(searchValue)){
                        list=list.stream().filter(a->a.getStockClass().contains(searchValue) || a.getStockCclassName().contains(searchValue)).collect(Collectors.toList());
                    }
                    return Mono.just(list);
                })
                .map(list -> R.ok().setResult(getChild("0", list)));
    }


    @PostMapping("findAllStockClass")
    public Mono<R> findAllStockClass(@RequestBody Map map, Pageable pageable) {
        // 当前页
        int page = Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size = Integer.parseInt(map.get("size").toString());
        String searchValue = map.get("searchValue").toString();
        String superid = map.get("superid").toString();
        String stockFlag = map.get("stockFlag").toString();
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return stockClassRepository.findAll()
                .collectList()
                .flatMap(item -> {
                    long total = item.size();
                    List<StockClass> list = item.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    if (StringUtils.isNotBlank(stockFlag)) {
                        list = item.stream().filter(v -> v.getFlag().equals(stockFlag)).collect(Collectors.toList());
                        total = list.size();
                    }
                    if (StringUtils.isNotBlank(searchValue)) {
                        list = item.stream().filter(v -> v.getStockClass().contains(searchValue) || v.getStockCclassName().contains(searchValue)).collect(Collectors.toList());
                        total = list.size();
                    }
                    if (StringUtils.isNotBlank(superid)) {
                        list = item.stream().filter(v -> v.getParentId().contains(superid)).collect(Collectors.toList());
                        total = list.size();
                    }

                    list.stream().forEach(v -> {
                        List<StockClass> collect = item.stream().filter(f -> v.getParentId().equals(f.getUniqueStockclass())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            v.setSuperClassName(collect.get(0).getStockCclassName());
                        }
                    });
                    totalAR.set(total);
                    return Mono.just(list);
                })
                .map(a -> R.page(a, pageable, (totalAR.get())));
    }

    @PostMapping("/save")
    public Mono save(@RequestBody StockClass customerClassMono) {
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        Mono add = Mono.just(customerClassMono).flatMap(item -> {
                    // 唯一码
                    if (item.getUniqueStockclass() == null || item.getUniqueStockclass().equals("")) {
                        item.setUniqueStockclass(IdUtil.objectId());
                    }
                    item.setStockBend("1");
                    item.setFlag("1");
                    // 上级ID等于空，默认是一级
                    if (StringUtils.isBlank(item.getParentId())) {
                        item.setParentId("0");
                        item.setStockClassGrade("1");
                        return stockClassRepository.findByMaxParentId("0")
                                .flatMap(sums -> {
                                    item.setStockGradeCode(String.format("%03d", Integer.valueOf(sums) + 1));
                                    return Mono.just(item);
                                });
                    }
                    return stockClassRepository.countByParentId(item.getParentId())
                            .flatMap(sums -> {
                                return stockClassRepository
                                        .findByUniqueStockclass(item.getParentId())
                                        .flatMap(item2 -> {
                                            // 级次加1
                                            item.setStockClassGrade(String.valueOf(Integer.valueOf(item2.getStockClassGrade()) + 1));
                                            item.setStockGradeCode(item2.getStockGradeCode() + String.format("%03d", sums + 1));
                                            // 修改上级末级状态
                                            item2.setStockBend("0");
                                            return stockClassRepository
                                                    .save(item2)
                                                    .map(item4 -> item);
                                        });
                            });
                })
                .flatMap(stockClassRepository::save)
                .map(o -> R.ok().setResult(o));


        Mono edit = Mono.just(customerClassMono).flatMap(item -> {
                    if (StringUtils.isBlank(item.getParentId())) {
                        item.setParentId("0");
                    }
                    return Mono.just(item);
                })
                .flatMap(stockClassRepository::save)
                .map(o -> R.ok().setResult(o));
        return StringUtils.isBlank(customerClassMono.getId()) ? add : edit;
    }

    @PostMapping("/del")
    public Mono<R> delete(String id) {
        return stockClassRepository.findById(id)
                .flatMap(ss -> {
                    Mono<String> str = stockClassRepository.countByParentId(ss.getParentId())
                            .flatMap(sum -> {
                                if (sum == 1) {
                                    return stockClassRepository
                                            .findByUniqueStockclass(ss.getParentId())
                                            .flatMap(item2 -> {
                                                // 修改上级末级状态
                                                item2.setStockBend("1");
                                                return stockClassRepository
                                                        .save(item2)
                                                        .flatMap(a -> Mono.just(id));
                                            });
                                }
                                return Mono.just(id);
                            });
                    return ss.getParentId().equals("0") ? Mono.just(id) : str;
                })
                .flatMap(stockClassRepository::deleteById)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("/importStockClass")
    public Mono<R> importStockClass(@RequestPart("file") FilePart filePartParm) throws Exception {
        // 获取表头个数
        AtomicInteger column = new AtomicInteger();
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
                // 遍历导入excel内容
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    list = excelReader.getExcelObj(tempFilePath.toString(), new String[]{"分类编码", "分类名称", "上级分类编码", ""}, 0);
                    Arrays.stream(list.get(0)).forEach(v -> {
                        if (v != null) {
                            column.addAndGet(1);
                        }
                    });
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("excelpath", tempFilePath);
                    mapArr.put("code", "200");
                    return stockClassRepository.findAll().collectList().flatMap(ite -> {
                        mapArr.put("cusclasslist", ite);
                        return stockRepository.findAll().collectList().flatMap(stocklist->{
                            mapArr.put("stocklist", stocklist);
                            return Mono.just(mapArr);
                        });
                    });
                })
                .flatMap(mapArr -> {
                    List<StockClass> newlist = new ArrayList<>();
                    List<StockClass> cusclasslist = (List<StockClass>) mapArr.get("cusclasslist");
                    List<Stock> stocklist = (List<Stock>) mapArr.get("stocklist");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    // 如果小于5 说明表头不对
                    if (column.get() != 3) {
                        mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    // 判断是否重复
                    for (int i = 1; i < excellist.size(); i++) {
                        int a = column.get();
                        Object[] obj = excellist.get(i);
                        int finalI = i;
                        List<String> errorText = new ArrayList<>();
                        // 编码是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<StockClass> collect = cusclasslist.stream().filter(cusc -> cusc.getStockClass().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("编码与当前已存在档案重复");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("编码不能为空");
                            obj[a] = errorText.toString();
                        }
                        // 名称是否重复
                        if (StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<StockClass> collect = cusclasslist.stream().filter(cusc -> cusc.getStockClass().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("名称与当前已存在档案重复");
                                obj[a] = errorText.toString();
                            }
                        } else {
                            errorText.add("名称不能为空");
                            obj[a] = errorText.toString();
                        }
                        // 上级编码是有存在
                        if (StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {
                            // 从表中查询
                            List<StockClass> collect = cusclasslist.stream().filter(cusc -> cusc.getStockClass().equals(excellist.get(finalI)[2].toString().trim()) || cusc.getStockCclassName().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            // 从文件中查询
                            List<Object[]> excel2 = excellist.stream().filter(v -> v[0].equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            if (collect.size() == 0 && excel2.size() == 0) {
                                errorText.add("上级分类不存在");
                                obj[a] = errorText.toString();
                            }else{
                                List<Stock> collect1 = stocklist.stream().filter(s -> s.getStockClass().equals(collect.get(0).getId())).collect(Collectors.toList());
                                if(collect1.size()>0){
                                    errorText.add("上级分类已有存货档案,不能增加下级");
                                    obj[a] = errorText.toString();
                                }
                            }
                        }
                        if (errorText.size() == 0) {
                            int sums = cusclasslist.size() > 0 ? Integer.valueOf(cusclasslist.get(0).getStockGradeCode().trim()) + finalI : finalI;
                            StockClass cusc = new StockClass();
                            cusc.setUniqueStockclass(IdUtil.objectId());
                            cusc.setStockClass(excellist.get(finalI)[0].toString().trim());
                            cusc.setStockCclassName(excellist.get(finalI)[1].toString().trim());
                            cusc.setFlag("1");
                            if (StringUtils.isBlank(excellist.get(finalI)[2].toString().trim())) {
                                cusc.setStockBend("1");
                                cusc.setStockClassGrade("1");
                                cusc.setParentId("0");
                                cusc.setStockGradeCode(String.format("%03d", sums));
                            } else {
                                // 查找上级分类编码 是否存在系统
                                List<StockClass> collect = cusclasslist.stream().filter(cl -> cl.getStockClass().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                                if (collect.size() > 0) {
                                    cusc.setStockBend("1");
                                    cusc.setParentId(collect.get(0).getUniqueStockclass());
                                    // 查询有几个下级
                                    List<StockClass> collect1 = cusclasslist.stream().filter(cl -> cl.getParentId().equals(collect.get(0).getUniqueStockclass())).collect(Collectors.toList());
                                    // 级次加1
                                    cusc.setStockClassGrade(String.valueOf(Integer.valueOf(collect1.get(0).getStockClassGrade()) + 1));
                                    cusc.setStockGradeCode(collect.get(0).getStockGradeCode() + String.format("%03d", collect1.size() + i));
                                }
                                // 查询 文件中是否包含下级
                                else {
                                    List<StockClass> collect12 = newlist.stream().filter(ex -> ex.getStockClass().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                                    cusc.setStockBend("1");
                                    cusc.setParentId(collect12.get(0).getUniqueStockclass());
                                    // 级次加1
                                    cusc.setStockClassGrade(String.valueOf(Integer.valueOf(collect12.get(0).getStockClassGrade()) + 1));
                                    a = a + 1;
                                    cusc.setStockGradeCode(collect12.get(0).getStockGradeCode() + String.format("%03d", a));
                                    // 上级修改末级标识
                                    collect12.get(0).setStockBend("0");
                                }
                            }
                            newlist.add(cusc);
                        } else {
                            mapArr.put("code", "200");
                            mapArr.put("error", "error");
                        }
                    }
                    // 进行去重
                    List<String> listStr1 = new ArrayList<>();
                    List<String> listStr2 = new ArrayList<>();
                    // 去重后list
                    long doublesize1 = listStr1.stream().distinct().count();
                    long doublesize2 = listStr2.stream().distinct().count();
                    if (doublesize1 < listStr1.size() || doublesize2 < listStr2.size()) {
                        mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    mapArr.put("list", newlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<StockClass> newlist = (List<StockClass>) mapArr.get("list");
                    return mapArr.get("code").equals("401") ? Mono.just(mapArr) : newlist.size() == 0 ? Mono.just(mapArr) : stockClassRepository.saveAll(newlist).map(t -> mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("findByUniqueStockclass")
    public Mono<R> findByUniqueStockclass(String uniqueStockclass) {
        return stockClassRepository.findByUniqueStockclass(uniqueStockclass).map(a -> R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @PostMapping("editStockCalssFlag")
    public Mono save(@RequestBody List<StockClass> obj){
        obj.forEach(a->{
            if(a.getFlag().equals("1")){
                a.setFlag("0");
            }else {
                a.setFlag("1");
            }
        });
        return stockClassRepository.saveAll(obj).collectList().map(a->R.ok().setResult(a));
    }

    public List<Map<String, Object>> getChild(String pid, List<StockClass> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (StockClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("id", ms.getId());
                map.put("uniqueStockclass", ms.getUniqueStockclass());
                map.put("title", ms.getStockCclassName());
                map.put("key", ms.getUniqueStockclass());
                map.put("stockClass", ms.getStockClass());
                map.put("stockClassGrade", ms.getStockClassGrade());
                map.put("stockCclassName", ms.getStockCclassName());
                map.put("stockBend", ms.getStockBend());
                map.put("flag", ms.getFlag());
                map.put("parentId", ms.getUniqueStockclass());
                map.put("parentId2", ms.getParentId());
                map.put("stockGradeCode", ms.getStockGradeCode());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(String.valueOf(map.get("parentId")), allList);
            map.put("children", tList);
        }
        return childList;
    }

}
