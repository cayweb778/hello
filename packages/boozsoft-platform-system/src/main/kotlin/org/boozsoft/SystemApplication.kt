package org.boozsoft

import org.springbooz.datasource.r2dbc.EnableBoozR2dbcRouter
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.scheduling.annotation.EnableAsync
import reactivefeign.spring.config.EnableReactiveFeignClients

@EnableR2dbcAuditing
@EnableBoozR2dbcRouter // 激活BoozR2dbc及数据源路由
@EnableDiscoveryClient
@EnableReactiveFeignClients
@EnableR2dbcRepositories("org.boozsoft.repo")
@EnableRedisRepositories("org.boozsoft.redis")
@EnableAsync
@EnableCaching
@SpringBootApplication(scanBasePackages = ["org.boozsoft", "org.springbooz.webflux"])
class SystemApplication

fun main(args: Array<String>) {
    SpringApplication.run(SystemApplication::class.java, *args)
}
