package org.boozsoft.rest.group;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupDataSeeSwitch;
import org.boozsoft.repo.SysAccountPeriodRepository;
import org.boozsoft.repo.group.GroupDataSeeSwitchRepository;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dataSee")
public class GroupDataSeeSwitchController {

    @Autowired
    GroupDataSeeSwitchRepository repository;

    @Autowired
    SysAccountPeriodRepository accountPeriodRepository;

    @PostMapping("findDataSeeAuthor")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findDataSeeAuthor(@RequestBody Map map) {
        if (map.keySet().size() != 2) return Mono.just(R.error());
        String uniqueCode = map.get("uniqueCode").toString();
        String type = map.get("type").toString();
        Mono<String> zt = type.equals("ZT") ? accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(uniqueCode).collectList().map(list -> list.size() > 0 ? list.get(0).getAccountMode() : uniqueCode) : Mono.just(uniqueCode);
        return zt.flatMap(accountMode-> repository.findAllByUniqueCodeOrderByIdAscUniqueCodeAsc(accountMode).map(it -> {
                if (type.equals("ZT"))it.setUniqueCode(uniqueCode);
                return it;}).collectList().map(list -> R.ok(assembleData(list))));
    }


    @PostMapping("saveDataSeeAuthor")
    @Transactional
    @ApiOperation(value = "添加", notes = "传入code")
    public Mono<R> saveDataSeeAuthor(@RequestBody Map map) {
        if (map.keySet().size() == 0) {
            return Mono.just(R.error());
        } else {
            List<GroupDataSeeSwitch> datas = JSON.parseArray(map.get("data").toString(), GroupDataSeeSwitch.class);
            String type = map.get("type").toString();
            String coCode = map.get("code").toString();
            if (type.equals("ZT")) {
               /* return accountPeriodRepository.findAllByAccountCoCodeOrderByAccountYearDesc(coCode).collectList().flatMap(list -> {
                    String tId = list.get(0).getAccountMode();
                    List<GroupDataSeeSwitch> saveList = datas.stream().map(it -> {
                        it.setUniqueCode(tId);
                        return it;
                    }).collect(Collectors.toList());
                    return repository.findAllByUniqueCodeOrderByIdAscUniqueCodeAsc( tId).collectList().flatMap(list1 -> list1.size() > 0 ? repository.deleteAll(list1).thenReturn(1).flatMap(e -> repository.saveAll(saveList).collectList()) : repository.saveAll(saveList).collectList()).map(l -> R.ok());
                });*/
                return repository.findAllByUniqueCodeOrderByIdAscUniqueCodeAsc( coCode).collectList().flatMap(list1 -> list1.size() > 0 ? repository.deleteAll(list1).thenReturn(1).flatMap(e -> datas.size()>0?repository.saveAll(datas).collectList():Mono.just(new ArrayList<>())) : datas.size()>0?repository.saveAll(datas).collectList():Mono.just(new ArrayList<>())).map(l -> R.ok());
            } else {
                String originId = datas.get(0).getUniqueCode();
                return repository.findAllByUniqueCodeOrderByIdAscUniqueCodeAsc( originId).collectList().flatMap(list1 -> list1.size() > 0 ? repository.deleteAll(list1).thenReturn(1).flatMap(e -> repository.saveAll(datas).collectList()) : repository.saveAll(datas).collectList()).map(l -> R.ok());
            }
        }
    }

    private Map<String, Object> assembleData(List<GroupDataSeeSwitch> list) {
        return list.size() > 0 ? CollectOfUtils.mapof("1", list.stream().filter(it -> it.getCtrlRange().equals("ZZ")).collect(Collectors.toList()), "2", list.stream().filter(it -> it.getCtrlRange().equals("ZT")).collect(Collectors.toList())) :null;
    }
    /********审批授权*********/

    @PostMapping("findAllByUniqueCodeAndDatabaseNameAndIsCtrl")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllByUniqueCodeAndDatabaseNameAndIsCtrl(@RequestBody Map map) {
        String uniqueCode = map.get("uniqueCode").toString();
        String databaseName = map.get("databaseName").toString();
        String ctrl = map.get("ctrl").toString();
        return repository.findAllByUniqueCodeAndDatabaseNameAndIsCtrl(uniqueCode,databaseName,ctrl).collectList()
        .flatMap(list->{
            return Mono.just(list);
        }).map(a->R.ok().setResult(a));
    }
}
