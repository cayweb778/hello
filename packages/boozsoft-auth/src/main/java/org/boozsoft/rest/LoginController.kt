package org.boozsoft.rest


import com.alibaba.fastjson.JSON
import org.boozsoft.domain.entity.group.GroupUser
import org.boozsoft.redis.domain.entity.RedisSystemUser
import org.boozsoft.repo.group.GroupSysUserRepository
import org.boozsoft.resource.TokenResource
import org.boozsoft.service.impl.LoadUserImpl
import org.springbooz.core.tool.result.R
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.util.*

//@RequiredArgsConstructor
@RestController
class LoginController {
    @Autowired
    lateinit var groupSysUserRepository: GroupSysUserRepository

    @Autowired
    lateinit var loadUserApi: LoadUserImpl


    @Autowired
    lateinit var tokenResource: TokenResource

    @Autowired
    lateinit var stringRedisTemplate: StringRedisTemplate


    fun getUserInfo(groupUser: GroupUser): Mono<Any> {
        val uuid = UUID.randomUUID().toString()
        var map = mutableMapOf<String, Any>("user" to groupUser, "username" to groupUser.username, "token" to uuid, "roles" to listOf<String>(), "permissions" to listOf<String>())
        var rolesFlux = loadUserApi.loadRoles((groupUser as GroupUser).id).collectList().doOnNext { map.put("roles", it) }

//                var permissionFlux =
//                    rolesFlux.flatMapMany { loadUserApi.loadPermissions(it) }.doOnNext { map.put("permissions", it) }
//                permissionFlux.then()
//        val thenReturn = loadUserApi.saveSecurtyStateMap2(Mono.just(uuid), map).then().thenReturn(map)
        return rolesFlux.thenReturn(map);

    }


    /**
     * return code
     * 200 is success login
     * 30001 is already logged
     * 30002 is error
     */
    var USERINFO_Prefx: String? = "USERINFO__"
    var TOKEN_Prefx = "USERTOKEN__"

    @RequestMapping("/login/auth")
//  fun getUser(): Flux<GroupUser> {
    fun login(username: String, password: String,/* force login */force: String?): Mono<R<Any>> {
        return groupSysUserRepository.findByUsernameAndAndPassword(username, password)
                .flatMap {
                    // if usename == dev skip repeat login
                    if(username=="dev" ){
                        this.getUserInfo(it)
                            .map { LoginResult.ok("200", "登陆成功", it) }
                    }
                    // 1: if force login  to direct logic , 2: if redis not exist user to direct logic
                    else if (force == "true") {
                        var redisUser = JSON.parseObject(stringRedisTemplate.opsForValue().get(USERINFO_Prefx + username), RedisSystemUser::class.java)
                        stringRedisTemplate.delete(USERINFO_Prefx + username)
                        stringRedisTemplate.delete(TOKEN_Prefx + redisUser.token)
                        // login handle
                        this.getUserInfo(it)
                                .map { LoginResult.ok("200", "登陆成功", it) }

                    } else if (!stringRedisTemplate.hasKey(USERINFO_Prefx+username)) {
                        this.getUserInfo(it)
                                .map { LoginResult.ok("200", "登陆成功", it) }
                    }
                    // redis not empty ,already logged in,  repeat login return getAlreadyLoggedResult()
                    else Mono.just(LoginResult.ok("30001", "已经登陆过了", null))
                }
                // Username or Password Error: groupSysUserRepository find not user , return '3002 username or password error'
                .defaultIfEmpty(LoginResult.ok("30002", "用户名或者密码错误", null))
    }

    @RequestMapping("/login/logout")
//  fun getUser(): Flux<GroupUser> {
    fun logout(username: String): Mono<R<Any>> {
        return tokenResource.getToken().doOnNext {
            val username = stringRedisTemplate.opsForValue().get(TOKEN_Prefx + it)
            stringRedisTemplate.delete(TOKEN_Prefx + it)
            stringRedisTemplate.delete(USERINFO_Prefx + username)
        }.map { R.ok() }
    }
}