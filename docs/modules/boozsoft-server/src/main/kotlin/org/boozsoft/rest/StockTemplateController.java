package org.boozsoft.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.StockTemplateSetting;
import org.boozsoft.domain.entity.StockTemplateSettings;
import org.boozsoft.repo.StockTemplateSettingRepository;
import org.boozsoft.repo.StockTemplateSettingsRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.*;

@RestController
@RequestMapping("/stock/template")
public class StockTemplateController {

    @Autowired
    private StockTemplateSettingRepository templateSettingRepository;
    @Autowired
    private StockTemplateSettingsRepository templateSettingsRepository;

    @PostMapping("tree")
    @ApiOperation(value = "查询所有主表", notes = "传入object")
    public Mono<R> tree(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just(R.error());
        String belong = map.get("belong").toString();
        return templateSettingRepository.findAllByTemplateBelongOrderByTemplateSortAsc(belong).collectList().map(o -> R.ok(ListUtil.sort(o, (o1, o2) -> Integer.valueOf(o1.getTemplateSort()).compareTo(Integer.valueOf(o2.getTemplateSort())))));
    }

    @PostMapping("init")
    @Transactional
    @ApiOperation(value = "保存", notes = "传入object")
    public Mono<R> init(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just(R.error());
        Map<String, Object> obj = (Map<String, Object>) map.get("json");
        StockTemplateSetting core = JSON.parseObject(JSON.toJSONString(obj.get("core")), StockTemplateSetting.class);
        List<StockTemplateSettings> list = JSON.parseArray(JSON.toJSONString(obj.get("list")), StockTemplateSettings.class);
        return templateSettingRepository.save(core).flatMap(db -> {
            for (StockTemplateSettings stockTemplateSettings : list)
                stockTemplateSettings.setParentId(db.getId());
            List<StockTemplateSetting> res = new ArrayList<>();
            res.add(db);
            return templateSettingsRepository.saveAll(list).collectList().thenReturn(R.ok(res));
        });
    }

    @PostMapping("table")
    @ApiOperation(value = "查询表体", notes = "传入object")
    public Mono<R> table(@RequestBody Map map) {
        if (!map.containsKey("selectValue")) return Mono.just(R.error());
        String[] values = map.get("selectValue").toString().split("-");
        return templateSettingsRepository.findAllByParentIdAndBelongAreaOrderByPaintSortAsc(values[0],values[1]).collectList().map(o -> R.ok(ListUtil.sort(o, (o1, o2) -> Integer.valueOf(o1.getPaintSort()).compareTo(Integer.valueOf(o2.getPaintSort())))));
    }

    @PostMapping("query")
    @ApiOperation(value = "获取指定菜单的模版数据", notes = "传入object")
    public Mono<R> query(@RequestBody Map map) {
        if (!map.containsKey("selectValue")) return Mono.just(R.error());
        String value = map.get("selectValue").toString();
        return templateSettingsRepository.findAllByParentIdOrderByBelongAreaAscPaintSortAsc(value).filter(it-> null != it.getIsPrint() && it.getIsPrint().equals("true")).collectList().map(o -> {// 多条件排序
            Collections.sort(o,((Comparator<StockTemplateSettings>) (o1, o2) -> Integer.parseInt(o1.getBelongArea()) - Integer.parseInt(o2.getBelongArea())).thenComparing((o1, o2) -> Integer.parseInt(o1.getPaintSort()) - Integer.parseInt(o2.getPaintSort())));
            return R.ok(o);
        });
    }

    @PostMapping("copy")
    @Transactional
    @ApiOperation(value = "复制", notes = "传入object")
    public Mono<R> copy(@RequestBody Map map) {
        if (map.keySet().size() != 3) return Mono.just(R.error());
        String source = map.get("source").toString();
        String name = map.get("name").toString();
        String type = map.get("type").toString();
        return templateSettingRepository.findById(source).zipWith(templateSettingsRepository.findAllByParentId(source).collectList()).flatMap(tips -> {
            StockTemplateSetting db = tips.getT1();
            List<StockTemplateSettings> list = tips.getT2();
            db.setId(null).setTemplateName(name).setTemplateType(type);
            String belong = db.getTemplateBelong();
            return templateSettingRepository.findFirstByTemplateBelongOrderByTemplateSortDesc(belong).flatMap(last->{
                db.setTemplateSort((last.getTemplateSort()+1)+"");
                return templateSettingRepository.save(db).flatMap(newDb->{
                    for (StockTemplateSettings stockTemplateSettings : list)
                        stockTemplateSettings.setId(null).setParentId(newDb.getId());
                    return templateSettingsRepository.saveAll(list).collectList().thenReturn(R.ok(newDb));
                });
            });
        });
    }

    @DeleteMapping
    @Transactional
    @ApiOperation(value = "删除", notes = "传入object")
    public Mono<R> del(@RequestBody Map map) {
        if (!map.containsKey("id")) return Mono.just(R.error());
        String source = map.get("id").toString();
        return templateSettingRepository.findById(source).zipWith(templateSettingsRepository.findAllByParentId(source).collectList()).flatMap(tips -> {
            StockTemplateSetting db = tips.getT1();
            List<StockTemplateSettings> list = tips.getT2();
            if (db.getTemplateType().equals("0"))return Mono.just(R.ok());
            return templateSettingRepository.delete(db).thenReturn("").zipWith(templateSettingsRepository.deleteAll(list).thenReturn("1")).flatMap(tips2->Mono.just(R.ok()));
        });
    }

    @PostMapping("check")
    @ApiOperation(value = "检查名称", notes = "传入object")
    public Mono<R> check(@RequestBody Map map) {
        if (map.keySet().size() != 2) return Mono.just(R.error());
        String belong = map.get("belong").toString();
        String name = map.get("name").toString().trim();
        return templateSettingRepository.countAllByTemplateBelongAndTemplateName(belong,name).map(R::ok);
    }

    @PostMapping("modify")
    @Transactional
    @ApiOperation(value = "更新", notes = "传入object")
    public Mono<R> modify(@RequestBody Map map) {
        if (map.keySet().size() != 2) return Mono.just(R.error());
        String[] values = map.get("selectValue").toString().split("-");
        List<StockTemplateSettings> jsonList = JSON.parseArray(map.get("json").toString(), StockTemplateSettings.class);
        int sort = 0;
        for (StockTemplateSettings settings : jsonList) {
            settings.setId(null).setParentId(values[0]).setPaintSort(sort+"").setBelongArea(values[1]);
            sort+=1;
        }
        return templateSettingsRepository.findAllByParentIdAndBelongAreaOrderByPaintSortAsc(values[0],values[1]).collectList().flatMap(dbList->templateSettingsRepository.deleteAll(dbList).thenReturn("")).flatMap(
                s -> templateSettingsRepository.saveAll(jsonList).collectList().thenReturn(R.ok())
        );
    }
}
