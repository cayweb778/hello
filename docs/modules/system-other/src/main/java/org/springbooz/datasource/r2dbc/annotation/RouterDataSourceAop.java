package org.springbooz.datasource.r2dbc.annotation;//package org.springbooz.datasource.r2dbc.annotation;
//
//import lombok.RequiredArgsConstructor;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springbooz.datasource.r2dbc.BoozR2dbcRoutingConnectionFactory;
//import org.springbooz.datasource.r2dbc.domain.entity.BoozDataSourceRouterApi;
//import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
//import org.springbooz.webflux.holder.BoozContextHolder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import reactor.core.CorePublisher;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Method;
//import java.util.Map;
//
//import static org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder.r2dbcRouterBuider;
//
//@Aspect
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@DependsOn("connectionFactory")
//
//@RequiredArgsConstructor
//public class RouterDataSourceAop {
//    private final BoozR2dbcRoutingConnectionFactory r2dbcRoutingConnectionFactory;
//    private final BoozDataSourceRouterApi boozDataSourceRouterApi;
//    @Lazy
//    @Autowired
//    R2dbcRouterLoader routerLoader;
//
//    @Around(value = "point()")
//    public Object aroudAop(ProceedingJoinPoint pjp) throws Throwable {
//      return pjp.proceed();
////
////        // 下面就是切换数据库的核心代码
////        MethodSignature signature = (MethodSignature) pjp.getSignature();
////        Method method = signature.getMethod();
////        R2dbcRouter dataSource = method.getAnnotation(R2dbcRouter.class);
////
////        Mono schemaSpecityMono = null;
////        if (dataSource.value().length > 0) {
////            SCHEMA_TYPE schemaType = dataSource.value()[0];
////            // 处理自动指定数据源 ( SESSION 、 默认 )
////            schemaSpecityMono = handleAutoSchema(schemaType);
////        } else {
////            // 处理手动指定数据源
////            schemaSpecityMono = Mono.just(handleManualSchema(dataSource));
////            // 处理模式
////
////        }
////
////        // 开始执行函数方法
////        Class<?> methodReturnType = method.getReturnType();
////        return handleProceeding(methodReturnType, pjp, schemaSpecityMono);
//
//
//    }
//
//
//
//    private Object handleProceeding(Class<?> methodReturnType, ProceedingJoinPoint pjp, Mono<R2dbcRouterDatasourceModel> dbcRouterDatasourceModel) {
//        if (!CorePublisher.class.isAssignableFrom(methodReturnType)) {
//            System.err.println("被@R2dbcRouter的类 应该返回Mono或者Flux");
//        }
//        Mono b=useSessionCompanySchemaName("bjsgkj-001")
//
//                .flatMap(item->dbcRouterDatasourceModel)
//                .flatMap(dbcRouterDatasourceModel2 -> {
//                    Mono a=null;
//                    CorePublisher corePublisher = null;
//                    try {
//                        corePublisher = (CorePublisher) pjp.proceed();
//                    } catch (Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//                    CorePublisher corePublisher1 = r2dbcRoutingConnectionFactory.loadDataSource(
//                            (Class<? extends CorePublisher>) methodReturnType,
//                            corePublisher,
//                            r2dbcRouterBuider()
//                                    .database(dbcRouterDatasourceModel2.databaseName)
//                                    .schema(dbcRouterDatasourceModel2.schemaName));
//                    if(corePublisher1 instanceof Mono){
//                        a= (Mono) corePublisher1;
//                    }else if(corePublisher1 instanceof Flux){
//                        a=((Flux)corePublisher1).collectList();
//                    }
//                    return a;
//                });
//        if(Mono.class.isAssignableFrom(methodReturnType)){
//            return b;
//        }
//        if(Flux.class.isAssignableFrom(methodReturnType)){
//            return Flux.from(b);
//        }
//        return null;
//
//
//    }
//
//    public Mono<Object> useSessionCompanySchemaName(String companySchemaName) {
//        return BoozContextHolder.getSession()
//                .map(session -> {
//                    Map<String, Object> sessionMap = session.getAttributes();
//                    sessionMap.put("companySchemaName", companySchemaName);
//                    return sessionMap;
//                });
//    }
//
//    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.RestController)")
//    public void point() {
//    }
//}
