package org.boozsoft.rest.group;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.ListUtils;
import org.boozsoft.domain.entity.group.GroupSysClassIndustry;
import org.boozsoft.domain.entity.group.GroupSysGroup;
import org.boozsoft.repo.group.GroupSysClassIndustryRepository;
import org.boozsoft.repo.group.GroupSysGroupRepository;
import org.boozsoft.util.XlsUtils3;
import org.springbooz.core.tool.result.R;
import org.springbooz.core.tool.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/group/sys-class-industry")
public class GroupSysClassIndustryController {

    @Autowired
    GroupSysGroupRepository groupSysGroupRepository;

    @Autowired
    GroupSysClassIndustryRepository groupSysClassIndustryRepository;

    @GetMapping
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByClass(Pageable pageable) {
        return groupSysGroupRepository.findAll().collectList()
                .flatMap(item -> groupSysGroupRepository.countAllBy()
                        .map(total -> R.page(item, pageable, total)));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysGroup entity) {
        if (null == entity.getId()) {
            entity.setUniqueCode(IdUtil.objectId());
            entity.setFlag("1");
        }
        return groupSysGroupRepository.save(entity)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysGroup entity) {
        //添加回滚信息，删除项目信息
        return groupSysGroupRepository.deleteById(entity.getId())
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询编码重复", notes = "传入code")
    public Mono findByCode(String id, String groupCode) {
        return groupSysGroupRepository.findFirstByGroupCode(groupCode).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                });
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findByName(String id, String groupName) {
        return groupSysGroupRepository.findFirstByGroupName(groupName).collectList()
                .map(list -> {
                    if (StrUtil.isNotBlank(id) && list.size() > 0 && list.get(0).getId().equals(id)) {
                        return R.ok().setResult(0);
                    } else {
                        return R.ok().setResult(list.size());
                    }
                });
    }

