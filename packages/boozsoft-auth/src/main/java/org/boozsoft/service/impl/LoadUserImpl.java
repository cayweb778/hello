package org.boozsoft.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.RequiredArgsConstructor;
import org.boozsoft.config.oauth2.BoozTokenUser;
import org.boozsoft.config.oauth2.LoadUserApi;
import org.boozsoft.config.oauth2.Permission;
import org.boozsoft.config.oauth2.Role;
import org.boozsoft.domain.vo.UserDTO;
import org.boozsoft.redis.domain.entity.RedisSystemUser;
import org.boozsoft.redis.domain.entity.RedisTokenAndUsername;
import org.boozsoft.redis.repo.RedisSystemUserRepository;
import org.boozsoft.redis.repo.RedisTokenAndUsernameRepository;
import org.boozsoft.repo.*;
import org.boozsoft.repo.entity.Jigou;
import org.boozsoft.repo.entity.User;
import org.boozsoft.resource.TokenResource;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemJigou;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemRole;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/21 10:59 上午
 */
@RequiredArgsConstructor
@Service
public class LoadUserImpl implements LoadUserApi {
    public final R2dbcEntityTemplate entityTemplate;
    public final JigouRepository jigouRepository;
    public final MenuRepository menuRepository;
    public final UserRepository repository;
    public final RoleMenuRepository roleMenuRepository;
    public final RoleRepository roleRepository;
    public final UserJiGouRepository userJiGouRepository;
    public final UserRoleRepository userRoleRepository;
//    Function<BoozOidcUser, Mono<OidcUser>> setContext = (oidcUser -> {
//        return Mono.just(oidcUser)
//                // 设置SystemRole
//                .flatMap(user2 -> findSystemRoleList(oidcUser))
//                .map(systemRoleSet -> {
//                    BoozOidcUserSet.setSystemRole(oidcUser, systemRoleSet);
//                    return oidcUser;
//                })
//                // 设置SystemJiGou
//                .flatMap(item -> findSystemJiGouList(oidcUser))
//                .map(systemJigouSet -> {
//                    BoozOidcUserSet.setSystemJiGou(oidcUser, systemJigouSet);
//                    return oidcUser;
//                })
//                // 设置SystemUserPermission
//                .flatMap(item -> findSystemJiGouList(oidcUser))
//                .map(systemJigouSet -> {
//                    SystemUserPermission systemUserPermission = new SystemUserPermission();
//                    BoozOidcUserSet.setSystemUserPermission(oidcUser, systemUserPermission);
//                    return oidcUser;
//                });
//    });

//    /**
//     * 获取所属机构集合
//     *
//     * @return
//     */
//    public Mono<Set<SystemJigou>> findSystemJiGouList(BoozOidcUser boozOidcUser) {
//        return userJiGouRepository.findAllByUserId(boozOidcUser.getUser().getId())
//                .switchIfEmpty(Mono.error(new Exception("异常：用户机构为空!")))
//                .map(item -> item.getJigouId()).collectList().map(HashSet::new)
//                .flatMapMany(jigouRepository::findAllById)
//                .map(item -> {
//                    SystemJigou systemJigou = new SystemJigou();
//                    systemJigou.setId(item.getId());
//                    systemJigou.setTenantId(item.getTenantId());
//                    systemJigou.setParentId(item.getParentId());
//                    systemJigou.setDeptType(item.getDeptType());
//                    systemJigou.setFullName(item.getFullName());
//                    systemJigou.setRemark(item.getRemark());
//                    systemJigou.setIsDeleted(item.getIsDeleted());
//                    systemJigou.setDeptName(item.getDeptName());
//                    return systemJigou;
//                })
//                .collectList().map(HashSet::new);
//    }
//
//    /**
//     * 获取所属角色集合
//     *
//     * @return
//     */
//    public Mono<Set<SystemRole>> findSystemRoleList(BoozOidcUser boozOidcUser) {
//        return userRoleRepository.findByUserId(boozOidcUser.getUser().getId())
//                .switchIfEmpty(Mono.error(new Exception("异常：用户角色为空!")))
//                .map(UserRole::getRoleId).collectList()
//                .flatMapMany(roleRepository::findAllById)
//                .map(item -> {
//                    SystemRole systemRole = new SystemRole();
//                    systemRole.setId(item.getId());
//                    systemRole.setTenantId(item.getTenantId());
//                    systemRole.setParentId(item.getParentId());
//                    systemRole.setRoleName(item.getRoleName());
//                    systemRole.setRoleAlias(item.getRoleAlias());
//                    systemRole.setIsDeleted(item.getIsDeleted());
//                    return systemRole;
//                })
//                .collectList()
//                .map(HashSet::new);
//    }
//
//    /**
//     * 获取oauth2上下文
//     *
//     * @param oidcUser
//     * @return
//     */
//    public Mono<BoozOidcUser> getOidcUser(BoozOidcUser oidcUser, Map claims) {
//        Map oidcUserClaims = JSON.parseObject(JSON.toJSONString(oidcUser.getClaims()), Map.class);
//        oidcUserClaims.putAll(claims);
//        return Mono.just(oidcUserClaims)
//                // 创建oauth2用户信息
//                .map(OidcUserInfo::new)
//                // 创建oauth2用户
//                .map(oidcUserInfo -> new BoozOidcUser(Collections.unmodifiableCollection(oidcUser.getAuthorities()), oidcUser.getIdToken(), oidcUserInfo));
//
//    }

