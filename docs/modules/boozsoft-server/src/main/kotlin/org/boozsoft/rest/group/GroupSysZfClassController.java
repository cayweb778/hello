package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.ExpenditureClass;
import org.boozsoft.domain.entity.group.GroupSysDeptClass;
import org.boozsoft.domain.entity.group.GroupSysZfClass;
import org.boozsoft.domain.vo.group.GroupExpenditureClassVo;
import org.boozsoft.repo.group.GroupSysZfClassRepository;
import org.boozsoft.util.TreeUtils;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/group/zfClass")
public class GroupSysZfClassController {

    @Autowired
    private GroupSysZfClassRepository groupExpenditureClassRepository;


    @GetMapping("treeDept")
    public Mono<Map> treeDept(String id, String flag) {
        if (StrUtil.isNotBlank(id) && !id.equals("0")) {
            return groupExpenditureClassRepository.findByIdOrderByDeptCode(id)
                    .collectList()
//                    .map(list->TreeUtils.castTreeList(list,GroupExpenditureClassVo.class))
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
        } else {
            return groupExpenditureClassRepository.findAllByOrderByEcCode()
                    .collectList()
                    .map(list -> TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
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
    }

    @GetMapping("treeDeptByFlag")
    public Mono<Map> treeDeptByFlag() {
        return groupExpenditureClassRepository.findByFlagOrderByEcCode("1")
                .collectList()
                .map(list->TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }

    @GetMapping("treeDeptByIsEnd")
    public Mono<Map> treeDeptByIsEnd() {
        return groupExpenditureClassRepository.findByFlagAndBendOrderByEcCode("1","1")
                .collectList()
                .map(list->TreeUtils.castTreeList(list, GroupExpenditureClassVo.class))
                .map(list -> CollectOfUtils.mapof(
                        "code", 0,
                        "message", "ok",
                        "type", "success",
                        "result", list
                ));
    }


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return groupExpenditureClassRepository.findAllByOrderByEcCode().collectList().map(R::page);
    }

    @GetMapping("all")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllDept(){
        return groupExpenditureClassRepository.findAllDeptCodeOrDeptNameByFlag().collectList().map(R::ok);
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id){
        Mono mono = groupExpenditureClassRepository.findById(id);
        return mono;
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysZfClass object){
        if (object.getUniqueCode()==null || object.getUniqueCode().equals("")){
            object.setUniqueCode(IdUtil.objectId());
        }
        if (object.getFlag()==null || object.getFlag().equals("")){
            object.setFlag("1");
            object.setIsDel("0");
        }

        //1级 无上级
        if (object.getParentId()==null || object.getParentId().equals("")){
            object.setParentId("0");
            object.setBend("1");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (object.getCreateDate()==null || object.getCreateDate().equals("")){
            object.setCreateDate(sdf.format(new Date()));
        }

        //有上级不是末级时
        if(object.getParentId()!=null && !object.getParentId().equals("") && object.getBend().equals("0")){
            object.setBend("1");
        }
        //有上级是末级时
        Mono<GroupSysZfClass> sysDeptClassMono = null;
        if(object.getParentId()!=null && !object.getParentId().equals("") && !object.getParentId().equals("0") && object.getBend().equals("1")){
            object.setBend("1");
            sysDeptClassMono = groupExpenditureClassRepository.findById(object.getParentId())
                    .map(v -> v.setBend("0"))
                    .flatMap(groupExpenditureClassRepository::save);
        }
        //增加
        if (object.getId()==null || object.getId().equals("")) {
            return Objects.isNull(sysDeptClassMono)? groupExpenditureClassRepository.save(object)
                    .map(o -> R.ok().setResult(o)):sysDeptClassMono.zipWith(groupExpenditureClassRepository.save(object)
                    .map(o -> R.ok().setResult(o))).thenReturn(R.ok());
        }

        return Objects.isNull(sysDeptClassMono)? groupExpenditureClassRepository.save(object)
                .map(o -> R.ok().setResult(o)):sysDeptClassMono.zipWith(groupExpenditureClassRepository.save(object)
                .map(o -> R.ok().setResult(o))).thenReturn(R.ok());
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupSysZfClass object) {
        return groupExpenditureClassRepository.findByIdOrderByDeptCode(object.getId())
                .collectList()
                .flatMap(item -> {
                    item.stream().forEach(v->{
                        v.setIsDel("1");
                        v.setDelDate(LocalDate.now().toString());
                        v.setDelName(object.getDelName());
                    });
                    return groupExpenditureClassRepository.saveAll(item).then();
                })
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupSysZfClass object) {
        if (object.getFlag().equals("1")){
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
    public Mono excel(@RequestBody List<GroupSysZfClass> object){
        object.stream().forEach(v->{
            v.setUniqueCode(IdUtil.objectId());
        });
        return groupExpenditureClassRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }
    @GetMapping("/getMaxCode")
    @ApiOperation(value = "获取最大code", notes = "获取最大code")
    public Mono getMaxCode(String id){
        return groupExpenditureClassRepository.findMaxCodeByPid(id)
                .map(v->{
                    return (Integer.valueOf(v)+1)+"";
                })
                .defaultIfEmpty("0")
                .map(v-> R.ok().setResult(v));
    }

    @PostMapping("findDelAll")
    @ApiOperation(value = "查询回收站列表", notes = "查询回收站列表")
    public Mono<R> findDelAll(@RequestBody Map map){
        //查询后过滤
        return groupExpenditureClassRepository.findAllByIsDel().collectList()
                .map(list->{
                    Map<String, String> searchMap = ((HashMap<String, String>) map.get("searchConditon"));
                    return list.stream().filter(v->{
                        // 搜索过滤
                        if (StrUtil.isNotBlank(searchMap.get("requirement")) && StrUtil.isNotBlank(searchMap.get("value"))) {
                            String value = searchMap.get("value");
                            String dbValue = getFieldValueByFieldName(searchMap.get("requirement").trim(), v);
                            if (null != dbValue && !dbValue.contains(value)) return false;
                        }
                        return true;
                    });
                })
                .map(v-> R.ok().setResult(v));
    }

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

    @PostMapping("deleteList")
    @ApiOperation(value = "回收站删除", notes = "传入ids")
    public Mono deleteList(@RequestBody List ids){
        return groupExpenditureClassRepository.deleteAllByIds(ids).thenReturn(R.ok());
    }

    @PostMapping("reductionList")
    @ApiOperation(value = "回收站还原", notes = "传入ids")
    public Mono reductionList(@RequestBody List ids){
        return groupExpenditureClassRepository.updateIsDel(ids).thenReturn(R.ok());
    }

}
