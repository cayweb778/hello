package org.boozsoft.rest.stock;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.Objects;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.ColumnSettings;
import org.boozsoft.domain.entity.SysLog;
import org.boozsoft.domain.entity.stock.StockColumnViewSettings;
import org.boozsoft.repo.ColumnSettingsRepository;
import org.boozsoft.repo.stock.StockColumnViewSettingsRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stockColumnViewSettings")
public class StockColumnViewSettingsController {

    @Autowired
    StockColumnViewSettingsRepository stockColumnViewSettingsRepository;

    @ApiOperation(value = "查询参数精度列表", notes = "")
    @GetMapping
    public Mono<R> findAll(String accId, String menuName, String type,String userId,String isType) {
        return stockColumnViewSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameAndUserIdAndIsTypeOrderByTkeyAsc(accId, menuName, type, userId,isType)
                .collectList()
                .map(list -> assemblyColumnData(list))
                .map(R::page);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> save(@RequestBody Map bodyMap) {
        String objects = getMapValueByKey(bodyMap, "objects");
        String accId = getMapValueByKey(bodyMap, "accId");
        String userId = getMapValueByKey(bodyMap, "userId");
        String menuName = getMapValueByKey(bodyMap, "menuName");
        String type = getMapValueByKey(bodyMap, "type");
        String isType = getMapValueByKey(bodyMap, "isType");
        //增加
        return stockColumnViewSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameAndUserIdAndIsTypeOrderByTkeyAsc(accId, menuName, type,userId,isType)
                .collectList()
                .flatMap(list ->
                        {
                            List<HashMap> lanMuList = JSON.parseArray(objects, HashMap.class);
                            return stockColumnViewSettingsRepository.saveAll(resolveColumnData(lanMuList, list, accId, menuName, type,userId,isType))
                                    .collectList()
                                    .map(list1 -> list1);
                        }
                ).map(o -> R.ok());
    }

    @PostMapping("delDigestColumnSettingsApi")
    public Mono<R> del(@RequestBody Map bodyMap){
        String accId = getMapValueByKey(bodyMap, "accId");
        String userId = getMapValueByKey(bodyMap, "userId");
        String menuName = getMapValueByKey(bodyMap, "menuName");
        String type = getMapValueByKey(bodyMap, "type");
        String isType = getMapValueByKey(bodyMap, "isType");
        return stockColumnViewSettingsRepository.findAllByAccountIdAndOwningMenuNameAndColumnTypeNameAndUserIdAndIsTypeOrderByTkeyAsc(accId, menuName, type,userId,isType)
                .collectList()
                .flatMap(list ->{
                  return list.size()>0?stockColumnViewSettingsRepository.delete(list.get(0)).then(Mono.just("")):Mono.just("");
                })
                .map(o->R.ok().setResult(o));

    }

    private List<Map<String, String>> assemblyColumnData(List<StockColumnViewSettings> list) {
        List<Map<String, String>> result = new ArrayList<>();
        for (StockColumnViewSettings entity : list) {
            Map<String, String> obj = new HashMap<>();
            obj.put("key", entity.getTkey());
            obj.put("pKey", entity.getPkey());
            obj.put("name", entity.getColumnName());
            obj.put("nameNew", entity.getDisplayName());
            obj.put("check", entity.getIfShow().equals("") ? "true" : entity.getIfShow());
            obj.put("width", entity.getColumnWidth());
            obj.put("align", entity.getColumnAlign());
            result.add(obj);
        }
        return result;
    }

    private List<StockColumnViewSettings> resolveColumnData(List<HashMap> list, List<StockColumnViewSettings> dbList, String accId, String menu, String type,String userId,String isType) {
        List<StockColumnViewSettings> result = new ArrayList<>();
        if (dbList.size() > 0) {
            for (StockColumnViewSettings entity : dbList) {
                for (Map<String, String> obj : list) {
                    if (entity.getTkey().equals(getMapValueByKey(obj, "key"))) {
                        String nameNew = getMapValueByKey(obj, "nameNew");
                        String check = getMapValueByKey(obj, "check");
                        String width = getMapValueByKey(obj, "width");
                        String align = getMapValueByKey(obj, "align");
                        if (StrUtil.isNotBlank(nameNew) && !Objects.equals(entity.getDisplayName(), nameNew))
                            entity.setDisplayName(nameNew);
                        if (StrUtil.isNotBlank(check) && !Objects.equals(entity.getDisplayName(), check))
                            entity.setIfShow(check);
                        if (StrUtil.isNotBlank(width) && !Objects.equals(entity.getDisplayName(), width))
                            entity.setColumnWidth(width);
                        if (StrUtil.isNotBlank(align) && !Objects.equals(entity.getDisplayName(), align))
                            entity.setColumnAlign(align);
                        result.add(entity);
                        break;
                    }
                }
            }
            //新增项add
            List<HashMap> newList = list.stream().filter(item -> {
                List<StockColumnViewSettings> key = dbList.stream().filter(item2 -> item2.getTkey().equals(getMapValueByKey(item, "key"))).collect(Collectors.toList());
                return key.size() > 0 ? false : true;
            }).collect(Collectors.toList());
            for (Map<String, String> obj : newList) {
                StockColumnViewSettings settings = new StockColumnViewSettings();
                String check = getMapValueByKey(obj, "check");
                String pKey = getMapValueByKey(obj, "pKey");
                settings.setTkey(getMapValueByKey(obj, "key"));
                settings.setColumnName(getMapValueByKey(obj, "name"));
                settings.setDisplayName(getMapValueByKey(obj, "nameNew"));
                settings.setColumnWidth(getMapValueByKey(obj, "width"));
                settings.setColumnAlign(getMapValueByKey(obj, "align"));
                settings.setIfShow(StrUtil.isBlank(check) ? "true" : check);
                settings.setAccountId(accId);
                settings.setOwningMenuName(menu);
                settings.setColumnTypeName(type);
                settings.setUserId(userId);
                settings.setIsType(isType);
                if (StrUtil.isNotBlank(pKey)) {
                    settings.setPkey(pKey);
                }
                result.add(settings);
            }
        } else if (list.size() > 0) {
            for (Map<String, String> obj : list) {
                StockColumnViewSettings settings = new StockColumnViewSettings();
                String check = getMapValueByKey(obj, "check");
                String pKey = getMapValueByKey(obj, "pKey");
                settings.setTkey(getMapValueByKey(obj, "key"));
                settings.setColumnName(getMapValueByKey(obj, "name"));
                settings.setDisplayName(getMapValueByKey(obj, "nameNew"));
                settings.setColumnWidth(getMapValueByKey(obj, "width"));
                settings.setColumnAlign(getMapValueByKey(obj, "align"));
                settings.setIfShow(StrUtil.isBlank(check) ? "true" : check);
                settings.setAccountId(accId);
                settings.setOwningMenuName(menu);
                settings.setColumnTypeName(type);
                settings.setUserId(userId);
                settings.setIsType(isType);
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
