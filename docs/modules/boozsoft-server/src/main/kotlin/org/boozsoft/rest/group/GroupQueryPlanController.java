package org.boozsoft.rest.group;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupQueryPlan;
import org.boozsoft.repo.ColumnSettingsRepository;
import org.boozsoft.repo.GroupQueryPlanRepository;
import org.boozsoft.service.SysLogService;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/group-query-plan")
public class GroupQueryPlanController {

    @Autowired
    ColumnSettingsRepository columnSettingsRepository;

    @Autowired
    SysLogService sysLogService;

    @Autowired
    GroupQueryPlanRepository groupQueryPlanRepository;

    @ApiOperation(value = "查询参数精度列表", notes = "传入实体")
    @GetMapping
    public Mono<R> findOne(String accountId,String owningMenuName,String owningPlan,String planPerson) {
        if (StrUtil.hasBlank(accountId,owningMenuName,owningPlan,planPerson)) return Mono.just(R.ok().setMessage("请检查查询条件！"));
        return groupQueryPlanRepository.findFirstByAccountIdAndOwningMenuNameAndOwningPlanAndPlanPerson(accountId, owningMenuName,owningPlan,planPerson).cache()
                .map(R::ok).defaultIfEmpty(R.ok().setResult(null));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入实体")
    public Mono<R> save(@RequestBody GroupQueryPlan entity) {
      return groupQueryPlanRepository.save(entity).map(o -> R.ok());
    }

    @PostMapping("findByAccIdAndMenuName")
    public Mono<R> findByAccIdAndMenuName(String accId,String menuName) {
        return groupQueryPlanRepository.findByAccountIdAndOwningMenuName(accId,menuName).map(item->R.ok().setResult(item)).defaultIfEmpty(R.ok().setResult(null));
    }

    @PostMapping("saveQueryPlan")
    public Mono<R> saveQueryPlan(@RequestBody Map map) {
        Map<String, String> queryConditions = ((HashMap<String,  String>) map.get("queryConditions"));
        GroupQueryPlan plan=new GroupQueryPlan();
        plan.setAccountId(map.get("accountId").toString());
        plan.setOwningPlan(map.get("owningPlan").toString());
        plan.setOwningMenuName(map.get("owningMenuName").toString());
        plan.setPlanPerson(map.get("planPerson").toString());
        plan.setQueryConditions(JSON.toJSONString(queryConditions));
        return groupQueryPlanRepository.delByMenuName(map.get("owningMenuName").toString())
                .then(Mono.just(plan))
                .flatMap(groupQueryPlanRepository::save).map(item->R.ok().setResult(item));
    }

 /*   @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono<R> save(@RequestBody GroupQueryPlan entity) {
        String username = "张三";
        String accId = "张三";
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
        sysLog.setOperationCont("【" + sysLog.getUserName() + "在" + time + "】自定义了" + entity.getOwningMenuName() + "菜单的查询参数");
        return groupQueryPlanRepository.save(entity).zipWith(sysLogService.save_log(sysLog)).map(o -> R.ok());
    }*/
}
