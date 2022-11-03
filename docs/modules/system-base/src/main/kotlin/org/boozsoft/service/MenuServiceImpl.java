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
package org.boozsoft.service;


import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.boozsoft.domain.vo.MenuVO;
import org.boozsoft.repo.MenuRepository;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.core.tool.node.ForestNodeMerger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl {

    private final MenuRepository repository;

    public Mono<List<MenuVO>> tree() {
        return repository.findAll()
                .filter(menu->menu.getCategory()!=1000)
                .map(menu -> {
            MenuVO menuVO = new MenuVO();
            Menu cc = menu;
            BeanUtil.copyProperties(menu, menuVO);
            menuVO.setMeta(CollectOfUtils.mapof(
                    "title", menu.getName()
            ));
            return menuVO;
        }).collectList().map(ForestNodeMerger::merge);
    }

    /**
     * 中间表（角色菜单）获取菜单
     *
     * @param roleId 传入roleId
     * @return
     */
    public Mono<List<MenuVO>> roleMenuByRoleId(Long roleId) {
        return null;
    }

    /**
     * 中间表（用户菜单）获取菜单
     *
     * @param userId 传入userId
     * @return
     */
    public Mono<List<MenuVO>> userMenuByUserId(Long userId) {
        return null;
    }

    public Mono<List<MenuVO>> routeTree(String roleId) {
        List<String> menuIds = CollectOfUtils.listof("1");
        return repository.findAllById(menuIds).map(menu -> {
            MenuVO menuVO = new MenuVO();
            Menu cc = menu;
            BeanUtil.copyProperties(menu, menuVO);
            menuVO.setName(menu.getParentId() + "." + menu.getPath());
            menuVO.setMeta(CollectOfUtils.mapof(
                    "title", menu.getName()
            ));
            return menuVO;
        }).collectList().map(list -> ForestNodeMerger.merge(list));
    }

    public Mono<List<MenuVO>> routeTree() {
        return routeTree(repository.findAll());
    }

    public Mono<List<MenuVO>> routeTree(Flux<? extends Menu> flux) {
        return flux.map(this::routeTreeMap)
                .collectList().map(ForestNodeMerger::merge);
    }

    public MenuVO routeTreeMap(Menu menu) {
        MenuVO menuVO = new MenuVO();
        BeanUtil.copyProperties(menu, menuVO);
//        menuVO.setName(menu.getParentId() + "." + menu.getPath());
//        menuVO.setName(menu.getParentId() + "." + menu.getPath());
//        menuVO.setName(menu.getParentId() + "." + menu.getPath());
        HashMap meta=new HashMap<>(CollectOfUtils.mapof(
                "title", menu.getName(),
                "target", menu
        ));
        if(menu.isHidden()){
            meta.put("hideMenu",true);
        }
        menuVO.setMeta(meta);
        if(menu.getComponentName()!=null){
            menuVO.setName(menu.getComponentName());
        }
        return menuVO;
    }

}
