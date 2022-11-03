package org.boozsoft.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.boozsoft.repo.JigouRepository;
import org.boozsoft.repo.entity.Jigou;
import org.boozsoft.service.JigouServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Api(value = "机构表", tags = "系统：机构表")
@RequestMapping("/sys/jigou")
public class JigouController {
    private final JigouRepository repository;
    private final JigouServiceImpl jigouService;
    @PostMapping
    @ApiOperation(value = "新增或修改", notes = "传入code")
    public Mono save(@RequestBody Jigou object){
        return repository.save(object).map(o-> R.ok().setResult(o));
    }
    @DeleteMapping
    @ApiOperation(value = "删除", notes = "传入code")
    public Mono delete(String id){
        return repository.deleteById(id).then(Mono.just(R.ok()));
    }
    @GetMapping
    @ApiOperation(value = "查询", notes = "传入code")
    public Mono get(String id){
        return repository.findById(id);
    }

    @GetMapping("/list")
    @ApiOperation(value = "查询列表", notes = "传入code")
    public Mono<R> list(){
        return repository.findAllBy().collectList().map(R::page);
    }

    @GetMapping("/tree")
    @ApiOperation(value = "树形结构", notes = "树形结构")
    public Mono<R> tree(String tenantId, String userId) {
        return jigouService.tree().map(o->R.ok().setResult(o));
    }
//    @Autowired
//    DatabaseClient client;
//    @Autowired
//    R2dbcEntityTemplate entityTemplate;
//
//    @GetMapping("/getAll")
//    public Mono<BoozResult> getAll() {;
//        return entityTemplate.select(Jigou.class).from("jigou").all().collectList()
//                .map(list-> BoozResult.ok().setResult(list));
//    }
//
//    @GetMapping("/getone")
//    public Flux<Map<String, Object>> getOne() {
//
//        return client.sql("select * from dept where id = '1' ").fetch().all();
//    }
//
//
//    @GetMapping("/insert")
//    public Mono create() {
//        return Mono.just("abc");
//    }
//
//    @GetMapping("/delete")
//    public Mono delete() {
//        return Mono.just("删除啦！");
//    }
//
//    @PutMapping("update")
//    public Mono update() {
//        return Mono.just("abc");
//    }


}
