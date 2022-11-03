package org.boozsoft.rest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.Objects;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.stock.StockHeadColumnSettings;
import org.boozsoft.repo.stock.StockHeadColumnSettingsRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stockColumnSettings")
public class StockHeadColumnSettingsController {

    @Autowired
    StockHeadColumnSettingsRepository columnSettingsRepository;

    @ApiOperation(value = "查询表头", notes = "")
    @GetMapping
    public Mono<R> findAll(String menuName) {
        return columnSettingsRepository.findAllByOwningMenuNameOrderBySerialAsc(menuName).collectList().map(list -> assemblyColumnData(list)).map(R::ok);
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> save(@RequestBody Map bodyMap) {
        String objects = getMapValueByKey(bodyMap, "objects");
        String menuName = getMapValueByKey(bodyMap, "menuName");
        String username = getMapValueByKey(bodyMap, "username");
        return columnSettingsRepository.findAllByOwningMenuNameOrderBySerialAsc(menuName).collectList().flatMap(dblist -> {
            List<HashMap> lanMuList = JSON.parseArray(objects, HashMap.class);
            return columnSettingsRepository.saveAll(resolveColumnData(lanMuList, dblist, menuName)).collectList();
        }).map(o -> R.ok());
    }

    @DeleteMapping
    public Mono<R> del(@RequestBody Map bodyMap) {
        String menuName = getMapValueByKey(bodyMap, "menuName");
        List<String> fieldFame = JSON.parseArray(getMapValueByKey(bodyMap, "fieldFame"),String.class);
        return columnSettingsRepository.findAllByOwningMenuNameAndColumnTypeNameAndFieldNameInOrderBySerialAsc(menuName,"2",fieldFame).collectList().flatMap(list -> list.size() > 0 ? columnSettingsRepository.deleteAll(list).then(Mono.just("")) : Mono.just("")).map(o -> R.ok().setResult(o));
    }

    private List<Map<String, Object>> assemblyColumnData(List<StockHeadColumnSettings> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StockHeadColumnSettings entity : list) {
            Map<String, Object> obj = new HashMap<>();
            obj.put("field", entity.getFieldName());
            obj.put("label", entity.getLabelName());
            obj.put("component", entity.getInputType());
            obj.put("componentProps", StrUtil.isBlank(entity.getParaSource()) ? null : entity.getParaSource());
            obj.put("required", entity.getRequired());
            obj.put("isShow", entity.getIsShow());
            obj.put("columnType", entity.getColumnTypeName());
            obj.put("serial", entity.getSerial());
            result.add(obj);
        }
        return result;
    }

    private List<StockHeadColumnSettings> resolveColumnData(List<HashMap> savelist, List<StockHeadColumnSettings> dbList, String menu) {
        List<StockHeadColumnSettings> result = new ArrayList<>();
        if (dbList.size() > 0) {
            for (StockHeadColumnSettings entity : dbList) {
                for (Map<String, String> obj : savelist) {
                    if (entity.getFieldName().equals(getMapValueByKey(obj, "field"))) {
                        String label = getMapValueByKey(obj, "label");
                        String component = getMapValueByKey(obj, "component");
                        String componentProps = getMapValueByKey(obj, "componentProps");
                        String required = getMapValueByKey(obj, "required");
                        String isShow = getMapValueByKey(obj, "isShow");
                        String columnType = getMapValueByKey(obj, "columnType");
                        String serial = getMapValueByKey(obj, "serial");
                        if (StrUtil.isNotBlank(label) && !Objects.equals(entity.getLabelName(), label))
                            entity.setLabelName(label);
                        if (StrUtil.isNotBlank(component) && !Objects.equals(entity.getInputType(), component))
                            entity.setInputType(component);
                        if (StrUtil.isNotBlank(componentProps) && !Objects.equals(entity.getParaSource(), componentProps))
                            entity.setParaSource(componentProps);
                        if (StrUtil.isNotBlank(required) && !Objects.equals(entity.getRequired(), required))
                            entity.setRequired(required);
                        if (StrUtil.isNotBlank(isShow) && !Objects.equals(entity.getIsShow(), isShow))
                            entity.setIsShow(isShow);
                        if (StrUtil.isNotBlank(columnType) && !Objects.equals(entity.getColumnTypeName(), columnType))
                            entity.setColumnTypeName(columnType);
                        if (StrUtil.isNotBlank(serial) && entity.getSerial()!= Long.parseLong(serial))
                            entity.setSerial(Long.parseLong(serial));
                        result.add(entity);
                        break;
                    }
                }
            }
            //新增项add
            List<HashMap> newList = savelist.stream().filter(item -> {
                List<StockHeadColumnSettings> key = dbList.stream().filter(item2 -> item2.getFieldName().equals(getMapValueByKey(item, "field"))).collect(Collectors.toList());
                return key.size() > 0 ? false : true;
            }).collect(Collectors.toList());
            for (Map<String, String> obj : newList) {
                StockHeadColumnSettings settings = new StockHeadColumnSettings();
                settings.setFieldName(getMapValueByKey(obj, "field"));
                settings.setLabelName(getMapValueByKey(obj, "label"));
                settings.setInputType(getMapValueByKey(obj, "component"));
                settings.setParaSource(getMapValueByKey(obj, "componentProps"));
                settings.setRequired(getMapValueByKey(obj, "required"));
                settings.setIsShow(getMapValueByKey(obj, "isShow"));
                settings.setColumnTypeName(getMapValueByKey(obj, "columnType"));
                settings.setSerial(Long.parseLong(getMapValueByKey(obj, "serial")));
                settings.setOwningMenuName(menu);
                result.add(settings);
            }
        } else if (savelist.size() > 0) {
            for (Map<String, String> obj : savelist) {
                StockHeadColumnSettings settings = new StockHeadColumnSettings();
                settings.setFieldName(getMapValueByKey(obj, "field"));
                settings.setLabelName(getMapValueByKey(obj, "label"));
                settings.setInputType(getMapValueByKey(obj, "component"));
                settings.setParaSource(getMapValueByKey(obj, "componentProps"));
                settings.setRequired(getMapValueByKey(obj, "required"));
                settings.setIsShow(getMapValueByKey(obj, "isShow"));
                settings.setColumnTypeName(getMapValueByKey(obj, "columnType"));
                settings.setSerial(Long.parseLong(getMapValueByKey(obj, "serial")));
                settings.setOwningMenuName(menu);
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
