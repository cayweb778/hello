package org.boozsoft.rest.group;//package org.boozsoft.rest;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.boozsoft.domain.entity.CustomerClass;
import org.boozsoft.domain.entity.group.GroupCustomerClass;
import org.boozsoft.domain.vo.AAtemp;
import org.boozsoft.repo.CustomerClassRollBackRepository;
import org.boozsoft.repo.GroupCustomerClassRepository;
import org.boozsoft.repo.GroupCustomerRepository;
import org.boozsoft.repo.SysLogRepository;
import org.boozsoft.repo.group.GroupCustomerClassAccountRepository;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
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
 * 客户分类档案
 */
@Slf4j
@RestController
@RequestMapping("/sys/cusromerclass_group")
@Api(value = "客户分类档案", tags = "API系统：客户分类档案")
public class GroupCustomerClassController {
    @Autowired
    DatabaseClient client;
    @Autowired
    R2dbcEntityTemplate entityTemplate;
    @Autowired
    GroupCustomerClassRepository customerClassRepository;
    @Autowired
    GroupCustomerRepository groupCustomerRepository;
    @Autowired
    GroupCustomerClassAccountRepository groupCustomerClassAccountRepository;
    @Autowired
    SysLogRepository logRepository;



    @PostMapping("verifyCusClass")
    public Mono<R> verifyCusClass(String cusClass) {
        return customerClassRepository.countByCusClass(cusClass).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifySuperCusClassName")
    public Mono<R> verifySuperCusClassName(String parentId, String cusCclassName) {
        return customerClassRepository.countByParentIdAndCusCclassName(parentId, cusCclassName).map(o -> R.ok().setResult(o));
    }

    @PostMapping("verifyCusClassName")
    public Mono<R> verifyCusClassName(String cusCclassName) {
        return customerClassRepository.countByCusCclassName(cusCclassName).map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllByCusBendEq1")
    public Mono<R> findAllByCusBendEq1() {
        return customerClassRepository.findAllByCusBend("1").collectList().map(R::page);
    }

    @GetMapping("treeCustomerClass")
    public Mono<R> treeCustomerClass() {
        return customerClassRepository.findAll()
                .collectList()
                .map(list -> R.ok().setResult(getChild("0", list)));
    }

    @PostMapping("findAllCustomerClass")
    public Mono<R> findAllCustomerClass(@RequestBody Map map, Pageable pageable) {
        // 当前页：默认1
        int page= Integer.parseInt(map.get("page").toString());
        // 页多少条
        int size= Integer.parseInt(map.get("size").toString());
        String searchValue=map.get("searchValue").toString();
        String superid=map.get("superid").toString();
        AtomicReference<Long> totalAR = new AtomicReference(0);
        return customerClassRepository.findAll()
                .collectList()
                .flatMap(item->{
                    long total=item.size();
                    List<GroupCustomerClass> list=item.stream().skip((page - 1) * size).limit(size).collect(Collectors.toList());
                    if(StringUtils.isNotBlank(searchValue)){
                        list=item.stream().filter(v->v.getCusClass().contains(searchValue) || v.getCusCclassName().contains(searchValue)).collect(Collectors.toList());
                        total=list.size();
                    }
                    if(StringUtils.isNotBlank(superid)){
                        list=item.stream().filter(v->v.getParentId().contains(superid)).collect(Collectors.toList());
                        total=list.size();
                    }
                    list.stream().forEach(v->{
                        List<GroupCustomerClass> collect = item.stream().filter(f -> v.getParentId().equals(f.getUniqueCustclass())).collect(Collectors.toList());
                        if(collect.size()>0){
                            v.setSuperClassName(collect.get(0).getCusCclassName());
                        }
                    });
                    totalAR.set(total);
                    return Mono.just(list);
                })
                .map(a -> R.page(a,pageable,(totalAR.get())));
    }

    @Transactional
    @PostMapping("/save")
    public Mono save(@RequestBody GroupCustomerClass customerClassMono, String username) {

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
                return customerClassRepository.findByMaxParentId("0")
                        .flatMap(sums -> {
                            item.setCusGradeCode(String.format("%03d", Integer.valueOf(sums) + 1));
                            return Mono.just(item);
                        });
            }
            return customerClassRepository.countByParentId(item.getParentId())
                    .flatMap(sums -> {
                        return customerClassRepository
                                .findByUniqueCustclass(item.getParentId())
                                .flatMap(item2 -> {
                                    // 级次加1
                                    item.setCusClassGrade(String.valueOf(Integer.valueOf(item2.getCusClassGrade()) + 1));
                                    item.setCusGradeCode(item2.getCusGradeCode() + String.format("%03d", sums + 1));
                                    // 修改上级末级状态
                                    item2.setCusBend("0");
                                    return customerClassRepository
                                            .save(item2)
                                            .map(item4 -> item);
                                });
                    });
        })
                .flatMap(customerClassRepository::save).map(o -> R.ok().setResult(o));


