package org.boozsoft.rest;

import com.alibaba.fastjson.JSON;
import org.boozsoft.repo.MenuRepository;
import org.boozsoft.repo.RoleMenuRepository;
import org.boozsoft.repo.RoleRepository;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.repo.entity.RoleMenu;
import org.springbooz.core.tool.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class RoleMenuController {
    @Autowired
    RoleMenuRepository repository;
    @Autowired
    MenuRepository menuRepository;
    // 对应平台的菜单


    @GetMapping("/sys/menu/tree2/byPlatform")
    public Mono<R> byPlatform() {

//    return toMenuVo(repository.findAll())
//        .map(R::ok);
        return null;
    }

    @GetMapping("/sys/roleMenu/getRoleMenu")
    public Mono<R> a() {
        return repository.findAll().collectList().map(R::ok);
    }

    @PostMapping("/sys/roleMenu/savePlatforms")
    public Mono<R> savePlatforms(@RequestBody Map params) {
        String roleId = params.get("roleId").toString();

        return menuRepository
                .findPlatformAll()
                .map(Menu::getId)
                .collectList()
                // 删除所有平台菜单授权
                .flatMap(it -> repository.deleteAllByRoleIdAndMenuIdIn(roleId, it))
                .thenReturn("")

                // 保存角色平台权限 start
                .map(it -> Flux.fromIterable(JSON.parseArray(params.get("menuIds").toString(), String.class)))
                .flatMapMany(it2 -> it2.map(it -> {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(it);
                    roleMenu.setRoleId(roleId);
                    return roleMenu;
                }))
                .collectList()
                .flatMapMany(repository::saveAll)
                .collectList()
                // 保存角色平台权限 end
                .map(R::ok);
    }


    @PostMapping("/sys/roleMenu/saveMenus")
    public Mono<R> saveMenus(@RequestBody Map params) {
        String roleId = params.get("roleId").toString();

        return menuRepository
                .findPlatformAll()
                .map(Menu::getId)
                .collectList()
                // 删除所有非平台菜单授权
                .flatMap(it -> repository.deleteAllByRoleIdAndMenuIdNotIn(roleId, it))
                .thenReturn("")
                // 保存角色菜单权限 start
                .map(it -> Flux.fromIterable(JSON.parseArray(params.get("menuIds").toString(), String.class)))
                .flatMapMany(it2 -> it2.map(it -> {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setMenuId(it);
                    roleMenu.setRoleId(roleId);
                    return roleMenu;
                }))
                .collectList()
                .flatMapMany(repository::saveAll)
                // 保存角色菜单权限 end
                .collectList()
                .map(R::ok);
    }

}
