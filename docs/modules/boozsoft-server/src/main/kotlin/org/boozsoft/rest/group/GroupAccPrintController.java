package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupAccPrint;
import org.boozsoft.repo.group.GroupAccPrintRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/groupAccPrint")
public class GroupAccPrintController {

    @Autowired
    GroupAccPrintRepository accPrintRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll() {
        return accPrintRepository.findAll()
                .collectList().map(R::page);
    }

    @GetMapping("findByAccIdAndMenuName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByAccIdAndMenuName(String accId, String menuName) {
        return accPrintRepository.findByAccIdAndMenuName(accId, menuName)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findById")
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono findById(String id) {
        Mono mono = accPrintRepository.findById(id);
        return mono.map(o -> R.ok().setResult(o));
    }

    @PostMapping("save")
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupAccPrint object) {
        return accPrintRepository.save(object)
                .map(o -> R.ok().setResult(o));
    }

    @DeleteMapping("")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono deleteById(String id) {
        return accPrintRepository.deleteById(id)
                .then(Mono.just(R.ok()));
    }

}