        Mono edit = Mono.just(customerClassMono).flatMap(item->{
            if (StringUtils.isBlank(item.getParentId())) {
                item.setParentId("0");
            }
            return customerClassRepository.save(item).map(o -> R.ok().setResult(o));
        });
        return StringUtils.isBlank(customerClassMono.getId()) ? add : edit;
    }

    @Transactional
    @PostMapping("/del")
    public Mono delete(@RequestBody List<GroupCustomerClass> list) {
        list.sort(Comparator.comparing(GroupCustomerClass::getCusClassGrade).reversed());
        // 不可删除
        List<String> noBend0Codelist=new ArrayList<>();
        // 可删除
        List<GroupCustomerClass> Bend1Codelist=new ArrayList<>();
        return customerClassRepository.findAll().collectList()
        .flatMap(listAll->{
            return groupCustomerRepository.findAll().collectList()
            .flatMap(cusList->{
                return customerClassRepository.findAll().collectList()
                .flatMap(listAll2->{
                    // 是否分配给组织
                    return groupCustomerClassAccountRepository.findAllByCtype("1").collectList()
                    .flatMap(assignCus->{
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
                        List<String> collect = list.stream().filter(a -> !a.getParentId().equals("0")).map(GroupCustomerClass::getParentId).distinct().collect(Collectors.toList());
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
                        Mono editFlg=customerClassRepository.updateBendByUniqueCust("1",editflag).then();
                        // 删除
                        // 去除包含不能删除的编码
                        List<GroupCustomerClass> str=Bend1Codelist.stream().filter(a->listStr.indexOf(a.getUniqueCustclass())==-1).collect(Collectors.toList());
                        Mono delData=customerClassRepository.deleteAll(str).then();

                        // 把不能删除的唯一码替换成名称
                        for (int i = 0; i < listStr.size(); i++) {
                            int finalI = i;
                            List<GroupCustomerClass> collect1 = listAll2.stream().filter(v -> v.getUniqueCustclass().equals(listStr.get(finalI))).collect(Collectors.toList());
                            if(collect1.size()>0){
                                listStr.remove(i);
                                listStr.add(collect1.get(0).getCusCclassName());
                            }
                        }
                        // 判断能删除的分类,是否已使用
                        List<String> useCus=new ArrayList<>();
                        List<String> assignCusList=new ArrayList<>();
                        for (int i = 0; i < str.size(); i++) {
                            int finalI = i;
                            long useSize=cusList.stream().filter(cus->cus.getUniqueCustclass().equals(str.get(finalI).getUniqueCustclass())).count();
                            if(useSize>0){
                                useCus.add(str.get(i).getCusCclassName());
                            }
                            // 分配的
                            long assignSize=assignCus.stream().filter(f->f.getDataUnique().equals(str.get(finalI).getUniqueCustclass())).count();
                            if(assignSize>0){
                                assignCusList.add(str.get(i).getCusCclassName());
                            }
                        }
                        Map map=new HashMap<>();
                        map.put("useCus",useCus);
                        map.put("listStr",listStr);
                        map.put("assignCusList",assignCusList);
                        return assignCusList.size()>0?Mono.just(map):useCus.size()>0?Mono.just(map):listStr.size()>0?delData.then(Mono.just(map)):Mono.zip(editFlg,delData).then(Mono.just(map));
                    });
                });
            });
        }).map(a->R.ok().setResult(a));
    }

    @Transactional
    @PostMapping("/sysimportCusClassGroup")
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
            mapArr.put("code", "200");
            return customerClassRepository.findAll().collectList().flatMap(ite -> {
                    mapArr.put("cusclasslist", ite);
                    return Mono.just(mapArr);
                });
        })
        .flatMap(mapArr -> {
            List<GroupCustomerClass> newlist = new ArrayList<>();
            List<GroupCustomerClass> cusclasslist = (List<GroupCustomerClass>) mapArr.get("cusclasslist");
            // 文件list
            List<Object[]> excellist = (List<Object[]>) mapArr.get("excellist");
            // 如果小于5 说明表头不对
            if (column.get()!=3) {
                mapArr.put("error", "导入模板表头不正确，请使用正确的模板进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }
            // 判断是否重复
            for (int i = 1; i < excellist.size(); i++) {
                int a=column.get();
                Object[] obj=excellist.get(i);
                int finalI = i;
                List<String>errorText=new ArrayList<>();
                // 编码是否重复
                if(StringUtils.isNotBlank(excellist.get(finalI)[0].toString())) {
                    List<GroupCustomerClass> collect = cusclasslist.stream().filter(cusc -> cusc.getCusClass().equals(excellist.get(finalI)[0])).collect(Collectors.toList());
                    if (collect.size() > 0) {
                        errorText.add("编码与当前已存在档案重复");
                        obj[a]=errorText.toString();
                    }
                }else{
                    errorText.add("编码不能为空");
                    obj[a]=errorText.toString();
                }
                // 名称是否重复
                if(StringUtils.isNotBlank(excellist.get(finalI)[1].toString())) {
                    List<GroupCustomerClass> collect = cusclasslist.stream().filter(cusc -> cusc.getCusCclassName().equals(excellist.get(finalI)[1])).collect(Collectors.toList());
                    if (collect.size() > 0) {
                        errorText.add("名称与当前已存在档案重复");
                        obj[a]=errorText.toString();
                    }
                }else{
                    errorText.add("名称不能为空");
                    obj[a]=errorText.toString();
                }
                // 上级编码是有存在
                if(StringUtils.isNotBlank(excellist.get(finalI)[2].toString())) {
                    List<GroupCustomerClass> collect = cusclasslist.stream().filter(cusc -> cusc.getParentId().equals(excellist.get(finalI)[2])).collect(Collectors.toList());
                    if (collect.size() == 0) {
                        errorText.add("上级编码不存在");
                        obj[a]=errorText.toString();
                    }
                }
                if(errorText.size()==0){
                    int sums = cusclasslist.size() > 0 ? Integer.valueOf(cusclasslist.get(0).getCusGradeCode()) + finalI : finalI;
                    GroupCustomerClass cusc = new GroupCustomerClass();
                    cusc.setUniqueCustclass(IdUtil.objectId());
                    cusc.setCusClass(excellist.get(finalI)[0].toString());
                    cusc.setCusCclassName(excellist.get(finalI)[1].toString());
                    cusc.setFlag("1");
                    if (StringUtils.isBlank(excellist.get(finalI)[2].toString())) {
                        cusc.setCusBend("1");
                        cusc.setCusClassGrade("1");
                        cusc.setParentId("0");
                        cusc.setCusGradeCode(String.format("%03d", sums));
                    } else {
                        // 查找上级分类编码 是否存在系统
                        List<GroupCustomerClass> collect = cusclasslist.stream().filter(cl -> cl.getCusGradeCode().equals(excellist.get(finalI)[2].toString())).collect(Collectors.toList());
                        if (collect.size() > 0) {
                            cusc.setCusBend("1");
                            cusc.setParentId(collect.get(0).getUniqueCustclass());
                            // 查询有几个下级
                            List<GroupCustomerClass> collect1 = cusclasslist.stream().filter(cl -> cl.getParentId().equals(collect.get(0).getUniqueCustclass())).collect(Collectors.toList());
                            // 级次加1
                            cusc.setCusClassGrade(String.valueOf(Integer.valueOf(collect1.get(0).getCusClassGrade()) + 1));
                            cusc.setCusGradeCode(collect.get(0).getCusGradeCode() + String.format("%03d", collect1.size() + i));
                        }
                        // 查询 文件中是否包含下级
                        else {
                            List<GroupCustomerClass> collect12 = newlist.stream().filter(ex -> ex.getCusClass().equals(excellist.get(finalI)[2].toString())).collect(Collectors.toList());
                            cusc.setCusBend("1");
                            cusc.setParentId(collect12.get(0).getUniqueCustclass());
                            // 级次加1
                            cusc.setCusClassGrade(String.valueOf(Integer.valueOf(collect12.get(0).getCusClassGrade()) + 1));
                            a=a+1;
                            cusc.setCusGradeCode(collect12.get(0).getCusGradeCode() + String.format("%03d",  a));
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
//            for (int i = 1; i < excellist.size(); i++) {
//                listStr1.add(excellist.get(i)[0].toString());    // 编码
//                listStr2.add(excellist.get(i)[1].toString());    // 名称
//            }
            // 去重后list
            long doublesize1=listStr1.stream().distinct().count();
            long doublesize2=listStr2.stream().distinct().count();
            if(doublesize1<listStr1.size() || doublesize2<listStr2.size()){
                mapArr.put("error", "文件中有重复数据，请仔细检查再进行导入");
                mapArr.put("code", "401");
                return Mono.just(mapArr);
            }
            mapArr.put("list", newlist);
            mapArr.put("column", column.get());
            return Mono.just(mapArr);
        })
        .flatMapMany(mapArr -> {
            List<GroupCustomerClass> newlist = (List<GroupCustomerClass>) mapArr.get("list");
            return mapArr.get("code").equals("401")?Mono.just(mapArr):newlist.size()==0?Mono.just(mapArr):customerClassRepository.saveAll(newlist).map(t->mapArr);
        })
        .collectList()
        .map(o -> R.ok().setResult(o));
    }
    @Transactional
    @PostMapping("/delImportExcel")
    public Mono<R> delImportExcel(@RequestPart("file") FilePart filePartParm) throws Exception {
    Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
    String msg="";
    try{
        Files.delete(tempFilePath);
        msg="ok";
    }catch (Exception e) {msg="error";}
    return Mono.just(R.ok().setResult(msg));
}

    /**
     * 模糊查询客户分类级次编码
     * @param cusGradeCode
     * @return
     */
    @PostMapping("/findByLikeCusGradeCode")
    public Mono<R> findByLikeCusGradeCode(String cusGradeCode,String cusClassGrade,String cusBend) {
        return customerClassRepository.findByLikeCusGradeCode(cusGradeCode+"%",cusClassGrade,cusBend)
                .collectList()
                .map(a -> R.ok().setResult(a));
    }

    public List<Map<String, Object>> getChild(String pid, List<GroupCustomerClass> allList) {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (GroupCustomerClass ms : allList) {
            if (ms.getParentId().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("id", ms.getId());
                map.put("uniqueCustclass", ms.getUniqueCustclass());
                map.put("cusClass", ms.getCusClass());
                map.put("cusClassGrade", ms.getCusClassGrade());
                map.put("cusCclassName", ms.getCusCclassName());
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
    public List<AAtemp> getChildDel(String pid, List<GroupCustomerClass> allList) {
        List<AAtemp> childList = new ArrayList<>();//用于保存子节点的list;
        for (GroupCustomerClass ms : allList) {
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
}
