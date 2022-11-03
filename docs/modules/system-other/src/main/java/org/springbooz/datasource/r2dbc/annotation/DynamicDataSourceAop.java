package org.springbooz.datasource.r2dbc.annotation;//package org.springbooz.datasource.r2dbc.annotation;
//
//import org.springbooz.datasource.r2dbc.BoozR2dbcBaseConnectionFactory;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.lang.reflect.Method;
//
//@Component
//@Aspect
//@Order(Ordered.HIGHEST_PRECEDENCE)
//public class DynamicDataSourceAop {
//
//    @Pointcut(value = "@annotation(org.springbooz.datasource.r2dbc.annotation.R2dbcRouter)")
//    public void point() {
//    }
//    @Around(value = "point()")
//    public Object aroudAop(ProceedingJoinPoint pjp) throws Throwable {
//        // 下面就是切换数据库的核心代码
//        MethodSignature signature = (MethodSignature) pjp.getSignature();
//        Method method = signature.getMethod();
//        R2dbcRouter dataSource = method.getAnnotation(R2dbcRouter.class);
//        if (method.getReturnType() == Mono.class) {
//            return BoozR2dbcBaseConnectionFactory.putDataSource((Mono<?>) pjp.proceed(), dataSource.databaseName());
//        } else {
//            return BoozR2dbcBaseConnectionFactory.putDataSource((Flux<?>) pjp.proceed(), dataSource.databaseName());
//        }
//    }
//
//}
