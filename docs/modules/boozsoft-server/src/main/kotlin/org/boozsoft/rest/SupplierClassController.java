package org.boozsoft.rest;//package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.SupplierClass;
import org.boozsoft.domain.vo.AAtemp;
import org.boozsoft.repo.SupplierClassRepository;
import org.boozsoft.repo.SupplierRepository;
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

/**
 * 供应商分类档案
 */
@Slf4j
@RestController
@RequestMapping("/sys/supplierclass")
@Api(value = "供应商分类档案", tags = "供应商分类档案")
public class SupplierClassController {
    @Autowired
    SupplierClassRepository supplierClassRepository;
    @Autowired
    SupplierRepository customerRepository;


    @PostMapping("verifyCusClass")
    public Mono<R> verifyCusClass(String cusClass) {
        return supplierClassRepository.countByCusClass(cusClass).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCusClassName")
    public Mono<R> verifyCusClassName(String parentId, String cusClassName) {
        return  StringUtils.isNotBlank(parentId)?
                supplierClassRepository.countByParentIdAndCusCclassName(parentId, cusClassName).map(o -> R.ok().setResult(o)):
                supplierClassRepository.countByCusCclassName(cusClassName).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByCusBendEq1")
    public Mono<R> findAllByCusBendEq1() {
        return supplierClassRepository.findAllByCusBend("1").collectList().map(R::page);
    }

    @GetMapping("treeCustomerClass")
    public Mono<R> treeCustomerClass() {
        return supplierClassRepository.findAll()
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @PostMapping("findAllCustomerClass")
    public Mono<R> findAllCustomerClass(@RequestBody Map map, Pageable pageable) {
        // 当前页
        int page= Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= Integer.parseInt(map.get("size").toString());
        String searchValue=map.get("searchValue").toString();
        String superid=map.get("superid").toString();
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return supplierClassRepository.findAll()
                .collectList()
                .flatMap(item->{
                    long total=item.size();
                    List<SupplierClass> list=item.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    if(StringUtils.isNotBlank(searchValue)){
                        list=item.stream().filter(v->v.getCusClass().contains(searchValue) || v.getCusCclassName().contains(searchValue)).collect(Collectors.toList());
                        total=list.size();
                    }
                    if(StringUtils.isNotBlank(superid)){
                        list=item.stream().filter(v->v.getParentId().contains(superid)).collect(Collectors.toList());
                        total=list.size();
                    }

                    list.stream().forEach(v->{
                        List<SupplierClass> collect = item.stream().filter(f -> v.getParentId().equals(f.getUniqueCustclass())).collect(Collectors.toList());
                        if(collect.size()>0){
                            v.setSuperClassName(collect.get(0).getCusCclassName());
                        }
                    });
                    totalAR.set(total);
                    return Mono.just(list);
                })
                .map(a -> R.page(a,pageable,(totalAR.get())));
    }

    @PostMapping("/save")
    public Mono save(@RequestBody SupplierClass customerClassMono) {
        Mono add = Mono.just(customerClassMono).flatMap(item -> {
                    // 唯一码
                    if (item.getUniqueCustclass() == null || item.getUniqueCustclass().equals("")) {
                        item.setUniqueCustclass(IdUtil.objectId());
                    }
                    item.setCusBend("1");
                    item.setFlag("1");
                    // 上级ID等于空，默认是一级
                    if (StringUtils.isBlank(item.getParentId())) {
                        item.setParentId("0");
                        item.setCusClassGrade("1");
                        return supplierClassRepository.findByMaxParentId("0")
                                .flatMap(sums -> {
                                    item.setCusGradeCode(String.format("%03d", Integer.valueOf(sums) + 1));
                                    return Mono.just(item);
                                });
                    }
                    return supplierClassRepository.countByParentId(item.getParentId())
                            .flatMap(sums -> {
                                return supplierClassRepository
                                        .findByUniqueCustclass(item.getParentId())
                                        .flatMap(item2 -> {
                                            // 级次加1
                                            item.setCusClassGrade(String.valueOf(Integer.valueOf(item2.getCusClassGrade()) + 1));
                                            item.setCusGradeCode(item2.getCusGradeCode() + String.format("%03d", sums + 1));
                                            // 修改上级末级状态
                                            item2.setCusBend("0");
                                            return supplierClassRepository
                                                    .save(item2)
                                                    .map(item4 -> item);
                                        });
                            });
                })
                .flatMap(supplierClassRepository::save)
                .map(o -> R.ok().setResult(o));


        Mono edit = Mono.just(customerClassMono).flatMap(item -> {
                    if (StringUtils.isBlank(item.getParentId())) {
                        item.setParentId("0");
                    }
                    return supplierClassRepository.save(item);
                })
                .flatMap(supplierClassRepository::save)
                .map(o -> R.ok().setResult(o));
        return StringUtils.isBlank(customerClassMono.getId()) ? add : edit;
    }

    @PostMapping("/saveCusClass")
    public Mono save(@RequestBody List<SupplierClass> customerClassMono){
        return supplierClassRepository.saveAll(customerClassMono).collectList().map(a->R.ok().setResult(a));
    }

    @PostMapping("/del")
    public Mono<R> delete(@RequestBody List<SupplierClass> list) {
        list.sort(Comparator.comparing(SupplierClass::getCusClassGrade).reversed());
        // 不可删除
        List<String> noBend0Codelist=new ArrayList<>();
        // 可删除
        List<SupplierClass> Bend1Codelist=new ArrayList<>();
        return supplierClassRepository.findAll().collectList()
            .flatMap(listAll->{
                return customerRepository.findAll().collectList()
                    .flatMap(cusList->{
                            return supplierClassRepository.findAll().collectList()
                                .flatMap(listAll2->{
                                    list.stream().distinct().forEach(a->{
                                        Bend1Codelist.add(a);
                                        listAll.removeIf(li->li.getCusClass().equals(a.getCusClass()));

                                        // 判断科目是否含有下级
                                        List<AAtemp> childDel = getChildDel(a.getUniqueCustclass(), listAll);
                                        if(childDel.size()>0){
                                            noBend0Codelist.add(a.getUniqueCustclass());
                                        }
                                    });
                                    // 过滤出上级科目,过滤集合中是否还有下级,没有修改末级标识
                                    List<String> editflag = new ArrayList<>();
                                    List<String> collect = list.stream().filter(a -> !a.getParentId().equals("0")).map(SupplierClass::getParentId).distinct().collect(Collectors.toList());
                                    collect.forEach(f->{
                                        List<AAtemp> childDel = getChildDel(f, listAll);
                                        if(childDel.size()==0){
                                            editflag.add(f);
                                        }
                                    });
                                    // 不能删除的科目去重
                                    List<String> listStr=noBend0Codelist.stream().distinct().collect(Collectors.toList());
                                    // 修改上级末级标志
                                    if(editflag.size()==0){ editflag.add("0"); }
                                    Mono editFlg=supplierClassRepository.updateBendByUniqueCust("1",editflag).then();
                                    // 删除
                                    // 去除包含不能删除的编码
                                    List<SupplierClass> str=Bend1Codelist.stream().filter(a->listStr.indexOf(a.getUniqueCustclass())==-1).collect(Collectors.toList());
                                    System.out.println("str>>>"+str);
                                    Mono delData=supplierClassRepository.deleteAll(str).then();

                                    // 把不能删除的唯一码替换成名称
                                    for (int i = 0; i < listStr.size(); i++) {
                                        int finalI = i;
                                        List<SupplierClass> collect1 = listAll2.stream().filter(v -> v.getUniqueCustclass().equals(listStr.get(finalI))).collect(Collectors.toList());
                                        if(collect1.size()>0){
                                            listStr.remove(i);
                                            listStr.add(collect1.get(0).getCusCclassName());
                                        }
                                    }

                                    // 判断能删除的分类,是否已使用
                                    List<String> useCus=new ArrayList<>();
                                    for (int i = 0; i < str.size(); i++) {
                                        int finalI = i;
                                        long useSize=cusList.stream().filter(cus->cus.getUniqueCustclass().equals(str.get(finalI).getUniqueCustclass())).count();
                                        if(useSize>0){
                                            useCus.add(str.get(i).getCusCclassName());
                                        }
                                    }

                                    Map map=new HashMap<>();
                                    map.put("useCus",useCus);
                                    map.put("listStr",listStr);
                                    return useCus.size()>0?Mono.just(map):listStr.size()>0?delData.then(Mono.just(map)):Mono.zip(editFlg,delData).then(Mono.just(map));
                                });
                        });
            }).map(a->R.ok().setResult(a));
    }
    public List<AAtemp> getChildDel(String pid, List<SupplierClass> allList) {
        List<AAtemp> childList = new ArrayList<>();//用于保存子节点的list;
        for (SupplierClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                AAtemp a=new AAtemp();
                a.setTitle(ms.getCusCclassName()).setValue(ms.getCusClass()).setKey(ms.getUniqueCustclass()).setBend(ms.getCusBend()).setChildren(new ArrayList<>()).setParentId(ms.getParentId());
                childList.add(a); //加入子节点
            }
        }
        for (AAtemp map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<AAtemp> tList = getChildDel(map.getKey(), allList);
            map.setChildren(tList);
        }
        return childList;
    }

    @PostMapping("/importCusClass")
    public Mono<R> importCusClass(@RequestPart("file") FilePart filePartParm) throws Exception {
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
                // 遍历导入excel内容
                .flatMap(objArr -> {
                    List<Object[]> list = null;
                    XlsUtils3 excelReader = new XlsUtils3(tempFilePath.toString());
                    list = excelReader.getExcelObj(tempFilePath.toString(), new String[]{"分类编码", "分类名称", "上级分类编码",""}, 0);
                    Arrays.stream(list.get(0)).forEach(v->{
                        if(v!=null){
                            column.addAndGet(1);
                        }
                    });
//                    assert tempFilePath != null;
//                    try {
//                        Files.delete(tempFilePath);
//                    } catch (IOException e) {}
                    return Mono.just(list);
                })
                .flatMap(item -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", item);   // excel中内容
                    mapArr.put("error", "");
                    mapArr.put("excelpath", tempFilePath);
                    mapArr.put("code", "200");
                    return supplierClassRepository.findAll().collectList().flatMap(ite -> {
                        mapArr.put("cusclasslist", ite);
                        return Mono.just(mapArr);
                    });
                })
                .flatMap(mapArr -> {
                    List<SupplierClass> newlist = new ArrayList<>();
                    List<SupplierClass> editBendlist = new ArrayList<>();
                    List<SupplierClass> cusclasslist = (List<SupplierClass>) mapArr.get("cusclasslist");
                    // 文件list
                    List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
                    // 如果小于5 说明表头不对
                    if (column.get()!=3) {
                        mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    // 判断是否重复
                    int sums=1;
                    for (int i = 1; i < excellist.size(); i++) {
                        int a=column.get();
                        Object[] obj=excellist.get(i);
                        int finalI = i;
                        List<String>errorText=new ArrayList<>();
                        // 编码是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString().trim())) {
                            List<SupplierClass> collect = cusclasslist.stream().filter(cusc -> cusc.getCusClass().equals(excellist.get(finalI)[0].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("编码与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("编码不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 名称是否重复
                        if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString().trim())) {
                            List<SupplierClass> collect = cusclasslist.stream().filter(cusc -> cusc.getCusCclassName().equals(excellist.get(finalI)[1].toString().trim())).collect(Collectors.toList());
                            if (collect.size() > 0) {
                                errorText.add("名称与当前已存在档案重复");
                                obj[a]=errorText.toString();
                            }
                        }else{
                            errorText.add("名称不能为空");
                            obj[a]=errorText.toString();
                        }
                        // 上级编码是有存在
                        if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString().trim())) {
                            // 从表中查询
                            List<SupplierClass> collect = cusclasslist.stream().filter(cusc -> cusc.getCusClass().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            // 从文件中查询
                            List<Object[]> excel2 = excellist.stream().filter(v -> v[0].equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                            if (collect.size() == 0 && excel2.size()==0) {
                                errorText.add("上级编码不存在");
                                obj[a]=errorText.toString();
                            }
                        }
                        if(errorText.size()==0){
                            if(cusclasslist.size()>0&&sums==1){
                                cusclasslist.sort(Comparator.comparing(SupplierClass::getCusGradeCode).reversed());
                                int max=Integer.valueOf(cusclasslist.get(0).getCusGradeCode())+1;
                                sums = max;
                            }
                            SupplierClass cusc = new SupplierClass();
                            cusc.setUniqueCustclass(IdUtil.objectId());
                            cusc.setCusClass(excellist.get(finalI)[0].toString().trim());
                            cusc.setCusCclassName(excellist.get(finalI)[1].toString().trim());
                            cusc.setFlag("1");
                            if (StringUtils.isBlank(excellist.get(finalI)[2].toString().trim())) {
                                cusc.setCusBend("1");
                                cusc.setCusClassGrade("1");
                                cusc.setParentId("0");
                                cusc.setCusGradeCode(String.format("%03d", sums));
                                sums++;
                            } else {
                                // 查找上级分类编码 是否存在系统
                                List<SupplierClass> collect = cusclasslist.stream().filter(cl -> cl.getCusClass().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());
                                if (collect.size() > 0) {
                                    SupplierClass c1=collect.get(0);
                                    c1.setCusBend("0");
                                    editBendlist.add(c1);

                                    cusc.setCusBend("1");
                                    cusc.setParentId(collect.get(0).getUniqueCustclass());
                                    // 级次加1
                                    cusc.setCusClassGrade(String.valueOf(Integer.valueOf(collect.get(0).getCusClassGrade()) + 1));

                                    // 查询有几个下级
                                    List<SupplierClass> collect1 = cusclasslist.stream().filter(cl -> cl.getParentId().equals(collect.get(0).getUniqueCustclass())).collect(Collectors.toList());
                                    long count = newlist.stream().filter(v -> v.getParentId().equals(collect.get(0).getUniqueCustclass())).count();
                                    cusc.setCusGradeCode(collect.get(0).getCusGradeCode() + String.format("%03d", collect1.size() + count+1));
                                }
                                // 查询 文件中是否包含下级
                                else {
                                    List<SupplierClass> collect12 = newlist.stream().filter(ex -> ex.getCusClass().equals(excellist.get(finalI)[2].toString().trim())).collect(Collectors.toList());

                                    cusc.setCusBend("1");
                                    cusc.setParentId(collect12.get(0).getUniqueCustclass());
                                    // 级次加1
                                    cusc.setCusClassGrade(String.valueOf(Integer.valueOf(collect12.get(0).getCusClassGrade()) + 1));

                                    long count = newlist.stream().filter(v -> v.getParentId().equals(collect12.get(0).getUniqueCustclass())).count();
                                    cusc.setCusGradeCode(collect12.get(0).getCusGradeCode() + String.format("%03d",  count+1));
                                    // 上级修改末级标识
                                    collect12.get(0).setCusBend("0");
                                }
                            }
                            newlist.add(cusc);
                        }else{ mapArr.put("code", "200");mapArr.put("error", "error");}
                    }
                    // 进行去重
                    List<String> listStr1=new ArrayList<>();
                    List<String> listStr2=new ArrayList<>();
                    // 去重后list
                    long doublesize1=listStr1.stream().distinct().count();
                    long doublesize2=listStr2.stream().distinct().count();
                    if(doublesize1<listStr1.size() || doublesize2<listStr2.size()){
                        mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                        mapArr.put("code", "401");
                        return Mono.just(mapArr);
                    }
                    mapArr.put("list", newlist);
                    mapArr.put("editBendlist", editBendlist);
                    mapArr.put("column", column.get());
                    return Mono.just(mapArr);
                })
                .flatMapMany(mapArr -> {
                    List<SupplierClass> newlist = (List<SupplierClass>) mapArr.get("list");
                    List<SupplierClass> editBendlist = (List<SupplierClass>) mapArr.get("editBendlist");
                    Mono add=supplierClassRepository.saveAll(newlist).collectList();
                    Mono editBend=supplierClassRepository.saveAll(editBendlist).collectList();
                    return mapArr.get("code").equals("401")?Mono.just(mapArr):newlist.size()==0?Mono.just(mapArr):Mono.zip(add,editBend).map(t->mapArr);
                })
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    // 查看授权的客户分类
    @PostMapping("getCusClassAuthorData")
    public Mono<R> findByCusClassAuthData(@RequestBody Map map){
        String tableName=map.get("tableName").toString();
        String userId=map.get("userId").toString();
        String accId=map.get("accId").toString();
        return supplierClassRepository.findByCusClassAuthData(tableName,accId,userId).collectList().map(a->R.ok().setResult(a)).defaultIfEmpty(R.ok().setResult(""));
    }

    @GetMapping("findAllSupClass")
    public Mono<R> findAllSupClass(){
        return supplierClassRepository.findAllSupClass().collectList().map(a->R.ok().setResult(a));
    }

    public List<Map<String, Object>> getChild(String pid, List<SupplierClass> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (SupplierClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("id", ms.getId());
                map.put("uniqueCustclass", ms.getUniqueCustclass());
                map.put("cusClass", ms.getCusClass());
                map.put("cusClassGrade", ms.getCusClassGrade());
                map.put("cusCclassName", ms.getCusCclassName());
                map.put("title", ms.getCusCclassName());
                map.put("key", ms.getUniqueCustclass());
                map.put("cusBend", ms.getCusBend());
                map.put("flag", ms.getFlag());
                map.put("parentId", ms.getUniqueCustclass());
                map.put("parentId2", ms.getParentId());
                map.put("cusGradeCode", ms.getCusGradeCode());
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
