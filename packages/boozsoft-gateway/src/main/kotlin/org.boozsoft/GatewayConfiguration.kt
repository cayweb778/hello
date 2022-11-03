package org.boozsoft

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayConfiguration {
    @Bean
    fun customRouteLocator(routeLocatorBuilder: RouteLocatorBuilder): RouteLocator {
        val routes = routeLocatorBuilder.routes()
//        routes //名称为gateway-beijing的路由,匹配地址 /bj/** 使用  Nacos  里的 beijing 去处理请求 lb为负载均衡
//            .route("auth") { r: PredicateSpec ->
//                r.path("/auth/**")
//                    .filters { f: GatewayFilterSpec -> f.rewritePath("/auth/(?<segment>.*)", "/$\\{segment}") }
//                    .uri("http://localhost:8082")
//            }
//            .route("gateway-shanghai") { r: PredicateSpec -> r.path("/sh/**").uri("lb://shanghai") }
//            .route(
//                "gateway-after"
//            ) { r: PredicateSpec ->  //匹配路径为 /show
//                r.path("/show") //多个断言之间,使用and方法连接
//                    //断言时间,只能在此时间后访问
//                    .and() //断言查询参数,必须包含age,如  /show?age=1
//                    .query("age") //设置过滤器,在过滤器内添加请求参数,那么实际控制器收到的请求为: /show?age=1&name=tom
//                    .filters { f: GatewayFilterSpec -> f.addRequestParameter("name", "tom") } //使用shanghai去处理请求
//                    .uri("lb://shanghai")
//            } //将路径为 /personal 的请求,转到石墨文档,石墨文档收到请求后,请求地址为: https://shimo.im/personal
//            .route("gateway-shimo") { r: PredicateSpec -> r.path("/personal").uri("https://shimo.im") }
//            .build()
        return routes.build()
    }
}