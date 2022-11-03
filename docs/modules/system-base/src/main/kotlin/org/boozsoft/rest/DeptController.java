package org.boozsoft.rest;//package org.boozsoft.rest;
//
//import io.swagger.annotations.ApiOperation;
//import lombok.RequiredArgsConstructor;
//import org.boozsoft.domain.vo.DeptVO;
//import org.boozsoft.service.DeptServiceImpl;
//import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
//import org.boozsoft.repo.entity.Jigou;
//import org.springbooz.core.tool.result.BoozResult;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.r2dbc.core.DatabaseClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/sys/dept")
//public class DeptController {
//    private final DatabaseClient client;
//    private final R2dbcEntityTemplate entityTemplate;
//    private final R2dbcRouterLoader routerLoader;
//    private final DeptServiceImpl deptService;
//
//    //    
//    @GetMapping("/getAll")
//    public Mono<BoozResult> getAll() {
//
//       return routerLoader.bind("prod-nc", "hello1", () -> entityTemplate.select(Jigou.class).from("dept").all()).collectList()
//         .map(list -> BoozResult.ok().setResult(list));
//    }
//
//    @GetMapping("/getone")
//    public Flux<Map<String, Object>> getOne() {
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
//
//
//
//    @GetMapping("/tree")
//    @ApiOperation(value = "树形结构", notes = "树形结构")
//    public Mono<List<DeptVO>> tree(String tenantId, String userId) {
//        return deptService.tree().map(list->{
//            return list;
//        });
//    }
//}
