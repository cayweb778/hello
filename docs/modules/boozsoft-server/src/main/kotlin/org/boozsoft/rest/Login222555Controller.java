//package org.boozsoft.rest;
//
//import cn.hutool.core.bean.BeanUtil;
//import io.swagger.annotations.ApiOperation;
//import org.boozsoft.config.oauth2.Role;
//import org.boozsoft.domain.entity.SysLog;
//import org.boozsoft.domain.entity.account.BankTemplate;
//import org.boozsoft.domain.entity.account.BankTemplateColumn;
//import org.boozsoft.domain.entity.group.GroupUser;
//import org.boozsoft.domain.vo.BankTemplateVo;
//import org.boozsoft.repo.BankTemplateColumnRepository;
//import org.boozsoft.repo.BankTemplateRepository;
//import org.boozsoft.repo.group.GroupSysUserRepository;
//import org.boozsoft.service.SysLogService;
//import org.boozsoft.service.impl.LoadUserImpl;
//import org.boozsoft.utils.CollectOfUtils;
//import org.springbooz.core.tool.result.R;
//
//import org.springbooz.datasource.r2dbc.annotation.SCHEMA_TYPE;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RestController
//@RequestMapping("/login")
//public class Login222555Controller {
//
//
//    @Autowired
//    GroupSysUserRepository groupSysUserRepository;
//
//
//    @Autowired
//    LoadUserImpl loadUserApi;
//
//    @GetMapping("abc")
//    @ApiOperation(value = "查询列表", notes = "传入code")
//    public Mono<R> findAll(String username, String password){
//        return groupSysUserRepository.findByUsernameAndAndPassword(username, password)
//
//            .flatMap (it->{
//                String uuid = UUID.randomUUID().toString();
//                Map map =new HashMap();
//                    map.put("user", it);
//                    map.put("token",  uuid);
//                    map.put("roles", new ArrayList<String>());
//                    map.put("permissions",new ArrayList<String>());
//
////                rolesFlux =
//                Mono<List<Role>> rolesFlux = loadUserApi.loadRoles(((GroupUser) it).getId()).collectList().doOnNext(it2 -> map.put("roles", it2));
//
////                var permissionFlux =
////                    rolesFlux.flatMapMany { loadUserApi.loadPermissions(it) }.doOnNext { map.put("permissions", it) }
////                permissionFlux.then()
////                val thenReturn =
//                Mono<Map> thenReturn = loadUserApi.saveSecurtyStateMap2(Mono.just(uuid), map).then().thenReturn(map);
//                return rolesFlux.then(thenReturn);
//            })
//            .map (it->R.ok(CollectOfUtils.mapof("info", it)) )
//            .switchIfEmpty(Mono.just(R.ok("失败")));
//    }
//
//}
