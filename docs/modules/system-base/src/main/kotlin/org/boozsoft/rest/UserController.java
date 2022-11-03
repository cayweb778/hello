package org.boozsoft.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.boozsoft.config.oauth2.Permission;
import org.boozsoft.domain.vo.UserVO;
import org.boozsoft.repo.JigouRepository;
import org.boozsoft.repo.RoleRepository;
import org.boozsoft.repo.UserRepository;
import org.boozsoft.repo.entity.Role;
import org.boozsoft.repo.entity.User;
import org.boozsoft.resource.TokenResource;
import org.boozsoft.service.UserServiceImplement;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.base.feign.ICrudClient;
import org.springbooz.core.tool.base.feign.R2dbcCrudClient;
import org.springbooz.core.tool.result.R;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/user")
@Api(value = "系统用户表", tags = "系统：用户表")
public class UserController extends R2dbcCrudClient<UserRepository, User> implements ICrudClient<User> {
  private final JigouRepository jigouRepository;
  private final UserRepository repository;
  private final RoleRepository roleRepository;
  private final TokenResource tokenResource;
  private final UserServiceImplement userServiceImplement;

  @GetMapping("findUserByRoleId")
  public Mono<R> findUserByRoleId(String roleId) {
    return userServiceImplement.findAllUserByRoleId(roleId)
        .collectList().map(o -> R.ok().setResult("babababa"));
  }

  @GetMapping("querySystemUserInfo")
  public Mono<R> querySystemUserInfo() {
    Mono<SystemUser> userMono = tokenResource.getSystemUser();
    Mono<List<Role>> userRoleListMono = tokenResource.getUserRoleList().collectList();
    Mono<List<Permission>> userPermissionListMono = tokenResource.getPermission().collectList();
    return Mono.zip(userMono, userRoleListMono, userPermissionListMono)
        .map(item -> {
          return R.ok(CollectOfUtils.mapof(
                  "user", item.getT1(),
                  "roles", item.getT2(),
                  "permissions", item.getT3()
          ));
        });
  }

  @Override
  @PostMapping
  @ApiOperation(value = "新增或修改", notes = "传入code")
  public Mono<R> save(@RequestBody User object) {
    return repository.save(object).map(o -> R.ok().setResult(o));
  }

  @Override
  @DeleteMapping
  @ApiOperation(value = "删除", notes = "传入code")
  public Mono delete(String id) {
    return repository.deleteById(id).then(Mono.just(R.ok()));
  }

  @Override
  @GetMapping
  @ApiOperation(value = "查询", notes = "传入code")
  public Mono get(String id) {
    return repository.findById(id);
  }

  @Override
  @GetMapping("/list")
  @ApiOperation(value = "查询列表", notes = "传入code")
  public Mono<R> list() {

    return repository.findAllBy()
        .map(user -> {
          UserVO userVO = new UserVO();
          BeanUtils.copyProperties(user, userVO);
          return userVO;
        }).flatMapSequential(userVO ->
            roleRepository.findAllById(client.sql("select role_id from sys_role role left join sys_user_role user_role on role.id=user_role.role_id where user_role.user_id=13").fetch().all().map(item -> (String) item.get("role_id")))
                .collectList()
                .doOnNext(role -> userVO.setRoleList(role))
                .onErrorMap((a) -> new Exception("无法找到角色"))
                .switchIfEmpty(Mono.error(new Exception("用户角色信息错误")))
                .then(Mono.just(userVO))
        ).collectList().map(R::page);
  }
}