    public Set<SystemJigou> jiGouCastSystemJiGou(Set<Jigou> jigouSet) {
        return null;
    }

    @Autowired
    TokenResource tokenResource;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    String USERINFO_Prefx="USERINFO__";
    String TOKEN_Prefx="USERTOKEN__";
    //    @Autowired
//    RedisUserPermissionRepository redisUserPermissionRepository;
//    @Autowired
//    RedisUserRoleRepository redisUserRoleRepository;
    public Mono<Map> saveSecurtyStateMap2(Mono<String> tokenMono, Map map) {

        Mono<RedisSystemUser> userMono = Mono.just(new RedisSystemUser())
                .flatMap(redisSystemUser -> {
                    return tokenMono.map(token -> {
                        redisSystemUser.setToken(token);
                        return redisSystemUser;
                    });
                })
                .map(redisSystemUser -> {
//                    SystemUser systemUser = (SystemUser) map.get("user");
//                    BeanUtils.copyProperties(systemUser, redisSystemUser);
//                    redisSystemUser.setId(token);
//                    redisSystemUser.setTargetId(systemUser.getId());
                    redisSystemUser.setAbc("666");
                    redisSystemUser.setUser(JSON.toJSONString(map.get("user")));
                    redisSystemUser.setRoles(JSON.toJSONString(map.get("roles")));
                    redisSystemUser.setPermissions(JSON.toJSONString(map.get("permissions")));

                    String username=((LinkedHashMap) map.get("user")).get("username").toString();
                    // set redis userinfo
                    stringRedisTemplate.opsForValue().set(USERINFO_Prefx+username,JSON.toJSONString(redisSystemUser));
                    // set redis tokeninfo
                    stringRedisTemplate.opsForValue().set(TOKEN_Prefx+redisSystemUser.getToken(), username);
                    return redisSystemUser;
                });



//        redisSystemUserRepository.saveAll()
        return userMono.map(item -> map);
    }


    @Override
    public Mono<Map> saveSecurtyStateMap(Map map) {

        return saveSecurtyStateMap2(tokenResource.getToken(), map);
    }

    @Override
    public Mono<Map> getSecurtyStateMap() {
        Long start = System.currentTimeMillis();
        return tokenResource.getToken()
                .flatMap(token -> {
                    RedisSystemUser redisSystemUser = new RedisSystemUser();
                    redisSystemUser.setToken(token);

                    RedisSystemUser redisSystemUser2 = new RedisSystemUser();
                    redisSystemUser2.setAbc("666");

                    String tokenUsername = stringRedisTemplate.opsForValue().get(TOKEN_Prefx + token);
                    String userInfoJson = stringRedisTemplate.opsForValue().get(USERINFO_Prefx + tokenUsername);
                    RedisSystemUser byToken = JSON.parseObject(userInfoJson, RedisSystemUser.class);

                    if (byToken == null) {
                        return Mono.empty();
                    }
                    return Mono.just(byToken);

                })
                .map(redisSystemUser -> {
                    System.out.println("速度：" + (System.currentTimeMillis() - start));
                    return CollectOfUtils.mapof(
                            "user", JSON.parseObject(redisSystemUser.getUser(), SystemUser.class),
                            "roles", JSONArray.parseArray(redisSystemUser.getRoles(), Role.class),
                            "permissions", JSONArray.parseArray(redisSystemUser.getRoles(), Permission.class)
                    );
//                            JSONArr
//                        SystemUser systemUser=;
//                    redisSystemUser.getUser(JSON.toJSONString((SystemUser)map.get("user")));
//                        redisSystemUser.getRoles(JSON.toJSONString((List<Role>)map.get("roles")));
//                        redisSystemUser.getRoles(JSON.toJSONString((List<Permission>)map.get("permission")));
//                    }
//                    JSONArray.parse(b)
//                    Iterable<RedisUserRole> allById = redisUserRoleRepository.findAllById(List.of(token));
//                    Iterable<RedisUserPermission> allById1 = redisUserPermissionRepository.findAllById(List.of(token));
//                    return Mono.just(Map.of());
//                );
                });
    }

