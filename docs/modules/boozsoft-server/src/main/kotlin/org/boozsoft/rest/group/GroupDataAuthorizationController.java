package org.boozsoft.rest.group;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupDataAuthorization;
import org.boozsoft.domain.entity.group.GroupDataSeeSwitch;
import org.boozsoft.repo.SysAccountPeriodRepository;
import org.boozsoft.repo.group.GroupDataAuthorizationRepository;
import org.boozsoft.repo.group.GroupDataSeeSwitchRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dataAuthor")
public class GroupDataAuthorizationController {

    @Autowired
    GroupDataSeeSwitchRepository groupDataSeeSwitchRepository;

    @Autowired
    GroupDataAuthorizationRepository groupDataAuthorizationRepository;

    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;

    @Autowired
    DatabaseClient client;

    @PostMapping("getTheControlArchives")
    @ApiOperation(value = "获取管控菜单", notes = "传入code")
    public Mono<R> getTheControlArchives(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just(R.error());
        String uniqueCode = map.get("uniqueCode").toString();
        return groupDataSeeSwitchRepository.findAllByUniqueCodeOrderByIdAscUniqueCodeAsc(uniqueCode).collectList().map(list -> R.ok(list));
    }

    @PostMapping("getArchivesAllDataList")
    @ApiOperation(value = "获取指定表的数据", notes = "传入code")
    public Mono<R> getArchivesDataList(@RequestBody Map map) {
        if (map.keySet().size() != 4) return Mono.just(R.error());
        Map<String, String> model = getQueryMap(map);
        // 常规查询 与 正对科目的年度 与关联条件
        String sql = "SELECT {} as code,{} as name,{} as unique {} from {} where {} = '{}'  {} {} order by {} asc";
        String rStr = "";
        String yStr = "";
        String dStr = "";
        if (StrUtil.isNotBlank(model.get("relation"))) rStr = StrUtil.format(",{} as relation",model.get("relation"));
        if (StrUtil.isNotBlank(model.get("year"))) yStr = StrUtil.format(" and iyear = '{}'",model.get("year"));
        if (false) dStr = StrUtil.format(" and is_del = '{}'","0");
        return client.sql(StrUtil.format(sql,model.get("code"), model.get("name"), model.get("unique"), rStr,model.get("table"), model.get("condition"), model.get("value"), dStr, yStr, model.get("code"))).fetch().all().collectList().map(a -> R.ok().setResult(a));
    }

    private Map<String, String> getQueryMap(Map map) {
        HashMap<String, String> rM = new HashMap<>();
        String tableName = map.get("tableName").toString();
        String value = map.get("uniqueCode").toString();
        String year = map.get("year").toString();
        GroupDataSeeSwitch query = JSON.parseObject(map.get("query").toString(), GroupDataSeeSwitch.class);
        rM.put("table", tableName);
        rM.put("value", value);
        rM.put("code", query.getDatabaseColCode());
        rM.put("name", query.getDatabaseColName());
        rM.put("unique", query.getUniqueColCode());
        rM.put("relation", query.getRelation());
        rM.put("condition", query.getDatabaseColCondition()); // 租户条件
        rM.put("year", query.getIsCtrlYear().equals("1") ? year : "");// 附加条件 默认正常状态
        return rM;
    }

    @PostMapping("findUserAuthor")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findUserAuthor(@RequestBody Map map) {
        String uniqueCode = map.get("uniqueCode").toString();
        String userUniqueCode = map.get("userId").toString();
        return groupDataAuthorizationRepository.findAllByOperatorIdAndTenantryIdOrderByTenantryIdAsc(userUniqueCode, uniqueCode).collectList().map(l -> R.ok(assembleData(l)));
    }


    @PostMapping("saveDataAuthor")
    @Transactional
    @ApiOperation(value = "分配或撤销权限", notes = "传入code")
    public Mono<R> saveDataAuthor(@RequestBody Map map) {
        String zt = map.get("ztUniqueCode").toString();
        String year = map.get("ztYear").toString();
        String archives = map.get("archives").toString();
        String user = map.get("userUniqueCode").toString();
        String type = map.get("type").toString();
        List<String> dataIds = JSON.parseArray(map.get("defaultLogin").toString(), String.class);
        if (type.equals("1")) {
            return groupDataAuthorizationRepository.saveAll(dataIds.stream().map(s -> new GroupDataAuthorization().setDataId(s).setArchivesId(archives).setTenantryId(zt).setIyear(archives.equals("code_kemu")?year:null).setOperatorId(user)).collect(Collectors.toList())).collectList().map(o -> R.ok());
        } else {
            return groupDataAuthorizationRepository.deleteAllByOperatorIdAndTenantryIdAndIyearAndDataIdIn(user, zt, archives.equals("code_kemu")?year:null, dataIds).thenReturn(R.ok());
        }
    }

    @PostMapping("saveUserAuthor2")
    @ApiOperation(value = "设置主管权限", notes = "传入code")
    public Mono<R> saveUserAuthor2(@RequestBody Map map) {
        String uniqueCode = map.get("ztUniqueCode").toString();
        String userUniqueCode = map.get("userUniqueCode").toString();
        String key = map.get("key").toString();
        boolean b = Boolean.parseBoolean(map.get("action").toString());
        return groupDataAuthorizationRepository.findAllByOperatorIdAndTenantryIdAndArchivesIdOrderByTenantryIdAsc(userUniqueCode, uniqueCode,key).collectList()
                .flatMap(list->(list.size()>0?groupDataAuthorizationRepository.deleteAll(list).thenReturn(""):Mono.just("")).flatMap(s->b?groupDataAuthorizationRepository.save(new GroupDataAuthorization().setOperatorId(userUniqueCode).setTenantryId(uniqueCode).setArchivesId(key).setIsDirector("1")).thenReturn(R.ok()):Mono.just(R.ok())));
    }


    private Map assembleData(List<GroupDataAuthorization> list) {
        Map<String, Object> map = new HashMap<>();
        for (String s : list.stream().map(e -> e.getArchivesId()).distinct().collect(Collectors.toList()))
            map.put(s, list.stream().filter(it -> it.getArchivesId().equals(s)).collect(Collectors.toList()));
        return map;
    }
    /********审批授权*********/

}
