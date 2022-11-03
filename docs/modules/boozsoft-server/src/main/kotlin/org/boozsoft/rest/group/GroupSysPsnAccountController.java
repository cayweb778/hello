package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupSysPsnAccount;
import org.boozsoft.domain.vo.GroupSysPsnAccountVo;
import org.boozsoft.repo.group.GroupSysPsnAccountRepository;
import org.boozsoft.util.DesUtil;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/groupSysPsnAccount")
public class GroupSysPsnAccountController {

    @Autowired
    GroupSysPsnAccountRepository repository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return repository.findAll()
                .collectList()
                .map(R::page);
    }

    @GetMapping("findByOriginId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByOriginId(String originId) {
        return repository.findAllByOriginId("1",originId)
                .collectList()
                .map(list->{
                    List<GroupSysPsnAccountVo> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsnAccountVo psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(R::page);
    }

    @GetMapping("findByTenantId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTenantId(String tenantId) {
        return repository.findAllByTenantId("2",tenantId)
                .collectList()
                .map(list->{
                    List<GroupSysPsnAccountVo> psnList = new ArrayList<>();
                    try {
                        DesUtil putong = new DesUtil("national");
                        for (int i = 0; i < list.size(); i++) {
                            GroupSysPsnAccountVo psn = list.get(i);
                            psn.setCellPhoneNum(putong.decrypt(psn.getCellPhoneNum()));
                            psnList.add(psn);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return psnList;
                })
                .map(R::page);
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupSysPsnAccount object) {
        return repository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveList")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveList(@RequestBody List<GroupSysPsnAccount> object) {
        return repository.saveAll(object)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("deleteById")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteById(String id) {
        return repository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

    @GetMapping("findByOriginIdAndUniqueCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByOriginIdAndUniqueCode(String originId,String uniqueCode) {
        return repository.findByCtypeAndOriginIdAndUniqueCodeOrderById("1",originId,uniqueCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByTenantIdAndUniqueCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTenantIdAndUniqueCode(String tenantId,String uniqueCode) {
        return repository.findByCtypeAndTenantIdAndUniqueCodeOrderById("2",tenantId,uniqueCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByUniqueCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByUniqueCode(String uniqueCode) {
        return repository.findByUniqueCodeOrderById(uniqueCode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

}
