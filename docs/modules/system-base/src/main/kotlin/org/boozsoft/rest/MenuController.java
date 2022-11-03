/**
 * Copyright (c) 2018-2028, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boozsoft.rest;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.boozsoft.domain.dto.RoleDto;
import org.boozsoft.domain.dto.SyncMenuAndRoleMenuDto;
import org.boozsoft.domain.vo.MenuVO;
import org.boozsoft.repo.MenuRepository;
import org.boozsoft.repo.RoleMenuRepository;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.repo.entity.RoleMenu;
import org.boozsoft.resource.TokenResource;
import org.boozsoft.service.MenuServiceImpl;
import org.springbooz.core.tool.result.R;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemRole;
import org.springbooz.webflux.holder.context.security.domain.entity.permission.BoozLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


@RestController
@RequiredArgsConstructor
@RequestMapping("/sys/menu")
@Api(value = "菜单表", tags = "系统：菜单表")
public class MenuController {
  private final R2dbcEntityTemplate entityTemplate;
  private final MenuServiceImpl menuService;
  private final MenuRepository repository;
  private final RoleMenuRepository roleMenuRepository;
  private final MenuRepository menuRepository ;
  @Autowired
  TokenResource tokenResource;

  @GetMapping("/all")
  @ApiOperation(value = "查询所有")
  public Mono<R> all() {
    return repository.findAllBy().collectList().map(R::page);
  }

//  @GetMapping("/findByLayoutId")
//  @ApiOperation(value = "根据用户查询列表")
//  public Mono<R> byuser(String layoutId) {
//    Flux<Menu> menu = tokenResource.getMenu(layoutId).filter(item -> item.getCategory() != 1000);
//    Mono<List<MenuVO>> menuVoMono = menuService.routeTree(menu.collectList().map(item->item).flatMapMany(Flux::fromIterable));
//    return menuVoMono.map(R::ok);
//  }

  @ApiOperation(value = "获取平台下的菜单集合", notes = "获取平台下的菜单集合 ")
  @GetMapping("/getPlatformMenus")
  public Mono<R>  getMenus(String platformId) {
    return menuService
            .routeTree(menuRepository.findAllByPlatformId(platformId))
            .map(R::ok);
  }



  @GetMapping("/commitLayout")
  @ApiOperation(value = "提交layoutid给上下文", notes = "传入LayoutId")
  public R commitLayout( Integer layoutId) {
    BoozLayout boozLayout = new BoozLayout();
    boozLayout.setCode(layoutId);
    boozLayout.setName("总账");
//    oauth2User.getPermission().setCurrentLayout(boozLayout);
    return R.ok().setResult(boozLayout);
  }

  @DeleteMapping
  @ApiOperation(value = "删除", notes = "传入co de")
  public Mono delete(String id) {
    return repository.deleteById(id).then(Mono.just(R.ok()));
  }

  @GetMapping("/route/findAllRoleMenu")
  @ApiOperation(value = "查找所有角色菜单")
  public Flux<RoleMenu> findAllRoleMenu(String tenantId, Long userId) {
    return roleMenuRepository.findAll();
  }

  @GetMapping
  @ApiOperation(value = "查询", notes = "传入code")
  public Mono get(String id) {
    return repository.findById(id);
  }

  @GetMapping("/list")
  @ApiOperation(value = "查询列表")
  public Mono<R> list() {
    return repository.findAllBy().collectList().map(R::page);
  }

  @GetMapping("/route/treeByUserId")
  @ApiOperation(value = "树形结构", notes = "树形结构")
  public Mono<R> routeTree(String tenantId, String userId) {
    return menuService.routeTree(userId).map(o -> R.ok().setResult(o));
  }

  @GetMapping("/route/treeAll")
  @ApiOperation(value = "树形结构", notes = "树形结构")
  public Mono<R> routeTreeAll() {
    return menuService.routeTree().map(o -> R.ok().setResult(o));
  }

  @PostMapping
  @ApiOperation(value = "新增或修改", notes = "传入code")
  public Mono save(@RequestBody Menu object) {
    return repository.save(object).map(o -> R.ok().setResult(o));
  }

  @PostMapping("/syncMenuAndRoleMenu")
  @ApiOperation(value = "同步前端菜单，仅开发使用")
  @Deprecated
  public Mono<R> syncMenu(@RequestBody SyncMenuAndRoleMenuDto map) {
    return null;
//    Flux<Menu> menuFlux = map.getMenuFlux().cache();
//
//    Mono<HashMap<String, Tuple2<String, Menu>>> requestMenuIdsFlux = Mono.just(new HashMap<String, Tuple2<String, Menu>>())
//        .flatMap(a -> menuFlux
//            .doOnNext(item -> a.put(item.getName() + "-" + item.getPath()+"-"+item.getComponent(), Tuples.of(item.getId(), item)))
//            .then().thenReturn(a).cache()
//        ).cache();
//    Flux<RoleMenu> roleMenuFlux = map.getRoleMenuFlux().doOnNext(item -> item.setId(null)).cache();
//
//    // 删除两个表的数据
//    Mono<String> deleteData = repository.deleteAll().thenReturn("")
//        .then(roleMenuRepository.deleteAll().thenReturn(""))
//        .map(res->res);
//
//    // 保存菜单数据
//    Flux<Menu> saveMenuList = repository
//        .saveAll(menuFlux.doOnNext(item -> item.setId(null)))
//        .cache();
//
////    saveMenuList = saveMenuList.timed().map(item->item.get());
//
//    saveMenuList = requestMenuIdsFlux.thenMany(saveMenuList);
//
//
//    // 把所有parentId成新生成的id
//    Flux<Menu> newSaveMenuList = saveMenuList.collectList()
//        .flatMapMany(itemx1 -> {
//          Flux<Menu> finalSaveMenuList = Flux.fromIterable(itemx1).cache();
//          Flux<Menu> menuFlux1 = finalSaveMenuList
//              .flatMap(item -> requestMenuIdsFlux.map(HashMap::values)
//                  .flatMapMany(Flux::fromIterable)
//                  .filter(item11->(item.getParentId().equals(item11.getT1()) ))
//                  .next()
//                  .doOnNext(itemx -> item.setParentId(itemx.getT2().getId())).then().thenReturn(item));
//          return repository.saveAll(menuFlux1);
//        }).cache();
//
//
//    // 过滤RoleMenu中模拟的模拟id，统统改为新生成的菜单Id
//    Flux<RoleMenu> roleMenuFlux1 = roleMenuFlux
//        .flatMap(item11 -> {
//
//          String menuId = item11.getMenuId();
//          String layoutId = item11.getLayoutId();
//
//          Mono<String> setMenuId = requestMenuIdsFlux
//              .map(HashMap::values)
//              .map(item2->item2)
//              .flatMapMany(Flux::fromIterable)
//              .filter(item33 -> item33.getT1().equals(menuId))
//              .next()
//              .map(item33 -> item33.getT2().getId())
//              .doOnNext(item11::setMenuId);
//
//          Mono<String> setPlatformId = requestMenuIdsFlux
//              .map(HashMap::values)
//              .flatMapMany(Flux::fromIterable)
//              .filter(item33 -> item33.getT1().equals(layoutId))
//              .next()
//              .map(item33 -> item33.getT2().getId())
//              .doOnNext(item11::setLayoutId);
//
//
//          return setMenuId.and(setPlatformId).thenReturn(item11);
//        })
//        .doOnNext(item -> item.setId(null));
//
//    return deleteData
//        .thenMany(newSaveMenuList)
//        .thenMany(roleMenuFlux1)
//        // 保存菜单角色中间表
//        .thenMany(roleMenuRepository.saveAll(roleMenuFlux1))
//        .then(Mono.just(R.ok()));
  }

  @GetMapping("/tree")
  @ApiOperation(value = "树形结构", notes = "树形结构")
  public Mono<R> tree(String tenantId, String userId) {
    return menuService.tree().map(o -> R.ok().setResult(o));
  }

  @GetMapping("/menuIdsByRoleId")
  @ApiOperation(value = "根据角色ID集合获取属菜单集合", notes = "传入角色ID集合")
  private Mono<R> menuListByRoleId(SystemRole role) {
    return null;
//    return R.ok(menuListByRoleList(Set.of(role), boozOidcUser).flatMapMany(Flux::fromIterable)
//        .map(item -> String.valueOf(item.getId())));
  }

  @GetMapping("/menuListByRoleIds")
  @ApiOperation(value = "根据LayoutId获取该用户菜单集合", notes = "传入LayoutId集合")
  private Mono<List<MenuVO>> menuListByRoleList(Set<SystemRole> roleList) {
//    Flux<String> ids = Flux.fromIterable(roleList).map(SystemRole::getId);
//
//    Flux<Menu> menuFLux = findMenuByRoleIdsAndLayoutId(ids, oauth2User)
//        .switchIfEmpty(Mono.error(new Exception("异常:角色的菜单为空,可能需要分配菜单")))
//        .collectList()
//        .flatMapMany(repository::findAllById)
//        .filter(item -> item.getCategory() != 1000);
//
//    return menuService.routeTree(menuFLux);
    return null;
  }

  public Flux<String> findMenuByRoleIdsAndLayoutId(Flux<String> idsFlux) {
//    return idsFlux
//        .collectList()
//        .flatMapMany(ids -> roleMenuRepository.findAllByRoleIdInAndLayoutIdOrderBySortNo(ids, oauth2User.getPermission().getCurrentLayout().getCode()))
//        .map(item -> item.getMenuId());
    return null;
  }

//  @GetMapping("/platfroms")
//  @ApiOperation(value = "根据角色ID获取属菜单集合", notes = "传入角色ID集合")
//  private Mono<R> platfroms() {
//    Flux<Menu> layouts = tokenResource.getLayouts();
//    return layouts.collectList().map(item -> {
//      return R.ok().setResult(item);
//    });
//    Flux<Long> idsFlux = tokenResource.getUserRoleList().map(item->{
//      int a=item;
//    });
//
//    Flux<Long> getMenuIdFlux=idsFlux
//        .collectList()
//        .flatMapMany(ids -> roleMenuRepository.findAllByRoleIdInOrderBySortNoAscMenuIdAsc(ids))
//        .map(item -> item.getMenuId());
//
//    return Mono.just(idsFlux)
//        .flatMapMany(idList -> getMenuIdFlux)
//        .switchIfEmpty(Mono.error(new Exception("异常:角色的菜单为空,可能需要分配菜单")))
//        .collectList()
//        .flatMap(idList ->
//            repository
//                .findAllByIdInAndCategoryOrderBySortNoAscIdAsc(idList, 1000)
//                .collectList()
//                // 排序
//                .map(menuList ->
//                    idList.stream()
//                        .filter(item -> !menuList.stream().filter(menu -> menu.getId().equals(item)).findFirst().get()==null)
//                        .map(sortmMenuId -> menuList.stream().filter(item -> item.getId().equals(sortmMenuId)).findFirst().orElseThrow())
//                        .collect(Collectors.toList())
//                )
//        )
//        .map(item -> R.ok().setResult(item));
//  }
//
//  @PostMapping("/saveMenusByRoleId")
//  @ApiOperation(value = "根据角色ID获取属菜单集合", notes = "传入角色ID,菜单idList")
//  private Mono<R> saveMenusByRoleId(@RequestBody RoleDto roleDto) {
//    return Mono.just(new StringBuilder("insert into sys_role_menu (role_id,menu_id) values "))
//        .doOnNext(sql -> roleDto.getMenuIds().forEach(menuId ->
//            sql.append("(%s,%s),".formatted(roleDto.getRoleId(), menuId))
//        ))
//        .map(StringBuilder::toString)
//        .map(sql -> sql.substring(0, sql.length() - 1))
//        .delaySubscription(entityTemplate.getDatabaseClient()
//            .sql("delete from sys_role_menu where role_id=" + roleDto.getRoleId())
//            .then())
//        .map(entityTemplate.getDatabaseClient()::sql)
//        .flatMap(DatabaseClient.GenericExecuteSpec::then)
//        .thenReturn(R.ok());
//  }
}


