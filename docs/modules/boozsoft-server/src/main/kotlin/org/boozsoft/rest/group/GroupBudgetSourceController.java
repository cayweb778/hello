package org.boozsoft.rest.group;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupExpenditureClass;
import org.boozsoft.domain.entity.group.GroupFaClass;
import org.boozsoft.domain.entity.group.GroupBudgetSource;
import org.boozsoft.repo.group.GroupBudgetSourceRepository;
import org.boozsoft.repo.group.GroupFaClassRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/budgetSource")
public class GroupBudgetSourceController {

    @Autowired
    private GroupBudgetSourceRepository groupBudgetSourceRepository;


    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable){
        return groupBudgetSourceRepository.findAllByOrderById(pageable).collectList().map(R::page);
    }

    @GetMapping("findByFlag")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByFlag(){
        return groupBudgetSourceRepository.findByFlagOrderById("1")
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String settModesCode){
        return groupBudgetSourceRepository.findByBsCodeOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }
    @GetMapping("findByBsName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByBsName(String settModesCode){
        return groupBudgetSourceRepository.findByBsNameOrderById(settModesCode)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupBudgetSource object){
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag()==null || object.getFlag().equals("")) {
            object.setFlag("1");
            object.setIsDel("0");
        }
        //增加
        // 关羽 千里 横扫  夏侯 裸衣 百骑 蔡文姬 34 盛气
        // 陆 夺魄 草船 太 挡风 速成 鲁 赞比 金帆
        //
        if (object.getId()==null || object.getId().equals("")) {
            object.setCreateDate(time);
        }
        return groupBudgetSourceRepository.save(object)
                .map(o-> R.ok().setResult(o));
    }

    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupBudgetSource object){
        return groupBudgetSourceRepository.findById(object.getId())
                .flatMap(obj->{
                    obj.setIsDel("1");
                    obj.setDelDate(LocalDate.now().toString());
                    obj.setDelName(object.getDelName());
                    return groupBudgetSourceRepository.save(obj);
                })
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("editFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editFlag(@RequestBody GroupBudgetSource object) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        if (object.getFlag().equals("1")){
            object.setStopDate(time);
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return groupBudgetSourceRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<GroupBudgetSource> object){
        return groupBudgetSourceRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

    @PostMapping("findDelAll")
    @ApiOperation(value = "查询回收站列表", notes = "查询回收站列表")
    public Mono<R> findDelAll(@RequestBody Map map){
        //查询后过滤
        return groupBudgetSourceRepository.findAllByIsDel().collectList()
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
        return groupBudgetSourceRepository.deleteAllByIds(ids).thenReturn(R.ok());
    }

    @PostMapping("reductionList")
    @ApiOperation(value = "回收站还原", notes = "传入ids")
    public Mono reductionList(@RequestBody List ids){
        return groupBudgetSourceRepository.updateIsDel(ids).thenReturn(R.ok());
    }
}
