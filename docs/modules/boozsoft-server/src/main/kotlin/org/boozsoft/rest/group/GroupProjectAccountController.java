package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupProjectAccount;
import org.boozsoft.repo.group.GroupProjectAccountRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupProjectAccount")
public class GroupProjectAccountController {

    @Autowired
    GroupProjectAccountRepository repository;

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
                .map(R::page);
    }

    @GetMapping("findByTenantId")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByTenantId(String tenantId) {
        return repository.findAllByTenantId("2",tenantId)
                .collectList()
                .map(R::page);
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupProjectAccount object) {
        return repository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping("saveList")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono saveList(@RequestBody List<GroupProjectAccount> object) {
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