    @GetMapping("/findAllIndustry")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono findAllIndustry() {
        return groupSysClassIndustryRepository.findAll().collectList()
                .map(list -> {
                    List<Map<String, Object>> list2 = null;
                    try {
                        list2 = getChild("0", list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return R.ok().setResult(list2);
                });
    }

    public List<Map<String, Object>> getChild(String pid, List<GroupSysClassIndustry> allList) throws Exception {
        List<Map<String, Object>> childList = new ArrayList<>();//用于保存子节点的list;
        for (GroupSysClassIndustry ms : allList) { // 先拿到一级
            if (ms.getPid().equals(pid)) {//判断传入的父id是否等于自身的，如果是，就说明自己是子节点
                Map<String, Object> map = new HashMap<>();
                map.put("value", ms.getUniqueCode());
                map.put("label", ms.getName());
                map.put("children", new Object[]{});
                childList.add(map); //加入子节点
            }
        }
        for (Map<String, Object> map : childList) {//遍历子节点，继续递归判断每个子节点是否还含有子节点
            List<Map<String, Object>> tList = getChild(map.get("value").toString(), allList);
            map.put("children", tList);
        }
        // 支持层级四级 暂时两级过滤方法
        for (Map<String, Object> map : childList) {
            List<Map<String, Object>> children = (List<Map<String, Object>>) map.get("children");
            for (Map<String, Object> child : children) {
                child.put("children", new ArrayList<>());
            }
        }
        return childList;
    }


    @Transactional
    @PostMapping("/import")
    public Flux<R> listOCR(@RequestPart("file") FilePart filePartParm) throws Exception {
        Path tempFilePath = Files.createTempFile("", new String(filePartParm.filename().getBytes("ISO-8859-1"), "UTF-8"));
        return Mono.just(filePartParm)
                .flatMap(filePart -> {
                    try {
                        return DataBufferUtils
                                .write(filePart.content(), AsynchronousFileChannel.open(tempFilePath, StandardOpenOption.WRITE), 0)
                                .collectList()
                                .map(item -> tempFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return Mono.just("");
                })// 上传到临时目录
                // 获取凭证导入动态表头
                .flatMap(item -> {
                    String[] tieles = new String[]{"（字母）门类代码", "（数字）大类代码", "（数字）中类顺序码", "（数字）小类顺序码", "行业名称"};
                    return Mono.just(tieles);
                })
                // 遍历导入excel内容
                .flatMap(titles -> { //根据用户自定义字段读
                    List<Object[]> list = null;
                    try {
                        list = new XlsUtils3(tempFilePath.toString()).getExcelObj(tempFilePath.toString(), titles, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assert tempFilePath != null;
                        try {
                            Files.delete(tempFilePath);
                        } catch (IOException e) {
                            System.err.println("读取导入文件内容时发生错误: ---------下---------");
                            e.printStackTrace();
                        }
                    }
                    return Mono.just(list);
                })
                // 进行条件判断
                .flatMap(list -> {
                    Map mapArr = new HashMap();
                    mapArr.put("excellist", list);   // excel中内容
                    return Mono.just(mapArr);
                })
                .flatMapMany(map -> {
                    String error = (String) map.get("error");
                    String code = (String) map.get("code");
                    if (StringUtils.isNotBlank(error)) {
                        return Mono.just(R.ok().setResult(error).setCode(Long.valueOf(code)));
                    }
                    List<Object[]> excellist = (List<Object[]>) map.get("excellist");
                    List<GroupSysClassIndustry> saveList = new ArrayList();
                    List<GroupSysClassIndustry> oneList = new ArrayList();
                    List<GroupSysClassIndustry> twoList = new ArrayList();
                    List<GroupSysClassIndustry> threeList = new ArrayList();
                    List<GroupSysClassIndustry> fourList = new ArrayList();
                    for (int j = 0; j < excellist.size(); j++) {
                        Object[] row = excellist.get(j);
                        GroupSysClassIndustry industry = new GroupSysClassIndustry();
                        industry.setUniqueCode(IdUtil.objectId());
                        industry.setCategory(row[0].toString());
                        industry.setBigKind(row[1].toString());
                        industry.setMiddleKind(row[2].toString());
                        industry.setSmallKind(row[3].toString());
                        industry.setName(row[4].toString());
                        if (StrUtil.isNotBlank(industry.getCategory()) && StrUtil.isBlank(industry.getBigKind())) {
                            industry.setPid("0");
                            oneList.add(industry);
                        } else if (StrUtil.isNotBlank(industry.getCategory()) && StrUtil.isNotBlank(industry.getBigKind()) && StrUtil.isBlank(industry.getMiddleKind())) {
                            industry.setPid(oneList.stream().filter(item -> item.getCategory().equals(industry.getCategory())).collect(Collectors.toList()).get(0).getUniqueCode());
                            twoList.add(industry);
                        } else if (StrUtil.isNotBlank(industry.getCategory()) && StrUtil.isNotBlank(industry.getBigKind())
                                && StrUtil.isNotBlank(industry.getMiddleKind()) && StrUtil.isBlank(industry.getSmallKind())) {
                            industry.setPid(twoList.stream().filter(item -> item.getCategory().equals(industry.getCategory()) && item.getBigKind().equals(industry.getBigKind()))
                                    .collect(Collectors.toList()).get(0).getUniqueCode());
                            threeList.add(industry);
                        } else {
                            List<GroupSysClassIndustry> collect = threeList.stream().filter(item -> item.getCategory().equals(industry.getCategory()) && item.getBigKind().equals(industry.getBigKind())
                                            && item.getMiddleKind().equals(industry.getMiddleKind()))
                                    .collect(Collectors.toList());
                            if (collect.size() == 0){
                                collect = twoList.stream().filter(item -> item.getCategory().equals(industry.getCategory()) && item.getBigKind().equals(industry.getBigKind()))
                                        .collect(Collectors.toList());
                            }
                            if (collect.size() == 0){
                                collect = oneList.stream().filter(item -> item.getCategory().equals(industry.getCategory())).collect(Collectors.toList());
                            }
                            industry.setPid(collect.get(0).getUniqueCode());
                            fourList.add(industry);
                        }

                    }
                    saveList.addAll(oneList);
                    saveList.addAll(twoList);
                    saveList.addAll(threeList);
                    saveList.addAll(fourList);
//                    return Flux.just(new GroupSysClassIndustry());
                    return groupSysClassIndustryRepository.saveAll(saveList).collectList();
                }).map(R::ok);
    }


}