    /**
     * 加载用户上下文
     *
     * @return
     */
    @Override
    public Mono<SystemUser> loadUser() {
        Mono<SystemUser> map1 = Mono.just("").flatMap(item -> tokenResource.getTokenUser()
                .map(BoozTokenUser::getOpenId)
                .flatMap(openId -> {
                    Mono<SystemUser> systemUser = findSystemUser(openId);
                    return systemUser.switchIfEmpty(createUser(openId));
                }));
        return Mono.deferContextual(ctx -> {
            ServerWebExchange serverWebExchange = ctx.get(ServerWebExchange.class);
            Map stateMap = serverWebExchange.getAttribute("stateMap");
            if (stateMap == null) {
                return map1;
            }
            return Mono.just((SystemUser) stateMap.get("user"));
        });

    }

    @Override
    public Flux<org.boozsoft.config.oauth2.Role> loadRoles(String userId) {

        return roleRepository
                .findAllById(userRoleRepository.findByUserId(userId).map(item -> item.getRoleId()))
                .filter(item -> item.getIsDeleted() == 0)
                .map(item -> {
                    Role role = new Role();
                    role.setId(item.getId());
                    role.setParentId(item.getParentId());
                    role.setRoleAlias(item.getRoleAlias());
                    role.setSort(item.getSort());
                    role.setSort(item.getSort());
                    role.setRoleName(item.getRoleName());
                    return role;
                });
//        Flux<Role> roleFlux = roleRepository.findAllById(roleIdFlux);
//        Flux<Long> menuIdFlux = roleFlux.collectList().flatMapMany(roleMenuRepository::findAllByRoleIdInOrderBySortNoAscMenuIdAsc).map(RoleMenu::getMenuId);
//        Flux<Menu> menuFlux = menuIdFlux.collectList().map(item->item).flatMapMany(menuRepository::findAllById);
//        return menuFlux.map(Menu::getPath)
//                .map(SimpleGrantedAuthority::new)
//                .collectList()
//                // 实例化上下文用户
//                .map(authoritieList -> new BoozOidcUser(new HashSet<>(authoritieList), idToken, userInfo, userNameAttributeName))
//                .flatMap(oidcUser -> {
//                    // 设置SystemUser
////                BoozOidcUserSet.setSystemUser(oidcUser, user);
////                // 设置上下文信息
////                return setContext.apply(oidcUser);
//                    return new SystemUser();
//                });
    }

    @Override
    @Deprecated
    public Flux<Permission> loadPermissions(List<org.boozsoft.config.oauth2.Role> roleList) {
        return null;
//    return menuRepository
//        .findAllById(roleMenuRepository.findAllByRoleIdInOrderBySortNoAscMenuIdAsc(roleList).map(item -> item.getMenuId()))
//        .map(item -> {
//          Permission permission = new Permission();
//          permission.setId(item.getId());
//          permission.setName(item.getPath());
//          return permission;
//        });
    }


//    @Override
//    public Flux<Menu> loadMenus(List<Role> roleList) {
//        return menuRepository
//                .findAllById(roleMenuRepository.findAllByRoleIdInOrderBySortNoAscMenuIdAsc(roleList).map(item -> item.getMenuId()));
//    }

    public Mono<SystemUser> findSystemUser(String openId) {
        return repository.findByOpenid(openId).map(this::userCastSystemUser);
    }

    public Mono<SystemUser> createUser(String openid) {
        int max = 100000000, min = 900000000;
        int ran3 = (int) (Math.random() * (max - min) + min);
        return Mono.just(new User())
                .map(user -> {
                    user = new User();
//                            user.setPhone(phone);
                    user.setOpenid(openid);
                    return user;
                })
                .flatMap(repository::save)
                .flatMap(user ->
                        Mono
                                .just("insert into sys_user_role (user_id,role_id) values('" + user.getId() + "','1340925547478441987')")
                                .flatMap(sqlStr ->
                                        entityTemplate
                                                .getDatabaseClient()
                                                .sql(sqlStr)
                                                .fetch()
                                                .rowsUpdated()
                                                .map(a -> user)
                                )
                ).map(this::userCastSystemUser);
    }

    public SystemUser userCastSystemUser(User user) {
        SystemUser systemUser = new SystemUser();
        systemUser.setId(user.getId());
        systemUser.setPhone(user.getPhone());
        return systemUser;
    }

    public Set<SystemRole> roleCastSystemRole(Set<Role> roleSet) {
        return null;
    }

    public SystemUser userCastSystemUser(UserDTO userDTO) {
        SystemUser systemUser = new SystemUser();
        systemUser.setId(userDTO.getId());
//    systemUser.setuserDTO.getCode();
//    systemUser.setuserDTO.getSex();
        systemUser.setPhone(userDTO.getPhone());
        return null;
    }
}
