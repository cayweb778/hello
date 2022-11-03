package org.boozsoft.rest.group;

import io.swagger.annotations.ApiOperation;
import org.boozsoft.domain.entity.group.GroupFuzhuHesuan;
import org.boozsoft.repo.group.GroupFuzhuHesuanRepository;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/groupFuzhuHesuan")
public class GroupFuzhuHesuanController {

    @Autowired
    GroupFuzhuHesuanRepository fuzhuHesuanRepository;

    @GetMapping("findAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAll(Pageable pageable) {
        return fuzhuHesuanRepository.findAllByOrderByCdfine(pageable)
                .collectList()
                .flatMap(item -> fuzhuHesuanRepository.countAllBy()
                        .map(total -> R.page(item, pageable, total)));
    }

    @GetMapping("getAll")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> getAll() {
        return fuzhuHesuanRepository.findByFlagOrderByCdfine("1")
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findAllList")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findAllList() {
        return fuzhuHesuanRepository.findAll()
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCode")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCode(String ccode) {
        return fuzhuHesuanRepository.findByCcodeOrderByCdfine(ccode)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByName")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByName(String cname) {
        return fuzhuHesuanRepository.findByCnameOrderByCdfine(cname)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @GetMapping("findByCankaoDuixiang")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> findByCankaoDuixiang(String cankaoDuixiang) {
        return fuzhuHesuanRepository.findByCankaoDuixiangOrderByCdfine(cankaoDuixiang)
                .collectList()
                .map(o -> R.ok().setResult(o));
    }

    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody GroupFuzhuHesuan object) {
        return fuzhuHesuanRepository.save(object)
                .map(item -> R.ok().setResult(item));
    }

    @DeleteMapping("del")
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(@RequestBody GroupFuzhuHesuan object){
        return fuzhuHesuanRepository.deleteById(object.getId())
                .then(Mono.just(R.ok()));
    }

    @DeleteMapping("deleteList")
    @ApiOperation(value = "批量删除", notes = "传入code")
    public Mono deleteList(@RequestBody List<GroupFuzhuHesuan> object){
        return fuzhuHesuanRepository.deleteAll(object)
                .then(Mono.just(R.ok()));
    }

    @PostMapping("editByFlag")
    @ApiOperation(value = "停用启用操作", notes = "传入code")
    public Mono editByFlag(@RequestBody GroupFuzhuHesuan object) {
        if (object.getFlag().equals("1")){
            object.setFlag("0");
        } else {
            object.setFlag("1");
        }
        return fuzhuHesuanRepository.save(object)
                .map(item->R.ok().setResult(item));
    }

    @PostMapping("excel")
    @ApiOperation(value = "前台解析excel,后台直接保存集合", notes = "传入code")
    @Transactional
    public Mono excel(@RequestBody List<GroupFuzhuHesuan> object){
        return fuzhuHesuanRepository.saveAll(object)
                .collectList()
                .map(o-> R.ok().setResult(o));
    }

}
