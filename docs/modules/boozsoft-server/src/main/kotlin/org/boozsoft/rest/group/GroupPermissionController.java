package org.boozsoft.rest.group;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupPermission;
import org.boozsoft.domain.entity.group.GroupUserOperatAuthZt;
import org.boozsoft.repo.SysAccountPeriodRepository;
import org.boozsoft.repo.group.GroupPermissionRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groupPermission")
public class GroupPermissionController {

    @Autowired
    GroupPermissionRepository repository;

    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return repository.findAll().collectList().map(R::page);
    }

    @GetMapping("findByUserIdAndOriginId")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findByUserIdAndOriginId(String userId, String originId, String baseEnName) {
        return repository.findByUserIdAndOriginIdAndCtypeAndBaseEnName(userId, originId, "1", baseEnName).collectList().map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUserIdAndTenantId")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findByUserIdAndTenantId(String userId, String tenantId, String baseEnName) {
        return repository.findByUserIdAndTenantIdAndCtypeAndBaseEnName(userId, tenantId, "2", baseEnName).collectList().map(o -> R.ok().setResult(o));
    }


    @PostMapping("findUserApproveAuthor")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findUserApproveAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 1) return Mono.just(R.error());
        String userId = map.get("userId").toString();
        return repository.findByUserIdOrderById(userId).flatMap(it -> {
            if (null != it && it.getCtype().equals("2"))
                return accountPeriodRepository.findAllByAccountModeOrderByAccountYearDesc(it.getTenantId()).collectList().flatMap(list -> {
                    if (list.size() > 0) it.setTenantId(list.get(0).getAccountCoCode());
                    return Mono.just(it);
                });
            return Mono.just(it);
        }).collectList().map(list -> R.ok(assembleData(list)));
    }


    @PostMapping("saveApproveAuthor")
    @Transactional
    @ApiOperation(value = "添加", notes = "传入code")
    public Mono<R> saveApproveAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 2) {
            return Mono.just(R.error());
        } else {
            String userId = map.get("userId").toString();
            List<GroupPermission> datas = JSON.parseArray(map.get("data").toString(), GroupPermission.class);
            String type = datas.get(0).getCtype();
            if (type.equals("2")) {
                String coCode = datas.get(0).getTenantId();
                return accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(coCode).collectList().flatMap(list -> {
                    String tId = list.get(0).getAccountMode();
                    List<GroupPermission> saveList = datas.stream().map(it -> {
                        it.setTenantId(tId);
                        return it;
                    }).collect(Collectors.toList());
                    return repository.findByUserIdAndTenantIdOrderById(userId, tId).collectList().flatMap(list1 -> list1.size() > 0 ? repository.deleteAll(list1).thenReturn(1).flatMap(e -> repository.saveAll(saveList).collectList()) : repository.saveAll(saveList).collectList()).map(l -> R.ok());
                });
            } else {
                String originId = datas.get(0).getOriginId();
                return repository.findByUserIdAndOriginIdOrderById(userId, originId).collectList().flatMap(list1 -> list1.size() > 0 ? repository.deleteAll(list1).thenReturn(1).flatMap(e -> repository.saveAll(datas).collectList()) : repository.saveAll(datas).collectList()).map(l -> R.ok());
            }
        }
    }

    private Map<String, Object> assembleData(List<GroupPermission> list) {
        return list.size() > 0 ? CollectOfUtils.mapof("1", list.stream().filter(it -> it.getCtype().equals("1")).collect(Collectors.toList()), "2", list.stream().filter(it -> it.getCtype().equals("2")).collect(Collectors.toList())) :null;
    }
    /********审批授权*********/

}
