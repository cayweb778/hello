package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.Objects;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.ColumnSettings;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.repo.ColumnSettingsRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/columnSettings")
public class ColumnSettingsController {

    @Autowired
    ColumnSettingsRepository columnSettingsRepository;

    @Autowired
    SysLogService sysLogService;

    @ApiOperation(value = "查询参数精度列表", notes = "")
    @GetMapping
    public Mono<R> findAll(String accId, String menuName, String type) {
        return columnSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameOrderByTkeyAsc(accId, menuName, type)
                .collectList().map(list -> assemblyColumnData(list)).map(R::page);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> save(@RequestBody Map bodyMap) {
        String objects = getMapValueByKey(bodyMap, "objects");
        String accId = getMapValueByKey(bodyMap, "accId");
        String menuName = getMapValueByKey(bodyMap, "menuName");
        String type = getMapValueByKey(bodyMap, "type");
        String username = getMapValueByKey(bodyMap, "username");
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String year = new SimpleDateFormat("yyyy").format(new Date());
        //系统操作日志
        SysLog sysLog = new SysLog();
        sysLog.setOperationDate(time);
        sysLog.setUniqueCode("a001");
        sysLog.setUserName(username);
        sysLog.setAccId(accId);
        sysLog.setIyear(year);
        //增加
        return columnSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameOrderByTkeyAsc(accId, menuName, type)
                .collectList()
                .flatMap(list ->
                        {
                            sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】自定义了" + menuName + "菜单的栏目参数");
                            List<HashMap> lanMuList = JSON.parseArray(objects, HashMap.class);
                            return columnSettingsRepository.saveAll(resolveColumnData(lanMuList, list, accId, menuName, type))
                                    .collectList()
                                    .map(list1 -> list1)
                                    .zipWith(sysLogService.save_log(sysLog));
                        }
                ).map(o -> R.ok());
    }

    @PostMapping("delDigestColumnSettingsApi")
    public Mono<R> del(@RequestBody Map bodyMap){
        String accId = getMapValueByKey(bodyMap, "accId");
        String menuName = getMapValueByKey(bodyMap, "menuName");
        String type = getMapValueByKey(bodyMap, "type");
        return columnSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameOrderByTkeyAsc(accId, menuName, type)
                .collectList()
                .flatMap(list ->{
                  return list.size()>0?columnSettingsRepository.delete(list.get(0)).then(Mono.just("")):Mono.just("");
                })
                .map(o->R.ok().setResult(o));

    }

    private List<Map<String, String>> assemblyColumnData(List<ColumnSettings> list) {
        List<Map<String, String>> result = new ArrayList<>();
        for (ColumnSettings entity : list) {
            Map<String, String> obj = new HashMap<>();
            obj.put("key", entity.getTkey());
            obj.put("pkey", entity.getPkey());
            obj.put("name", entity.getColumnName());
            obj.put("nameNew", entity.getDisplayName());
            obj.put("check", entity.getIfShow().equals("") ? "true" : entity.getIfShow());
            obj.put("width", entity.getColumnWidth());
            obj.put("align", entity.getColumnAlign());
            result.add(obj);
        }
        return result;
    }

    private List<ColumnSettings> resolveColumnData(List<HashMap> list, List<ColumnSettings> dbList, String accId, String menu, String type) {
        List<ColumnSettings> result = new ArrayList<>();
        if (dbList.size() > 0) {
            for (ColumnSettings entity : dbList) {
                for (Map<String, String> obj : list) {
                    if (entity.getTkey().equals(getMapValueByKey(obj, "key"))) {
                        String nameNew = getMapValueByKey(obj, "nameNew");
                        String check = getMapValueByKey(obj, "check");
                        String width = getMapValueByKey(obj, "width");
                        String align = getMapValueByKey(obj, "align");
                        String pkey = getMapValueByKey(obj, "pkey");
                        if (StrUtil.isNotBlank(nameNew) && !Objects.equals(entity.getDisplayName(), nameNew))
                            entity.setDisplayName(nameNew);
                        if (StrUtil.isNotBlank(check) && !Objects.equals(entity.getDisplayName(), check))
                            entity.setIfShow(check);
                        if (StrUtil.isNotBlank(width) && !Objects.equals(entity.getDisplayName(), width))
                            entity.setColumnWidth(width);
                        if (StrUtil.isNotBlank(align) && !Objects.equals(entity.getDisplayName(), align))
                            entity.setColumnAlign(align);
                        if (StrUtil.isNotBlank(pkey) && !Objects.equals(entity.getPkey(), pkey))
                            entity.setPkey(pkey);
                        result.add(entity);
                        break;
                    }
                }
            }
            //新增项add
            List<HashMap> newList = list.stream().filter(item -> {
                List<ColumnSettings> key = dbList.stream().filter(item2 -> item2.getTkey().equals(getMapValueByKey(item, "key"))).collect(Collectors.toList());
                return key.size() > 0 ? false : true;
            }).collect(Collectors.toList());
            for (Map<String, String> obj : newList) {
                ColumnSettings settings = new ColumnSettings();
                String check = getMapValueByKey(obj, "check");
                String pKey = getMapValueByKey(obj, "pkey");
                settings.setTkey(getMapValueByKey(obj, "key"));
                settings.setColumnName(getMapValueByKey(obj, "name"));
                settings.setDisplayName(getMapValueByKey(obj, "nameNew"));
                settings.setColumnWidth(getMapValueByKey(obj, "width"));
                settings.setColumnAlign(getMapValueByKey(obj, "align"));
                settings.setIfShow(StrUtil.isBlank(check) ? "true" : check);
                settings.setAccountId(accId);
                settings.setOwningMenuName(menu);
                settings.setColumnTypeName(type);
                if (StrUtil.isNotBlank(pKey)) {
                    settings.setPkey(pKey);
                }
                result.add(settings);
            }
        } else if (list.size() > 0) {
            for (Map<String, String> obj : list) {
                ColumnSettings settings = new ColumnSettings();
                String check = getMapValueByKey(obj, "check");
                String pKey = getMapValueByKey(obj, "pkey");
                settings.setTkey(getMapValueByKey(obj, "key"));
                settings.setColumnName(getMapValueByKey(obj, "name"));
                settings.setDisplayName(getMapValueByKey(obj, "nameNew"));
                settings.setColumnWidth(getMapValueByKey(obj, "width"));
                settings.setColumnAlign(getMapValueByKey(obj, "align"));
                settings.setIfShow(StrUtil.isBlank(check) ? "true" : check);
                settings.setAccountId(accId);
                settings.setOwningMenuName(menu);
                settings.setColumnTypeName(type);
                if (StrUtil.isNotBlank(pKey)) {
                    settings.setPkey(pKey);
                }
                result.add(settings);
            }
        }
        return result;
    }

    private String getMapValueByKey(Map map, String key) {
        try {
            return map.get(key).toString().trim();
        } catch (Exception e) {
            return "";
        }
    }
}
