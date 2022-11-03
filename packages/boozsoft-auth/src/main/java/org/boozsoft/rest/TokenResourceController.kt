package org.boozsoft.rest

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.boozsoft.config.oauth2.LoadUserApi
import org.boozsoft.repo.*
import org.boozsoft.resource.TokenResource
import org.boozsoft.service.MenuServiceImpl
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Predicate as Predicate1

@RestController
@RequestMapping("/tokenResource")
@Api(value = "token资源", tags = ["token资源"])
class TokenResourceController {
    @Autowired
    lateinit var tokenResource: TokenResource

    @Autowired
    lateinit var menuService: MenuServiceImpl

    @Autowired
    lateinit var loadUserApi: LoadUserApi

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRoleRepository: UserRoleRepository

    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var repository: UserRepository

    @Autowired
    lateinit var roleMenuRepository: RoleMenuRepository

    // roleMenuIds,
    // 过滤出平台
    // 获取菜单
    @GetMapping("/getTokenPlatforms")
    @ApiOperation(value = "获取平台集合", notes = "获取平台集合")
    fun getTokenPlatforms(): Mono<R<Any>> {
        // 查询所有平台
        val allPlatformMono = menuRepository.findPlatformAll().cache().map { it }.cache()
        var start = System.currentTimeMillis()
        return Mono.zip(
            allPlatformMono.collectList(),
            tokenResource.systemUser
                .flatMapMany {
                    println("2:" + (System.currentTimeMillis() - start))
                    userRoleRepository.findByUserId(it.id)
                }
                .map { it.roleId }
                .collectList()
                .flatMapMany {
                    println("3:" + (System.currentTimeMillis() - start))
                    roleMenuRepository.findAllByRoleIdIn(it)
                }
                .map { it.menuId }
                .collectList()
                .doOnNext{ println("8:" + (System.currentTimeMillis() - start))}
        )
            .map {
                it.t1.filter { it3 ->
                    println("55:" + (System.currentTimeMillis() - start))
                    it.t2.contains(it3.id)
                }
            }
            .map {
                println("4:" + (System.currentTimeMillis() - start))
                R.ok(it)
            }

//        println("1:" + start)
//
//
//            // 角色所拥有的平台id集合
//            .flatMapMany { it2 -> allPlatformMono.filter { platformItem -> it2.contains(platformItem.id) } }
//            .collectList()


        // 最终角色对应的平台集合
    }

    @ApiOperation(value = "获取菜单集合", notes = "获取菜单集合 ")
    @GetMapping("/getTokenTreeMenus")
    fun getMenus(platformId: String): Mono<R<Any>>? {
        return menuService
            .routeTree(menuRepository.findAllByPlatformId(platformId))
            .map { R.ok(it) }
    }


    @ApiOperation(value = "mock 上传用户数据", notes = "mock 上传用户数据 ")
    @PostMapping("/pushUserState")
    fun pushUserState(@RequestBody params: Map<String, Any>): Mono<R<Any>>? {
        return loadUserApi
            .saveSecurtyStateMap(
                mapOf(
                    "user" to params["user"],
                    "roles" to params["roles"],
                    "permissions" to params["permissions"],
                    "token" to params["token"]
                )
            )
            .map { R.ok() }
    }

}